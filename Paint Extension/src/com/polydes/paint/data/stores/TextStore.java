package com.polydes.paint.data.stores;

import java.io.File;
import java.util.Stack;

import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.DefaultViewableBranch;
import com.polydes.paint.data.LinkedDataItem;
import com.polydes.paint.data.TextSource;
import com.polydes.paint.io.Text;

public abstract class TextStore extends DefaultViewableBranch
{
	protected TextStore(String name)
	{
		super(name);
	}
	
	public abstract void load(File file);
	public abstract void saveChanges(File file);
	
	protected void titledTextLoad(File file, String titleMarker, String folderStartMarker, String folderEndMarker)
	{
		boolean foldersUsed = !(folderStartMarker.equals("") || folderEndMarker.equals(""));
		
		Stack<DefaultBranch> folderStack = new Stack<DefaultBranch>();
		folderStack.push(this);
		
		TextSource source = new TextSource("...");
		addItem(source);
		String line;
		Text.startReading(file);
		while((line = Text.getNextLine(file)) != null)
		{
			if(foldersUsed && line.startsWith(folderStartMarker))
			{
				folderStack.push(new DefaultViewableBranch(line.substring(folderStartMarker.length())));
			}
			else if(foldersUsed && line.startsWith(folderEndMarker))
			{
				DefaultBranch newFolder = folderStack.pop();
				folderStack.peek().addItem(newFolder);
			}
			else if(line.startsWith(titleMarker))
			{
				String sourceName = line.substring(titleMarker.length());
				source = new TextSource(sourceName);
				folderStack.peek().addItem(source);
			}
			else
				source.addLine(line);
		}
		Text.closeInput(file);
		
		trimItem(this);
		
		source = (TextSource) getItemByName("...");
		if(source.getLines().isEmpty())
			removeItem(source);
		
		setDirty(false);
	}
	
	private void trimItem(DefaultLeaf item)
	{
		if(item instanceof TextSource)
			((TextSource) item).trimLeadingTailingNewlines();
		else if(item instanceof DefaultBranch)
		{
			for(DefaultLeaf curItem : ((DefaultBranch) item).getItems())
			{
				trimItem(curItem);
			}
		}
	}
	
	protected void titledTextSave(File file, String titleMarker, String folderStartMarker, String folderEndMarker)
	{
		updateItem(this);
		
		if(isDirty())
		{
			Text.startWriting(file);
			for(DefaultLeaf item : getItems())
			{
				writeItem(item, file, titleMarker, folderStartMarker, folderEndMarker);
			}
			Text.closeOutput(file);
		}
		
		setDirty(false);
	}
	
	private void updateItem(DefaultLeaf item)
	{
		if(item instanceof LinkedDataItem && item.isDirty())
		{
			((LinkedDataItem) item).updateContents();
			setDirty(true);
		}
		else if(item instanceof DefaultBranch)
		{
			if(item.isDirty())
				setDirty(true);
			
			for(DefaultLeaf curItem : ((DefaultBranch) item).getItems())
			{
				updateItem(curItem);
			}
		}
	}
	
	private void writeItem(DefaultLeaf item, File file, String titleMarker, String folderStartMarker, String folderEndMarker)
	{
		if(item instanceof DefaultBranch)
		{
			Text.writeLine(file, folderStartMarker + item.getName());
			
			for(DefaultLeaf currItem : ((DefaultBranch) item).getItems())
			{
				writeItem(currItem, file, titleMarker, folderStartMarker, folderEndMarker);
			}
			
			Text.writeLine(file, folderEndMarker);
		}
		else if(item instanceof TextSource)
		{
			if(!item.getName().equals("..."))
				Text.writeLine(file, titleMarker + item.getName());
			for(String line : ((TextSource) item).getLines())
				Text.writeLine(file, line);
			Text.writeLine(file, "");
		}
	}
}
