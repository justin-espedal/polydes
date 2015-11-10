package com.polydes.common.ui.darktree;

import static com.polydes.common.util.Lang.map;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang3.event.EventListenerSupport;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.NodeSelection;
import com.polydes.common.nodes.NodeSelectionEvent;
import com.polydes.common.nodes.NodeSelectionListener;

public class DTreeSelectionModel<T extends Leaf<T,U>, U extends Branch<T,U>> implements TreeSelectionModel, NodeSelectionListener<T,U>
{
	private Class<T> leafClass;
	
	private PropertyChangeSupport propertyChanges;
	private EventListenerSupport<TreeSelectionListener> selectionEvents;
	private NodeSelection<T,U> selection;
	private int mode;
	
	public DTreeSelectionModel(HierarchyModel<T,U> model)
	{
		this.selection = model.getSelection();
		propertyChanges = new PropertyChangeSupport(this);
		selectionEvents = new EventListenerSupport<>(TreeSelectionListener.class);
		
		leafClass = model.leafClass;
		
		selection.addSelectionListener(this);
	}
	
	@Override
	public void setSelectionMode(int mode)
	{
		int oldMode = this.mode;
		this.mode = mode;
		
		if(oldMode != mode)
			propertyChanges.firePropertyChange(DefaultTreeSelectionModel.SELECTION_MODE_PROPERTY, oldMode, mode);
	}

	@Override
	public int getSelectionMode()
	{
		return mode;
	}

	@SuppressWarnings("unchecked")
	private T treePathToNode(TreePath path)
	{
		return (T) path.getLastPathComponent();
	}
	
	@SuppressWarnings("unchecked")	
	private T[] treePathsToNodes(TreePath[] paths)
	{
		return map(paths, leafClass, path -> path == null ? null : (T) path.getLastPathComponent());
	}

	@Override
	public void setSelectionPath(TreePath path)
	{
		if(path == null)
			clearSelection();
		else
			selection.set(treePathToNode(path));
	}
	
	@Override
	public void setSelectionPaths(TreePath[] paths)
	{
		if(paths == null)
			clearSelection();
		else
			selection.setAll(treePathsToNodes(paths));
	}
	
	@Override
	public void addSelectionPath(TreePath path)
	{
		if(path == null)
			return;
		
		selection.add(treePathToNode(path));
	}

	@Override
	public void addSelectionPaths(TreePath[] paths)
	{
		if(paths == null)
			return;
		
		selection.addAll(treePathsToNodes(paths));
	}

	@Override
	public void removeSelectionPath(TreePath path)
	{
		if(path == null)
			return;
		
		selection.remove(treePathToNode(path));
	}

	@Override
	public void removeSelectionPaths(TreePath[] paths)
	{
		if(paths == null)
			return;
		
		selection.removeAll(treePathsToNodes(paths));
	}

	@Override
	public TreePath getSelectionPath()
	{
		T first = selection.firstNode();
		if(first == null)
			return null;
		
		return selection.treePath(first);
	}

	@Override
	public TreePath[] getSelectionPaths()
	{
		TreePath[] paths = new TreePath[selection.size()];
		int i = 0;
		
		for(T node : selection)
			paths[i++] = selection.treePath(node);
		
		return paths;
	}

	@Override
	public int getSelectionCount()
	{
		return selection.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isPathSelected(TreePath path)
	{
		return selection.contains((T) path.getLastPathComponent());
	}

	@Override
	public boolean isSelectionEmpty()
	{
		return selection.isEmpty();
	}

	@Override
	public void clearSelection()
	{
		selection.clear();
	}

	@Override
	public void setRowMapper(RowMapper newMapper)
	{
		
	}

	@Override
	public RowMapper getRowMapper()
	{
		return null;
	}

	@Override
	public int[] getSelectionRows()
	{
		return null;
	}

	@Override
	public int getMinSelectionRow()
	{
		return 0;
	}

	@Override
	public int getMaxSelectionRow()
	{
		return 0;
	}

	@Override
	public boolean isRowSelected(int row)
	{
		return false;
	}

	@Override
	public void resetRowSelection()
	{
		
	}

	@Override
	public int getLeadSelectionRow()
	{
		return 0;
	}

	@Override
	public TreePath getLeadSelectionPath()
	{
		return selection.treePath(selection.lead());
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChanges.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChanges.removePropertyChangeListener(listener);
	}

	@Override
	public void addTreeSelectionListener(TreeSelectionListener x)
	{
		selectionEvents.addListener(x);
	}

	@Override
	public void removeTreeSelectionListener(TreeSelectionListener x)
	{
		selectionEvents.removeListener(x);
	}
	
	@Override
	public void selectionChanged(NodeSelectionEvent<T,U> e)
	{
		TreePath oldLead = selection.treePath(e.getOldLead());
		TreePath newLead = selection.treePath(e.getNewLead());
		TreePath[] changes = selection.treePaths(e.getNodes());
		
		if(changes.length > 1)
			selectionEvents.fire().valueChanged(new TreeSelectionEvent(this, changes, e.areNew(), oldLead, newLead));
		else
			selectionEvents.fire().valueChanged(new TreeSelectionEvent(this, changes[0], e.areNew()[0], oldLead, newLead));
	}
}
