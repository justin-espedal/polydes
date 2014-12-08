package stencyl.ext.polydes.scenelink.data;

import stencyl.ext.polydes.scenelink.Main;
import stencyl.ext.polydes.scenelink.ui.MainPage;

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
		return Main.getPageModel(id);
	}
}
