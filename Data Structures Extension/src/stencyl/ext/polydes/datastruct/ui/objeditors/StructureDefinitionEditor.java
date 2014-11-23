package stencyl.ext.polydes.datastruct.ui.objeditors;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang3.StringUtils;

import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.folder.DataItemListener;
import stencyl.ext.polydes.datastruct.data.folder.EditableObject;
import stencyl.ext.polydes.datastruct.data.folder.Folder;
import stencyl.ext.polydes.datastruct.data.folder.FolderHierarchyModel;
import stencyl.ext.polydes.datastruct.data.structure.Structure;
import stencyl.ext.polydes.datastruct.data.structure.StructureDefinition;
import stencyl.ext.polydes.datastruct.data.structure.StructureField;
import stencyl.ext.polydes.datastruct.data.structure.StructureHeader;
import stencyl.ext.polydes.datastruct.data.structure.StructureTab;
import stencyl.ext.polydes.datastruct.data.structure.StructureTabset;
import stencyl.ext.polydes.datastruct.data.structure.cond.StructureCondition;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.res.Resources;
import stencyl.ext.polydes.datastruct.ui.UIConsts;
import stencyl.ext.polydes.datastruct.ui.list.ListUtils;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheet;
import stencyl.ext.polydes.datastruct.ui.tree.DTree;
import stencyl.ext.polydes.datastruct.ui.utils.PopupUtil.PopupItem;

public class StructureDefinitionEditor extends JPanel
{
	public StructureDefinition def;
	public FolderHierarchyModel model;
	public DTree tree;
	public JComponent treeView;
	public Structure preview;
	
	private PropertiesSheet sheet;
	
	public StructureDefinitionEditor(final StructureDefinition def)
	{
		super(new BorderLayout());
		setBackground(UIConsts.TEXT_EDITOR_COLOR);
		
		this.def = def;
		
		sheet = getPreview().getEditor().properties;
		model = new FolderHierarchyModel(def.guiRoot);
		
		//TODO: Sheet still has yet to build at all under this implementation.
		sheet.finishedBuilding();
		
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
		tree = model.getTree();
		
		tree.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setListEditEnabled(true);
		tree.setNodeCreator(model.new DefaultNodeCreator()
		{
			@Override
			public Collection<PopupItem> getCreatableNodeList()
			{
				if(selectionState.nodes.size() < 1)
					return null;
				
				Object uo = selectionState.nodes.get(0).getUserObject();
				EditableObject o;
				if(uo instanceof Folder)
					o = ((Folder) uo).getObject();
				else
					o = (EditableObject) uo;
				
				return childNodes.get(o.getClass());
			}
			
			@Override
			public Object createNode(PopupItem selected, String nodeName)
			{
				//TODO:
				//def.guiChanged();
				
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
					StructureField newField = new StructureField(StringUtils.deleteWhitespace(nodeName).toLowerCase(Locale.ENGLISH), Types._String, nodeName, "", false, new ExtrasMap());
					def.addField(newField, preview);
					
					return new DataItem(newField.getVarname(), newField);
				}
				else if(cls == StructureHeader.class)
					return new DataItem(nodeName, new StructureHeader(nodeName));
				else if(cls == StructureCondition.class)
				{
					//TODO: new StructureCondition
					StructureCondition cond = new StructureCondition(null, null);
					return new Folder(cond.toString(), cond);
				}
				
				return null;
			}
			
			@Override
			public void nodeRemoved(DataItem toRemove)
			{
				//TODO:
				//def.guiChanged();
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
		
		treeView = ListUtils.addHeader(tree, "View");
	}
	
	private static final HashMap<Class<?>, ArrayList<PopupItem>> childNodes = new HashMap<Class<?>, ArrayList<PopupItem>>();
	static
	{
		ImageIcon tabsetIcon = Resources.loadIcon("tabset.png");
		ImageIcon tabIcon = Resources.loadIcon("tab.png");
		ImageIcon fieldIcon = Resources.loadIcon("field.png");
		ImageIcon headerIcon = Resources.loadIcon("header.png");
		ImageIcon conditionIcon = Resources.loadIcon("condition.png");
		
		ArrayList<PopupItem> items = new ArrayList<PopupItem>();
		items.add(new PopupItem("Tab", StructureTab.class, tabIcon));
		childNodes.put(StructureTabset.class, items);
		
		items = new ArrayList<PopupItem>();
		items.add(new PopupItem("Field", StructureField.class, fieldIcon));
		items.add(new PopupItem("Header", StructureHeader.class, headerIcon));
		items.add(new PopupItem("Condition", StructureCondition.class, conditionIcon));
		items.add(new PopupItem("Tabset", StructureTabset.class, tabsetIcon));
		
		childNodes.put(StructureTab.class, items);
		childNodes.put(StructureField.class, items);
		childNodes.put(StructureHeader.class, items);
		childNodes.put(StructureCondition.class, items);
	}
	
	public Structure getPreview()
	{
		if(preview == null)
			preview = new Structure(-1, def.name, def);
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
		
		disposePreview();
		def = null;
		tree = null;
		treeView = null;
		sheet = null;
	}
}
