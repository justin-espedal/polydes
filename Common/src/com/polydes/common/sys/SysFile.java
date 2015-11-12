package com.polydes.common.sys;

import static com.polydes.common.util.Lang.asArray;

import java.awt.BorderLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.polydes.common.comp.TitledPanel;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.LeafListener;
import com.polydes.common.nodes.NodeCreator.NodeAction;
import com.polydes.common.ui.object.ViewableObject;
import com.polydes.common.util.PopupUtil;

public class SysFile implements Leaf<SysFile,SysFolder>, ViewableObject
{
	protected File file;
	protected String path;
	protected int hash;
	
	protected ArrayList<LeafListener<SysFile,SysFolder>> listeners;
	protected SysFolder parent;
	
	protected String name;
	
	public SysFile(File file)
	{
		this.file = file;
		path = file.getAbsolutePath().intern();
		hash = path.hashCode();
		
		listeners = new ArrayList<LeafListener<SysFile,SysFolder>>();
		parent = null;
	}
	
	@Override
	public void setParent(SysFolder parent, boolean addToParent)
	{
		if(this.parent == parent)
			return;
		
		if(parent == null && !addToParent)
		{
			this.parent = null;
			return;
		}
		
		if(this.parent != null)
			this.parent.removeItem(this);
		
		this.parent = parent;
		if(addToParent)
			parent.addItem(this);
	}
	
	public File getFile()
	{
		return file;
	}

	@Override
	public SysFolder getParent()
	{
		return parent;
	}

	@Override
	public void addListener(LeafListener<SysFile,SysFolder> l)
	{
		listeners.add(l);
	}

	@Override
	public void removeListener(LeafListener<SysFile,SysFolder> l)
	{
		listeners.remove(l);
	}

	@Override
	public void setName(String newName)
	{
		if(name != newName)
		{
			file.renameTo(new File(file.getParentFile(), newName));
			FileMonitor.refresh();
		}
	}
	
	@Override
	public String getName()
	{
		return file.getName();
	}

	@Override
	public boolean canEditName()
	{
		return true;
	}
	
	public void clearIcon()
	{
		cachedIcon = null;
	}

	public void notifyChanged()
	{
		disposeView();
		cachedIcon = null;
		
		for(LeafListener<SysFile,SysFolder> l : listeners)
			l.leafStateChanged(this);
	}
	
	private ImageIcon cachedIcon = null;
	
	@Override
	public ImageIcon getIcon()
	{
		if(cachedIcon == null)
			cachedIcon = FileRenderer.generateThumb(file);
		
		return cachedIcon;
	}

	@Override
	public boolean isDirty()
	{
		return false;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof SysFile)
			return path == ((SysFile) obj).path;
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return hash;
	}

	protected JPanel view = null;
	
	@Override
	public JPanel getView()
	{
		if(view == null)
		{
			view = new TitledPanel(getName(), null);
			JPanel previewPanel = FilePreviewer.getPreview(this);
			
			previewPanel.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					maybeShowPopup(e);
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
						HierarchyModel<SysFile,SysFolder> folderModel = FileMonitor.getExtrasModel();
						
						ArrayList<JMenuItem> menuItems = new ArrayList<>();
						
						ArrayList<NodeAction<SysFile>> actionItems = folderModel.getNodeActions(new SysFile[] {SysFile.this});
						menuItems.addAll(Arrays.asList(PopupUtil.asMenuItems(actionItems)));
						
						JPopupMenu popup = PopupUtil.buildPopup(asArray(menuItems, JMenuItem.class));
						
						PopupUtil.installListener(popup, (item) -> {
							((NodeAction<SysFile>) item).callback.accept(SysFile.this);
						});
						
						Point p = previewPanel.getMousePosition(true);
						if(p == null)
						{
							p = MouseInfo.getPointerInfo().getLocation();
							SwingUtilities.convertPointFromScreen(p, previewPanel);
						}
						popup.show(previewPanel, p.x, p.y);
					}
				}
			});
			
			view.add(previewPanel, BorderLayout.CENTER);
		}
		
		return view;
	}

	@Override
	public void disposeView()
	{
		view = null;
	}

	@Override
	public boolean fillsViewHorizontally()
	{
		return false;
	}
}