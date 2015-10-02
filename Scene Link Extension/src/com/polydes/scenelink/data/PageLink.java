package com.polydes.scenelink.data;

import com.polydes.scenelink.SceneLinkExtension;
import com.polydes.scenelink.ui.MainPage;

public class PageLink extends Link
{
	public PageLink(int id)
	{
		super(id);
	}

	@Override
	public void open()
	{
		MainPage.get().switchToPage(id);
	}
	
	@Override
	public Object getModel()
	{
		return SceneLinkExtension.getPageModel(id);
	}
}
