package com.polydes.dialog.data.stores;

import java.io.File;
import java.util.ArrayList;

import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.dialog.data.TextSource;
import com.polydes.dialog.io.Text;

public class Macros extends TextStore
{
	private static Macros _instance;
	
	private HierarchyModel<DefaultLeaf,DefaultBranch> folderModel;
	
	private Macros()
	{
		super("Macros");
		folderModel = new HierarchyModel<DefaultLeaf, DefaultBranch>(this, DefaultLeaf.class, DefaultBranch.class);
	}
	
	public static Macros get()
	{
		if(_instance == null)
			_instance = new Macros();
		
		return _instance;
	}

	public HierarchyModel<DefaultLeaf, DefaultBranch> getFolderModel()
	{
		return folderModel;
	}
	
	@Override
	public void load(File file)
	{
		TextSource info = new TextSource("-Info-", new ArrayList<>());
		TextSource tags = new TextSource("Tags", new ArrayList<>());
		TextSource characters = new TextSource("Characters", new ArrayList<>());
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
		
		for(DefaultLeaf item : getItems())
			((TextSource) item).trimLeadingTailingNewlines();
		
		setDirty(false);
	}
	
	@Override
	public void saveChanges(File file)
	{
		if(isDirty())
		{
			Text.startWriting(file);
			for(DefaultLeaf item : getItems())
			{
				if(item.isDirty())
					((TextSource) item).updateLines();
				for(String line : ((TextSource) item).getLines())
					Text.writeLine(file, line);
				Text.writeLine(file, "");
			}
			Text.closeOutput(file);
		}
		
		setDirty(false);
	}
}
