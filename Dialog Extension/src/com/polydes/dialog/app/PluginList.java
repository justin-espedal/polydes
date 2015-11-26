package com.polydes.dialog.app;
import static com.polydes.common.util.Lang.asArray;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.HierarchyRepresentation;
import com.polydes.common.nodes.NodeCreator.CreatableNodeInfo;
import com.polydes.common.nodes.NodeCreator.NodeAction;
import com.polydes.common.nodes.NodeUtils;
import com.polydes.common.ui.filelist.LeafList.LeafRenderer;
import com.polydes.common.util.PopupUtil;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureDefinitions;
import com.polydes.dialog.app.pages.PluginsPage;

public class PluginList extends JList<DefaultLeaf> implements HierarchyRepresentation<DefaultLeaf,DefaultBranch>
{
	HierarchyModel<DefaultLeaf,DefaultBranch> model;
	ArrayList<DefaultLeaf> defs;
	
	DefaultListModel<DefaultLeaf> listModel;
	
	public PluginList(HierarchyModel<DefaultLeaf,DefaultBranch> model)
	{
		super(new DefaultListModel<>());
		listModel = (DefaultListModel<DefaultLeaf>) getModel();
		
		setBackground(null);
		setCellRenderer(new LeafRenderer<DefaultLeaf,DefaultBranch>(60, 48, 24, 24));
		setLayoutOrientation(JList.HORIZONTAL_WRAP);
		setVisibleRowCount(-1);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
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
					
					DefaultLeaf[] targets = selectionTargeted ?
							asArray(getSelectedValuesList(), DefaultLeaf.class) :
							null;
					
					ArrayList<JMenuItem> menuItems = new ArrayList<>();
					
					if(!selectionTargeted)
						menuItems.add(PluginsPage.createNewPlugin.asMenuItem());
					if(selectionTargeted)
					{
						ArrayList<NodeAction<DefaultLeaf>> actionItems = model.getNodeActions(targets);
						menuItems.addAll(Arrays.asList(PopupUtil.asMenuItems(actionItems)));
					}
					
					JPopupMenu popup = PopupUtil.buildPopup(asArray(menuItems, JMenuItem.class));
					
					PopupUtil.installListener(popup, (item) -> {
						
						if(item instanceof NodeAction)
							for(DefaultLeaf target : targets)
								((NodeAction<DefaultLeaf>) item).callback.accept(target);
						else if(item instanceof CreatableNodeInfo)
							model.createNewItem((CreatableNodeInfo) item);
						
					});
					
					Point p = getMousePosition(true);
					if(p == null)
					{
						p = MouseInfo.getPointerInfo().getLocation();
						SwingUtilities.convertPointFromScreen(p, PluginList.this);
					}
					popup.show(PluginList.this, p.x, p.y);
				}
			}

			private boolean isMenuShortcutKeyDown(InputEvent event)
			{
				return (event.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;
			}
			
		});

		this.model = model;
		model.addRepresentation(this);
		defs = new ArrayList<>();
		refresh();
	}
	
	public void dispose()
	{
		model.removeRepresentation(this);
	}
	
	public void refresh()
	{
		removeAll();
		defs.clear();
		
		StructureDefinitions sdefs = DataStructuresExtension.get().getStructureDefinitions();
		StructureDefinition dialogExtensionSuperclass = sdefs.getItem("dialog.ds.DialogExtension");
		
		NodeUtils.recursiveRun(model.getRootBranch(), (DefaultLeaf leaf) -> {
			if(leaf.getUserData() instanceof StructureDefinition)
			{
				StructureDefinition def = (StructureDefinition) ((DefaultLeaf) leaf).getUserData();
				if(def.parent == dialogExtensionSuperclass)
					defs.add(leaf);
//				NodeUtils.recursiveRun(def.guiRoot, defElement -> {
//					if(((DefaultLeaf) defElement).getObject() instanceof StructureExtension)
//						
//				});
			}
		});
		
		for(DefaultLeaf leaf : defs)
		{
			listModel.addElement(leaf);
		}
	}
	

	/*================================================*\
	 | Folder Hierarchy Representation
	\*================================================*/
	
	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		
	}

	@Override
	public void itemAdded(DefaultBranch folder, DefaultLeaf item, int position)
	{
		refresh();
	}

	@Override
	public void itemRemoved(DefaultBranch folder, DefaultLeaf item, int oldPosition)
	{
		refresh();
	}
}
