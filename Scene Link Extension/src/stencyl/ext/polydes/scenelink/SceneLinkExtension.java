package stencyl.ext.polydes.scenelink;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JPanel;

import stencyl.ext.polydes.scenelink.data.LinkModel;
import stencyl.ext.polydes.scenelink.data.LinkPageModel;
import stencyl.ext.polydes.scenelink.io.XML;
import stencyl.ext.polydes.scenelink.res.Resources;
import stencyl.ext.polydes.scenelink.ui.MainPage;
import stencyl.ext.polydes.scenelink.ui.combos.PageComboModel;
import stencyl.ext.polydes.scenelink.util.ColorUtil;
import stencyl.sw.ext.GameExtension;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;

public class SceneLinkExtension extends GameExtension
{
	public static File pagesFolder;
	public static File resourcesFolder;
	
	public static HashMap<Integer, LinkPageModel> pages;
	
	/*
	 * Happens when StencylWorks launches. 
	 * 
	 * Avoid doing anything time-intensive in here, or it will
	 * slow down launch.
	 */
	@Override
	public void onStartup()
	{
		super.onStartup();
		
		isInMenu = true;
		menuName = "Scene Link";

		isInGameCenter = true;
		gameCenterName = "Scene Link";
		
		pages = null;
	}
	
	@Override
	public void extensionsReady()
	{
//		ExtensionInterface.sendMessage("stencyl.ext.polydes.extrasmanager", "requestFolderOwnership", this, dataFolderName);
	}
	
	/*
	 * Happens when the extension is told to display.
	 * 
	 * May happen multiple times during the course of the app. 
	 * 
	 * A good way to handle this is to make your extension a singleton.
	 */
	@Override
	public void onActivate()
	{
		
	}
	
	@Override
	public JPanel onGameCenterActivate()
	{
		return MainPage.get();
	}
	
	/*
	 * Happens when StencylWorks closes.
	 *  
	 * Usually used to save things out.
	 */
	@Override
	public void onDestroy()
	{
		
	}
	
	/*
	 * Happens when a game is saved.
	 */
	@Override
	public void onGameWithDataSaved()
	{
		for(LinkPageModel model : pages.values())
		{
			String pageName = pagesFolder.getAbsolutePath() + File.separator + model.getId() + ".xml";
			XML.wrObjectToFile(pageName, model);
		}
	}
	
	/*
	 * Happens when a game is opened.
	 */
	@Override
	public void onGameWithDataOpened()
	{
		resourcesFolder = openFolder(new File(getDataFolder(), "resources"));
		pagesFolder = openFolder(new File(getDataFolder(), "pages"));
		
		Resources.loadResourceNames();
		
		pages = new HashMap<Integer, LinkPageModel>();
		
		File[] pageFiles = pagesFolder.listFiles();
		for(File f : pageFiles)
		{
			LinkPageModel m = (LinkPageModel) XML.rObjectFromFile(f.getAbsolutePath(), LinkPageModel.class);
			pages.put(m.getId(), m);
		}
		PageComboModel.updatePages();
		
		if(pageFiles.length == 0)
			generateNewModel();
	}
	
	public File openFolder(File f)
	{
		if(!f.exists())
			f.mkdirs();
		return f;
	}

	/*
	 * Happens when a game is closed.
	 */
	@Override
	public void onGameWithDataClosed()
	{
		pages = null;
	}
	
	@Override
	public void onInstalledForGame()
	{
		if(detectOldInstall())
			updateFromVersion(1);
		
	}
	
	private boolean detectOldInstall()
	{
		return new File(Locations.getGameLocation(getGame()) + "extras/[ext] scene link").exists();
	}

	@Override
	public void onUninstalledForGame()
	{
		
	}

	@Override
	public void updateFromVersion(int fromVersion)
	{
		if(fromVersion <= 1)
		{
			File oldExtrasFolder = new File(Locations.getGameLocation(getGame()) + "extras/[ext] scene link");
			
			FileHelper.copyDirectory(oldExtrasFolder, getExtrasFolder());
			FileHelper.delete(oldExtrasFolder);
		}
	}
	
	/*
	 * Happens when the user requests the Options dialog for your extension.
	 * 
	 * You need to provide the form. We wrap it in a dialog.
	 */
	@Override
	public OptionsPanel onOptions()
	{
		return null;
	}
	
	@Override
	protected boolean hasOptions()
	{
		return false;
	}
	
	/*
	 * Happens when the extension is first installed.
	 */
	@Override
	public void onInstall()
	{
		
	}
	
	/*
	 * Happens when the extension is uninstalled.
	 * 
	 * Clean up files.
	 */
	@Override
	public void onUninstall()
	{
		
	}
	
	public static Collection<LinkPageModel> getPages()
	{
		return pages.values();
	}
	
	public static LinkPageModel getPageModel(int id)
	{
		return pages.get(id);
	}
	
	private static int nextPageModelID()
	{
		int id = -1;
		for(int i : pages.keySet())
			id = Math.max(id, i);
		return ++id;
	}
	
	public static LinkPageModel generateNewModel()
	{
		int id = nextPageModelID();
		LinkPageModel newModel = new LinkPageModel
		(
			id,
			"Page " + id,
			"",
			new Dimension(640, 640),
			"",
			new Point(0, 0),
			ColorUtil.decode("#ff333333"),
			new Point(0, 0),
			new Dimension(32, 32),
			ColorUtil.decode("#ff81969a"),
			true,
			true,
			new HashMap<Integer, LinkModel>()
		);
		pages.put(id, newModel);
		PageComboModel.updatePages();
		return newModel;
	}
}