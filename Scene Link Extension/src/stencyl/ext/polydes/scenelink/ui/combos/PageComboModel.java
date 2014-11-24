package stencyl.ext.polydes.scenelink.ui.combos;

import javax.swing.DefaultComboBoxModel;

import stencyl.ext.polydes.scenelink.Main;
import stencyl.ext.polydes.scenelink.data.LinkPageModel;


public class PageComboModel extends DefaultComboBoxModel
{
	public static LinkPageModel[] models = new LinkPageModel[0];
	
	public int size = -1;
	
	public static void updatePages()
	{
		models = Main.getPages().toArray(new LinkPageModel[0]);
	}
	
	@Override
	public Object getElementAt(int index)
	{
		return models[index];
	}
	
	@Override
	public int getSize()
	{
		if(size != models.length)
		{
			size = models.length;
			fireContentsChanged(this, 0, size);			
		}
		
		return size;
	}
	
	@Override
	public void setSelectedItem(Object anObject)
	{
		super.setSelectedItem(anObject);
	}
}