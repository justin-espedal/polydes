package stencyl.ext.polydes.dialog.app.editors;

import stencyl.ext.polydes.dialog.app.editors.text.Highlighter;
import stencyl.ext.polydes.dialog.app.editors.text.TextArea;
import stencyl.ext.polydes.dialog.data.LinkedDataItem;
import stencyl.ext.polydes.dialog.data.TextSource;

public class EditorFactory
{
	public static Highlighter textAreaHighlighter;
	
	public static <T extends LinkedDataItem> DataItemEditor getEditor(T item)
	{
		if(item.getEditor() == null)
		{
			if(item instanceof TextSource)
				item.setEditor(new TextArea((TextSource) item, textAreaHighlighter));
		}
		
		return (DataItemEditor) item.getEditor();
	}
}
