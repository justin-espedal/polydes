package com.polydes.datastruct.grammar;

import java.util.ArrayList;

import com.polydes.datastruct.data.core.Node;

public class SyntaxNode extends Node
{
	public LangElement type;
	
	public SyntaxNode(LangElement type)
	{
		this.type = type;
	}
	
	public SyntaxNode(LangElement type, Object data)
	{
		this.type = type;
		this.data = data;
		if(data == null)
			children = new ArrayList<Node>();
	}
	
	public SyntaxNode(LangElement type, SyntaxNode... children)
	{
		super(children);
		this.type = type;
	}
	
	@Override
	public SyntaxNode get(int i)
	{
		if(i < 0 || i >= children.size())
			return null;
		
		return (SyntaxNode) children.get(i);
	}
}