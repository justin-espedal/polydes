package com.polydes.paint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;

import com.polydes.paint.app.MainEditor;
import com.polydes.paint.data.stores.Fonts;
import com.polydes.paint.data.stores.Images;
import com.polydes.paint.defaults.Defaults;

import stencyl.core.lib.Game;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;

public class PaintExtension extends BaseExtension
{
	private static PaintExtension _instance;

	private File extras;
	private File fontsFile;
	private File imagesFile;

	public static PaintExtension get()
	{
		return _instance;
	}

	/*
	 * Happens when StencylWorks launches.
	 * 
	 * Avoid doing anything time-intensive in here, or it will slow down launch.
	 */
	@Override
	public void onStartup()
	{
		super.onStartup();
		
		_instance = this;

		isInMenu = true;
		menuName = "Paint Extension";

		isInGameCenter = true;
		gameCenterName = "Paint Extension";
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
		return MainEditor.get();
	}

	public JPanel getBlankPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(new Color(62, 62, 62));
		return panel;
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
	public void onGameSave(Game game)
	{
		Fonts.get().saveChanges(fontsFile);
		Images.get().saveChanges(imagesFile);
		
		MainEditor.get().gameSaved();
	}

	/*
	 * Happens when the user runs, previews or exports the game.
	 */
	@Override
	public void onGameBuild(Game game)
	{
		onGameSave(game);
	}

	/*
	 * Happens when a game is opened.
	 */
	@Override
	public void onGameOpened(Game game)
	{
		extras = new File(Locations.getGameLocation(game) + "extras/" + getManifest().id);
		extras.mkdirs();
		
		if(extras.list().length == 0)
			loadDefaults();
		
		fontsFile = new File(extras, "fonts");
		imagesFile = new File(extras, "images");
		
		Fonts.get().load(fontsFile);
		Images.get().load(imagesFile);
	}

	private void loadDefaults()
	{
		File f;
		try
		{
			f = new File(extras, "fonts/Default Font.fnt");
			f.getParentFile().mkdirs();
			if (!f.exists())
				FileHelper.writeStringToFile(f.getAbsolutePath(), Defaults.load("Default Font.fnt"));
	
			f = new File(extras, "fonts/Default Font.png");
			if (!f.exists())
				FileHelper.writeToPNG(f.getAbsolutePath(),
						Defaults.loadImage("Default Font.png"));
	
			f = new File(extras, "images/Default Window.png");
			f.getParentFile().mkdirs();
			if (!f.exists())
				FileHelper.writeToPNG(f.getAbsolutePath(),
						Defaults.loadImage("Default Window.png"));
	
			f = new File(extras, "images/Pointer.png");
			if (!f.exists())
				FileHelper.writeToPNG(f.getAbsolutePath(),
						Defaults.loadImage("Pointer.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Happens when a game is closed.
	 */
	@Override
	public void onGameClosed(Game game)
	{
		super.onGameClosed(game);

		Fonts.get().unload();
		Images.get().unload();

		MainEditor.disposePages();
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
}
