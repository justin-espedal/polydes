package stencyl.ext.polydes.paint.app.pages;

import stencyl.ext.polydes.common.ui.darktree.SelectionType;
import stencyl.ext.polydes.paint.app.editors.bitmapfont.BMFontEditPane;
import stencyl.ext.polydes.paint.app.editors.bitmapfont.FontDrawArea;
import stencyl.ext.polydes.paint.data.BitmapFont;
import stencyl.ext.polydes.paint.data.Folder;

public class BitmapFontPage extends BasicPage
{
	private BMFontEditPane editorPane;
	private FontDrawArea currentEditor;
	
	protected BitmapFontPage(Folder rootFolder)
	{
		super(rootFolder);
		
		editorPane = new BMFontEditPane();
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
			BitmapFont toEdit = (BitmapFont) selectionState.nodes.get(0).getUserObject();
			
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
