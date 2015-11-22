package com.polydes.dialog.data;

import java.util.ArrayList;

import javax.swing.JPanel;

import com.polydes.common.nodes.DefaultEditableLeaf;
import com.polydes.common.ui.object.EditableObject;
import com.polydes.dialog.app.editors.text.BasicHighlighter;
import com.polydes.dialog.app.editors.text.DialogHighlighter;
import com.polydes.dialog.app.editors.text.Highlighter;
import com.polydes.dialog.app.editors.text.TextArea;

public class TextSource extends DefaultEditableLeaf
{
	public static final Highlighter basicHighlighter = new BasicHighlighter();
	public static final Highlighter dialogHighlighter = new DialogHighlighter();
	
	public class EditableText extends EditableObject
	{
		private ArrayList<String> lines;
		
		public EditableText(ArrayList<String> lines)
		{
			this.lines = lines;
		}
		
		TextArea editor;
		
		@Override
		public JPanel getEditor()
		{
			if(editor == null)
				editor = new TextArea(TextSource.this, dialogHighlighter);
			
			return editor;
		}
		
		public void updateLines()
		{
			if(editor != null)
				lines = editor.getLines();
		}
		
		@Override
		public void revertChanges()
		{
			
		}
		
		@Override
		public boolean fillsViewHorizontally()
		{
			return true;
		}
		
		@Override
		public void disposeEditor()
		{
			
		}
	}
	
	public TextSource(String name, ArrayList<String> lines)
	{
		super(name, null);
		setUserData(new EditableText(lines));
	}
	
	public void trimLeadingTailingNewlines()
	{
		ArrayList<String> lines = ((EditableText) getUserData()).lines;
		
		for(int i = 0; i < lines.size(); ++i)
		{
			if(lines.get(i).isEmpty())
				lines.remove(i);
			else
				break;
		}
		for(int i = lines.size() - 1; i >= 0; --i)
		{
			if(lines.get(i).isEmpty())
				lines.remove(i);
			else
				break;
		}
	}
	
	public ArrayList<String> getLines()
	{
		return ((EditableText) getUserData()).lines;
	}
	
	public void updateLines()
	{
		((EditableText) getUserData()).updateLines();
	}

	public void addLine(String line)
	{
		((EditableText) getUserData()).lines.add(line);
	}
	
	@Override
	public boolean fillsViewHorizontally()
	{
		return true;
	}
}
