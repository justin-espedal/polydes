package com.polydes.common.ui.filelist;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.BranchListener;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;

import stencyl.sw.app.lists.AbstractItemRenderer;
import stencyl.sw.app.lists.AbstractList;
import stencyl.sw.app.lists.AbstractListIcon.OverlayIcon;
import stencyl.sw.util.Loader;
import stencyl.sw.util.gfx.ImageUtil;

public class LeafList<T extends Leaf<T,U>, U extends Branch<T,U>> extends JList<T> implements BranchListener<T,U>
{
	U folder;
	DefaultListModel<T> listModel;
	LeafListTransferHandler<T, U> transferHandler;
	
	public LeafList(U folder, HierarchyModel<T, U> folderModel)
	{
		super(new DefaultListModel<>());
		listModel = (DefaultListModel<T>) getModel();
		
		setBackground(null);
		setCellRenderer(new LeafRenderer<T,U>(60, 48, 24, 24));
		setLayoutOrientation(JList.HORIZONTAL_WRAP);
		setVisibleRowCount(-1);
		setSelectionModel(new LeafListSelectionModel<T, U>(folderModel, folder));
		
		setDragEnabled(true);
		setDropMode(DropMode.ON_OR_INSERT);
		setTransferHandler(transferHandler = new LeafListTransferHandler<>(folderModel, this));
		
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
	
	@Override
	public void setCellRenderer(ListCellRenderer<? super T> cellRenderer)
	{
		super.setCellRenderer(cellRenderer);
		
		if(cellRenderer instanceof LeafRenderer)
		{
			Dimension cellSize = ((LeafRenderer<?,?>) cellRenderer).getCellSize();
			
			setFixedCellWidth(AbstractList.H_PADDING + cellSize.width);
			setFixedCellHeight(AbstractList.V_PADDING + cellSize.height);
		}
	}
	
	public void dispose()
	{
		folder.removeFolderListener(this);
		folder = null;
		transferHandler.dispose();
	}
	
	public void refresh()
	{
		removeAll();
		
		for(T l : folder.getItems())
			listModel.addElement(l);
	}
	
	public U getFolder()
	{
		return folder;
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
	
	public static class LeafRenderer<T extends Leaf<T,U>, U extends Branch<T,U>> extends AbstractItemRenderer<T>
	{
		public LeafRenderer(int w, int h, int iconWidth, int iconHeight)
		{
			super(w, h, iconWidth, iconHeight);
		}
		
		public Dimension getCellSize()
		{
			return new Dimension(icon.getIconWidth(), icon.getIconHeight());
		}
		
		public Dimension getIconSize()
		{
			return new Dimension(iconWidth, iconHeight);
		}
		
		private HashMap<T, Image> iconCache = new HashMap<>();
		private HashMap<T, Image> renderedIconCache = new HashMap<>();
		
		@Override
		public Component getListCellRendererComponent(JList<? extends T> arg0, T arg1, int arg2, boolean arg3, boolean arg4)
		{
			super.getListCellRendererComponent(arg0, arg1, arg2, arg3, arg4);
			
			setText(arg1.getName());
			
			return this;
		}
		
		@Override
		public Image fetchIcon(T item)
		{
			Image img = renderedIconCache.get(item);
			
			if(img == null)
			{
				img = item.getIcon().getImage();
				
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
	public void branchLeafAdded(U folder, T item, int position)
	{
		listModel.add(position, item);
	}

	@Override
	public void branchLeafRemoved(U folder, T item, int position)
	{
		listModel.remove(position);
	}
}
