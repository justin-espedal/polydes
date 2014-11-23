package stencyl.ext.polydes.datastruct.data.structure;

import stencyl.ext.polydes.datastruct.data.folder.Folder;

public class StructureFolder extends Folder
{
	public StructureFolder(String name)
	{
		super(name);
	}

	public StructureDefinition childType;
}