package com.polydes.datastruct.ui.objeditors;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import com.polydes.common.comp.TitledPanel;
import com.polydes.common.comp.utils.Layout;
import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.common.util.IconUtil;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.ui.table.PropertiesSheet;

public class StructureEditor extends TitledPanel implements PropertyChangeListener
{
	public PropertiesSheet properties;
	public Structure structure;
	
	public StructureEditor(Structure structure)
	{
		this(structure, null);
	}
	
	public StructureEditor(Structure structure, HierarchyModel<DefaultLeaf,DefaultBranch> model)
	{
		super(structure.dref.getName(), structure.getIcon());
		
		this.structure = structure;
		
		properties = new PropertiesSheet(structure, model, PropertiesSheetStyle.DARK);
		structure.dref.addListener(this);
		
		add(Layout.aligned(properties, SwingConstants.LEFT, SwingConstants.TOP), BorderLayout.CENTER);
		
		revalidate();
	}
	
	public void highlightElement(DefaultLeaf n)
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
		structure.dref.removeListener(this);
		structure = null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		switch(evt.getPropertyName())
		{
			case Leaf.NAME:
				label.setText((String) evt.getNewValue());
				break;
			case Leaf.ICON:
				label.setIcon(IconUtil.getIcon((ImageIcon) evt.getNewValue(), TitledPanel.ICON_SIZE));
				break;
			default:
				//do nothing
				break;
		}
	}
}
