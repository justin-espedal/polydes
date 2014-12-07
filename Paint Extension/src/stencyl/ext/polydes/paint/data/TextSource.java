package stencyl.ext.polydes.paint.data;

import java.util.ArrayList;

public class TextSource extends LinkedDataItem
{
	private ArrayList<String> lines;
	
	public TextSource(String name)
	{
		super(name);
		contents = lines = new ArrayList<String>();
	}
	
	//for initial reading in, does not mark as changed
	public void addLine(String line)
	{
		lines.add(line);
	}
	
	public void trimLeadingTailingNewlines()
	{
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
		return lines;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setContents(Object o)
	{
		if(o instanceof ArrayList)
		{
			lines = (ArrayList<String>) o;
			super.setContents(o);
		}
	}
}
