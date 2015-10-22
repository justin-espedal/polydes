package com.polydes.datastruct.data.structure.elements;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureDefinitionElement;
import com.polydes.datastruct.data.structure.StructureDefinitionElementType;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.ExtraProperties;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.Types;
import com.polydes.datastruct.io.XML;
import com.polydes.datastruct.res.Resources;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.table.Row;
import com.polydes.datastruct.ui.table.RowGroup;
import com.polydes.datastruct.utils.DelayedInitialize;

public class StructureField extends StructureDefinitionElement
{
	private StructureDefinition owner;
	
	private String varname;
	private DataType<?> type;
	private String label;
	private String hint;
	private boolean optional;
	private ExtraProperties extras;
	
	public StructureField(StructureDefinition owner, String varname, DataType<?> type, String label, String hint, boolean optional, ExtrasMap extras)
	{
		this.owner = owner;
		this.varname = varname;
		this.type = type;
		this.label = label;
		this.hint = hint;
		this.optional = optional;
		if(type != null)
			this.extras = type.loadExtras(extras);
	}
	
	public StructureDefinition getOwner()
	{
		return owner;
	}
	
	public void loadExtras(ExtrasMap extras)
	{
		this.extras = type.loadExtras(extras);
	}
	
	public ExtraProperties getExtras()
	{
		return extras;
	}
	
	public void setExtras(ExtraProperties extras)
	{
		this.extras = extras;
	}
	
	public String getHint()
	{
		return hint;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public String getVarname()
	{
		return varname;
	}
	
	public DataType<?> getType()
	{
		return type;
	}
	
	public boolean isOptional()
	{
		return optional;
	}
	
	public void setHint(String hint)
	{
		this.hint = hint;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public void setVarname(String varname)
	{
		owner.setFieldName(this, varname);
		this.varname = varname;
	}
	
	public void setOptional(boolean optional)
	{
		this.optional = optional;
	}
	
	public void setTypeForPreview(DataType<?> type)
	{
		this.type = type;
		owner.setFieldTypeForPreview(this, type);
	}
	
	public void setType(DataType<?> type)
	{
		this.type = type;
		owner.setFieldType(this, type);
	}
	
	@Override
	public String toString()
	{
		return varname + ":" + type;
	}
	
	private StructureFieldPanel editor;
	
	@Override
	public JPanel getEditor()
	{
		if(editor == null)
			editor = new StructureFieldPanel(this, PropertiesSheetStyle.LIGHT);
		
		return editor;
	}
	
	@Override
	public void disposeEditor()
	{
		editor.dispose();
		editor = null;
	}
	
	@Override
	public void revertChanges()
	{
		editor.revert();
	}
	
	public static String formatVarname(String s)
	{
		s = StringUtils.removePattern(s, "[^a-zA-Z0-9_]");
		
		if(s.isEmpty())
			return s;
		
		if(Character.isDigit(s.charAt(0)))
			s = "_" + s;
		if(Character.isUpperCase(s.charAt(0)))
			s = Character.toLowerCase(s.charAt(0)) + s.substring(1);
		
		return s;
	}

	@Override
	public String getDisplayLabel()
	{
		return label;
	}
	
	public static class FieldType extends StructureDefinitionElementType<StructureField>
	{
		public FieldType()
		{
			sdeClass = StructureField.class;
			tag = "field";
			isBranchNode = false;
			icon = Resources.thumb("field.png", 16);
			childTypes = null;
		}
		
		@Override
		public StructureField read(StructureDefinition model, Element e)
		{
			HashMap<String, String> map = XML.readMap(e);
			
			String name = take(map, "name");
			String type = take(map, "type");
			String label = take(map, "label");
			String hint = take(map, "hint");
			boolean optional = take(map, "optional").equals("true");
			ExtrasMap emap = new ExtrasMap();
			emap.putAll(map);
			
			//DataType<?> dtype = Types.fromXML(type);
			StructureField toAdd = new StructureField(model, name, null, label, hint, optional, emap);
			model.addField(toAdd);
			
			DelayedInitialize.addObject(toAdd, "type", type);
			DelayedInitialize.addMethod(toAdd, "loadExtras", new Object[]{emap}, type);
			
			return toAdd;
		}
		
		@Override
		public Element write(StructureField f, Document doc)
		{
			Element e = doc.createElement("field");
			e.setAttribute("name", f.getVarname());
			e.setAttribute("type", f.getType().haxeType);
			e.setAttribute("label", StringEscapeUtils.escapeXml10(f.getLabel()));
			if(!f.getHint().isEmpty())
				e.setAttribute("hint", StringEscapeUtils.escapeXml10(f.getHint()));
			if(f.isOptional())
				e.setAttribute("optional", "true");
			
			DataType<?> dtype = f.getType();
			ExtrasMap emap = dtype.saveExtras(f.getExtras());
			if(emap != null)
			{
				for(Entry<String,String> entry : emap.entrySet())
				{
					e.setAttribute(entry.getKey(), entry.getValue());
					//e.setAttribute(field.getName(), StringEscapeUtils.escapeXml10(writeValue));
				}
			}
			return e;
		}
		
		private String take(HashMap<String, String> map, String name)
		{
			if(map.containsKey(name))
				return map.remove(name);
			else
				return "";
		}

		@Override
		public StructureField create(StructureDefinition def, String nodeName)
		{
			StructureField newField =
					new StructureField(def, StructureField.formatVarname(nodeName), Types._String, nodeName, "", false, new ExtrasMap());
			def.addField(newField, def.getEditor().preview);
			return newField;
		}

		@Override
		public void psLoad(PropertiesSheet sheet, RowGroup group, DataItem node, StructureField f)
		{
			String name = f.getLabel().isEmpty() ? f.getVarname() : f.getLabel();
			
			group.rows = new Row[0];
			group.add(sheet.style.createLabel(name), sheet.createEditor(f));
			if(!f.getHint().isEmpty())
			{
				group.add(sheet.style.hintgap);
				group.add(null, sheet.style.createDescriptionRow(f.getHint()));
			}
			group.add(sheet.style.rowgap);
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureField value)
		{
			RowGroup group = (RowGroup) gui;
			
			((JLabel) group.rows[0].components[0]).setText(value.getLabel());
			if(!value.getHint().isEmpty())
				sheet.style.setDescription((JLabel) group.rows[2].components[1], value.getHint());
		}
	}
}