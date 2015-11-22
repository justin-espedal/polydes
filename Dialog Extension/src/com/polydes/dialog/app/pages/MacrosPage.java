package com.polydes.dialog.app.pages;

import com.polydes.dialog.data.TextSource;
import com.polydes.dialog.data.stores.Macros;

public class MacrosPage extends SourcePage<TextSource>
{
	private static MacrosPage _instance;
	
	private MacrosPage()
	{
		super(Macros.get().getFolderModel());
		
		treePage.getFolderModel().setNodeCreator(null);
		treePage.getTree().setListEditEnabled(false);
	}

	public static MacrosPage get()
	{
		if (_instance == null)
			_instance = new MacrosPage();

		return _instance;
	}

	public static void disposeInstance()
	{
		_instance = null;
	}
}