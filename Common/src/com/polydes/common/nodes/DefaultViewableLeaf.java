package com.polydes.common.nodes;

import javax.swing.JPanel;

import com.polydes.common.ui.object.ViewableObject;

public class DefaultViewableLeaf extends DefaultLeaf implements ViewableObject
{
	public DefaultViewableLeaf(String name, ViewableObject userData)
	{
		super(name, userData);
	}

	@Override
	public JPanel getView()
	{
		return ((ViewableObject) userData).getView();
	}

	@Override
	public void disposeView()
	{
		((ViewableObject) userData).disposeView();
	}

	@Override
	public boolean fillsViewHorizontally()
	{
		return false;
	}
}
