package com.polydes.common.ui.filelist;

import static com.polydes.common.util.Lang.asArray;
import static com.polydes.common.util.Lang.newarray;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.BranchListener;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.NodeCreator.CreatableNodeInfo;
import com.polydes.common.nodes.NodeCreator.NodeAction;
import com.polydes.common.util.PopupUtil;

import stencyl.sw.app.lists.AbstractItemRenderer;
import stencyl.sw.app.lists.AbstractList;
import stencyl.sw.app.lists.AbstractListIcon.OverlayIcon;
import stencyl.sw.util.Loader;
import stencyl.sw.util.gfx.ImageUtil;

public class LeafList<T extends Leaf<T,U>, U extends Branch<T,U>> extends JList<T> implements BranchListener<T,U>, PropertyChangeListener
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
				if(locationToIndex(e.getPoint()) == -1 && !e.isShiftDown() && !isMenuShortcutKeyDown(e))
					clearSelection();
			}
			
			@Override
			public void mousePressed(MouseEvent e)
			{
				maybeShowPopup(e);
			}
			
			private boolean eventIsOverSelection(MouseEvent e)
			{
				return
					locationToIndex(e.getPoint()) != -1 &&
					isSelectedIndex(locationToIndex(e.getPoint()));
			}
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				maybeShowPopup(e);
			}
			
			@SuppressWarnings("unchecked")
			private void maybeShowPopup(MouseEvent e)
			{
				if(e.isPopupTrigger())
				{
					boolean selectionTargeted = eventIsOverSelection(e);
					
					if(!selectionTargeted)
					{
						int index = locationToIndex(e.getPoint());
						if(index != -1)
						{
							setSelectedIndex(index);
							selectionTargeted = true;
						}
					}
					
					boolean singleFolderTargeted = !selectionTargeted ||
						(getSelectedIndices().length == 1 && getSelectedValue() instanceof Branch);
					
					T[] targets = selectionTargeted ?
							asArray(getSelectedValuesList(), folderModel.leafClass) :
							newarray(folderModel.leafClass, (T) folder);
					
					ArrayList<JMenuItem> menuItems = new ArrayList<>();
					
					if(singleFolderTargeted)
					{
						ArrayList<CreatableNodeInfo> createItems = folderModel.getCreatableNodes(folder);
						menuItems.add(PopupUtil.menu("Create", PopupUtil.asMenuItems(createItems)));
					}
					if(selectionTargeted)
					{
						ArrayList<NodeAction<T>> actionItems = folderModel.getNodeActions(targets);
						menuItems.addAll(Arrays.asList(PopupUtil.asMenuItems(actionItems)));
					}
					
					JPopupMenu popup = PopupUtil.buildPopup(asArray(menuItems, JMenuItem.class));
					
					PopupUtil.installListener(popup, (item) -> {
						
						if(item instanceof NodeAction)
							for(T target : targets)
								((NodeAction<T>) item).callback.accept(target);
						else if(item instanceof CreatableNodeInfo)
							folderModel.createNewItem((CreatableNodeInfo) item);
						
					});
					
					Point p = getMousePosition(true);
					if(p == null)
					{
						p = MouseInfo.getPointerInfo().getLocation();
						SwingUtilities.convertPointFromScreen(p, LeafList.this);
					}
					popup.show(LeafList.this, p.x, p.y);
				}
			}

			private boolean isMenuShortcutKeyDown(InputEvent event)
			{
				return (event.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;
			}
			
		});
		
		this.folder = folder;
		folder.addFolderListener(this);
		
		for(T l : folder.getItems())
		{
			listModel.addElement(l);
			l.addListener(Leaf.ICON, this);
		}
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
		for(T l : folder.getItems())
			l.removeListener(Leaf.ICON, this);
		folder.removeFolderListener(this);
		folder = null;
		transferHandler.dispose();
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
		
		public void refreshIcon(T value)
		{
			iconCache.remove(value);
			renderedIconCache.remove(value);
		}
		
		private HashMap<T, Image> iconCache = new HashMap<>();
		private HashMap<T, Image> renderedIconCache = new HashMap<>();
		
		@SuppressWarnings("unchecked")
		@Override
		public String fetchText(Object value)
		{
			return ((T) value).getName();
		}
		
		@Override
		public Image fetchIcon(T item)
		{
			Image img = renderedIconCache.get(item);
			
			if(img == null)
			{
				if(item.getIcon() == null || item.getIcon().getImage() == null)
					img = Loader.loadIcon("res/global/warning.png").getImage();
				else
					img = item.getIcon().getImage();
				
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
		item.addListener(this);
	}

	@Override
	public void branchLeafRemoved(U folder, T item, int position)
	{
		item.removeListener(this);
		listModel.remove(position);
	}

	/*================================================*\
	 | Leaf Listener
	\*================================================*/
	
	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		T changed = (T) evt.getSource();
		((LeafRenderer<T,U>) getCellRenderer()).refreshIcon(changed);
		int refreshedCell = folder.indexOfItem(changed);
		repaint(getCellBounds(refreshedCell, refreshedCell));
	}
}
