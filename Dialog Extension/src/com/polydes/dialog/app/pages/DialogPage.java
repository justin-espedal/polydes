package com.polydes.dialog.app.pages;

import com.polydes.dialog.data.TextSource;
import com.polydes.dialog.data.stores.Dialog;

public class DialogPage extends SourcePage<TextSource>
{
	private static DialogPage _instance;
	
	private DialogPage()
	{
		super(Dialog.get().getFolderModel());
		
		treePage.getTree().setListEditEnabled(true);
		treePage.getFolderModel().setUniqueLeafNames(true);
	}

	public static DialogPage get()
	{
		if (_instance == null)
			_instance = new DialogPage();

		return _instance;
	}

	public static void disposeInstance()
	{
		_instance = null;
	}
}