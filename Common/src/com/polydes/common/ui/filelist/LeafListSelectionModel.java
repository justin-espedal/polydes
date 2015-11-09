package com.polydes.common.ui.filelist;

import static com.polydes.common.util.Lang.asArray;
import static com.polydes.common.util.Lang.fori;
import static com.polydes.common.util.Lang.hashset;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.event.EventListenerSupport;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.NodeSelection;
import com.polydes.common.nodes.NodeSelectionEvent;
import com.polydes.common.nodes.NodeSelectionListener;
import com.polydes.common.nodes.NodeUtils;

public class LeafListSelectionModel<T extends Leaf<T,U>, U extends Branch<T,U>> implements ListSelectionModel, NodeSelectionListener<T, U>
{
	private Class<T> leafClass;
	
	private EventListenerSupport<ListSelectionListener> selectionEvents;
	private NodeSelection<T,U> selection;
	private int mode;
	
	private U folder;
	
	public LeafListSelectionModel(HierarchyModel<T,U> model, U folder)
	{
		this.selection = model.getSelection();
		this.folder = folder;
		
		selectionEvents = new EventListenerSupport<>(ListSelectionListener.class);
		leafClass = model.leafClass;
		
		selection.addSelectionListener(this);
	}
	
	@Override
	public void setSelectionInterval(int index0, int index1)
	{
		ArrayList<T> toAdd = new ArrayList<>();
		ArrayList<T> toRemove = new ArrayList<>();
		
		int i0 = Math.min(index0, index1);
		int i1 = Math.max(index0, index1);
		
		for(int i = 0, n = folder.getItems().size(); i < n; ++i)
		{
			if(i >= i0 && i <= i1)
				toAdd.add(folder.getItemAt(i));
			else
				toRemove.add(folder.getItemAt(i));
		}
		
		selection.change(asArray(toAdd,  leafClass), asArray(toRemove, leafClass));
	}

	@Override
	public void addSelectionInterval(int index0, int index1)
	{
		ArrayList<T> toAdd = new ArrayList<>();
		
		int i0 = Math.min(index0, index1);
		int i1 = Math.max(index0, index1);
		
		for(int i = 0, n = folder.getItems().size(); i < n; ++i)
			if(i >= i0 && i <= i1)
				toAdd.add(folder.getItemAt(i));
		
		selection.addAll(asArray(toAdd, leafClass));
	}

	@Override
	public void removeSelectionInterval(int index0, int index1)
	{
		ArrayList<T> toRemove = new ArrayList<>();
		
		int i0 = Math.min(index0, index1);
		int i1 = Math.max(index0, index1);
		
		fori(folder.getItems(), (i, node) -> {
			if(i >= i0 && i <= i1)
				toRemove.add(node);
		});
		
		selection.removeAll(asArray(toRemove, leafClass));
	}

	@Override
	public int getMinSelectionIndex()
	{
		for(int i = 0, n = folder.getItems().size(); i < n; ++i)
			if(selection.contains(folder.getItemAt(i)))
				return i;
		
		return -1;
	}

	@Override
	public int getMaxSelectionIndex()
	{
		for(int i = folder.getItems().size() - 1, n = 0; i >= n; --i)
			if(selection.contains(folder.getItemAt(i)))
				return i;
		
		return -1;
	}

	@Override
	public boolean isSelectedIndex(int index)
	{
		return selection.contains(folder.getItemAt(index));
	}

	@Override
	public int getAnchorSelectionIndex()
	{
		return 0;
	}

	@Override
	public void setAnchorSelectionIndex(int index)
	{
		
	}

	@Override
	public int getLeadSelectionIndex()
	{
		return 0;
	}

	@Override
	public void setLeadSelectionIndex(int index)
	{
		
	}

	@Override
	public void clearSelection()
	{
		selection.removeAll(asArray(folder.getItems(), leafClass));
	}

	@Override
	public boolean isSelectionEmpty()
	{
		return getMinSelectionIndex() == -1;
	}

	@Override
	public void insertIndexInterval(int index, int length, boolean before)
	{
		//maybe don't need to do anything here
	}

	@Override
	public void removeIndexInterval(int index0, int index1)
	{
		//maybe don't need to do anything here
	}

	private boolean adjusting = false;
	
	@Override
	public void setValueIsAdjusting(boolean valueIsAdjusting)
	{
		adjusting = valueIsAdjusting;
	}

	@Override
	public boolean getValueIsAdjusting()
	{
		return adjusting;
	}

	@Override
	public void setSelectionMode(int selectionMode)
	{
		mode = selectionMode;
	}

	@Override
	public int getSelectionMode()
	{
		return mode;
	}

	@Override
	public void addListSelectionListener(ListSelectionListener x)
	{
		selectionEvents.addListener(x);
	}

	@Override
	public void removeListSelectionListener(ListSelectionListener x)
	{
		selectionEvents.removeListener(x);
	}

	@Override
	public void selectionChanged(NodeSelectionEvent<T, U> e)
	{
		HashSet<T> nodes = hashset(e.getNodes());
		nodes.retainAll(folder.getItems());
		if(nodes.isEmpty())
			return;
		
		System.out.print(folder.getName() + ": nodes in this list that changed: ");
		NodeUtils.print(nodes);
		
		boolean oldAdjusting = adjusting;
		adjusting = true;
		
		int i = -1;
		int n = folder.getItems().size();
		int begin = -1;
		while(++i < n)
		{
			if(nodes.contains(folder.getItemAt(i)))
			{
				if(begin == -1)
					begin = i;
			}
			else
			{
				if(begin != -1)
				{
					ListSelectionEvent event = new ListSelectionEvent(this, begin, i - 1, adjusting);
					selectionEvents.fire().valueChanged(event);
					begin = -1;
				}
			}
		}
		
		adjusting = oldAdjusting;
	}
}
