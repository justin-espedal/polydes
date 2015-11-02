package com.polydes.datastruct.ui.page;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.BranchListener;
import com.polydes.common.nodes.Leaf;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.Structure;

import stencyl.sw.app.lists.AbstractItemRenderer;
import stencyl.sw.app.lists.AbstractList;
import stencyl.sw.app.lists.AbstractListIcon.OverlayIcon;
import stencyl.sw.util.Loader;
import stencyl.sw.util.gfx.ImageUtil;

public class DataItemList extends JList<DataItem> implements BranchListener<DataItem>
{
	Folder folder;
	DefaultListModel<DataItem> listModel;
	
	public DataItemList(Folder folder)
	{
		super(new DefaultListModel<>());
		listModel = (DefaultListModel<DataItem>) getModel();
		
		int iconWidth = 24;
		int iconHeight = 24;
		int cellWidth = 60;
		int cellHeight = 48;
		
		setBackground(null);
		setCellRenderer(new DataItemRenderer(cellWidth, cellHeight, iconWidth, iconHeight));
		setLayoutOrientation(JList.HORIZONTAL_WRAP);
		setVisibleRowCount(-1);
		
		setFixedCellWidth(AbstractList.H_PADDING + cellWidth);
		setFixedCellHeight(AbstractList.V_PADDING + cellHeight);
		
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (locationToIndex(e.getPoint()) == -1 && !e.isShiftDown() && !isMenuShortcutKeyDown(e))
				{
					clearSelection();
				}
			}
			
			private boolean isMenuShortcutKeyDown(InputEvent event)
			{
				return (event.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;
			}
		});
		
		this.folder = folder;
		folder.addFolderListener(this);
		refresh();
	}
	
	public void dispose()
	{
		folder.removeFolderListener(this);
		folder = null;
	}
	
	public void refresh()
	{
		removeAll();
		
		for(Leaf<DataItem> l : folder.getItems())
			listModel.addElement((DataItem) l);
	}
	
	@Override
	public int locationToIndex(Point location)
	{
		int index = super.locationToIndex(location);
		if (index != -1 && !getCellBounds(index, index).contains(location))
		{
			return -1;
		}
		else
		{
			return index;
		}
	}
	
	public static class DataItemRenderer extends AbstractItemRenderer<DataItem>
	{
		public DataItemRenderer(int w, int h, int iconWidth, int iconHeight)
		{
			super(w, h, iconWidth, iconHeight);
		}
		
		private static HashMap<DataItem, Image> iconCache = new HashMap<>();
		private static HashMap<DataItem, Image> renderedIconCache = new HashMap<>();
		private static Folder folderKey = new Folder("");
		
		@Override
		public Image fetchIcon(DataItem item)
		{
			if(item instanceof Folder)
				item = folderKey;
			
			Image img = renderedIconCache.get(item);
			
			if(img == null)
			{
				if(item instanceof Folder)
					img = Folder.folderIcon.getImage();
				else if(item.getObject() instanceof Structure)
					img = ((Structure) item.getObject()).getTemplate().getIconImg();
				
				if(img == null)
				{
					img = Loader.loadIcon("res/global/warning.png").getImage();
				}
				
				img = ImageUtil.getBufferedImage(img);
				
				Image img2 = icon.prerenderIcon(img, OverlayIcon.NONE);	
				
				iconCache.put(item, img);
				renderedIconCache.put(item, img2);
				
				img = img2;
			}
			
			icon.setOverlay(OverlayIcon.NONE);
			
			return img;
		}
	}
	
	/*================================================*\
	 | Folder Listener
	\*================================================*/
	
	@Override
	public void branchLeafAdded(Branch<DataItem> folder, Leaf<DataItem> item, int position)
	{
		listModel.add(position, (DataItem) item);
	}

	@Override
	public void branchLeafRemoved(Branch<DataItem> folder, Leaf<DataItem> item, int position)
	{
		listModel.remove(position);
	}
}
