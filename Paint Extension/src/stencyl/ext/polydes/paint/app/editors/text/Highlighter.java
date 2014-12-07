package stencyl.ext.polydes.paint.app.editors.text;

import javax.swing.text.StyledDocument;

public interface Highlighter
{
	public void update(StyledDocument doc, int offset, int length);
}
