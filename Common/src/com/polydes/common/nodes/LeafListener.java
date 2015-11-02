package com.polydes.common.nodes;



public interface LeafListener<T extends Leaf<T,U>, U extends Branch<T,U>>
{
	public void leafStateChanged(T source);
	public void leafNameChanged(T source, String oldName);
}
