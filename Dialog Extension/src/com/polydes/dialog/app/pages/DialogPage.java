package com.polydes.dialog.app.pages;

import com.polydes.dialog.app.editors.text.DialogHighlighter;
import com.polydes.dialog.data.TextSource;
import com.polydes.dialog.data.stores.Dialog;

public class DialogPage extends SourcePage<TextSource>
{
	private static DialogPage _instance;
	
	private DialogPage()
	{
		super(TextSource.class, Dialog.get());
		
		setListEditEnabled(true);
		folderModel.setUniqueLeafNames(true);
		textAreaHighlighter = new DialogHighlighter();
	}

	public static DialogPage get()
	{
		if (_instance == null)
			_instance = new DialogPage();

		return _instance;
	}

	public static void dispose()
	{
		_instance = null;
	}
}