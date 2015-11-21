package com.polydes.datastruct.ui.page;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.TreeSelectionModel;

import com.polydes.common.comp.MiniSplitPane;
import com.polydes.common.comp.StatusBar;
import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.DefaultNodeCreator;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.NodeCreator.CreatableNodeInfo;
import com.polydes.common.nodes.NodeSelection;
import com.polydes.common.nodes.NodeSelectionEvent;
import com.polydes.common.nodes.NodeSelectionListener;
import com.polydes.common.ui.darktree.DarkTree;
import com.polydes.common.ui.darktree.SelectionType;
import com.polydes.common.ui.object.EditableObject;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.Prefs;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.ui.UIConsts;
import com.polydes.datastruct.ui.list.ListUtils;
import com.polydes.datastruct.ui.objeditors.PreviewableEditor;
import com.polydes.datastruct.ui.objeditors.StructureDefinitionEditor;
import com.polydes.datastruct.ui.utils.SnappingDialog;

import stencyl.sw.util.UI;

public class StructureDefinitionPage extends JPanel
{
	private static StructureDefinitionPage _instance;
	
	public MiniSplitPane splitPane;
	private JPanel emptySidebarBottom;
	
	private HierarchyModel<DefaultLeaf,DefaultBranch> definitionsfm;
	private DarkTree<DefaultLeaf,DefaultBranch> definitionTree;
	
	private HierarchyModel<DefaultLeaf,DefaultBranch> editorModel;
	private JComponent definitionTreeView;
	
	protected JScrollPane scroller;
	protected JPanel page;
	
	private StructureDefinitionEditor editor;
	
	private NodeSelectionListener<DefaultLeaf,DefaultBranch> definitionStateListener = new NodeSelectionListener<DefaultLeaf,DefaultBranch>()
	{
		@Override
		public void selectionChanged(NodeSelectionEvent<DefaultLeaf, DefaultBranch> e)
		{
			NodeSelection<DefaultLeaf, DefaultBranch> selection = definitionsfm.getSelection();
			
			page.removeAll();
			
			int dl = splitPane.getDividerLocation();
			
			if(selection.getType() == SelectionType.FOLDERS)
			{
				revalidate();
				repaint();
				splitPane.setBottomComponent(emptySidebarBottom);
				splitPane.setDividerLocation(dl);
				return;
			}
			DefaultLeaf di = selection.firstNode();
			StructureDefinition toEdit = (StructureDefinition) di.getUserData();
			editor = toEdit.getEditor();
			editor.setAlignmentX(LEFT_ALIGNMENT);
			
			page.add(editor, BorderLayout.CENTER);
			
			if(editorModel != null)
				editorModel.getSelection().removeSelectionListener(editorStateListener);
			editorModel = editor.model;
			editorModel.getSelection().addSelectionListener(editorStateListener);
			
			splitPane.setTopComponent(definitionTreeView);
			splitPane.setBottomComponent(editor.treeView);
			splitPane.setDividerLocation(dl);
			
			editor.tree.getTree().setSelectionPath(editor.tree.getRootPath());
			editorStateListener.selectionChanged(null);
			
			revalidate();
			repaint();
		}
	};
	
	private NodeSelectionListener<DefaultLeaf,DefaultBranch> editorStateListener = new NodeSelectionListener<DefaultLeaf,DefaultBranch>()
	{
		@Override
		public void selectionChanged(NodeSelectionEvent<DefaultLeaf, DefaultBranch> e)
		{
			NodeSelection<DefaultLeaf, DefaultBranch> selection = editorModel.getSelection();
			
			PropertiesWindow propsWindow = StructureDefinitionsWindow.get().getPropsWindow();
			
			DefaultLeaf di = selection.get(0);
			EditableObject selected = (di == null) ? null : (EditableObject) di.getUserData();
			if(selected != null)
			{
				EditableObject toEdit = (EditableObject) selected;
				
				if(toEdit.getEditor() != EditableObject.BLANK_EDITOR)
				{
					if(toEdit.getEditor() instanceof PreviewableEditor)
						((PreviewableEditor) toEdit.getEditor()).setPreviewSheet(editor.getPreview().getEditor().properties, di);
					
					propsWindow.setObject(toEdit);
					if(!propsWindow.isVisible())
					{
						propsWindow.setVisible(true);
						propsWindow.addComponentListener(propsWindowAdapter);
					}
				}
				else if(propsWindow.isVisible())
				{
					propsWindow.removeComponentListener(propsWindowAdapter);
					propsWindow.setObject(null);
					propsWindow.setVisible(false);
				}
				
				editor.getPreview().getEditor().highlightElement(di);
			}
			else if(propsWindow.isVisible())
			{
				propsWindow.removeComponentListener(propsWindowAdapter);
				propsWindow.setObject(null);
				propsWindow.setVisible(false);
			}
			
			revalidate();
			repaint();
		}
	};
	
	private ComponentAdapter propsWindowAdapter = new ComponentAdapter()
	{
		@Override
		public void componentHidden(ComponentEvent e)
		{
			StructureDefinitionsWindow.get().getPropsWindow().removeComponentListener(this);
			if(editorModel != null)
				editorModel.getSelection().clear();
		}
	};
	
	public static StructureDefinitionPage get()
	{
		if (_instance == null)
			_instance = new StructureDefinitionPage();
		
		return _instance;
	}
	
	public StructureDefinitionPage()
	{
		super(new BorderLayout());
		
		Folder root = DataStructuresExtension.get().getStructureDefinitions().root;
		definitionsfm = new HierarchyModel<DefaultLeaf,DefaultBranch>(root, DefaultLeaf.class, DefaultBranch.class);
		definitionTree = new DarkTree<DefaultLeaf,DefaultBranch>(definitionsfm);
		definitionTree.setNamingEditingAllowed(false);
		definitionTree.expand((Folder) DataStructuresExtension.get().getStructureDefinitions().root.getItemByName("My Structures"));
		
		page = new JPanel(new BorderLayout());
		page.setBackground(UIConsts.TEXT_EDITOR_COLOR);
		scroller = UI.createScrollPane(page);
		scroller.setBackground(null);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		splitPane = new MiniSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		definitionTree.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		setBackground(UIConsts.TEXT_EDITOR_COLOR);
		add(StatusBar.createStatusBar(), BorderLayout.SOUTH);
		add(scroller, BorderLayout.CENTER);
		
		definitionTree.setListEditEnabled(true);
		definitionTree.enablePropertiesButton();
		definitionsfm.setUniqueLeafNames(true);
		definitionsfm.getSelection().addSelectionListener(definitionStateListener);
		
		definitionsfm.setNodeCreator(new DefaultNodeCreator<DefaultLeaf,DefaultBranch>()
		{
			@Override
			public ArrayList<CreatableNodeInfo> getCreatableNodeList(DefaultBranch creationBranch)
			{
				return createNodeList;
			}
			
			@Override
			public DefaultLeaf createNode(CreatableNodeInfo item, String nodeName)
			{
				if(item.name.equals("Folder"))
					return new Folder(nodeName);
				
				CreateStructureDefinitionDialog dg = new CreateStructureDefinitionDialog();
				dg.setNodeName(nodeName);
				StructureDefinition toCreate = dg.newDef;
				dg.dispose();
				
				if(toCreate == null)
					return null;
				
				DataStructuresExtension.get().getStructureDefinitions().registerItem(toCreate);
				return toCreate.dref;
			}
			
			@Override
			public ArrayList<NodeAction<DefaultLeaf>> getNodeActions(DefaultLeaf[] targets)
			{
				return null;
			}
			
			@Override
			public void editNode(DefaultLeaf toEdit)
			{
				if(toEdit instanceof DefaultBranch)
					return;
				
				if(!(toEdit.getUserData() instanceof StructureDefinition))
					return;
				
				CreateStructureDefinitionDialog dg = new CreateStructureDefinitionDialog();
				dg.setDefinition((StructureDefinition) toEdit.getUserData());
				dg.dispose();
				definitionTree.repaint();
			}
			
			@Override
			public boolean attemptRemove(List<DefaultLeaf> toRemove)
			{
				if(toRemove.size() > 0 && toRemove.get(0).getUserData() instanceof StructureDefinition)
				{
					StructureDefinition def = (StructureDefinition) toRemove.get(0).getUserData();
					UI.Choice result =
						UI.showYesCancelPrompt(
							"Remove Structure Definition",
							"Are you sure you want to remove this structure definition? (Will delete " + Structures.structures.get(def).size() + " structures)",
							"Remove", "Cancel"
						);
					
					return result == UI.Choice.YES;
				}
				return false;
			}
			
			@Override
			public void nodeRemoved(DefaultLeaf toRemove)
			{
				if(toRemove.getUserData() instanceof StructureDefinition)
				{
					StructureDefinition def = (StructureDefinition) toRemove.getUserData();
					def.remove();
				}
			}
		});
		
		emptySidebarBottom = new JPanel(new BorderLayout());
		emptySidebarBottom.setBackground(UIConsts.SIDEBAR_COLOR);
		
		int initDivLoc = Prefs.DEFPAGE_SIDEWIDTH;
		
		splitPane.setTopComponent(definitionTreeView = ListUtils.addHeader(definitionTree, "Object"));
		splitPane.setBottomComponent(emptySidebarBottom);
		splitPane.setDividerLocation(initDivLoc);

		definitionTree.forceRerender();
		
		PropertiesWindow propsWindow = StructureDefinitionsWindow.get().getPropsWindow();
		propsWindow.snapToComponent(scroller.getViewport(), SnappingDialog.TOP_RIGHT);
		propsWindow.setVisible(false);
	}
	
	public void selectNone()
	{
		if(editorModel != null)
			editorModel.getSelection().clear();
		definitionsfm.getSelection().clear();
	}
	
	private static final ArrayList<CreatableNodeInfo> createNodeList = new ArrayList<CreatableNodeInfo>();
	static
	{
		createNodeList.add(new CreatableNodeInfo("Structure", null, null));
	}
	
	public JComponent getSidebar()
	{
		return splitPane;
	}
	
	public static void dispose()
	{
		if(_instance != null)
		{
			_instance.definitionsfm.dispose();
			_instance.definitionTree.dispose();
		}
		_instance = null;
	}
}
