package com.polydes.datastruct.ui.objeditors;

import java.awt.BorderLayout;

import javax.swing.SwingConstants;

import com.polydes.common.comp.TitledPanel;
import com.polydes.common.comp.utils.Layout;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.ui.table.PropertiesSheet;

public class StructureEditor extends TitledPanel
{
	public PropertiesSheet properties;
	public Structure structure;
	
	public StructureEditor(Structure structure)
	{
		this(structure, null);
	}
	
	public StructureEditor(Structure structure, HierarchyModel<DataItem,Folder> model)
	{
		super(structure.dref.getName(), structure.getIcon());
		
		this.structure = structure;
		
		properties = new PropertiesSheet(structure, model, PropertiesSheetStyle.DARK);
		
		add(Layout.aligned(properties, SwingConstants.LEFT, SwingConstants.TOP), BorderLayout.CENTER);
		
		revalidate();
	}
	
	public void highlightElement(DataItem n)
	{
		properties.highlightElement(n);
	}

	@Override
	public void dispose()
	{
		super.dispose();
		
		if(properties != null)
			properties.dispose();
		properties = null;
		structure = null;
	}
}
