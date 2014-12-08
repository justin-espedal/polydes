package stencyl.ext.polydes.common.nodes;


public interface LeafListener<T extends Leaf<T>>
{
	public void leafStateChanged(Leaf<T> source);
	public void leafNameChanged(Leaf<T> source, String oldName);
}
