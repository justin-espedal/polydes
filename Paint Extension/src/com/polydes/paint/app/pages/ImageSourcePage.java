package com.polydes.paint.app.pages;

import com.polydes.common.ui.darktree.SelectionType;
import com.polydes.paint.app.editors.image.DrawArea;
import com.polydes.paint.app.editors.image.ImageEditPane;
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
	public void selectionStateChanged()
	{
		if(selectionState.nodes.size() != 1)
			return;
		
		if(currentEditor == null && (selectionState.type == SelectionType.FOLDERS))
			return;
		
		if(currentEditor != null)
		{
			editorPane.removeDrawArea();
		}
		currentEditor = null;
		
		if(selectionState.type == SelectionType.FOLDERS)
		{
			editorPane.showToolbar(false);
		}
		else
		{
			ImageSource toEdit = (ImageSource) selectionState.nodes.get(0).getUserObject();
			
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
