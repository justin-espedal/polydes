package stencyl.ext.polydes.datastruct.data.structure;

import stencyl.ext.polydes.common.nodes.HierarchyModel;
import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureEditor;

public class PreviewStructure extends Structure
{
	private HierarchyModel<DataItem> model;
	
	public PreviewStructure(StructureDefinition template, HierarchyModel<DataItem> model)
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
