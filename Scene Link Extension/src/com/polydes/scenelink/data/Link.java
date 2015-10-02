package com.polydes.scenelink.data;

public class Link
{
	protected int id;
	
	public static Link createBlank()
	{
		return new Link(-1);
	}
	
	public static Link create(String refType, int refID)
	{
		if(refType.equals("Scene"))
			return new SceneLink(refID);
		else if(refType.equals("Page"))
			return new PageLink(refID);
		else
			return createBlank();
	}
	
	protected Link(int id)
	{
		this.id = id;
	}
	
	public Object getModel()
	{
		return null;
	}
	
	public void open()
	{
		
	}

	public static String getStringRef(Link l)
	{
		if(l instanceof SceneLink)
			return "Scene " + l.id;
		else if(l instanceof PageLink)
			return "Page " + l.id;
		else
			return "";
	}
}
