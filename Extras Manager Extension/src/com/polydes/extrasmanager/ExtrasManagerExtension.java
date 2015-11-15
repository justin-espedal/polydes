package com.polydes.extrasmanager;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.sys.FileMonitor;
import com.polydes.common.sys.SysFile;
import com.polydes.common.sys.SysFolder;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.extrasmanager.app.MainEditor;
import com.polydes.extrasmanager.data.ExtrasNodeCreator;
import com.polydes.extrasmanager.data.FileEditor;
import com.polydes.extrasmanager.io.FileOperations;

import stencyl.core.lib.Game;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;
import stencyl.sw.util.dg.DialogPanel;

public class ExtrasManagerExtension extends BaseExtension
{
	private static ExtrasManagerExtension _instance;
	
	private static HierarchyModel<SysFile,SysFolder> model;
	
	private static boolean gameOpen;
	
	private String gameDir;
	private String extrasDir;

	public static ExtrasManagerExtension get()
	{
		return _instance;
	}

	public static HierarchyModel<SysFile,SysFolder> getModel()
	{
		return model;
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
		menuName = "Extras Manager";

		isInGameCenter = true;
		gameCenterName = "Extras Manager";

		gameDir = "";
		
//		requestFolderOwnership(this, dataFolderName);
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
		if(gameOpen)
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
		gameOpen = true;
		
		gameDir = Locations.getGameLocation(game);
		extrasDir = gameDir + "extras/";
		File extrasFile = new File(extrasDir);
		
		if(!extrasFile.exists())
			extrasFile.mkdir();
		
		model = FileMonitor.getExtrasModel();
		model.setNodeCreator(new ExtrasNodeCreator(model));
		
		File templatesFile = new File(Locations.getExtensionGameDataLocation(game, getManifest().id), "templates");
		
		if(!templatesFile.exists())
		{
			templatesFile.mkdir();
			loadDefaults(templatesFile);
		}
		
		FileOperations.templatesFile = templatesFile;
		
		String input = readData();
		
		String[] data;
		if(input == null || input.isEmpty())
			data = new String[] {"", ""};
		else
			data = input.split("\n");
		
		String textPath = data[0];
		String imagePath = data[1];
		FileEditor.typeProgramMap.put("text/plain", textPath);
		FileEditor.typeProgramMap.put("image/png", imagePath);
	}
	
	public void loadDefaults(File templates)
	{
		try
		{
			FileHelper.writeStringToFile
			(
				new File(templates, "File.txt").getAbsolutePath(),
				""
			);
			FileHelper.writeToPNG
			(
				new File(templates, "Image.png").getAbsolutePath(),
				new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
			);
		}
		catch(IOException e)
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

		model.dispose();
		model = null;
		FileMonitor.unregister();
		
		gameDir = "";
		extrasDir = "";
		
		MainEditor.disposePages();
		
		gameOpen = false;
	}

	@Override
	public OptionsPanel onOptions()
	{
		return new OptionsPanel()
		{
			PropertiesSheetSupport sheet;
			
			@Override
			public void init()
			{
				DialogPanel panel = new DialogPanel(PropertiesSheetStyle.DARK.pageBg);
				
				sheet = new PropertiesSheetSupport(panel, properties);
				
				sheet.build()
					.header("Options")
					.field("textEditor")._filePath().add()
					.field("imageEditor")._filePath().add()
					.finish();
				
				panel.addFinalRow(new JLabel());
				add(panel, BorderLayout.CENTER);
			}

			@Override
			public void onPressedOK()
			{
				FileEditor.typeProgramMap.put("application/octet-stream", readStringProp("textEditorPath", null));
				FileEditor.typeProgramMap.put("text/plain", readStringProp("textEditorPath", null));
				FileEditor.typeProgramMap.put("image/png", readStringProp("imageEditorPath", null));
				sheet.dispose();
				sheet = null;
			}

			@Override
			public void onPressedCancel()
			{
				sheet.revertChanges();
				sheet.dispose();
				sheet = null;
			}
			
			@Override
			public void onShown()
			{
			}
		};
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
