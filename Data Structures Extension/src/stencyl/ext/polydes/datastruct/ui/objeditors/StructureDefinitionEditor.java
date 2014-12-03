package stencyl.ext.polydes.datastruct.ui.objeditors;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang3.StringUtils;

import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.folder.DataItemListener;
import stencyl.ext.polydes.datastruct.data.folder.EditableObject;
import stencyl.ext.polydes.datastruct.data.folder.Folder;
import stencyl.ext.polydes.datastruct.data.folder.FolderHierarchyModel;
import stencyl.ext.polydes.datastruct.data.structure.PreviewStructure;
import stencyl.ext.polydes.datastruct.data.structure.Structure;
import stencyl.ext.polydes.datastruct.data.structure.StructureDefinition;
import stencyl.ext.polydes.datastruct.data.structure.StructureField;
import stencyl.ext.polydes.datastruct.data.structure.StructureHeader;
import stencyl.ext.polydes.datastruct.data.structure.StructureTab;
import stencyl.ext.polydes.datastruct.data.structure.StructureTable;
import stencyl.ext.polydes.datastruct.data.structure.StructureTabset;
import stencyl.ext.polydes.datastruct.data.structure.cond.StructureCondition;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.res.Resources;
import stencyl.ext.polydes.datastruct.ui.UIConsts;
import stencyl.ext.polydes.datastruct.ui.list.ListUtils;
import stencyl.ext.polydes.datastruct.ui.tree.DTree;
import stencyl.ext.polydes.datastruct.ui.tree.DefaultNodeCreator;
import stencyl.ext.polydes.datastruct.ui.tree.SelectionType;
import stencyl.ext.polydes.datastruct.ui.utils.PopupUtil.PopupItem;
import stencyl.sw.util.gfx.GraphicsUtilities;

public class StructureDefinitionEditor extends JPanel
{
	public StructureDefinition def;
	public FolderHierarchyModel model;
	public DTree tree;
	public JComponent treeView;
	public Structure preview;
	
	private int getPosAvoidingTabsetParent(Folder parent)
	{
		int insertPosition;
		
		ArrayList<DefaultMutableTreeNode> selNodes = tree.getSelectionState().nodes;
		
		if(tree.getSelectionState().type == SelectionType.FOLDERS && !(parent.getObject() instanceof StructureTabset))
			insertPosition = parent.getItems().size();
		else
			insertPosition = parent.getItems().indexOf(selNodes.get(selNodes.size() - 1).getUserObject()) + 1;
		
		return insertPosition;
	}
	
	private Action createFieldAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Folder newNodeFolder = tree.getCreationParentFolder(tree.getSelectionState());
			int insertPosition = getPosAvoidingTabsetParent(newNodeFolder);
			tree.createNewItemFromFolder(fieldItem, newNodeFolder, insertPosition);
		}
	};
	
	private Action createHeaderAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Folder newNodeFolder = tree.getCreationParentFolder(tree.getSelectionState());
			int insertPosition = getPosAvoidingTabsetParent(newNodeFolder);
			tree.createNewItemFromFolder(headerItem, newNodeFolder, insertPosition);
		}
	};
	
	private Action createTabAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Folder newNodeFolder = tree.getCreationParentFolder(tree.getSelectionState());
			if(newNodeFolder.getObject() instanceof StructureTabset) 
				tree.createNewItem(tabItem);
			else if(newNodeFolder.getObject() instanceof StructureTab && !(newNodeFolder.getObject() instanceof StructureTable))
			{
				Folder tabset = newNodeFolder.getParent();
				int insertPosition = tabset.indexOfItem(newNodeFolder) + 1;
				tree.createNewItemFromFolder(tabItem, tabset, insertPosition);
			}
			else
				tree.createNewItem(tabsetItem);
		}
	};
	
	private Action createConditionAction = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Folder newNodeFolder = tree.getCreationParentFolder(tree.getSelectionState());
			int insertPosition = getPosAvoidingTabsetParent(newNodeFolder);
			tree.createNewItemFromFolder(conditionItem, newNodeFolder, insertPosition);
		}
	};
	
	public StructureDefinitionEditor(final StructureDefinition def)
	{
		super(new BorderLayout());
		setBackground(UIConsts.TEXT_EDITOR_COLOR);
		
		this.def = def;
		
		model = new FolderHierarchyModel(def.guiRoot);
		
		model.getRootFolder().addListener(new DataItemListener()
		{
			@Override
			public void dataItemStateChanged(DataItem source)
			{
				if(source.isDirty())
					def.setDirty(true);
			}
			
			@Override
			public void dataItemNameChanged(DataItem source, String oldName)
			{
			}
		});
		
		model.setUniqueItemNames(false);
		
		tree = new DTree(model);
		
		tree.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setListEditEnabled(true);
		tree.setNodeCreator(new DefaultNodeCreator()
		{
			@Override
			public Collection<PopupItem> getCreatableNodeList()
			{
				if(selectionState.nodes.size() < 1)
					return null;
				
				Object uo = selectionState.nodes.get(0).getUserObject();
				EditableObject o = ((DataItem) uo).getObject();
				return childNodes.get(o.getClass());
			}
			
			@Override
			public Object createNode(PopupItem selected, String nodeName)
			{
				Class<?> cls = (Class<?>) selected.data;
				
				if(cls == StructureTabset.class)
				{
					return new Folder(nodeName, new StructureTabset());
				}
				else if(cls == StructureTab.class)
				{
					return new Folder(nodeName, new StructureTab(nodeName));
				}
				else if(cls == StructureField.class)
				{
					StructureField newField = new StructureField(def, StringUtils.deleteWhitespace(nodeName).toLowerCase(Locale.ENGLISH), Types._String, nodeName, "", false, new ExtrasMap());
					def.addField(newField, preview);
					
					return new DataItem(nodeName, newField);
				}
				else if(cls == StructureHeader.class)
					return new DataItem(nodeName, new StructureHeader(nodeName));
				else if(cls == StructureCondition.class)
				{
					StructureCondition cond = new StructureCondition(def, "");
					return new Folder(cond.toString(), cond);
				}
				
				return null;
			}
			
			@Override
			public void nodeRemoved(DataItem toRemove)
			{
				if(toRemove instanceof Folder)
				{
					for(DataItem item : ((Folder) toRemove).getItems())
						nodeRemoved(item);
				}
				else 
				{
					if(toRemove.getObject() instanceof StructureField)
						def.removeField((StructureField) toRemove.getObject(), preview);
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
	}
	
	private void uninstallActions(JComponent c)
	{
		uninstallAction(c, "F", "createField");
		uninstallAction(c, "H", "createHeader");
		uninstallAction(c, "T", "createTab");
		uninstallAction(c, "K", "createCondition");
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
	
	private static final HashMap<Class<?>, ArrayList<PopupItem>> childNodes = new HashMap<Class<?>, ArrayList<PopupItem>>();
	private static final PopupItem tabsetItem;
	private static final PopupItem tabItem;
	private static final PopupItem fieldItem;
	private static final PopupItem headerItem;
	private static final PopupItem conditionItem;
	static
	{
		
		ImageIcon tabsetIcon = thumb("tabset.png");
		ImageIcon tabIcon = thumb("tab.png");
		ImageIcon fieldIcon = thumb("field.png");
		ImageIcon headerIcon = thumb("header.png");
		ImageIcon conditionIcon = thumb("condition.png");
		
		ArrayList<PopupItem> items = new ArrayList<PopupItem>();
		items.add(tabItem = new PopupItem("Tab", StructureTab.class, tabIcon));
		childNodes.put(StructureTabset.class, items);
		
		items = new ArrayList<PopupItem>();
		items.add(fieldItem = new PopupItem("Field", StructureField.class, fieldIcon));
		items.add(headerItem = new PopupItem("Header", StructureHeader.class, headerIcon));
		items.add(conditionItem = new PopupItem("Condition", StructureCondition.class, conditionIcon));
		items.add(tabsetItem = new PopupItem("Tabset", StructureTabset.class, tabsetIcon));
		
		childNodes.put(StructureTab.class, items);
		childNodes.put(StructureField.class, items);
		childNodes.put(StructureHeader.class, items);
		childNodes.put(StructureCondition.class, items);
		childNodes.put(StructureTable.class, items);
	}
	
	private static final ImageIcon thumb(String loc)
	{
		Image img = Resources.loadIcon(loc).getImage();
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		g.drawImage(img, 0, 0, bi.getWidth(), bi.getHeight(), null);
		return new ImageIcon(GraphicsUtilities.createThumbnail(bi, 16));
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
