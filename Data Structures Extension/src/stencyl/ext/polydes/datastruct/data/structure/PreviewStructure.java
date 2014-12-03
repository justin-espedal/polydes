package stencyl.ext.polydes.datastruct.data.structure;

import stencyl.ext.polydes.datastruct.data.folder.FolderHierarchyModel;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureEditor;

public class PreviewStructure extends Structure
{
	private FolderHierarchyModel model;
	
	public PreviewStructure(StructureDefinition template, FolderHierarchyModel model)
	{
		super(-1, template.getName(), template);
		this.model = model;
	}

	@Override
	public StructureEditor getEditor()
	{
		if(editor == null)
			editor = new StructureEditor(this, model);
		
		return editor;
	}
}
