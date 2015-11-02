package com.polydes.datastruct.data.structure;

import com.polydes.common.nodes.HierarchyModel;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.ui.objeditors.StructureEditor;

public class PreviewStructure extends Structure
{
	private HierarchyModel<DataItem,Folder> model;
	
	public PreviewStructure(StructureDefinition template, HierarchyModel<DataItem,Folder> model)
	{
		super(-1, template.getName(), template);
		this.model = model;
		loadDefaults();
	}

	@Override
	public StructureEditor getEditor()
	{
		if(editor == null)
			editor = new StructureEditor(this, model);
		
		return editor;
	}
}
