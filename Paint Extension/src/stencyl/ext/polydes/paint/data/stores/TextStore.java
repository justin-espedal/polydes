package stencyl.ext.polydes.paint.data.stores;

import java.io.File;
import java.util.Stack;

import stencyl.ext.polydes.paint.data.DataItem;
import stencyl.ext.polydes.paint.data.Folder;
import stencyl.ext.polydes.paint.data.LinkedDataItem;
import stencyl.ext.polydes.paint.data.TextSource;
import stencyl.ext.polydes.paint.io.Text;

public abstract class TextStore extends Folder
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
		
		Stack<Folder> folderStack = new Stack<Folder>();
		folderStack.push(this);
		
		TextSource source = new TextSource("...");
		addItem(source);
		String line;
		Text.startReading(file);
		while((line = Text.getNextLine(file)) != null)
		{
			if(foldersUsed && line.startsWith(folderStartMarker))
			{
				folderStack.push(new Folder(line.substring(folderStartMarker.length())));
			}
			else if(foldersUsed && line.startsWith(folderEndMarker))
			{
				Folder newFolder = folderStack.pop();
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
		
		setClean();
	}
	
	private void trimItem(DataItem item)
	{
		if(item instanceof TextSource)
			((TextSource) item).trimLeadingTailingNewlines();
		else if(item instanceof Folder)
		{
			for(DataItem curItem : ((Folder) item).getItems())
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
			for(DataItem item : getItems())
			{
				writeItem(item, file, titleMarker, folderStartMarker, folderEndMarker);
			}
			Text.closeOutput(file);
		}
		
		setClean();
	}
	
	private void updateItem(DataItem item)
	{
		if(item instanceof LinkedDataItem && item.isDirty())
		{
			((LinkedDataItem) item).updateContents();
			setDirty();
		}
		else if(item instanceof Folder)
		{
			if(item.isDirty())
				setDirty();
			
			for(DataItem curItem : ((Folder) item).getItems())
			{
				updateItem(curItem);
			}
		}
	}
	
	private void writeItem(DataItem item, File file, String titleMarker, String folderStartMarker, String folderEndMarker)
	{
		if(item instanceof Folder)
		{
			Text.writeLine(file, folderStartMarker + item.getName());
			
			for(DataItem currItem : ((Folder) item).getItems())
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
