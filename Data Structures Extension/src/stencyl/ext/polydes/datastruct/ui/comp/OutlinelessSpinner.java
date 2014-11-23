package stencyl.ext.polydes.datastruct.ui.comp;

import java.awt.Graphics;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;

public class OutlinelessSpinner extends JSpinner
{
	JFormattedTextField field;
	
	public OutlinelessSpinner(SpinnerModel model)
	{
		super(model);
		field = ((JSpinner.DefaultEditor) getEditor()).getTextField();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
//		Graphics2D g2d = (Graphics2D) g.create();
//		g2d.setPaint(getBackground());
//		g2d.fillRect(0, 0, getWidth(), getHeight());
//		g2d.dispose();
	}

	@Override
	protected void paintChildren(Graphics g)
	{
		super.paintChildren(g);
		
//		Rectangle r = field.getBounds();
//		g.setColor(getBackground());
//		g.drawRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2);
//		g.drawRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4);
	}
}