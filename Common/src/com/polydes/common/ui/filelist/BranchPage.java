package com.polydes.common.ui.filelist;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;

import com.polydes.common.comp.TitledPanel;
import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.sys.FileRenderer;
import com.polydes.common.ui.filelist.LeafList.LeafRenderer;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.common.util.IconUtil;

public class BranchPage<T extends Leaf<T,U>, U extends Branch<T,U>> extends TitledPanel
{
	private LeafList<T,U> list;
	
	private static ImageIcon folderThumb = IconUtil.getIcon(FileRenderer.folderThumb, 32);
	
	public BranchPage(U folder, LeafRenderer<T,U> renderer)
	{
		super(folder.getName(), folderThumb);
		setBackground(PropertiesSheetStyle.DARK.pageBg);
		
		list = new LeafList<T,U>(folder, renderer);
		
		add(list, BorderLayout.CENTER);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		list.dispose();
		list = null;
	}
}
