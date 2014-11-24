package stencyl.ext.polydes.dialog.app.editors;

import java.awt.LayoutManager;

import javax.swing.JPanel;

public abstract class DataItemEditor extends JPanel
{
	public DataItemEditor(LayoutManager layoutManager)
	{
		super(layoutManager);
	}
	
	public abstract boolean isDirty();
	public abstract Object getContents();
	public abstract void setClean();
	public abstract void setDirty();
	public abstract void nameChanged(String name);
	public abstract void allowExpandVertical(boolean value);
}
