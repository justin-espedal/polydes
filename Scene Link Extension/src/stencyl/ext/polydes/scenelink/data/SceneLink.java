package stencyl.ext.polydes.scenelink.data;

import stencyl.core.lib.Game;
import stencyl.core.lib.scene.SceneModel;
import stencyl.sw.SW;

public class SceneLink extends Link
{
	public SceneLink(int id)
	{
		super(id);
	}

	@Override
	public void open()
	{
		SceneModel model = Game.getGame().getScene(id);
		if(model == null)
		{
			System.out.println("Cannot open null scene: " + id);
			return;
		}
		
		SW.get().getWorkspace().openResource(model, false);
	}
	
	@Override
	public Object getModel()
	{
		return Game.getGame().getScene(id);
	}
}
