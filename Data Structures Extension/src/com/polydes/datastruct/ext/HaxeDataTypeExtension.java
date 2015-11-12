package com.polydes.datastruct.ext;

import java.io.File;
import java.util.ArrayList;

import com.polydes.common.data.types.Types;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.HaxeObjectType;
import com.polydes.datastruct.data.types.haxe.HaxeObjectHaxeType;
import com.polydes.datastruct.io.read.HaxeObjectDefinitionReader;

import stencyl.sw.util.FileHelper;

public interface HaxeDataTypeExtension
{
	public ArrayList<HaxeDataType> getHaxeDataTypes();
	
	/*-------------------------------------*\
	 * Building from xml
	\*-------------------------------------*/ 
	
	public static ArrayList<HaxeDataType> readTypesFolder(File f)
	{
		ArrayList<HaxeDataType> types = new ArrayList<HaxeDataType>();
		
		for(File file : FileHelper.listFiles(f))
			types.add(readType(file.getAbsolutePath()));
		
		return types;
	}
	
	public static HaxeDataType readType(String path)
	{
		HaxeObjectType newDT = new HaxeObjectType(HaxeObjectDefinitionReader.read(path));
		Types.get().registerItem(newDT);
		return new HaxeObjectHaxeType(newDT);
	}
}
