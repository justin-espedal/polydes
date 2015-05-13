package stencyl.ext.polydes.datastruct.ui.page;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import stencyl.ext.polydes.common.comp.MiniSplitPane;
import stencyl.ext.polydes.common.comp.StatusBar;
import stencyl.ext.polydes.common.nodes.HierarchyModel;
import stencyl.ext.polydes.common.nodes.Leaf;
import stencyl.ext.polydes.common.ui.darktree.DTreeSelectionListener;
import stencyl.ext.polydes.common.ui.darktree.DTreeSelectionState;
import stencyl.ext.polydes.common.ui.darktree.DarkTree;
import stencyl.ext.polydes.common.ui.darktree.DefaultNodeCreator;
import stencyl.ext.polydes.common.ui.darktree.SelectionType;
import stencyl.ext.polydes.common.util.PopupUtil.PopupItem;
import stencyl.ext.polydes.datastruct.Prefs;
import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.folder.EditableObject;
import stencyl.ext.polydes.datastruct.data.folder.Folder;
import stencyl.ext.polydes.datastruct.data.structure.StructureDefinition;
import stencyl.ext.polydes.datastruct.data.structure.StructureDefinitions;
import stencyl.ext.polydes.datastruct.data.structure.StructureTable;
import stencyl.ext.polydes.datastruct.data.structure.StructureTabset;
import stencyl.ext.polydes.datastruct.data.structure.Structures;
import stencyl.ext.polydes.datastruct.ui.UIConsts;
import stencyl.ext.polydes.datastruct.ui.list.ListUtils;
import stencyl.ext.polydes.datastruct.ui.objeditors.PreviewableEditor;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureDefinitionEditor;
import stencyl.ext.polydes.datastruct.ui.utils.SnappingDialog;
import stencyl.sw.util.UI;

public class StructureDefinitionPage extends JPanel
{
	private static StructureDefinitionPage _instance;
	
	public MiniSplitPane splitPane;
	private JPanel emptySidebarBottom;
	
	private HierarchyModel<DataItem> definitionsfm;
	
	private DarkTree<DataItem> definitionTree;
	private DarkTree<DataItem> editorTree;
	private JComponent definitionTreeView;
	
	private DTreeSelectionState<DataItem> selectionState;
	
	protected JScrollPane scroller;
	protected JPanel page;
	
	private StructureDefinitionEditor editor;
	
	private DTreeSelectionListener<DataItem> definitionStateListener = new DTreeSelectionListener<DataItem>()
	{
		@Override
		public void selectionStateChanged()
		{
			page.removeAll();
			
			int dl = splitPane.getDividerLocation();
			
			if(selectionState.type == SelectionType.FOLDERS)
			{
				revalidate();
				repaint();
				splitPane.setBottomComponent(emptySidebarBottom);
				splitPane.setDividerLocation(dl);
				return;
			}
			DataItem di = (DataItem) selectionState.nodes.get(0).getUserObject();
			StructureDefinition toEdit = (StructureDefinition) di.getObject();
			editor = toEdit.getEditor();
			editor.setAlignmentX(LEFT_ALIGNMENT);
			
			page.add(editor, BorderLayout.CENTER);
			
			if(editorTree != null)
				editorTree.removeTreeListener(editorStateListener);
			editorTree = editor.tree;
			editorTree.addTreeListener(editorStateListener);
			
			splitPane.setTopComponent(definitionTreeView);
			splitPane.setBottomComponent(editor.treeView);
			splitPane.setDividerLocation(dl);
			
			editorTree.getTree().setSelectionPath(new TreePath(new TreeNode[] {editorTree.getRoot()}));
			editorStateListener.selectionStateChanged();
			
			revalidate();
			repaint();
		}
		
		@Override
		public void setSelectionState(DTreeSelectionState<DataItem> state)
		{
			selectionState = state;
		}
	};
	
	private DTreeSelectionState<DataItem> editorState;
	
	private DTreeSelectionListener<DataItem> editorStateListener = new DTreeSelectionListener<DataItem>()
	{
		@Override
		public void setSelectionState(DTreeSelectionState<DataItem> state)
		{
			editorState = state;
		}
		
		@Override
		public void selectionStateChanged()
		{
			PropertiesWindow propsWindow = StructureDefinitionsWindow.get().getPropsWindow();
			
			DataItem di = (DataItem) editorState.nodes.get(0).getUserObject();
			EditableObject selected = (di == null) ? null : di.getObject();
			if(selected != null)
			{
				if(!(selected instanceof StructureTable || selected instanceof StructureTabset))
				{
					EditableObject toEdit = (EditableObject) selected;
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
			if(editorTree != null)
				if(editorTree.getTree() != null)
					editorTree.getTree().getSelectionModel().clearSelection();
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
		
		definitionsfm = new HierarchyModel<DataItem>(StructureDefinitions.root);
		definitionTree = new DarkTree<DataItem>(definitionsfm);
		definitionTree.setNamingEditingAllowed(false);
		definitionTree.addTreeListener(definitionStateListener);
		definitionTree.expand((Folder) StructureDefinitions.root.getItemByName("My Structures"));
		
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
		
		definitionTree.setNodeCreator(new DefaultNodeCreator<DataItem>()
		{
			@Override
			public Collection<PopupItem> getCreatableNodeList()
			{
				return createNodeList;
			}
			
			@Override
			public Leaf<DataItem> createNode(PopupItem item, String nodeName)
			{
				if(item.text.equals("Folder"))
					return new Folder(nodeName);
				
				CreateStructureDefinitionDialog dg = new CreateStructureDefinitionDialog();
				dg.setNodeName(nodeName);
				StructureDefinition toCreate = dg.newDef;
				dg.dispose();
				
				if(toCreate == null)
					return null;
				
				StructureDefinitions.addDefinition(toCreate);
				return toCreate.dref;
			}
			
			@Override
			public void editNode(Leaf<DataItem> toEdit)
			{
				if(toEdit instanceof Folder)
					return;
				
				if(!(((DataItem) toEdit).getObject() instanceof StructureDefinition))
					return;
				
				CreateStructureDefinitionDialog dg = new CreateStructureDefinitionDialog();
				dg.setDefinition((StructureDefinition) ((DataItem) toEdit).getObject());
				dg.dispose();
				definitionTree.repaint();
			}
			
			@Override
			public boolean attemptRemove(List<Leaf<DataItem>> toRemove)
			{
				if(toRemove.size() > 0 && ((DataItem) toRemove.get(0)).getObject() instanceof StructureDefinition)
				{
					StructureDefinition def = (StructureDefinition) ((DataItem) toRemove.get(0)).getObject();
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
			public void nodeRemoved(Leaf<DataItem> toRemove)
			{
				if(((DataItem) toRemove).getObject() instanceof StructureDefinition)
				{
					StructureDefinition def = (StructureDefinition) ((DataItem) toRemove).getObject();
					def.remove();
				}
			}
		});
		
		emptySidebarBottom = new JPanel(new BorderLayout());
		emptySidebarBottom.setBackground(UIConsts.SIDEBAR_COLOR);
		
		int initDivLoc = Prefs.<Integer>get(Prefs.DEFPAGE_SIDEWIDTH);
		
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
		if(editorTree != null)
			editorTree.getTree().getSelectionModel().clearSelection();
		definitionTree.getTree().getSelectionModel().clearSelection();
	}
	
	private static final ArrayList<PopupItem> createNodeList = new ArrayList<PopupItem>();
	static
	{
		createNodeList.add(new PopupItem("Structure", null, null));
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
