package com.polydes.datastruct.ext;

import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Element;

import com.polydes.common.util.Lang;
import com.polydes.datastruct.data.core.HaxeField;
import com.polydes.datastruct.data.core.HaxeObjectDefinition;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.general.HaxeObjectType;
import com.polydes.datastruct.io.XML;
import com.polydes.datastruct.utils.DelayedInitialize;

import stencyl.sw.util.FileHelper;

public interface DataTypeExtension
{
	public ArrayList<DataType<?>> getDataTypes();
	
	public static ArrayList<DataType<?>> readTypesFolder(File f)
	{
		ArrayList<DataType<?>> types = new ArrayList<DataType<?>>();
		
		for(File file : FileHelper.listFiles(f))
			types.add(readType(file.getAbsolutePath()));
		
		return types;
	}
	
	public static DataType<?> readType(String path)
	{
		Element root = XML.getFile(path);
		String haxeClass = root.getAttribute("class");
		Element fields = XML.child(root, "fields");
		
		HaxeField[] hfs = Lang.mapCA(XML.children(fields), HaxeField.class, (field) ->
		{
			String name = field.getAttribute("name");
			String type = field.getAttribute("type");
			ExtrasMap editorData = null;
			
			if(field.hasChildNodes())
			{
				Element editor = XML.child(field, "editor");
				if(editor != null)
				{
					editorData = new ExtrasMap();
					editorData.putAll(XML.readMap(editor));
				}
			}
			
			HaxeField hf = new HaxeField(name, null, editorData);
			
			if(field.hasAttribute("default"))
				hf.defaultValue = field.getAttribute("default");
			
			DelayedInitialize.addObject(hf, "type", type);
			
			return hf;
		});
		
		HaxeObjectDefinition def = new HaxeObjectDefinition(haxeClass, hfs);
		
		def.showLabels = XML.readBoolean(fields, "showlabels", true);			
		
		return new HaxeObjectType(def);
	}
}
