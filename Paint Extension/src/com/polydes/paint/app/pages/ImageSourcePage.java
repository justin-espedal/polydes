package com.polydes.paint.app.pages;

import com.polydes.common.nodes.NodeSelection;
import com.polydes.common.nodes.NodeSelectionEvent;
import com.polydes.common.ui.darktree.SelectionType;
import com.polydes.paint.app.editors.image.DrawArea;
import com.polydes.paint.app.editors.image.ImageEditPane;
import com.polydes.paint.data.DataItem;
import com.polydes.paint.data.Folder;
import com.polydes.paint.data.ImageSource;

public class ImageSourcePage extends BasicPage
{
	private ImageEditPane editorPane;
	private DrawArea currentEditor;
	
	protected ImageSourcePage(Folder rootFolder)
	{
		super(rootFolder);
		
		editorPane = new ImageEditPane();
		splitPane.setRightComponent(editorPane);
		currentEditor = null;
	}
	
	@Override
	public void selectionChanged(NodeSelectionEvent<DataItem, Folder> e)
	{
		NodeSelection<DataItem, Folder> selection = folderModel.getSelection();
		
		if(selection.size() != 1)
			return;
		
		if(currentEditor == null && (selection.getType() == SelectionType.FOLDERS))
			return;
		
		if(currentEditor != null)
		{
			editorPane.removeDrawArea();
		}
		currentEditor = null;
		
		if(selection.getType() == SelectionType.FOLDERS)
		{
			editorPane.showToolbar(false);
		}
		else
		{
			ImageSource toEdit = (ImageSource) selection.firstNode();
			
			if(toEdit.getEditor() == null)
				new DrawArea(toEdit);
			
			currentEditor = (DrawArea) toEdit.getEditor();
		}
		
		if (currentEditor != null)
		{
			editorPane.setDrawArea(currentEditor);
		}
		
		revalidate();
		repaint();
	}
}
