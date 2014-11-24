package stencyl.ext.polydes.dialog.app.editors.text;

import javax.swing.text.StyledDocument;

public interface Highlighter
{
	public void update(StyledDocument doc, int offset, int length);
}
