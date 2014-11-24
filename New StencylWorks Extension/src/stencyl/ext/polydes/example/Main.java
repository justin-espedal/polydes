package stencyl.ext.polydes.example;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import stencyl.core.lib.Game;
import stencyl.ext.polydes.example.res.Resources;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.ext.OptionsPanel;

public class Main extends BaseExtension
{
	/*
	 * Happens when StencylWorks launches. 
	 * 
	 * Avoid doing anything time-intensive in here, or it will
	 * slow down launch.
	 */
	public void onStartup()
	{
		super.onStartup();

		icon = Resources.loadIcon("icon.png");
		
		name = "Extension Name";
		description = "Extension Description.";
		authorName = "Author Name";
		website = "http://example.com/";
		internalVersion = 1;
		version = "1.0.0";
		
		isInMenu = true;
		menuName = "Extension Name";
		
		isInGameCenter = true;
		gameCenterName = "Extension Name";
	}
	
	/*
	 * Happens when the extension is told to display.
	 * 
	 * May happen multiple times during the course of the app. 
	 * 
	 * A good way to handle this is to make your extension a singleton.
	 */
	public void onActivate()
	{
		System.out.println("SampleExtension : Activated");
	}
	
	/*
	 * Happens when StencylWorks closes.
	 *  
	 * Usually used to save things out.
	 */
	public void onDestroy()
	{
		System.out.println("SampleExtension : Destroyed");
	}
	
	/*
	 * Happens when a game is saved.
	 */
	public void onGameSave(Game game)
	{
		System.out.println("SampleExtension : Saved");
	}
	
	/*
	 * Happens when a game is opened.
	 */
	public void onGameOpened(Game game)
	{
		System.out.println("SampleExtension : Opened");
	}

	/*
	 * Happens when a game is closed.
	 */
	public void onGameClosed(Game game)
	{
		System.out.println("SampleExtension : Closed");
	}
	
	/*
	 * Happens when the user requests the Options dialog for your extension.
	 * 
	 * You need to provide the form. We wrap it in a dialog.
	 */
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
	public void onInstall()
	{
		System.out.println("SampleExtension : Install");
	}
	
	/*
	 * Happens when the extension is uninstalled.
	 * 
	 * Clean up files.
	 */
	public void onUninstall()
	{
		System.out.println("SampleExtension : Uninstall");
	}
}