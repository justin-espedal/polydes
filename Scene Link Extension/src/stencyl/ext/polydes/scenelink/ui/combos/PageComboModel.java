package stencyl.ext.polydes.scenelink.ui.combos;

import javax.swing.DefaultComboBoxModel;

import stencyl.ext.polydes.scenelink.SceneLinkExtension;
import stencyl.ext.polydes.scenelink.data.LinkPageModel;


public class PageComboModel extends DefaultComboBoxModel<LinkPageModel>
{
	public static LinkPageModel[] models = new LinkPageModel[0];
	
	public int size = -1;
	
	public static void updatePages()
	{
		models = SceneLinkExtension.getPages().toArray(new LinkPageModel[0]);
	}
	
	@Override
	public LinkPageModel getElementAt(int index)
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