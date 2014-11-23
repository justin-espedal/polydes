package stencyl.ext.polydes.datastruct.data.structure.cond;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import stencyl.ext.polydes.datastruct.data.folder.EditableObject;
import stencyl.ext.polydes.datastruct.data.structure.Structure;
import stencyl.ext.polydes.datastruct.data.structure.StructureDefinition;
import stencyl.ext.polydes.datastruct.grammar.ExpressionParser;
import stencyl.ext.polydes.datastruct.grammar.Lang;
import stencyl.ext.polydes.datastruct.grammar.Node;
import stencyl.ext.polydes.datastruct.grammar.SyntaxException;
import stencyl.ext.polydes.datastruct.io.XML;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureConditionPanel;

public class StructureCondition extends EditableObject
{
	public StructureDefinition def;
	public Node root;
	
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
	private static HashMap<String, Object> idMap = new HashMap<String, Object>();
	private static Structure structureRef;
	
	private boolean check(Node n) throws SyntaxException
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
	
	private Object eval(Node n) throws SyntaxException
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
				
				case AND: return Lang.and(eval(n.get(0)), eval(n.get(1)));
				case OR: return Lang.or(eval(n.get(0)), eval(n.get(1)));
				case NOT: return Lang.not(eval(n.get(0)));
				case EQUAL: return Lang.equals(eval(n.get(0)), eval(n.get(1)));
				case NOTEQUAL: return !Lang.equals(eval(n.get(0)), eval(n.get(1)));
				case GT: return Lang.gt(eval(n.get(0)), eval(n.get(1)));
				case LT: return Lang.lt(eval(n.get(0)), eval(n.get(1)));
				case GE: return Lang.ge(eval(n.get(0)), eval(n.get(1)));
				case LE: return Lang.le(eval(n.get(0)), eval(n.get(1)));
				
				case ADD: return Lang.add(eval(n.get(0)), eval(n.get(1)));
				case SUB: return Lang.sub(eval(n.get(0)), eval(n.get(1)));
				case MOD: return Lang.mod(eval(n.get(0)), eval(n.get(1)));
				case DIVIDE: return Lang.divide(eval(n.get(0)), eval(n.get(1)));
				case MULTIPLY: return Lang.multiply(eval(n.get(0)), eval(n.get(1)));
				case NEGATE: return Lang.negate(eval(n.get(0)));
				
				case FIELD:
					Object o = eval(n.get(0));
					String fieldName = (String) n.get(1).data;
					
//					System.out.println("FIELD");
//					System.out.println(o);
//					System.out.println(fieldName);
					
					if(o instanceof Structure)
						return ((Structure) o).getPropByName(fieldName);
					else
						return Lang.field(o, fieldName);
					
				case METHOD:
					//TODO: May not work with fields that have primary parameters.
					Object callOn = eval(n.get(0));
					String methodName = (String) n.get(1).data;
					List<Object> args = new ArrayList<Object>();
					for(int i = 2; i < n.getNumChildren(); ++ i)
						args.add(eval(n.get(i)));
					
//					System.out.println("METHOD");
//					System.out.println(callOn);
//					System.out.println(methodName);
//					System.out.println(StringUtils.join(args.toArray(), ", "));
					
					return Lang.invoke(callOn, methodName, args);
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
	
	//TODO: StructureCondition revert and dispose
	private StructureConditionPanel editor;
	
	@Override
	public void disposeEditor()
	{
	}
	
	@Override
	public JPanel getEditor()
	{
		if(editor == null)
			editor = new StructureConditionPanel(this);
		
		return editor;
	}
	
	@Override
	public void revertChanges()
	{
	}
	
	@Override
	public String toString()
	{
		return "if " + text;
	}
	
	//=== REVERSE COMPATIBILITY
	
	/*public static Node fromText(String s)
	{
		
	}*/
	
	public static StructureCondition fromXML(StructureDefinition def, Element e)
	{
		if(!e.getTagName().equals("if"))
			return null;
		
		StructureCondition toReturn = null;//new StructureCondition(subFromXML(def, XML.child(e, 0)));
		
		return toReturn;
	}
	
	public static SubCondition subFromXML(StructureDefinition def, Element e)
	{
		if(e.getTagName().equals("is"))
		{
			return new IsCondition(def.getField(XML.read(e, "field")), XML.read(e, "value"));
		}
		else if(e.getTagName().equals("not"))
		{
			return new NotCondition(subFromXML(def, XML.child(e, 0)));
		}
		else if(e.getTagName().equals("and"))
		{
			return new AndCondition(subFromXML(def, XML.child(e, 0)), subFromXML(def, XML.child(e, 1)));
		}
		else if(e.getTagName().equals("or"))
		{
			return new OrCondition(subFromXML(def, XML.child(e, 0)), subFromXML(def, XML.child(e, 1)));
		}
		else
			return null;
	}
	
	public static Element toXML(Document doc, StructureCondition data)
	{
		Element e = doc.createElement("if");
		//e.appendChild(subToXML(doc, ((StructureCondition) data).c));
		return e;
	}
	
	public static Element subToXML(Document doc, SubCondition data)
	{
		Element e = null;
		if(data == null)
			return doc.createElement("null");
		else if(data instanceof IsCondition)
		{
			IsCondition c = (IsCondition) data;
			e = doc.createElement("is");
			e.setAttribute("field", c.field.getVarname());
			e.setAttribute("value", c.value);
		}
		else if(data instanceof NotCondition)
		{
			NotCondition c = (NotCondition) data;
			e = doc.createElement("not");
			e.appendChild(subToXML(doc, c.c));
		}
		else if(data instanceof AndCondition)
		{
			AndCondition c = (AndCondition) data;
			e = doc.createElement("and");
			e.appendChild(subToXML(doc, c.c1));
			e.appendChild(subToXML(doc, c.c2));
		}
		else if(data instanceof OrCondition)
		{
			OrCondition c = (OrCondition) data;
			e = doc.createElement("or");
			e.appendChild(subToXML(doc, c.c1));
			e.appendChild(subToXML(doc, c.c2));
		}
		
		return e;
	}
}