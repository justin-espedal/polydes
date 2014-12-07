package stencyl.ext.polydes.paint.app.editors.image.tools;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ToolOptions extends JPanel
{
	public ToolOptions()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	public void glue()
	{
		add(Box.createVerticalGlue());
	}
}
