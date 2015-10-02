package com.polydes.datastruct.ui.page;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;

import com.polydes.common.comp.StatusBar;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.ui.darktree.DTreeSelectionListener;
import com.polydes.common.ui.darktree.DTreeSelectionState;
import com.polydes.common.ui.darktree.DarkTree;
import com.polydes.common.ui.darktree.DefaultNodeCreator;
import com.polydes.common.ui.darktree.SelectionType;
import com.polydes.common.util.PopupUtil.PopupItem;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureDefinitions;
import com.polydes.datastruct.data.structure.StructureFolder;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.res.Resources;
import com.polydes.datastruct.ui.UIConsts;
import com.polydes.datastruct.ui.list.ListUtils;
import com.polydes.datastruct.ui.objeditors.StructureEditor;

import stencyl.sw.SW;
import stencyl.sw.util.UI;

public class StructurePage extends JPanel implements DTreeSelectionListener<DataItem>
{
	private static StructurePage _instance;
	
	private JComponent sidebar;
	
	private Boolean listEditEnabled;
	
	private HierarchyModel<DataItem> folderModel;
	private DarkTree<DataItem> tree;
	
	private DTreeSelectionState<DataItem> selectionState;
	
	private JScrollPane multiScroller;
	private JPanel multiPage;
	private JPanel folderPage;
	
	private JLabel folderIcon;
	private int folderWidth = Resources.loadIcon("page/folder-large.png").getIconWidth();
	
	private JComponent currView;
	
	private ArrayList<JPanel> currPages;
	
	public StructurePage(Folder rootFolder)
	{
		super(new BorderLayout());
		
		folderModel = new HierarchyModel<DataItem>(rootFolder);
		tree = new DarkTree<DataItem>(folderModel);
		tree.addTreeListener(this);
		tree.expandLevel(1);
		
		multiPage = new JPanel();
		multiPage.setLayout(new BoxLayout(multiPage, BoxLayout.Y_AXIS));
		multiPage.setBackground(UIConsts.TEXT_EDITOR_COLOR);
		multiScroller = UI.createScrollPane(multiPage);
		multiScroller.setBackground(null);
		multiScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		multiScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		currPages = new ArrayList<JPanel>();
		
		setBackground(UIConsts.TEXT_EDITOR_COLOR);
		add(StatusBar.createStatusBar(), BorderLayout.SOUTH);
		
		folderPage = new JPanel(new BorderLayout());
		folderPage.setBackground(null);
		folderIcon = new JLabel(Resources.loadIcon("page/folder-large.png"));
		folderPage.add(folderIcon, BorderLayout.CENTER);
		
		addComponentListener(new ComponentListener()
		{
			@Override
			public void componentHidden(ComponentEvent e)
			{
			}
			
			@Override
			public void componentMoved(ComponentEvent e)
			{
			}
			
			@Override
			public void componentResized(ComponentEvent e)
			{
				if(currView == folderPage)
				{
					if(folderPage.getWidth() < folderWidth + 5)
						folderIcon.setIcon(Resources.loadIcon("page/folder-large-hurt.png"));
					else
						folderIcon.setIcon(Resources.loadIcon("page/folder-large.png"));
				}
			}
			
			@Override
			public void componentShown(ComponentEvent e)
			{
			}
		});
		
		currView = folderPage;
		add(currView, BorderLayout.CENTER);
		
		setListEditEnabled(true);
		folderModel.setUniqueLeafNames(true);
		
		tree.enablePropertiesButton();
		tree.setNodeCreator(new DefaultNodeCreator<DataItem>()
		{
			//For our purposes here, the object these folders point to is a type limiter.
			
			@Override
			public Collection<PopupItem> getCreatableNodeList()
			{
				StructureFolder parent = (StructureFolder) tree.getCreationParentFolder(selectionState);
				
				ArrayList<PopupItem> items = new ArrayList<PopupItem>();
				if(parent.childType != null)
					items.add(new PopupItem(parent.childType.getName(), parent.childType, parent.childType.getSmallIcon()));
				else
					for(StructureDefinition def : StructureDefinitions.defMap.values())
						items.add(new PopupItem(def.getName(), def, def.getSmallIcon()));
				return items;
			}
			
			@Override
			public Leaf<DataItem> createNode(PopupItem selected, String nodeName)
			{
				if(selected.text.equals("Folder"))
					return new StructureFolder(nodeName);
				
				int id = Structures.newID();
				StructureDefinition type = (StructureDefinition) selected.data;
				Structure toReturn = new Structure(id, nodeName, type);
				toReturn.loadDefaults();
				Structures.structures.get(type).add(toReturn);
				Structures.structuresByID.put(id, toReturn);
				return toReturn.dref;
			}
			
			@Override
			public boolean attemptRemove(List<Leaf<DataItem>> toRemove)
			{
				int numStructuresToRemove = 0;
				for(Leaf<DataItem> item : toRemove)
					if(!(item instanceof Folder))
						++numStructuresToRemove;
				
				String plural = (numStructuresToRemove > 1 ? "s" : "");
				
				UI.Choice result =
					UI.showYesCancelPrompt(
						"Remove Selected Structure" + plural,
						"Are you sure you want to remove " + numStructuresToRemove +  " structure" + plural + "?",
						"Remove", "Cancel"
					);
				
				return result == UI.Choice.YES;
			}
			
			@Override
			public void nodeRemoved(Leaf<DataItem> toRemove)
			{
				if(((DataItem) toRemove).getObject() instanceof Structure)
				{
					Structure s = (Structure) ((DataItem) toRemove).getObject();
					Structures.structures.get(s.getTemplate()).remove(s);
					Structures.structuresByID.remove(s.getID());
					s.dispose();
				}
			}
			
			@Override
			public void editNode(Leaf<DataItem> toEdit)
			{
				if(!(toEdit instanceof StructureFolder))
					return;
				
				EditFolderDialog dg = new EditFolderDialog(SW.get());
				dg.setFolder((StructureFolder) toEdit);
				dg.dispose();
			}
		});
		
		sidebar = ListUtils.addHeader(tree, "Data");
		
		new java.util.Timer().schedule(new java.util.TimerTask()
		{
			@Override
			public void run()
			{
				tree.refreshDisplay();
			}
		}, 10);
		
		new java.util.Timer().schedule(new java.util.TimerTask()
		{
			@Override
			public void run()
			{
				tree.refreshDisplay();
			}
		}, 100);
	}
	
	public JComponent getSidebar()
	{
		return sidebar;
	}
	
	public static StructurePage get()
	{
		if (_instance == null)
			_instance = new StructurePage(Structures.root);

		return _instance;
	}
	
	public HierarchyModel<DataItem> getFolderModel()
	{
		return folderModel;
	}
	
	public void refreshSelected()
	{
		selectionStateChanged();
	}
	
	@Override
	public void selectionStateChanged()
	{
		if(currView == folderPage && (selectionState.type == SelectionType.FOLDERS))
			return;
		
		if(currView != null)
			remove(currView);
		
		multiPage.removeAll();
		currPages.clear();
		
		currView = null;
		
		if(selectionState.type == SelectionType.FOLDERS)
			currView = folderPage;
		else
		{
			ArrayList<Structure> toEdit = new ArrayList<Structure>();
			
			for(DefaultMutableTreeNode node : selectionState.nodes)
			{
				DataItem uo = (DataItem) node.getUserObject();
				if(uo == null)
					continue;
				if(uo.getObject() instanceof Structure)
					toEdit.add((Structure) uo.getObject());
			}
			
			StructureEditor editor;
			
			for(int i = 0; i < toEdit.size(); ++i)
			{
				editor = toEdit.get(i).getEditor();
				currPages.add(editor);
				multiPage.add(editor);
				editor.setAlignmentX(LEFT_ALIGNMENT);
				
				if(i + 1 < toEdit.size())
					multiPage.add(new HorizontalDivider(2));
			}
			
			currView = multiScroller;
		}

		if (currView != null)
			add(currView, BorderLayout.CENTER);
		
		revalidate();
		repaint();
	}
	
	public void setListEditEnabled(boolean value)
	{
		if(listEditEnabled == null || listEditEnabled != value)
		{
			listEditEnabled = value;
			if(listEditEnabled)
			{
				tree.setListEditEnabled(true);
			}
			else
			{
				tree.setListEditEnabled(false);
			}
		}
	}
	
	@Override
	public void setSelectionState(DTreeSelectionState<DataItem> state)
	{
		this.selectionState = state;
	}
	
	public static void dispose()
	{
		if(_instance != null)
		{
			_instance.removeAll();
			_instance.sidebar.removeAll();
			_instance.folderModel.dispose();
			_instance.tree.dispose();
		}
		_instance = null;
	}
}
