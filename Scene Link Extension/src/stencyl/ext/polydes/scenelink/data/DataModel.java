package stencyl.ext.polydes.scenelink.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class DataModel
{
	public final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
	}
}