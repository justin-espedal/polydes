package stencyl.ext.polydes.points;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import stencyl.core.lib.Game;
import stencyl.ext.polydes.common.ext.GameExtension;
import stencyl.ext.polydes.points.app.PointEditorPage;
import stencyl.ext.polydes.points.app.PointEditorWindow;
import stencyl.ext.polydes.points.res.Resources;
import stencyl.sw.ext.OptionsPanel;

public class Main extends GameExtension
{
	private static Main instance;
	
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
		
		icon = Resources.loadIcon("icon.png");
		
		instance = this;
		
		name = "Points Extension";
		description = "Visually select points on an actor.";
		authorName = "Polydes Extensions";
		website = "https://github.com/justin-espedal/polydes";
		internalVersion = 1;
		version = "1.0.0";
		
		isInMenu = true;
		menuName = "Points Extension";
	}
	
	public static Main get()
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
		System.out.println("SampleExtension : Destroyed");
	}
	
	
	/*
	 * Happens when the user requests the Options dialog for your extension.
	 * 
	 * You need to provide the form. We wrap it in a dialog.
	 */
	@Override
	public OptionsPanel onOptions()
	{
		System.out.println("SampleExtension : Options");
		
		return new OptionsPanel()
		{
			JTextField text;
			JCheckBox check;
			JComboBox dropdown;
			
			/*
			 * Construct the form.
			 * 
			 * We provide a simple way to construct forms without
			 * knowing Swing (Java's GUI library).
			 */
			@Override
			public void init()
			{
				startForm();
				addHeader("Options");
				text = addTextfield("Name:");
				check = addCheckbox("Do you like chocolate?");
				dropdown = addDropdown("Where are you from?", new String[] {"Americas", "Europe", "Asia", "Other"});
				endForm();
				
				//Set the form's values
				text.setText("" + properties.get("name"));
				check.setSelected(Boolean.parseBoolean("" + properties.get("choc")));
				dropdown.setSelectedItem(properties.get("loc"));
			}
			
			/*
			 * Use this to save the form data out.
			 * All you need to do is place the properties into preferences.
			 */
			@Override
			public void onPressedOK()
			{
				properties.put("name", text.getText());
				properties.put("choc", check.isSelected());
				properties.put("loc", dropdown.getSelectedItem());
			}

			@Override
			public void onPressedCancel()
			{
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
		System.out.println("SampleExtension : Install");
	}
	
	/*
	 * Happens when the extension is uninstalled.
	 * 
	 * Clean up files.
	 */
	@Override
	public void onUninstall()
	{
		System.out.println("SampleExtension : Uninstall");
	}

	@Override
	public JPanel getMainPage()
	{
		return null;
	}

	@Override
	public boolean isInstalledForGame(Game game)
	{
		return true;
	}

	@Override
	public void onInstalledForGame(Game game)
	{
		
	}

	@Override
	public void onUninstalledForGame(Game game)
	{
		
	}

	@Override
	public void onGameWithDataOpened(Game game)
	{
		
	}

	@Override
	public void onGameWithDataSaved(Game game)
	{
		PointEditorPage.save();
	}

	@Override
	public void onGameWithDataClosed(Game game)
	{
		PointEditorWindow.disposeWindow();
		PointEditorPage.dispose();
	}

	@Override
	public void updateFromVersion(Game game, int fromVersion)
	{
		
	}
}