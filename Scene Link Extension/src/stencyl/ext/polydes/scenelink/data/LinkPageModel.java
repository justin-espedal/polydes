package stencyl.ext.polydes.scenelink.data;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;

public class LinkPageModel extends DataModel
{
	@ModelProperty(order=0, label="ID", editable=false)
	private int id;
	@ModelProperty(order=1, label="Name", refresh=false)
	private String name;
	@ModelProperty(order=2, label="Description",  type="LargeString", refresh=false)
	private String desc;
	@ModelProperty(order=3, label="Size")
	private Dimension size;
	@ModelProperty(order=4, label="Background", type="ImageReference")
	private String bgImage;
	@ModelProperty(order=5, label="Background Pos")
	private Point bgImagePos;
	@ModelProperty(order=6, label="Page Color")
	private Color bgColor;
	@ModelProperty(order=7, label="Grid Offset")
	private Point gridOffset;
	@ModelProperty(order=8, label="Grid Cell Size")
	private Dimension gridSize;
	@ModelProperty(order=9, label="Grid Color")
	private Color gridColor;
	@ModelProperty(order=10, label="Align to Grid")
	private boolean autoAlign;
	@ModelProperty(order=11, label="Coordinate Table")
	private boolean viewTable;

	private HashMap<Integer, LinkModel> links;

	public LinkPageModel()
	{
		
	}
	
	public LinkPageModel(int id, String name, String desc, Dimension size,
			String bgImage, Point bgImagePos, Color bgColor,
			Point gridOffset, Dimension gridSize, Color gridColor,
			boolean autoAlign, boolean viewTable,
			HashMap<Integer, LinkModel> links)
	{
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.size = size;
		this.bgImage = bgImage;
		this.bgImagePos = bgImagePos;
		this.bgColor = bgColor;
		this.gridOffset = gridOffset;
		this.gridSize = gridSize;
		this.gridColor = gridColor;
		this.autoAlign = autoAlign;
		this.viewTable = viewTable;
		this.links = links;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	public Dimension getSize()
	{
		return size;
	}

	public void setSize(Dimension size)
	{
		this.size = size;
	}

	public String getBgImage()
	{
		return bgImage;
	}

	public void setBgImage(String bgImage)
	{
		this.bgImage = bgImage;
	}

	public Point getBgImagePos()
	{
		return bgImagePos;
	}

	public void setBgImagePos(Point bgImagePos)
	{
		this.bgImagePos = bgImagePos;
	}

	public Color getBgColor()
	{
		return bgColor;
	}

	public void setBgColor(Color bgColor)
	{
		this.bgColor = bgColor;
	}

	public Point getGridOffset()
	{
		return gridOffset;
	}

	public void setGridOffset(Point gridOffset)
	{
		this.gridOffset = gridOffset;
	}

	public Dimension getGridSize()
	{
		return gridSize;
	}

	public void setGridSize(Dimension gridSize)
	{
		this.gridSize = gridSize;
	}

	public Color getGridColor()
	{
		return gridColor;
	}

	public void setGridColor(Color gridColor)
	{
		this.gridColor = gridColor;
	}

	public boolean isAutoAlign()
	{
		return autoAlign;
	}

	public void setAutoAlign(boolean autoAlign)
	{
		this.autoAlign = autoAlign;
	}

	public boolean isViewTable()
	{
		return viewTable;
	}

	public void setViewTable(boolean viewTable)
	{
		this.viewTable = viewTable;
	}

	public int getId()
	{
		return id;
	}

	public HashMap<Integer, LinkModel> getLinks()
	{
		return links;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
