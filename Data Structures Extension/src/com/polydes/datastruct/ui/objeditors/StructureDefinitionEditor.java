package com.polydes.datastruct.ui.objeditors;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.tree.TreeSelectionModel;

import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultEditableLeaf;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.DefaultNodeCreator;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.NodeSelection;
import com.polydes.common.nodes.NodeUtils;
import com.polydes.common.ui.darktree.DarkTree;
import com.polydes.common.ui.darktree.SelectionType;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.PreviewStructure;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
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
	public HierarchyModel<DefaultLeaf, DefaultBranch> model;
	public DarkTree<DefaultLeaf,DefaultBranch> tree;
	public JComponent treeView;
	public Structure preview;
	
	private int getPosAvoidingTabsetParent(DefaultBranch newNodeFolder)
	{
		int insertPosition;
		
		NodeSelection<DefaultLeaf,DefaultBranch> selection = tree.getSelectionState();
		
		if(selection.getType() == SelectionType.FOLDERS && !(newNodeFolder.getUserData() instanceof StructureTabset))
			insertPosition = newNodeFolder.getItems().size();
		else
			insertPosition = NodeUtils.getIndex(selection.lastNode()) + 1;
		return insertPosition;
	}
	
	private final Action createFieldAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			DefaultBranch newNodeFolder = model.getCreationParentFolder(model.getSelection());
			int insertPosition = getPosAvoidingTabsetParent(newNodeFolder);
			model.createNewItemFromFolder(SDETypes.asCNInfo.get(StructureField.class), newNodeFolder, insertPosition);
		}
	};
	
	private final Action createHeaderAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			DefaultBranch newNodeFolder = model.getCreationParentFolder(model.getSelection());
			int insertPosition = getPosAvoidingTabsetParent(newNodeFolder);
			model.createNewItemFromFolder(SDETypes.asCNInfo.get(StructureHeader.class), newNodeFolder, insertPosition);
		}
	};
	
	private final Action createTextAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			DefaultBranch newNodeFolder = model.getCreationParentFolder(model.getSelection());
			int insertPosition = getPosAvoidingTabsetParent(newNodeFolder);
			model.createNewItemFromFolder(SDETypes.asCNInfo.get(StructureText.class), newNodeFolder, insertPosition);
		}
	};
	
	private final Action createTabAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			DefaultBranch newNodeFolder = model.getCreationParentFolder(model.getSelection());
			if(newNodeFolder.getUserData() instanceof StructureTabset) 
				model.createNewItem(SDETypes.asCNInfo.get(StructureTab.class));
			else if(newNodeFolder.getUserData() instanceof StructureTab && !(newNodeFolder.getUserData() instanceof StructureTable))
			{
				DefaultBranch tabset = newNodeFolder.getParent();
				int insertPosition = tabset.indexOfItem(newNodeFolder) + 1;
				model.createNewItemFromFolder(SDETypes.asCNInfo.get(StructureTab.class), tabset, insertPosition);
			}
			else
				model.createNewItem(SDETypes.asCNInfo.get(StructureTabset.class));
		}
	};
	
	private final Action createConditionAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			DefaultBranch newNodeFolder = model.getCreationParentFolder(model.getSelection());
			int insertPosition = getPosAvoidingTabsetParent(newNodeFolder);
			model.createNewItemFromFolder(SDETypes.asCNInfo.get(StructureCondition.class), newNodeFolder, insertPosition);
		}
	};
	
	public StructureDefinitionEditor(final StructureDefinition def)
	{
		super(new BorderLayout());
		setBackground(UIConsts.TEXT_EDITOR_COLOR);
		
		this.def = def;
		
		model = new HierarchyModel<DefaultLeaf,DefaultBranch>(def.guiRoot, DefaultLeaf.class, DefaultBranch.class);
		model.getRootBranch().addListener(Leaf.STATE, evt -> {
			if(((Leaf<?,?>) evt.getSource()).isDirty())
				def.setDirty(true);
		});
		
		model.setUniqueLeafNames(false);
		
		tree = new DarkTree<DefaultLeaf,DefaultBranch>(model);
		
		tree.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setListEditEnabled(true);
		model.setNodeCreator(new DefaultNodeCreator<DefaultLeaf,DefaultBranch>()
		{
			@Override
			public ArrayList<CreatableNodeInfo> getCreatableNodeList(DefaultBranch creationBranch)
			{
				if(model.getSelection().isEmpty())
					return null;
				
				DefaultBranch parent = model.getCreationParentFolder(model.getSelection());
				SDE o = (SDE) parent.getUserData();
				SDEType<?> type = SDETypes.fromClass(o.getClass());
				
				ArrayList<CreatableNodeInfo> toReturn = new ArrayList<>();
				type.childTypes.forEach((listedType) -> toReturn.add(SDETypes.asCNInfo.get(listedType)));
				return toReturn;
			}
			
			@Override
			public DefaultLeaf createNode(CreatableNodeInfo selected, String nodeName)
			{
				@SuppressWarnings("unchecked")
				Class<? extends SDE> cls = (Class<? extends SDE>) selected.data;
				
				SDEType<?> type = SDETypes.fromClass(cls);
				
				if(type.isBranchNode)
					return new Folder(nodeName, type.create(def, nodeName));
				else
					return new DefaultEditableLeaf(nodeName, type.create(def, nodeName));
			}
			
			NodeAction<DefaultLeaf> setAsIcon = new NodeAction<DefaultLeaf>("Set as Icon", null, leaf -> {
				StructureField field = (StructureField) leaf.getUserData();
				def.iconSource = field.getVarname();
			});
			
			@Override
			public ArrayList<NodeAction<DefaultLeaf>> getNodeActions(DefaultLeaf[] targets)
			{
				ArrayList<NodeAction<DefaultLeaf>> actions = new ArrayList<>();
				if(targets.length == 1)
				{
					Object data = targets[0].getUserData();
					if(data instanceof StructureField)
					{
						StructureField field = (StructureField) data;
						if(field.getType().isIconProvider())
							actions.add(setAsIcon);
					}
				}
				return actions;
			}
			
			@Override
			public void nodeRemoved(DefaultLeaf toRemove)
			{
				if(toRemove instanceof DefaultBranch)
				{
					for(DefaultLeaf item : ((DefaultBranch) toRemove).getItems())
						nodeRemoved(item);
				}
				else 
				{
					if(toRemove.getUserData() instanceof StructureField)
						def.removeField((StructureField) toRemove.getUserData(), preview);
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
