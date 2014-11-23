package stencyl.ext.polydes.datastruct.ui.page;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collection;

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
import stencyl.ext.polydes.datastruct.ui.MiniSplitPane;
import stencyl.ext.polydes.datastruct.ui.StatusBar;
import stencyl.ext.polydes.datastruct.ui.UIConsts;
import stencyl.ext.polydes.datastruct.ui.list.ListUtils;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureDefinitionEditor;
import stencyl.ext.polydes.datastruct.ui.tree.DTree;
import stencyl.ext.polydes.datastruct.ui.tree.DTreeSelectionListener;
import stencyl.ext.polydes.datastruct.ui.tree.DTreeSelectionState;
import stencyl.ext.polydes.datastruct.ui.tree.SelectionType;
import stencyl.ext.polydes.datastruct.ui.utils.SnappingDialog;
import stencyl.ext.polydes.datastruct.ui.utils.PopupUtil.PopupItem;
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
	
	private StructureDefinition currentDefinition;
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
			StructureDefinition toEdit = currentDefinition = (StructureDefinition) di.getObject();
			editor = toEdit.getEditor();
			editor.setAlignmentX(LEFT_ALIGNMENT);
			
			page.add(editor, BorderLayout.CENTER);
			
			if(editorTree != null)
				editorTree.removeTreeListener(editorStateListener);
			editorTree = editor.model.getTree();
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
			editor.removeAll();
			
			DataItem di = (DataItem) editorState.nodes.get(0).getUserObject();
			EditableObject selected = (di == null) ? null : di.getObject();
			if(selected != null)
			{
				EditableObject toEdit = (EditableObject) selected;
				PropertiesWindow.setObject(toEdit);
				PropertiesWindow.showWindow();
				PropertiesWindow.get().addComponentListener(propsWindowAdapter);
				
				editor.add(currentDefinition.getEditor().getPreview().getEditor());
				//TODO: highlight currently selected node
				//currentDefinition.getEditor().getPreview().getEditor().highlightElement(toEdit.gui);
			}
			else
			{
				PropertiesWindow.setObject(null);
				PropertiesWindow.hideWindow();
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
			PropertiesWindow.get().removeComponentListener(this);
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
		definitionTree = definitionsfm.getTree();
		definitionTree.addTreeListener(definitionStateListener);
		
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
		
		definitionTree.setNodeCreator(definitionsfm.new DefaultNodeCreator()
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
				//TODO: Folder editing
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
			public void nodeRemoved(DataItem toRemove)
			{
				if(toRemove.getObject() instanceof StructureDefinition)
				{
					//StructureDefinitions.
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
		
		PropertiesWindow.get().snapToComponent(scroller.getViewport(), SnappingDialog.TOP_RIGHT);
		PropertiesWindow.hideWindow();
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
		_instance = null;
	}
}
