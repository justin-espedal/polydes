package com.polydes.datastruct.data.types.haxe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.data.types.builtin.basic.StringType.SingleLineStringEditor;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.common.util.Lang;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.elements.StructureCondition;
import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.StructureType;
import com.polydes.datastruct.data.types.StructureType.Extras;
import com.polydes.datastruct.io.Text;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.table.PropertiesSheet;

import stencyl.sw.util.Locations;

public class StructureHaxeType extends HaxeDataType
{
	StructureType type;
	
	public StructureHaxeType(StructureType structureType)
	{
		super(structureType, structureType.def.getFullClassname(), "OBJECT");
		this.type = structureType;
	}
	
	@Override
	public List<String> generateHaxeClass()
	{
		File file = new File(Locations.getGameExtensionLocation("com.polydes.datastruct"), "templates/Structure.hx");
		String s = Text.readString(file);
		
		String fieldmap = "";
		String variables = "";
		String typeinfo = "";
		typeinfo += "\t\tm.set(\"name\", \"String\");\n";
		for(StructureField f : type.def.getFields())
		{
			fieldmap += String.format("\t\tm.set(\"%s\", \"%s\");\n", f.getLabel(), f.getVarname());
			variables += String.format("\tpublic var %s:%s;\n", f.getVarname(), getType(f));
			typeinfo += String.format("\t\tm.set(\"%s\", \"%s\");\n", f.getVarname(), f.getType());
		}
		
		s = StringUtils.replace(s, "[PACKAGE]", getPackage());
		s = StringUtils.replace(s, "[CLASSNAME]", getSimpleClassname());
		s = StringUtils.replace(s, "[FIELDMAP]", fieldmap, 1);
		s = StringUtils.replace(s, "[TYPEINFO]", typeinfo, 1);
		s = StringUtils.replace(s, "[VARIABLES]", variables, 1);
		s = StringUtils.replace(s, "[CUSTOM]", type.def.customCode + runCustomCodeInjector(type.def), 1);
		
		ArrayList<String> lines = new ArrayList<String>();
		for(String s2 : s.split("\n"))
			lines.add(s2);
		
		return lines;
	}
	
	private String getType(StructureField f)
	{
		//TODO: Formalize type parameters again instead of handling it explicitly
		String type = f.getType().getHaxeType();
		if(type.equals("Array"))
			type = "Array<Dynamic>";
		else if(type.equals("Set"))
			type = "Set<Dynamic>";
		if(f.isOptional())
			return "Null<" + type + ">";
		else
			return type;
	}
	
	private String runCustomCodeInjector(StructureDefinition def)
	{
		StringBuilder builder = new StringBuilder();
		runCustomCodeInjector(def.guiRoot, builder);
		return builder.toString();
	}
	
	@SuppressWarnings("unchecked")
	private <S extends SDE> void runCustomCodeInjector(DataItem d, StringBuilder builder)
	{
		S sde = (S) d.getObject();
		SDEType<S> sdetype = (SDEType<S>) SDETypes.fromClass(sde.getClass());
		sdetype.genCode(sde, builder);
		
		if(d instanceof Folder)
		{
			for(DataItem item : ((Folder) d).getItems())
				runCustomCodeInjector(item, builder);
		}
	}
	
	@Override
	public List<String> generateHaxeReader()
	{
		return Lang.arraylist("StringData.registerStructureReader(\"" + getHaxeType() + "\");");
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

		final DataEditor<String> filterField = new SingleLineStringEditor(null, style);
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
		
		final DataEditor<Structure> defaultField = type.new StructureEditor();
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
}
