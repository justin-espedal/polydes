package com.polydes.datastruct.data.structure;

import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.datastruct.ui.objeditors.StructureEditor;

public class PreviewStructure extends Structure
{
	private HierarchyModel<DefaultLeaf,DefaultBranch> model;
	
	public PreviewStructure(StructureDefinition template, HierarchyModel<DefaultLeaf,DefaultBranch> model)
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
