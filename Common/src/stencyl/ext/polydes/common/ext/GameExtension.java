package stencyl.ext.polydes.common.ext;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

import stencyl.core.lib.Game;
import stencyl.ext.polydes.common.util.CommonLoader;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Locations;

public abstract class GameExtension extends BaseExtension
{
	private static final String PREFS = "prefs";
	
	private File dataFolder;
	private File extrasFolder;
	private HashMap<String, Object> extensionGameData = new HashMap<String, Object>();
	private boolean installedForGame;
	
	public File getDataFolder()
	{
		return dataFolder;
	}
	
	public File getExtrasFolder()
	{
		return extrasFolder;
	}
	
	public String readInternalData()
	{
		return super.readData();
	}
	
	public void writeInternalData(String data)
	{
		saveData(data);
	}
	
	@Override
	public final JPanel onGameCenterActivate()
	{
		if(!installedForGame)
			return getEnablerPanel();
		else
			return getMainPage();
	}
	
	public abstract JPanel getMainPage();
	
	public JPanel getEnablerPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(new Color(62, 62, 62));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(null);
		
		final JButton installButton = new JButton("Install " + gameCenterName + " for this project");
		installButton.addActionListener(new InstallActionListener(panel));
		
		buttonPanel.add(installButton);
		buttonPanel.setPreferredSize(buttonPanel.getMinimumSize());
		
		panel.add(buttonPanel, BorderLayout.CENTER);
		
		return panel;
	}
	
	private class InstallActionListener implements ActionListener
	{
		JComponent panel;
		
		public InstallActionListener(JComponent panel)
		{
			this.panel = panel;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			installForGame(Game.getGame());
			
			Container parent = panel.getParent();
			parent.remove(panel);
			parent.add(onGameCenterActivate());
		}
	}
	
	public void installForGame(Game game)
	{
		installedForGame = true;
		dataFolder = new File(GameExtension.this.getExtensionGameDataLocation());
		dataFolder.mkdirs();
		
		onInstalledForGame(game);
		extensionGameData.clear();
		extensionGameData.put("version", internalVersion);
		onGameWithDataOpened(game);
	}
	
	
	//TODO: Provide a way to uninstall an extension.
	
//	private class UninstallActionListener implements ActionListener
//	{
//		JComponent panel;
//		
//		public UninstallActionListener(JComponent panel)
//		{
//			this.panel = panel;
//		}
//		
//		@Override
//		public void actionPerformed(ActionEvent e)
//		{
//			uninstallForGame(Game.getGame());
//			Container parent = panel.getParent();
//			parent.remove(panel);
//			parent.add(onGameCenterActivate());
//		}
//	}
	
	public void uninstallForGame(Game game)
	{
		installedForGame = false;
		FileHelper.delete(dataFolder);
		dataFolder = null;
		onUninstalledForGame(game);
	}
	
	public abstract boolean isInstalledForGame(Game game);
	public abstract void onInstalledForGame(Game game);
	public abstract void onUninstalledForGame(Game game);
	
	@Override
	public final void onGameOpened(Game game)
	{
		dataFolder = new File(getExtensionGameDataLocation());
		extrasFolder = new File(Locations.getGameLocation(game) + "extras" + File.separator + classname);
		
		installedForGame = isInstalledForGame(game);
		if(!installedForGame)
			return;
		
		File prefsFile = new File(dataFolder, PREFS);
		if(!prefsFile.exists()) //we're coming from an old, non-tracked version
			try
			{
				FileUtils.writeStringToFile(prefsFile, "version=-1");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		
		CommonLoader.loadPropertiesFromFile(new File(dataFolder, PREFS).getAbsolutePath(), extensionGameData);
		
		int dataVersion = (Integer) extensionGameData.get("version");
		if(dataVersion < internalVersion)
			updateFromVersion(game, dataVersion);
		extensionGameData.put("version", internalVersion);
		
		onGameWithDataOpened(game);
	}
	
	@Override
	public final void onGameSave(Game game)
	{
		if(!installedForGame)
			return;
		
		onGameWithDataSaved(game);
	}
	
	@Override
	public final void onGameClosed(Game game)
	{
		super.onGameClosed(game);
		dataFolder = null;
		extrasFolder = null;
		extensionGameData.clear();
		
		if(!installedForGame)
			return;
		
		installedForGame = false;
		onGameWithDataClosed(game);
	}
	
	public abstract void onGameWithDataOpened(Game game);
	public abstract void onGameWithDataSaved(Game game);
	public abstract void onGameWithDataClosed(Game game);
	
	public abstract void updateFromVersion(Game game, int fromVersion);
	
	public void showMessage(final String largeText, final String smallText)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				showMessageDialog(
					largeText,
					smallText
				);
			}
		});
	}
}