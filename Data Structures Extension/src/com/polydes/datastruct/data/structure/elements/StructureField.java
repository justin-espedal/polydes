package com.polydes.datastruct.data.structure.elements;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.data.types.builtin.extra.ColorType;
import com.polydes.common.data.types.builtin.extra.ColorType.ColorEditor;
import com.polydes.common.ext.RORealizer;
import com.polydes.common.io.XML;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.HaxeTypeConverter;
import com.polydes.datastruct.data.types.HaxeTypes;
import com.polydes.datastruct.data.types.StructureType;
import com.polydes.datastruct.res.Resources;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.page.StructureDefinitionsWindow;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.Row;
import com.polydes.datastruct.ui.table.RowGroup;
import com.polydes.datastruct.ui.utils.Layout;

public class StructureField extends SDE implements RORealizer<HaxeDataType>
{
	private StructureDefinition owner;
	
	private String varname;
	private HaxeDataType type;
	private String label;
	private String hint;
	private boolean optional;
	private Object defaultValue;
	private EditorProperties props;
	
	private ExtrasMap emap;
	
	public StructureField(StructureDefinition owner, String varname, String type, String label, String hint, boolean optional, String defaultValue, ExtrasMap extras)
	{
		this.owner = owner;
		this.varname = varname;
		this.label = label;
		this.hint = hint;
		this.optional = optional;
		this.defaultValue = defaultValue;
		
		emap = extras;
		DataStructuresExtension.get().getHaxeTypes().requestValue(type, this);
	}
	
	@Override
	public void realizeRO(HaxeDataType type)
	{
		this.type = type;
		this.props = type.loadExtras(emap);
		if(defaultValue instanceof String)
			this.defaultValue = HaxeTypeConverter.decode(type.dataType, (String) defaultValue);
		emap = null;
		if(waitingForTypeInfo != null)
			for(Runnable r : waitingForTypeInfo)
				r.run();
		waitingForTypeInfo = null;
	}
	
	private Runnable[] waitingForTypeInfo = null;
	
	public void waitForTypeInfo(Runnable callback)
	{
		if(type != null)
			callback.run();
		else
		{
			if(waitingForTypeInfo == null)
				waitingForTypeInfo = new Runnable[] {callback};
			else
				waitingForTypeInfo = ArrayUtils.add(waitingForTypeInfo, callback);
		}
	}
	
	public StructureDefinition getOwner()
	{
		return owner;
	}
	
	public void loadExtras(ExtrasMap extras)
	{
		this.props = type.loadExtras(extras);
	}
	
	public EditorProperties getEditorProperties()
	{
		return props;
	}
	
	public void setEditorProperties(EditorProperties props)
	{
		this.props = props;
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
	
	public HaxeDataType getType()
	{
		return type;
	}
	
	public boolean isOptional()
	{
		return optional;
	}
	
	public Object getDefaultValue()
	{
		return defaultValue;
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
	
	public void setDefaultValue(Object defaultValue)
	{
		this.defaultValue = defaultValue;
	}
	
	public void setTypeForPreview(HaxeDataType type)
	{
		this.type = type;
		owner.setFieldTypeForPreview(this, type);
	}
	
	public void setType(HaxeDataType type)
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
	
	public static class FieldType extends SDEType<StructureField>
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
			String defaultValue = take(map, "default");
			ExtrasMap emap = new ExtrasMap();
			emap.putAll(map);
			if(e.hasChildNodes())
				XML.children(e).forEach((child) -> emap.put(child.getTagName(), readExtrasFromElement(child)));
			
			StructureField toAdd = new StructureField(model, name, type, label, hint, optional, defaultValue, emap);
			model.addField(toAdd);
			toAdd.waitForTypeInfo(() -> model.realizeFieldHaxeType(toAdd, toAdd.type));
			
			return toAdd;
		}
		
		public static ExtrasMap readExtrasFromElement(Element e)
		{
			ExtrasMap emap = new ExtrasMap();
			emap.putAll(XML.readMap(e));
			if(e.hasChildNodes())
				XML.children(e).forEach((child) -> emap.put(child.getTagName(), readExtrasFromElement(child)));
			return emap;
		}
		
		@Override
		public void write(StructureField f, Element e)
		{
			e.setAttribute("name", f.getVarname());
			e.setAttribute("type", f.getType().getHaxeType());
			XML.write(e, "label", f.getLabel());
			if(!f.getHint().isEmpty())
				XML.write(e, "hint", f.getHint());
			if(f.isOptional())
				e.setAttribute("optional", "true");
			if(f.getDefaultValue() != null)
				e.setAttribute("default", f.getType().dataType.checkEncode(f.getDefaultValue()));
			
			HaxeDataType dtype = f.getType();
			ExtrasMap emap = dtype.saveExtras(f.getEditorProperties());
			if(emap != null)
				writeExtrasToElement(e.getOwnerDocument(), e, emap);
		}
		
		public static void writeExtrasToElement(Document doc, Element e, ExtrasMap emap)
		{
			for(Entry<String,Object> entry : emap.entrySet())
			{
				if(entry.getValue() instanceof ExtrasMap)
				{
					Element child = doc.createElement(entry.getKey());
					writeExtrasToElement(doc, child, (ExtrasMap) entry.getValue());
					e.appendChild(child);
				}
				else
					e.setAttribute(entry.getKey(), (String) entry.getValue());
			}
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
					new StructureField(def, StructureField.formatVarname(nodeName), HaxeTypes._String.getHaxeType(), nodeName, "", false, null, new ExtrasMap());
			def.addField(newField, def.getEditor().preview);
			return newField;
		}
		
		@Override
		public GuiObject psAdd(PropertiesSheet sheet, Folder parent, DataItem node, StructureField value, int i)
		{
			Card parentCard = sheet.getFirstCardParent(parent);
			
			RowGroup group = new RowGroup(value);
			psLoad(sheet, group, node, value);
			
			parentCard.addGroup(i, group);
			
			if(!sheet.isChangingLayout)
				parentCard.layoutContainer();
			
			return group;
		}
		
		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureField value)
		{
			RowGroup group = (RowGroup) gui;
			Card card = group.card;
			
			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);
			
			psLoad(sheet, group, node, value);
			
			card.addGroup(groupIndex, group);
			card.layoutContainer();
		}
		
		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureField value)
		{
			RowGroup group = (RowGroup) gui;
			Card card = group.card;
			
			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);
			
			sheet.fieldEditorMap.remove(value).dispose();
			
			card.layoutContainer();
		}
		
		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureField value)
		{
			RowGroup group = (RowGroup) gui;
			
			((JLabel) group.rows[0].components[0]).setText(value.getLabel());
			if(!value.getHint().isEmpty())
				sheet.style.setDescription((JLabel) group.rows[2].components[1], value.getHint());
		}
		
		/*================================================*\
		 | Helpers
		\*================================================*/
		
		public void psLoad(PropertiesSheet sheet, RowGroup group, DataItem node, StructureField f)
		{
			String name = f.getLabel().isEmpty() ? f.getVarname() : f.getLabel();
			
			group.rows = new Row[0];
			group.add(sheet.style.createLabel(name), createEditor(sheet, f));
			if(!f.getHint().isEmpty())
			{
				group.add(sheet.style.hintgap);
				group.add(null, sheet.style.createDescriptionRow(f.getHint()));
			}
			group.add(sheet.style.rowgap);
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public JComponent createEditor(PropertiesSheet sheet, final StructureField f)
		{
			JComponent editPanel = null;
			
			DataType type = f.getType().dataType;
			
			if(sheet.fieldEditorMap.containsKey(f))
				sheet.fieldEditorMap.get(f).dispose();
			
			final DataEditor deditor;
			
			//special case for "Structure" editors, because they may need to know which Structure they're in for filtering.
			if(type instanceof StructureType)
				deditor = ((StructureType) type).new StructureEditor(f.getEditorProperties(), sheet.model);
			else
				deditor = type.createEditor(f.getEditorProperties(), sheet.style);
			
			//special case for Color editors inside preview structures. Need to make sure the popup window works.
			if(type instanceof ColorType && sheet.model.getID() == -1)
				((ColorEditor) deditor).setOwner(StructureDefinitionsWindow.get());
			
			deditor.setValue(sheet.model.getProperty(f));
			deditor.addListener(new UpdateListener()
			{
				@Override
				public void updated()
				{
					sheet.model.setProperty(f, deditor.getValue());
					sheet.refreshVisibleComponents();
				}
			});
			
			sheet.fieldEditorMap.put(f, deditor);
			
			editPanel = Layout.horizontalBox(sheet.style.fieldDimension, deditor.getComponents());
			
			if(f.isOptional())
				return constrict(sheet.style, createEnabler(sheet.model, editPanel, f), editPanel);
			else
				return editPanel;
		}
		
		private JCheckBox createEnabler(final Structure model, final JComponent component, final StructureField f)
		{
			final JCheckBox enabler = new JCheckBox();
			enabler.setSelected(model.isPropertyEnabled(f));
			enabler.setBackground(null);
			
			enabler.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if(model.isPropertyEnabled(f) != enabler.isSelected())
					{
						component.setVisible(enabler.isSelected());
						model.setPropertyEnabled(f, enabler.isSelected());
						if(!enabler.isSelected())
							model.clearProperty(f);
						model.setDirty(true);
					}
				}
			});
			
			component.setVisible(model.isPropertyEnabled(f));
			
			return enabler;
		}
		
		private JPanel constrict(PropertiesSheetStyle style, JComponent... comps)
		{
			return Layout.horizontalBox(style.fieldDimension, comps);
		}
	}
}