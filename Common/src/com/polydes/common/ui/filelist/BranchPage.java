package com.polydes.common.ui.filelist;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;

import com.polydes.common.comp.TitledPanel;
import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.sys.FileRenderer;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.common.util.IconUtil;

public class BranchPage<T extends Leaf<T,U>, U extends Branch<T,U>> extends TitledPanel
{
	private LeafList<T,U> list;
	
	private static ImageIcon folderThumb = IconUtil.getIcon(FileRenderer.folderThumb, 32);
	
	public BranchPage(U folder, HierarchyModel<T, U> folderModel)
	{
		super(folder.getName(), folderThumb);
		setBackground(PropertiesSheetStyle.DARK.pageBg);
		
		list = new LeafList<T,U>(folder, folderModel);
		
		add(list, BorderLayout.CENTER);
	}
	
	public LeafList<T, U> getList()
	{
		return list;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		list.dispose();
		list = null;
	}
}
