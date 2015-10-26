package com.polydes.datastruct.ext;

import java.io.File;
import java.util.ArrayList;

import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.Types;
import com.polydes.datastruct.data.types.general.HaxeObjectType;
import com.polydes.datastruct.io.read.HaxeObjectDefinitionReader;

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
		if(!Types.isInitialized())
			Types.initialize();
		return new HaxeObjectType(HaxeObjectDefinitionReader.read(path));
	}
}
