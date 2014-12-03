package stencyl.ext.polydes.datastruct.data.types.general;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import stencyl.ext.polydes.datastruct.data.core.CollectionPredicate;
import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.structure.Structure;
import stencyl.ext.polydes.datastruct.data.structure.StructureDefinition;
import stencyl.ext.polydes.datastruct.data.structure.StructureField;
import stencyl.ext.polydes.datastruct.data.structure.Structures;
import stencyl.ext.polydes.datastruct.data.structure.cond.StructureCondition;
import stencyl.ext.polydes.datastruct.data.types.DataEditor;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.data.types.UpdateListener;
import stencyl.ext.polydes.datastruct.data.types.builtin.StringType.SingleLineStringEditor;
import stencyl.ext.polydes.datastruct.io.Text;
import stencyl.ext.polydes.datastruct.res.Resources;
import stencyl.ext.polydes.datastruct.ui.comp.UpdatingCombo;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheet;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class StructureType extends DataType<Structure>
{
	public StructureDefinition def;
	
	public StructureType(StructureDefinition def)
	{
		super(Structure.class, def.getClassname(), "OBJECT", def.getName());
		this.def = def;
	}
	
	@Override
	public List<String> generateHaxeClass()
	{
		String s = Text.readString(Resources.getUrlStream("code/Structure.hx"));
		
		String fieldmap = "";
		String variables = "";
		String typeinfo = "";
		typeinfo += String.format("\t\tm.set(\"name\", \"String\");%s", IOUtils.LINE_SEPARATOR_WINDOWS);
		for(StructureField f : def.getFields())
		{
			fieldmap += String.format("\t\tm.set(\"%s\", \"%s\");%s", f.getLabel(), f.getVarname(), IOUtils.LINE_SEPARATOR_WINDOWS);
			variables += String.format("\tpublic var %s:%s;%s", f.getVarname(), getType(f), IOUtils.LINE_SEPARATOR_WINDOWS);
			typeinfo += String.format("\t\tm.set(\"%s\", \"%s\");%s", f.getVarname(), f.getType(), IOUtils.LINE_SEPARATOR_WINDOWS);
		}
		
		s = StringUtils.replace(s, "[PACKAGE]", haxePackage);
		s = StringUtils.replace(s, "[CLASSNAME]", haxeClassname);
		s = StringUtils.replace(s, "[FIELDMAP]", fieldmap, 1);
		s = StringUtils.replace(s, "[TYPEINFO]", typeinfo, 1);
		s = StringUtils.replace(s, "[VARIABLES]", variables, 1);
		s = StringUtils.replace(s, "[CUSTOM]", def.customCode, 1);
		
		ArrayList<String> lines = new ArrayList<String>();
		for(String s2 : s.split(IOUtils.LINE_SEPARATOR_WINDOWS))
			lines.add(s2);
		
		return lines;
	}
	
	private String getType(StructureField f)
	{
		String type = f.getType().haxeType;
		if(f.isOptional())
			return "Null<" + type + ">";
		else
			return type;
	}

	@Override
	public List<String> generateHaxeReader()
	{
		List<String> toReturn = new ArrayList<String>();
		toReturn.add("\tpublic static function r" + xml + "(s:String):" + haxeType);
		toReturn.add("\t{");
		toReturn.add("\t\tif(s == \"\")");
		toReturn.add("\t\t\treturn null;");
		toReturn.add("\t\t");
		toReturn.add("\t\treturn cast DataStructures.idMap.get(Std.parseInt(s));");
		toReturn.add("\t}");
		return toReturn;
	}

	@Override
	public DataEditor<Structure> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new StructureEditor((Extras) extras, null);
	}

	@Override
	public Structure decode(String s)
	{
		try
		{
			int id = Integer.parseInt(s);
			Structure model = Structures.getStructure(id);
			if(model.getTemplate() == def)
				return model;
			
			return null;
		}
		catch(NumberFormatException ex)
		{
			return null;
		}
	}

	@Override
	public String encode(Structure model)
	{
		if(model == null)
			return "";
		
		return "" + model.getID();
	}

	@Override
	public String toDisplayString(Structure data)
	{
		return String.valueOf(data);
	}
	
	@Override
	public Structure copy(Structure t)
	{
		return t;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		final PropertiesSheet preview = panel.getPreview();
		final DataItem previewKey = panel.getPreviewKey();
		final PropertiesSheetStyle style = panel.style;
		
		//=== Source Filter

		final DataEditor<String> filterField = new SingleLineStringEditor(style);
		filterField.setValue(e.sourceFilter == null ? null : e.sourceFilter.getText());
		filterField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				String text = filterField.getValue();
				
				if(e.sourceFilter == null && !text.isEmpty())
					e.sourceFilter = new StructureCondition(null, text);
				else if(e.sourceFilter != null && text.isEmpty())
					e.sourceFilter = null;
				else if(e.sourceFilter != null && !text.isEmpty())
					e.sourceFilter.setText(text);
				
				preview.refreshDataItem(previewKey);
			}
		});
		
		//=== Default Value
		
		final DataEditor<Structure> defaultField = new StructureEditor();
		defaultField.setValue(e.defaultValue);
		defaultField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.defaultValue = defaultField.getValue();
			}
		});
		
		panel.addEnablerRow(expansion, "Filter", filterField, e.sourceFilter != null);
		panel.addGenericRow(expansion, "Default", defaultField);
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		String filterText = extras.get("sourceFilter", Types._String, null);
		if(filterText != null)
			e.sourceFilter = new StructureCondition(null, filterText);
		e.defaultValue = extras.get(DEFAULT_VALUE, this, null);
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		if(e.sourceFilter != null)
			emap.put("sourceFilter", e.sourceFilter.getText());
		if(e.defaultValue != null)
			emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		return emap;
	}
	
	public class Extras extends ExtraProperties
	{
		public StructureCondition sourceFilter;
		public Structure defaultValue;
		
		@Override
		public Object getDefault()
		{
			return defaultValue;
		}
	}
	
	public class StructureEditor extends DataEditor<Structure>
	{
		private UpdatingCombo<Structure> editor;
		
		/**
		 * @param e 
		 * @param currentStructure
		 */
		public StructureEditor(Extras e, Structure currentStructure)
		{
			CollectionPredicate<Structure> filter =
					e.sourceFilter == null ? null :
					new StructurePredicate(e.sourceFilter, currentStructure);
			
			editor = new UpdatingCombo<Structure>(Structures.getList(def), filter);
			
			editor.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
		}
		
		public StructureEditor()
		{
			editor = new UpdatingCombo<Structure>(Structures.getList(def), null);
			
			editor.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
		}
		
		@Override
		public void set(Structure t)
		{
			editor.setSelectedItem(t);
		}
		
		@Override
		public Structure getValue()
		{
			return editor.getSelected();
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return comps(editor);
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			editor.dispose();
			editor = null;
		}
	}
	
	class StructurePredicate implements CollectionPredicate<Structure>
	{
		private StructureCondition condition;
		private Structure s;
		
		public StructurePredicate(StructureCondition condition, Structure s)
		{
			this.condition = condition;
			this.s = s;
		}
		
		@Override
		public boolean test(Structure s2)
		{
			return condition.check(s, s2);
		}
	}
}