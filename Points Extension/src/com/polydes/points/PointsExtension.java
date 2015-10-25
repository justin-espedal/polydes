package com.polydes.points;

import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import com.polydes.points.app.PointEditorPage;
import com.polydes.points.app.PointEditorWindow;

import stencyl.sw.ext.GameExtension;
import stencyl.sw.ext.OptionsPanel;

public class PointsExtension extends GameExtension
{
	private static PointsExtension instance;
	
	public static Rectangle pointWindowPos;
	public static int pointWindowSideWidth;
	
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
		
		instance = this;
		
		isInMenu = true;
		menuName = "Points Extension";
		
		pointWindowPos = new Rectangle(
			readIntProp("pointwin.x", -1),
			readIntProp("pointwin.y", -1),
			readIntProp("pointwin.width", 640),
			readIntProp("pointwin.height", 480)
		);
		pointWindowSideWidth = readIntProp("pointwindow.sidewidth", 265);
	}
	
	public static PointsExtension get()
	{
		return instance;
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
		PointEditorWindow.get();
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				PointEditorWindow.get().setVisible(true);
			}
		});
	}
	
	/*
	 * Happens when StencylWorks closes.
	 *  
	 * Usually used to save things out.
	 */
	@Override
	public void onDestroy()
	{
		putProp("pointwin.x", pointWindowPos.x);
		putProp("pointwin.y", pointWindowPos.y);
		putProp("pointwin.width", pointWindowPos.width);
		putProp("pointwin.height", pointWindowPos.height);
		putProp("pointwin.sidewidth", pointWindowSideWidth);
		super.onDestroy();
	}
	
	@Override
	protected boolean hasOptions()
	{
		return false;
	}
	
	@Override
	public OptionsPanel onOptions()
	{
		return null;
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

	@Override
	public void onGameWithDataOpened()
	{
		
	}
	
	@Override
	public void onGameWithDataSaved()
	{
		PointEditorPage.save();
	}

	@Override
	public void onGameWithDataClosed()
	{
		PointEditorWindow.disposeWindow();
		PointEditorPage.dispose();
	}

	@Override
	public void onInstalledForGame()
	{
		
	}

	@Override
	public void onUninstalledForGame()
	{
		
	}

	@Override
	public void updateFromVersion(int arg0)
	{
		
	}
}