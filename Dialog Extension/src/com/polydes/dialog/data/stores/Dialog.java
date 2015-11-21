package com.polydes.dialog.data.stores;

import java.io.File;

import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.DefaultViewableBranch;
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
		setDirty(false);
	}
	
	public void load(DefaultBranch f, TextObject o)
	{
		if(o instanceof TextFolder)
		{
			DefaultBranch newFolder = new DefaultViewableBranch(o.name);
			for(TextObject object : ((TextFolder) o).parts.values())
				load(newFolder, object);
			f.addItem(newFolder);
		}
		else if(o instanceof TextSection)
		{
			TextSource source = new TextSource(o.name);
			source.setUserData(((TextSection) o).parts);
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
			for(DefaultLeaf item : getItems())
				save(item, toWrite);
			Text.writeSectionedText(file, toWrite, "#");
		}
		setDirty(false);
	}
	
	public void save(DefaultLeaf item, TextFolder f)
	{
		if(item instanceof DefaultBranch)
		{
			TextFolder newFolder = new TextFolder(item.getName());
			for(DefaultLeaf d : ((DefaultBranch) item).getItems())
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
