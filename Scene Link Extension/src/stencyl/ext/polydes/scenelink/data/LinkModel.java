package stencyl.ext.polydes.scenelink.data;

import java.awt.Color;
import java.awt.Rectangle;

public class LinkModel extends DataModel
{
	@ModelProperty(order=0, label="ID", editable=false)
	private int id;
	@ModelProperty(order=1, label="Label")
	private String label;
	@ModelProperty(order=2, label="Color")
	private Color color;
	@ModelProperty(display=false)
	private Rectangle pos;
	@ModelProperty(order=3, label="Link")
	private Link link;
	
	public LinkModel()
	{
		
	}
	
	public LinkModel(int id, String label, Color color, Rectangle pos, Link link)
	{
		this.id = id;
		this.label = label;
		this.color = color;
		this.pos = pos;
		this.link = link;
	}
	
	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public Rectangle getPos()
	{
		return pos;
	}

	public void setPos(Rectangle pos)
	{
		this.pos = pos;
	}

	public Link getLink()
	{
		return link;
	}

	public void setLink(Link link)
	{
		this.link = link;
	}

	public int getId()
	{
		return id;
	}
}
