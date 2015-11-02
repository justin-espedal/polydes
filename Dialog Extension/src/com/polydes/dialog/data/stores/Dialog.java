package com.polydes.dialog.data.stores;

import java.io.File;

import com.polydes.dialog.data.DataItem;
import com.polydes.dialog.data.Folder;
import com.polydes.dialog.data.TextSource;
import com.polydes.dialog.io.Text;
import com.polydes.dialog.io.Text.TextFolder;
import com.polydes.dialog.io.Text.TextObject;
import com.polydes.dialog.io.Text.TextSection;

public class Dialog extends TextStore
{
	private static Dialog _instance;
	
	private Dialog()
	{
		super("Dialog");
	}
	
	public static Dialog get()
	{
		if(_instance == null)
			_instance = new Dialog();
		
		return _instance;
	}
	
	@Override
	public void load(File file)
	{
		TextFolder root = Text.readSectionedText(file, "#");
		for(TextObject object : root.parts.values())
			load(this, object);
		setClean();
	}
	
	public void load(Folder f, TextObject o)
	{
		if(o instanceof TextFolder)
		{
			Folder newFolder = new Folder(o.name);
			for(TextObject object : ((TextFolder) o).parts.values())
				load(newFolder, object);
			f.addItem(newFolder);
		}
		else if(o instanceof TextSection)
		{
			TextSource source = new TextSource(o.name);
			source.setContents(((TextSection) o).parts);
			f.addItem(source);
		}
	}
	
	@Override
	public void saveChanges(File file)
	{
		updateItem(this);
		if(isDirty())
		{
			TextFolder toWrite = new TextFolder("root");
			for(DataItem item : getItems())
				save(item, toWrite);
			Text.writeSectionedText(file, toWrite, "#");
		}
		setClean();
	}
	
	public void save(DataItem item, TextFolder f)
	{
		if(item instanceof Folder)
		{
			TextFolder newFolder = new TextFolder(item.getName());
			for(DataItem d : ((Folder) item).getItems())
				save(d, newFolder);
			f.add(newFolder);
		}
		else if(item instanceof TextSource)
		{
			TextSource source = (TextSource) item;
			TextSection newSection = new TextSection(item.getName());
			newSection.parts = source.getLines();
			f.add(newSection);
		}
	}
}
