package stencyl.ext.polydes.datastruct.data.types.general;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import stencyl.ext.polydes.datastruct.data.structure.Structure;
import stencyl.ext.polydes.datastruct.data.structure.StructureDefinition;
import stencyl.ext.polydes.datastruct.data.structure.StructureField;
import stencyl.ext.polydes.datastruct.data.structure.Structures;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.io.Text;
import stencyl.ext.polydes.datastruct.res.Resources;
import stencyl.ext.polydes.datastruct.ui.comp.UpdatingCombo;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class StructureType extends DataType<Structure>
{
	public StructureDefinition def;
	
	public StructureType(StructureDefinition def)
	{
		super(Structure.class, def.classname, "OBJECT", def.name);
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
	public JComponent[] getEditor(final DataUpdater<Structure> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		final UpdatingCombo<Structure> editor = new UpdatingCombo<Structure>(Structures.getList(def.name), null);
		editor.setSelectedItem(updater.get());
		
		editor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				updater.set(editor.getSelected());
			}
		});
		
		return comps(editor);
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
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		return null;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		return null;
	}
}