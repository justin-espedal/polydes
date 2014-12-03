package stencyl.ext.polydes.datastruct.data.structure.cond;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import org.w3c.dom.Element;

import stencyl.ext.polydes.datastruct.data.folder.EditableObject;
import stencyl.ext.polydes.datastruct.data.structure.Structure;
import stencyl.ext.polydes.datastruct.data.structure.StructureDefinition;
import stencyl.ext.polydes.datastruct.grammar.ExpressionParser;
import stencyl.ext.polydes.datastruct.grammar.RuntimeLanguage;
import stencyl.ext.polydes.datastruct.grammar.SyntaxException;
import stencyl.ext.polydes.datastruct.grammar.SyntaxNode;
import stencyl.ext.polydes.datastruct.io.XML;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureConditionPanel;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.sw.util.VerificationHelper;

public class StructureCondition extends EditableObject
{
	public StructureDefinition def;
	public SyntaxNode root;
	
	private String text;
	
	public StructureCondition(StructureDefinition def, String text)
	{
		this.def = def;
		setText(text);
	}
	
	public boolean check(Structure s)
	{
		return check(s, null);
	}
	
	public boolean check(Structure s, Object listItem)
	{
		System.out.println("check");
		
		if(root == null)
			return false;
		
		try
		{
			System.out.println("eval begin");
			structureRef = s;
			idMap.put("this", s);
			idMap.put("item", listItem);
			return check(root);
		}
		catch (SyntaxException e)
		{
			System.out.println("Bad syntax, returning false");
//			System.out.println("===");
//			e.printStackTrace();
//			System.out.println("===");
			return false;
		}
	}
	
	//State variables.
	private static HashMap<String, Object> idMap = new HashMap<String, Object>(2);
	private static Structure structureRef;
	
	public static void dispose()
	{
		idMap.clear();
		structureRef = null;
	}
	
	private boolean check(SyntaxNode n) throws SyntaxException
	{
		try
		{
			return (Boolean) eval(n);
		}
		catch(ClassCastException ex)
		{
			throw new SyntaxException(ex);
		}
		catch(NullPointerException ex)
		{
			throw new SyntaxException(ex);
		}
	}
	
	private Object eval(SyntaxNode n) throws SyntaxException
	{
		try
		{
//			if(n.children != null)
//				System.out.println(n.type + ": " + StringUtils.join(n.children.toArray(), ", "));
//			else
//				System.out.println(n.type + ": no children");
			
			switch(n.type)
			{
				case BOOL: return (Boolean) n.data;
				case FLOAT: return (Float) n.data;
				case INT: return (Integer) n.data;
				case STRING: return (String) n.data;
				case NULL: return null;
				case REFERENCE:
					String refName = (String) n.data;
					Object ref = idMap.get(refName);
					if(ref == null)
						ref = structureRef.getPropByName(refName);
					return ref;
				
				case AND: return RuntimeLanguage.and(eval(n.get(0)), eval(n.get(1)));
				case OR: return RuntimeLanguage.or(eval(n.get(0)), eval(n.get(1)));
				case NOT: return RuntimeLanguage.not(eval(n.get(0)));
				case EQUAL: return RuntimeLanguage.equals(eval(n.get(0)), eval(n.get(1)));
				case NOTEQUAL: return !RuntimeLanguage.equals(eval(n.get(0)), eval(n.get(1)));
				case GT: return RuntimeLanguage.gt(eval(n.get(0)), eval(n.get(1)));
				case LT: return RuntimeLanguage.lt(eval(n.get(0)), eval(n.get(1)));
				case GE: return RuntimeLanguage.ge(eval(n.get(0)), eval(n.get(1)));
				case LE: return RuntimeLanguage.le(eval(n.get(0)), eval(n.get(1)));
				
				case ADD: return RuntimeLanguage.add(eval(n.get(0)), eval(n.get(1)));
				case SUB: return RuntimeLanguage.sub(eval(n.get(0)), eval(n.get(1)));
				case MOD: return RuntimeLanguage.mod(eval(n.get(0)), eval(n.get(1)));
				case DIVIDE: return RuntimeLanguage.divide(eval(n.get(0)), eval(n.get(1)));
				case MULTIPLY: return RuntimeLanguage.multiply(eval(n.get(0)), eval(n.get(1)));
				case NEGATE: return RuntimeLanguage.negate(eval(n.get(0)));
				
				case FIELD:
					Object o = eval(n.get(0));
					String fieldName = (String) n.get(1).data;
					
//					System.out.println("FIELD");
//					System.out.println(o);
//					System.out.println(fieldName);
					
					if(o instanceof Structure)
						return ((Structure) o).getPropByName(fieldName);
					else
						return RuntimeLanguage.field(o, fieldName);
					
				case METHOD:
					//TODO: May not work with fields that have primary parameters.
					//Can look at Haxe's Runtime callField() to see what happens there
					Object callOn = eval(n.get(0));
					String methodName = (String) n.get(1).data;
					List<Object> args = new ArrayList<Object>();
					for(int i = 2; i < n.getNumChildren(); ++ i)
						args.add(eval(n.get(i)));
					
//					System.out.println("METHOD");
//					System.out.println(callOn);
//					System.out.println(methodName);
//					System.out.println(StringUtils.join(args.toArray(), ", "));
					
					return RuntimeLanguage.invoke(callOn, methodName, args);
			}
		}
		catch(ClassCastException ex)
		{
			throw new SyntaxException(ex);
		}
		catch(NullPointerException ex)
		{
			throw new SyntaxException(ex);
		}
		
		return null;
	}
	
	public void setText(String text)
	{
		this.text = text;
		root = ExpressionParser.buildTree(text);
	}
	
	public String getText()
	{
		return text;
	}
	
	public StructureCondition copy()
	{
		StructureCondition sc = new StructureCondition(def, text);
		return sc;
	}
	
	private StructureConditionPanel editor;
	
	@Override
	public void disposeEditor()
	{
		if(editor != null)
			editor.dispose();
		
		editor = null;
	}
	
	@Override
	public JPanel getEditor()
	{
		if(editor == null)
			editor = new StructureConditionPanel(this, PropertiesSheetStyle.LIGHT);
		
		return editor;
	}
	
	@Override
	public void revertChanges()
	{
		if(editor != null)
			setText(editor.getOldText());
	}
	
	@Override
	public String toString()
	{
		return "if " + text;
	}
	
	//Backwards compatibility
	
	public static StructureCondition fromXML(StructureDefinition def, Element e)
	{
		if(!e.getTagName().equals("if"))
			return null;
		
		return new StructureCondition(def, subFromXML(XML.child(e, 0)));
	}
	
	public static String subFromXML(Element e)
	{
		if(e.getTagName().equals("is"))
		{
			return XML.read(e, "field") + " == " + codeRepresentation(XML.read(e, "value"));
		}
		else if(e.getTagName().equals("not"))
		{
			if(XML.child(e, 0).getTagName().equals("is"))
			{
				Element sub = XML.child(e, 0);
				return XML.read(sub, "field") + " != " + codeRepresentation(XML.read(sub, "value"));
			}
			else
				return "!(" + subFromXML(XML.child(e, 0)) + ")";
		}
		else if(e.getTagName().equals("and"))
		{
			return subFromXML(XML.child(e, 0)) + " && " + subFromXML(XML.child(e, 1));
		}
		else if(e.getTagName().equals("or"))
		{
			return subFromXML(XML.child(e, 0)) + " || " + subFromXML(XML.child(e, 1));
		}
		else
			return "";
	}
	
	public static String codeRepresentation(String value)
	{
		if(VerificationHelper.isInteger(value) || VerificationHelper.isFloat(value) || value.equals("true") || value.equals("false"))
			return value;
		else
			return "\"" + value + "\"";
	}
}