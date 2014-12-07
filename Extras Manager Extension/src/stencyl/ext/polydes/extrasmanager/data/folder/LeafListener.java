package stencyl.ext.polydes.extrasmanager.data.folder;


public interface LeafListener<T extends Leaf<T>>
{
	public void LeafStateChanged(Leaf<T> source);
	public void leafNameChanged(Leaf<T> source, String oldName);
}
