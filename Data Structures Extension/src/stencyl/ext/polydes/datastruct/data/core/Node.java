package stencyl.ext.polydes.datastruct.data.core;

import java.util.Arrays;
import java.util.List;

public class Node
{
	public Object data;
	
	private Node parent;
	public List<Node> children;
	
	public Node(Object data)
	{
		this.data = data;
	}
	
	public Node(Node... children)
	{
		this.children = Arrays.asList(children);
	}
	
	public int getNumChildren()
	{
		return children.size();
	}
	
	public void add(Node child)
	{
		if(child.parent != null)
			child.parent.remove(child);
		
		children.add(child);
		child.parent = this;
	}
	
	public void remove(Node child)
	{
		children.remove(child);
		child.parent = null;
	}
	
	public Node get(int i)
	{
		if(i < 0 || i >= children.size())
			return null;
		
		return children.get(i);
	}
	
	@Override
	public String toString()
	{
		return "Node";
	}
}