package stencyl.ext.polydes.datastruct.ui.page;

import javax.swing.JPanel;

import stencyl.ext.polydes.datastruct.data.folder.EditableObject;
import stencyl.ext.polydes.datastruct.ui.utils.LightweightWindow;
import stencyl.sw.lnf.Theme;
import stencyl.sw.util.UI;

public class PropertiesWindow extends LightweightWindow
{
	/*-------------------------------------*\
	 * Globals
	\*-------------------------------------*/
	
	private static PropertiesWindow instance;

	/*-------------------------------------*\
	 * Constructor
	\*-------------------------------------*/

	public static PropertiesWindow get()
	{
		if(instance == null)
			instance = new PropertiesWindow();
		
		return instance;
	}
	
	public static void showWindow()
	{
		get().setVisible(true);
	}
	
	public static void hideWindow()
	{
		get().setVisible(false);
	}

	private PropertiesWindow()
	{
		super(StructureDefinitionsWindow.get());
		setBackground(Theme.LIGHT_BUTTON_BAR_START);
		setContents(createContents());
	}

	public static void disposeWindow()
	{
		if(instance != null)
			instance.dispose();
		instance = null;
	}
	
	/*-------------------------------------*\
	 * Construct UI
	\*-------------------------------------*/
	
	private static EditableObject toEdit;
	
	public JPanel createContents()
	{
		if(toEdit != null)
			return toEdit.getEditor();
		else
			return new JPanel();
	}
	
	public static void setObject(EditableObject toEdit)
	{
		if(PropertiesWindow.toEdit != null)
		{
			PropertiesWindow.toEdit.disposeEditor();
			
			//TODO: Get setting dirty back in
			//would be better to only set dirty if a change has happened
			//((GuiStructure) currentStructure).sheet.model.getTemplate().setDirty();
		}
		
		PropertiesWindow.toEdit = toEdit;
		instance.setContents(instance.createContents());
		
		instance.validate();
		instance.repaint();
	}
	
	@Override
	public void submit()
	{
		if(toEdit != null)
		{
			toEdit.disposeEditor();
			
			//TODO: Get setting dirty back in
			//would be better to only set dirty if a change has happened
			//((GuiStructure) currentStructure).sheet.model.getTemplate().setDirty();
		}
		
		toEdit = null;
		
		super.submit();
	}
	
	@Override
	public void cancel()
	{
		int result =
			UI.showYesCancelPrompt(
				"Discard Changes",
				"Are you sure you'd like to discard changes?"
			);
		
		if(UI.choseYes(result))
		{
			if(toEdit != null)
			{
				toEdit.revertChanges();
				toEdit.disposeEditor();
			}
			
			toEdit = null;
			
			super.cancel();
		}
	}

	@Override
	protected boolean verify()
	{
		boolean result = true;
		
		okButton.setEnabled(result);
		
		return result;
	}
}