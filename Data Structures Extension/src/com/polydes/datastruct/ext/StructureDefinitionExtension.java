package com.polydes.datastruct.ext;

import java.util.ArrayList;

import com.polydes.datastruct.data.structure.StructureDefinitionElementType;

public interface StructureDefinitionExtension
{
	public ArrayList<StructureDefinitionElementType<?>> getSdeTypes();
}
