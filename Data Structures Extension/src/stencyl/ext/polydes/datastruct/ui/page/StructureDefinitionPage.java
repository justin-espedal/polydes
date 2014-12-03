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

import stencyl.ext.polydes.datastruct.Prefs;
import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.folder.EditableObject;
import stencyl.ext.polydes.datastruct.data.folder.Folder;
import stencyl.ext.polydes.datastruct.data.folder.FolderHierarchyModel;
import stencyl.ext.polydes.datastruct.data.structure.StructureDefinition;
import stencyl.ext.polydes.datastruct.data.structure.StructureDefinitions;
import stencyl.ext.polydes.datastruct.data.structure.StructureTable;
import stencyl.ext.polydes.datastruct.data.structure.StructureTabset;
import stencyl.ext.polydes.datastruct.data.structure.Structures;
import stencyl.ext.polydes.datastruct.ui.MiniSplitPane;
import stencyl.ext.polydes.datastruct.ui.StatusBar;
import stencyl.ext.polydes.datastruct.ui.UIConsts;
import stencyl.ext.polydes.datastruct.ui.list.ListUtils;
import stencyl.ext.polydes.datastruct.ui.objeditors.PreviewableEditor;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureDefinitionEditor;
import stencyl.ext.polydes.datastruct.ui.tree.DTree;
import stencyl.ext.polydes.datastruct.ui.tree.DTreeSelectionListener;
import stencyl.ext.polydes.datastruct.ui.tree.DTreeSelectionState;
import stencyl.ext.polydes.datastruct.ui.tree.DefaultNodeCreator;
import stencyl.ext.polydes.datastruct.ui.tree.SelectionType;
import stencyl.ext.polydes.datastruct.ui.utils.PopupUtil.PopupItem;
import stencyl.ext.polydes.datastruct.ui.utils.SnappingDialog;
import stencyl.sw.util.UI;

public class StructureDefinitionPage extends JPanel
{
	private static StructureDefinitionPage _instance;
	
	public MiniSplitPane splitPane;
	private JPanel emptySidebarBottom;
	
	private FolderHierarchyModel definitionsfm;
	
	private DTree definitionTree;
	private DTree editorTree;
	private JComponent definitionTreeView;
	
	private DTreeSelectionState selectionState;
	
	protected JScrollPane scroller;
	protected JPanel page;
	
	private StructureDefinitionEditor editor;
	
	private DTreeSelectionListener definitionStateListener = new DTreeSelectionListener()
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
		public void setSelectionState(DTreeSelectionState state)
		{
			selectionState = state;
		}
	};
	
	private DTreeSelectionState editorState;
	
	private DTreeSelectionListener editorStateListener = new DTreeSelectionListener()
	{
		@Override
		public void setSelectionState(DTreeSelectionState state)
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
		
		definitionsfm = new FolderHierarchyModel(StructureDefinitions.root);
		definitionTree = new DTree(definitionsfm);
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
		definitionsfm.setUniqueItemNames(true);
		
		definitionTree.setNodeCreator(new DefaultNodeCreator()
		{
			@Override
			public Collection<PopupItem> getCreatableNodeList()
			{
				return createNodeList;
			}
			
			@Override
			public Object createNode(PopupItem item, String nodeName)
			{
				if(item.text.equals("Folder"))
					return new Folder(nodeName);
				
				CreateStructureDefinitionDialog dg = new CreateStructureDefinitionDialog();
				dg.setNodeName(nodeName);
				StructureDefinition toCreate = dg.newDef;
				dg.dispose();
				
				if(toCreate != null)
					StructureDefinitions.addDefinition(toCreate);
				
				return toCreate.dref;
			}
			
			@Override
			public void editNode(DataItem toEdit)
			{
				if(toEdit instanceof Folder)
					return;
				
				if(!(toEdit.getObject() instanceof StructureDefinition))
					return;
				
				CreateStructureDefinitionDialog dg = new CreateStructureDefinitionDialog();
				dg.setDefinition((StructureDefinition) toEdit.getObject());
				dg.dispose();
				definitionTree.repaint();
			}
			
			@Override
			public boolean attemptRemove(List<DataItem> toRemove)
			{
				if(toRemove.size() > 0 && toRemove.get(0).getObject() instanceof StructureDefinition)
				{
					StructureDefinition def = (StructureDefinition) toRemove.get(0).getObject();
					int result =
						UI.showYesCancelPrompt(
							"Remove Structure Definition",
							"Are you sure you want to remove this structure definition? (Will delete " + Structures.structures.get(def).size() + " structures)",
							"Remove", "Cancel"
						);
					
					return UI.choseYes(result);
				}
				return false;
			}
			
			@Override
			public void nodeRemoved(DataItem toRemove)
			{
				if(toRemove.getObject() instanceof StructureDefinition)
				{
					StructureDefinition def = (StructureDefinition) toRemove.getObject();
					def.remove();
				}
			}
		});
		
		emptySidebarBottom = new JPanel(new BorderLayout());
		emptySidebarBottom.setBackground(UIConsts.SIDEBAR_COLOR);
		
		int initDivLoc = Prefs.get(Prefs.DEFPAGE_SIDEWIDTH);
		
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
