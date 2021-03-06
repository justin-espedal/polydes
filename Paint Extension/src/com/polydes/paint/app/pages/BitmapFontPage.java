package com.polydes.paint.app.pages;

import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.NodeSelection;
import com.polydes.common.nodes.NodeSelectionEvent;
import com.polydes.common.ui.darktree.SelectionType;
import com.polydes.paint.app.editors.bitmapfont.BMFontEditPane;
import com.polydes.paint.app.editors.bitmapfont.FontDrawArea;
import com.polydes.paint.data.BitmapFont;

public class BitmapFontPage extends BasicPage
{
	private BMFontEditPane editorPane;
	private FontDrawArea currentEditor;
	
	protected BitmapFontPage(DefaultBranch rootFolder)
	{
		super(rootFolder);
		
		editorPane = new BMFontEditPane();
		splitPane.setRightComponent(editorPane);
		currentEditor = null;
	}
	
	@Override
	public void selectionChanged(NodeSelectionEvent<DefaultLeaf, DefaultBranch> e)
	{
		NodeSelection<DefaultLeaf, DefaultBranch> selection = folderModel.getSelection();
		
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
			BitmapFont toEdit = (BitmapFont) selection.firstNode();
			
			if(toEdit.getEditor() == null)
				new FontDrawArea(toEdit);
			
			currentEditor = (FontDrawArea) toEdit.getEditor();
		}
		
		if (currentEditor != null)
		{
			editorPane.setDrawArea(currentEditor);
		}
		
		revalidate();
		repaint();
	}
}
