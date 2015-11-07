package com.polydes.common.nodes;

import java.lang.reflect.Array;

public class NodeSelectionEvent<T extends Leaf<T,U>, U extends Branch<T,U>>
{
	private final Object source;
	private final T[] nodes;
	private final boolean[] areNew;
	private final T oldLead;
	private final T newLead;
	
	@SuppressWarnings("unchecked")
	public NodeSelectionEvent(Object source, T node, boolean isNew, T oldLead, T newLead)
	{
		this.source = source;
		this.oldLead = oldLead;
		this.newLead = newLead;
		
		nodes = (T[]) Array.newInstance(node.getClass(), 1);
		nodes[0] = node;
		areNew = new boolean[] {isNew};
	}
	
	public NodeSelectionEvent(Object source, T[] nodes, boolean[] areNew, T oldLead, T newLead)
	{
		this.source = source;
		this.nodes = nodes;
		this.areNew = areNew;
		this.oldLead = oldLead;
		this.newLead = newLead;
	}
	
	public Object getSource()
	{
		return source;
	}
	
	public T[] getNodes()
	{
		return nodes;
	}
	
	public boolean[] areNew()
	{
		return areNew;
	}
	
	public T getOldLead()
	{
		return oldLead;
	}
	
	public T getNewLead()
	{
		return newLead;
	}
}