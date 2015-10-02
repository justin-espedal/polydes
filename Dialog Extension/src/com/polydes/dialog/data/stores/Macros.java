package com.polydes.dialog.data.stores;

import java.io.File;

import com.polydes.common.nodes.Leaf;
import com.polydes.dialog.data.DataItem;
import com.polydes.dialog.data.LinkedDataItem;
import com.polydes.dialog.data.TextSource;
import com.polydes.dialog.io.Text;

public class Macros extends TextStore
{
	private static Macros _instance;
	
	private Macros()
	{
		super("Macros");
	}
	
	public static Macros get()
	{
		if(_instance == null)
			_instance = new Macros();
		
		return _instance;
	}
	
	@Override
	public void load(File file)
	{
		TextSource info = new TextSource("-Info-");
		TextSource tags = new TextSource("Tags");
		TextSource characters = new TextSource("Characters");
		addItem(info);
		addItem(tags);
		addItem(characters);
		
		for(String line : Text.readLines(file))
		{
			if(line.startsWith("{"))
				tags.addLine(line);
			else if(line.startsWith("!"))
				characters.addLine(line);
			else
				info.addLine(line);
		}
		
		for(Leaf<DataItem> item : getItems())
		{
			((TextSource) item).trimLeadingTailingNewlines();
		}
		
		setClean();
	}
	
	@Override
	public void saveChanges(File file)
	{
		for(Leaf<DataItem> item : getItems())
		{
			if(item.isDirty())
			{
				if(item instanceof LinkedDataItem)
					((LinkedDataItem) item).updateContents();
				
				setDirty();
			}
		}
		
		if(isDirty())
		{
			Text.startWriting(file);
			for(Leaf<DataItem> item : getItems())
			{
				for(String line : ((TextSource) item).getLines())
					Text.writeLine(file, line);
				Text.writeLine(file, "");
			}
			Text.closeOutput(file);
		}
		
		setClean();
	}
}
