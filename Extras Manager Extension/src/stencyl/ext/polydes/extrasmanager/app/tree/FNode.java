package stencyl.ext.polydes.extrasmanager.app.tree;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

@SuppressWarnings("serial")
public class FNode extends DefaultMutableTreeNode
{
	public FNode(File f)
	{
		super(f);
	}

	@Override
	public String toString()
	{
		if(super.userObject != null && super.userObject instanceof File)
			return ((File) super.userObject).getName();
		return "";
	}
	
	public File getFile()
	{
		return (File) userObject;
	}
}
