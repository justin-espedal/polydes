package com.polydes.datastruct.data.types.builtin.extra;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import com.polydes.datastruct.data.core.DataList;
import com.polydes.datastruct.data.core.DataSet;
import com.polydes.datastruct.data.core.Dynamic;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.ExtraProperties;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.Types;
import com.polydes.datastruct.data.types.UpdateListener;
import com.polydes.datastruct.data.types.builtin.BuiltinType;
import com.polydes.datastruct.data.types.builtin.basic.ArrayType.SimpleArrayEditor;
import com.polydes.datastruct.data.types.builtin.basic.StringType.SingleLineStringEditor;
import com.polydes.datastruct.data.types.builtin.extra.SelectionType.DropdownSelectionEditor;
import com.polydes.datastruct.data.types.general.StencylResourceType;
import com.polydes.datastruct.data.types.general.StructureType;
import com.polydes.datastruct.data.types.hidden.DataTypeType;
import com.polydes.datastruct.data.types.hidden.DataTypeType.DataTypeEditor;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.objeditors.StructureObjectPanel;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.utils.Layout;
import com.polydes.datastruct.utils.DLang;

import stencyl.core.lib.Game;

public class SetType extends BuiltinType<DataSet>
{
	public SetType()
	{
		super(DataSet.class, "com.polydes.datastruct.Set", "OBJECT");
	}

	@Override
	public DataEditor<DataSet> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		Extras e = (Extras) extras;
		String properType = "";
		Collection<?> source = null;
		if(e.source != null)
		{
			if(e.sourceType.equals(SourceType.Structure))
			{
				StructureDefinition def = ((StructureType) e.source).def;
				if(def != null)
				{
					source = Structures.getList(def);
					properType = def.getName();
				}
			}
			else if(e.sourceType.equals(SourceType.Resource))
			{
				StencylResourceType<?> dtype = (StencylResourceType<?>) e.source;
				if(dtype != null)
				{
					source = Game.getGame().getResources().getResourcesByType(dtype.javaType);
					properType = dtype.haxeType;
				}
			}
			else
			{
				source = (Collection<?>) e.source;
				if(source == null)
					source = new ArrayList<Dynamic>();
				properType = "String";
			}
		}
		
		if(source == null)
			return new InvalidEditor<DataSet>("Select a valid data source", style);
		
		if(source.isEmpty())
			return new InvalidEditor<DataSet>("The selected source has no items", style);
		
//		if(e.editor.equals(Editor.Grid))
//			return comps(style.createSoftLabel("Grid is unimplemented"));
		
		else// if(editorType.equals("Checklist"))
		{
			return new ChecklistDataSetEditor(source, style, properType);
		}
	}

	@Override
	public DataSet decode(String s)
	{
		int typeMark = s.lastIndexOf(":");
		if(typeMark == -1)
			return new DataSet(Types.fromXML("Dynamic"));
		
		DataType<?> dtype = Types.fromXML(s.substring(typeMark + 1));
		if(dtype == null)
			return new DataSet(Types.fromXML("Dynamic"));
		
		DataSet toReturn = new DataSet(dtype);
		
		for(String s2 : StringUtils.split(s.substring(1, typeMark - 1), ","))
			toReturn.add(dtype.decode(s2));
		
		return toReturn;
	}

	@Override
	public String encode(DataSet t)
	{
		Object[] a = t.toArray(new Object[0]);
		String s = "[";
		DataType<?> type = t.genType;
		
		for(int i = 0; i < a.length; ++i)
		{
			s += type.checkEncode(a[i]);
			
			if(i < a.length - 1)
				s += ",";
		}
		
		s += "]:" + type.haxeType;
		
		return s;
	}
	
	//For set, because none of the data should ever actually be modified, a shallow copy is fine.
	@Override
	public DataSet copy(DataSet t)
	{
		DataSet copySet = new DataSet(t.genType);
		Iterator<Object> it = t.iterator();
		while(it.hasNext())
			copySet.add(it.next());
		return copySet;
	}
	
	@Override
	public void applyToFieldPanel(final StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		final PropertiesSheet preview = panel.getPreview();
		final DataItem previewKey = panel.getPreviewKey();
		final PropertiesSheetStyle style = panel.style;
		
		final int dataSourceRow;
		final int customSourceRow;
		final int dataFilterRow;
		
		//=== Editor
		
//		DataList editorChoices = Lang.datalist(Types._String, "Checklist"/*, "Grid"*/);
//		final DataEditor<String> editorChooser = new SelectionType.DropdownSelectionEditor(editorChoices);
//		editorChooser.setValue("Plain");
//		editorChooser.addListener(new UpdateListener()
//		{
//			@Override
//			public void updated()
//			{
//				e.editor = Editor.valueOf(editorChooser.getValue());
//				preview.refreshDataItem(previewKey);
//			}
//		});
		
		//=== Source Type
		
		DataList sourceTypeChoices = DLang.datalist(Types._String, "Structure", "Resource", "Custom");
		final DataEditor<String> sourceTypeChooser = new DropdownSelectionEditor(sourceTypeChoices);
		sourceTypeChooser.setValue(e.sourceType.name());
		//sourceTypeChooser listener later, after dataSourceRow and customSourceRow are added.
		
		//=== Sources
		
		final DataTypeEditor dataSourceField = new DataTypeEditor();
		dataSourceField.setValue((DataType<?>) e.source);
		dataSourceField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.source = dataSourceField.getValue();
				preview.refreshDataItem(previewKey);
			}
		});
		
		final DataEditor<DataList> customSourceField = new SimpleArrayEditor(style, Types._String);
		customSourceField.setValue((DataList) e.source);
		customSourceField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.source = customSourceField.getValue();
				preview.refreshDataItem(previewKey);
			}
		});
		
		//=== Source Filter

		final DataEditor<String> filterField = new SingleLineStringEditor(null, style);
		filterField.setValue(e.sourceFilter);
		filterField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.sourceFilter = filterField.getValue();
				preview.refreshDataItem(previewKey);
			}
		});
		
		//panel.addGenericRow(expansion, "Editor", editorChooser);
		
		panel.addGenericRow(expansion, "Source Type", sourceTypeChooser);
		dataSourceRow = panel.addGenericRow(expansion, "Source", dataSourceField);
		dataFilterRow = panel.addEnablerRow(expansion, "Filter", filterField, e.sourceFilter != null);
		customSourceRow = panel.addGenericRow(expansion, "Source", customSourceField, StructureObjectPanel.RESIZE_FLAG);
		
		sourceTypeChooser.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.sourceType = SourceType.valueOf(sourceTypeChooser.getValue());
				
				boolean custom = e.sourceType == SourceType.Custom;
				panel.setRowVisibility(dataSourceRow, !custom);
				panel.setRowVisibility(dataFilterRow, !custom);
				panel.setRowVisibility(customSourceRow, custom);
				
				if(custom)
				{
					e.source = customSourceField.getValue();
					e.sourceFilter = null;
				}
				else
				{
					if(e.sourceType == SourceType.Resource)
						dataSourceField.setFilter(DataTypeType.onlyStencylTypes);
					else
						dataSourceField.setFilter(DataTypeType.onlyStructureDefinitions);
					dataSourceField.setValue(null);
					e.source = null;
					e.sourceFilter = null;
				}
				
				preview.refreshDataItem(previewKey);
			}
		});
		
		boolean custom = e.sourceType == SourceType.Custom;
		panel.setRowVisibility(dataSourceRow, !custom);
		panel.setRowVisibility(dataFilterRow, !custom);
		panel.setRowVisibility(customSourceRow, custom);
		
		if(!custom)
		{
			if(e.sourceType == SourceType.Resource)
				dataSourceField.setFilter(DataTypeType.onlyStencylTypes);
			else
				dataSourceField.setFilter(DataTypeType.onlyStructureDefinitions);
		}
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		e.editor = extras.get(EDITOR, Editor.Checklist);
		e.sourceType = SourceType.valueOf(extras.get("sourceType", "Structure"));
		if(e.sourceType.equals(SourceType.Structure) || e.sourceType.equals(SourceType.Resource))
			e.source = Types.fromXML(extras.get("source", ""));
		else
			e.source = extras.get("source", Types._Array, null);
		e.sourceFilter = extras.get("sourceFilter", Types._String, null);
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		emap.put(EDITOR, "" + e.editor);
		emap.put("sourceType", "" + e.sourceType);
		if(e.source != null)
		{
			if(e.sourceType.equals(SourceType.Structure) || e.sourceType.equals(SourceType.Resource))
				emap.put("source", ((DataType<?>) e.source).haxeType);
			else
				emap.put("source", Types._Array.checkEncode(e.source));
		}
		if(e.sourceFilter != null)
			emap.put("sourceFilter", e.sourceFilter);
		return emap;
	}
	
	public class Extras extends ExtraProperties
	{
		public Editor editor;
		public SourceType sourceType;
		public Object source; //StructureType, StencylType, or (Array, Simple editor)
		public String sourceFilter;
	}
	
	enum Editor
	{
		Checklist/*,
		Grid*/
	}
	
	public enum SourceType
	{
		Structure,
		Resource,
		Custom
	}
	
	public static class ChecklistDataSetEditor extends DataEditor<DataSet>
	{
		DataSet set;
		JPanel buttonPanel;
		IdentityHashMap<Object, JCheckBox> map;
		String properType;
		
		public ChecklistDataSetEditor(Collection<?> source, PropertiesSheetStyle style, String properType)
		{
			this.properType = properType;
			
			ArrayList<JCheckBox> buttons = new ArrayList<JCheckBox>();
			
			map = new IdentityHashMap<Object, JCheckBox>();
			
			for(final Object o : source)
			{
				final JCheckBox b = new JCheckBox("" + o);
				buttons.add(b);
				map.put(o, b);
				
				b.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						if(b.isSelected())
							set.add(o);
						else
							set.remove(o);
						updated();
					}
				});
				
				b.setBackground(null);
				b.setForeground(style.labelColor);
			}
			
			buttonPanel = Layout.verticalBox(0, buttons.toArray(new JCheckBox[0]));
		}
		
		@Override
		public void set(DataSet t)
		{
			if(t == null)
				t = new DataSet(Types.fromXML(properType));
			Iterator<Object> it = t.iterator();
			while(it.hasNext())
				map.get(it.next()).setSelected(true);
			set = t;
		}
		
		@Override
		public DataSet getValue()
		{
			return set;
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return comps(buttonPanel);
		}
	}
}
