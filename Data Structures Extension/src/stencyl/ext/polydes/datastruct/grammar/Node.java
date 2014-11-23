package stencyl.ext.polydes.datastruct.grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node
{
	public LangElement type;
	public Object data;
	
//	private Node parent;
	public List<Node> children;
	
	public Node(LangElement type)
	{
		this.type = type;
	}
	
	public Node(LangElement type, Object data)
	{
		this.type = type;
		this.data = data;
		if(data == null)
			children = new ArrayList<Node>();
	}
	
	public Node(LangElement type, Node... children)
	{
		this.type = type;
		this.children = Arrays.asList(children);
	}
	
	public int getNumChildren()
	{
		return children.size();
	}
	
//	public void add(Node child)
//	{
//		if(child.parent != null)
//			child.parent.remove(child);
//		
//		children.add(child);
//		child.parent = this;
//	}
//	
//	public void remove(Node child)
//	{
//		children.remove(child);
//		child.parent = null;
//	}
	
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