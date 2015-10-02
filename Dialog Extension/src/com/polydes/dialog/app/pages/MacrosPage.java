package com.polydes.dialog.app.pages;

import com.polydes.dialog.app.editors.text.PreferenceHighlighter;
import com.polydes.dialog.data.TextSource;
import com.polydes.dialog.data.stores.Macros;

public class MacrosPage extends SourcePage<TextSource>
{
	private static MacrosPage _instance;
	
	private MacrosPage()
	{
		super(TextSource.class, Macros.get());
		
		tree.setNodeCreator(null);
		setListEditEnabled(false);
		textAreaHighlighter = new PreferenceHighlighter();
	}

	public static MacrosPage get()
	{
		if (_instance == null)
			_instance = new MacrosPage();

		return _instance;
	}

	public static void dispose()
	{
		_instance = null;
	}
}