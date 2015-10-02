package com.polydes.paint.app.editors.image.tools;

import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;

import com.polydes.paint.app.editors.image.DrawArea;
import com.polydes.paint.app.editors.image.ImageUtils;
import com.polydes.paint.app.editors.image.ShapeUtils;
import com.polydes.paint.app.utils.DocumentIntFilter;

public class Brush implements Tool
{
	private DrawArea area;
	private ToolOptions options;
	private JTextField brushSizeInput;
	private int brushSize;
	
	private Point p;
	private Point[] cachedPoints;

	public Brush()
	{
		options = new ToolOptions();
		brushSizeInput = new JTextField();
		brushSizeInput.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				brushSizeInputUpdate();
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				brushSizeInputUpdate();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				brushSizeInputUpdate();
			}
		});
		((PlainDocument) brushSizeInput.getDocument()).setDocumentFilter(new DocumentIntFilter(1, 999));
		
		brushSizeInput.setText("3");
		brushSizeInput.setColumns(4);
		brushSizeInput.setMaximumSize(brushSizeInput.getPreferredSize());
		brushSizeInputUpdate();
		
		options.add(brushSizeInput);
		options.glue();
		
		p = new Point(0, 0);
	}
	
	public void brushSizeInputUpdate()
	{
		try
		{
			brushSize = Integer.parseInt(brushSizeInput.getText());
			int o = - ((brushSize - 1) / 2);
			ShapeUtils.clip(o, o, brushSize, brushSize);
			cachedPoints = ShapeUtils.getFilledEllipse(o, o, o + brushSize, o + brushSize);
		}
		catch(NumberFormatException e)
		{
			
		}
	}
	
	@Override
	public void setArea(DrawArea area)
	{
		this.area = area;
	}

	@Override
	public void press(int x, int y)
	{
		ShapeUtils.clip(0, 0, area.img.getWidth(), area.img.getHeight());
		ImageUtils.drawPoints(area.img, ShapeUtils.getClipped(ShapeUtils.translatePoints(cachedPoints, x, y)), area.currentRGB);
		int offset = ((brushSize - 1) / 2);
		area.repaint(x - offset, y - offset, brushSize + 1, brushSize + 1);
		area.setDirty();
	}

	@Override
	public void drag(int x, int y)
	{
		ShapeUtils.clip(0, 0, area.img.getWidth(), area.img.getHeight());
		
		if(Math.abs(area.lastUpdated.x - x) + Math.abs(area.lastUpdated.y - y) >= 2)
		{
			for(Point p : ShapeUtils.getLine(area.lastUpdated.x, area.lastUpdated.y, x, y))
			{
				ImageUtils.drawPoints(area.img, ShapeUtils.getClipped(ShapeUtils.translatePoints(cachedPoints, p.x, p.y)), area.currentRGB);
			}
			
			int offset = ((brushSize - 1) / 2);
			
			int x1 = Math.min(area.lastUpdated.x, x) - offset;
			int y1 = Math.min(area.lastUpdated.y, y) - offset;
			int w = Math.max(area.lastUpdated.x, x) - x1 + 1 + offset;
			int h = Math.max(area.lastUpdated.y, y) - y1 + 1 + offset;
			
			area.repaint(x1, y1, w + 2, h + 2);
		}
		else
		{
			ImageUtils.drawPoints(area.img, ShapeUtils.getClipped(ShapeUtils.translatePoints(cachedPoints, x, y)), area.currentRGB);
			int offset = ((brushSize - 1) / 2);
			area.repaint(x - offset, y - offset, brushSize + 1, brushSize + 1);
		}
		
		area.setDirty();
	}
	
	public void brushAt(int x, int y)
	{
		
	}

	@Override
	public void release(int x, int y)
	{
	}

	@Override
	public void enter(int x, int y)
	{
	}

	@Override
	public void exit(int x, int y)
	{
	}
	
	@Override
	public void render(Graphics g, int x, int y, int w, int h)
	{
		
	}

	@Override
	public void move(int x, int y)
	{
		p.x = x;
		p.y = y;
	}
	
	@Override
	public ToolOptions getOptions()
	{
		return options;
	}
}
