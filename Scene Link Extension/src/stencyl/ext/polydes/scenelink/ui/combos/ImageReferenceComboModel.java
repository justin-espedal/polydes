package stencyl.ext.polydes.scenelink.ui.combos;

import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;

import stencyl.ext.polydes.scenelink.res.Resources;


public class ImageReferenceComboModel extends DefaultComboBoxModel
{
	public static String[] imageNames = new String[0];
	public static final String UNSELECTED = "no image";
	
	int size = -1;
	
	public ImageReferenceComboModel()
	{
	}
	
	public static void updateImages()
	{
		ArrayList<String> a = Resources.getResourceNames();
		imageNames = new String[a.size() + 1];
		imageNames[0] = UNSELECTED;
		int i = 1;
		for(String s : a)
		{
			imageNames[i++] = s;
		}
	}
	
	public String getImageString()
	{
		String s = (String) getSelectedItem();
		if(s == null || s == UNSELECTED)
			return "";
		return s;
	}
	
	@Override
	public Object getSelectedItem()
	{
		if(super.getSelectedItem() == null || super.getSelectedItem().equals(""))
		{
			super.setSelectedItem(UNSELECTED);
			return UNSELECTED;
		}
		
		return super.getSelectedItem();
	}
	
	@Override
	public void setSelectedItem(Object o)
	{
		if(o == null || ((String)o).equals(""))
		{
			super.setSelectedItem(UNSELECTED);
			return;
		}
		
		super.setSelectedItem(o);
	}
	
	@Override
	public Object getElementAt(int index)
	{
		return imageNames[index];
	}
	
	@Override
	public int getSize()
	{
		if(size != imageNames.length)
		{
			size = imageNames.length;
			fireContentsChanged(this, 0, size);			
		}
		
		return size;
	}
}