package stencyl.ext.polydes.extrasmanager.app.tree;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

public class FNode extends DefaultMutableTreeNode
{
	public FNode(File f)
	{
		super(f);
	}

	@Override
	public String toString()
	{
		if(userObject != null && userObject instanceof File)
			return ((File) userObject).getName();
		return "";
	}
	
	public File getFile()
	{
		return (File) userObject;
	}
}
