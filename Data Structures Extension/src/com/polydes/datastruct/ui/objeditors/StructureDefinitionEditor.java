package com.polydes.datastruct.ui.objeditors;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.tree.TreeSelectionModel;

import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.LeafListener;
import com.polydes.common.ui.darktree.DarkTree;
import com.polydes.common.ui.darktree.DefaultNodeCreator;
import com.polydes.common.ui.darktree.SelectionType;
import com.polydes.common.ui.darktree.TNode;
import com.polydes.common.util.PopupUtil.PopupItem;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.PreviewStructure;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.StructureTable;
import com.polydes.datastruct.data.structure.elements.StructureCondition;
import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.data.structure.elements.StructureHeader;
import com.polydes.datastruct.data.structure.elements.StructureTab;
import com.polydes.datastruct.data.structure.elements.StructureTabset;
import com.polydes.datastruct.data.structure.elements.StructureText;
import com.polydes.datastruct.ui.UIConsts;
import com.polydes.datastruct.ui.list.ListUtils;

public class StructureDefinitionEditor extends JPanel
{
	public StructureDefinition def;
	public HierarchyModel<DataItem> model;
	public DarkTree<DataItem> tree;
	public JComponent treeView;
	public Structure preview;
	
	private int getPosAvoidingTabsetParent(Folder parent)
	{
		int insertPosition;
		
		ArrayList<TNode<DataItem>> selNodes = tree.getSelectionState().nodes;
		
		if(tree.getSelectionState().type == SelectionType.FOLDERS && !(parent.getObject() instanceof StructureTabset))
			insertPosition = parent.getItems().size();
		else
			insertPosition = parent.getItems().indexOf(selNodes.get(selNodes.size() - 1).getUserObject()) + 1;
		
		return insertPosition;
	}
	
	private final Action createFieldAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Folder newNodeFolder = (Folder) tree.getCreationParentFolder(tree.getSelectionState());
			int insertPosition = getPosAvoidingTabsetParent(newNodeFolder);
			tree.createNewItemFromFolder(SDETypes.asPopupItem.get(StructureField.class), newNodeFolder, insertPosition);
		}
	};
	
	private final Action createHeaderAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Folder newNodeFolder = (Folder) tree.getCreationParentFolder(tree.getSelectionState());
			int insertPosition = getPosAvoidingTabsetParent(newNodeFolder);
			tree.createNewItemFromFolder(SDETypes.asPopupItem.get(StructureHeader.class), newNodeFolder, insertPosition);
		}
	};
	
	private final Action createTextAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Folder newNodeFolder = (Folder) tree.getCreationParentFolder(tree.getSelectionState());
			int insertPosition = getPosAvoidingTabsetParent(newNodeFolder);
			tree.createNewItemFromFolder(SDETypes.asPopupItem.get(StructureText.class), newNodeFolder, insertPosition);
		}
	};
	
	private final Action createTabAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Folder newNodeFolder = (Folder) tree.getCreationParentFolder(tree.getSelectionState());
			if(newNodeFolder.getObject() instanceof StructureTabset) 
				tree.createNewItem(SDETypes.asPopupItem.get(StructureTab.class));
			else if(newNodeFolder.getObject() instanceof StructureTab && !(newNodeFolder.getObject() instanceof StructureTable))
			{
				Folder tabset = (Folder) newNodeFolder.getParent();
				int insertPosition = tabset.indexOfItem(newNodeFolder) + 1;
				tree.createNewItemFromFolder(SDETypes.asPopupItem.get(StructureTab.class), tabset, insertPosition);
			}
			else
				tree.createNewItem(SDETypes.asPopupItem.get(StructureTabset.class));
		}
	};
	
	private final Action createConditionAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Folder newNodeFolder = (Folder) tree.getCreationParentFolder(tree.getSelectionState());
			int insertPosition = getPosAvoidingTabsetParent(newNodeFolder);
			tree.createNewItemFromFolder(SDETypes.asPopupItem.get(StructureCondition.class), newNodeFolder, insertPosition);
		}
	};
	
	public StructureDefinitionEditor(final StructureDefinition def)
	{
		super(new BorderLayout());
		setBackground(UIConsts.TEXT_EDITOR_COLOR);
		
		this.def = def;
		
		model = new HierarchyModel<DataItem>(def.guiRoot);
		
		model.getRootBranch().addListener(new LeafListener<DataItem>()
		{
			@Override
			public void leafStateChanged(Leaf<DataItem> source)
			{
				if(source.isDirty())
					def.setDirty(true);
			}
			
			@Override
			public void leafNameChanged(Leaf<DataItem> source, String oldName)
			{
			}
		});
		
		model.setUniqueLeafNames(false);
		
		tree = new DarkTree<DataItem>(model);
		
		tree.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setListEditEnabled(true);
		tree.setNodeCreator(new DefaultNodeCreator<DataItem>()
		{
			@Override
			public Collection<PopupItem> getCreatableNodeList()
			{
				if(selectionState.nodes.size() < 1)
					return null;
				
				Folder parent = (Folder) tree.getCreationParentFolder(selectionState);
				SDE o = (SDE) parent.getObject();
				SDEType<?> type = SDETypes.fromClass(o.getClass());
				
				Collection<PopupItem> toReturn = new ArrayList<>();
				type.childTypes.forEach((listedType) -> toReturn.add(SDETypes.asPopupItem.get(listedType)));
				return toReturn;
			}
			
			@Override
			public Leaf<DataItem> createNode(PopupItem selected, String nodeName)
			{
				@SuppressWarnings("unchecked")
				Class<? extends SDE> cls = (Class<? extends SDE>) selected.data;
				
				SDEType<?> type = SDETypes.fromClass(cls);
				
				if(type.isBranchNode)
					return new Folder(nodeName, type.create(def, nodeName));
				else
					return new DataItem(nodeName, type.create(def, nodeName));
			}
			
			@Override
			public void nodeRemoved(Leaf<DataItem> toRemove)
			{
				if(toRemove instanceof Folder)
				{
					for(Leaf<DataItem> item : ((Folder) toRemove).getItems())
						nodeRemoved(item);
				}
				else 
				{
					if(((DataItem) toRemove).getObject() instanceof StructureField)
						def.removeField((StructureField) ((DataItem) toRemove).getObject(), preview);
				}
			}
		});
		
		tree.setNamingEditingAllowed(false);
		
		//tree.expandLevel(0);
		
		treeView = ListUtils.addHeader(tree, "View");
		
		installActions(this);
		installActions(tree);
		
		add(getPreview().getEditor());
	}
	
	private void installActions(JComponent c)
	{
		installAction(c, "F", "createField", createFieldAction);
		installAction(c, "H", "createHeader", createHeaderAction);
		installAction(c, "T", "createTab", createTabAction);
		installAction(c, "K", "createCondition", createConditionAction);
		installAction(c, "D", "createText", createTextAction);
	}
	
	private void uninstallActions(JComponent c)
	{
		uninstallAction(c, "F", "createField");
		uninstallAction(c, "H", "createHeader");
		uninstallAction(c, "T", "createTab");
		uninstallAction(c, "K", "createCondition");
		uninstallAction(c, "D", "createText");
	}
	
	private void installAction(JComponent c, String key, String name, Action action)
	{
		c.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ctrl " + key), name);
		c.getActionMap().put(name, action);
	}
	
	private void uninstallAction(JComponent c, String key, String name)
	{
		c.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ctrl " + key), null);
		c.getActionMap().put(name, null);
	}
	
	public Structure getPreview()
	{
		if(preview == null)
			preview = new PreviewStructure(def, model);
		
		return preview;
	}
	
	public void disposePreview()
	{
		if(preview != null)
			preview.dispose();
		preview = null;
	}
	
	public void dispose()
	{
		removeAll();
		
		uninstallActions(this);
		uninstallActions(tree);
		
		disposePreview();
		model.dispose();
		model = null;
		def = null;
		tree.dispose();
		tree = null;
		treeView = null;
	}
}
