import java.util.*;

class Debug
{
	public static void main (String[] args) throws java.lang.Exception
	{
		//String tree = "[[[x,y],[x,y],[x,y]]:Point,42:Int]";
		String tree = "[i,,,5,,,3,,,4,,3,,,,,]";
		System.out.println(Debug.readTree(tree));
	}
	
	public static BranchNode readTree(String s)
	{
		BranchNode resultBranch = null;
		
		BranchNode curBranch = null;
		Node curNode = null;
		Stack<BranchNode> branches = new Stack<BranchNode>();
		for(int i = 0; i < s.length(); ++i)
		{
			if(s.charAt(i) == '[')
			{
				branches.push(curBranch = new BranchNode(null));
			}
			else if(s.charAt(i) == ']')
			{
				if(curNode != null)
				{
					curNode.data = s.substring((Integer) curNode.data, i).trim();
					curBranch.add(curNode);
					curNode = null;
				}
				
				BranchNode finishedBranch = branches.pop();
				if(branches.isEmpty())
				{
					curBranch = null;
					if(s.length() > i)
						finishedBranch.data = s.substring(i + 1).trim();
					resultBranch = finishedBranch;
					break;
				}
				else
					curBranch = branches.peek();
				if(s.length() >= i && s.charAt(i + 1) == ':')
				{
					++i;
					curNode = finishedBranch;
					curNode.data = (i + 1);
				}
				else
					curBranch.add(finishedBranch);
			}
			else if(s.charAt(i) == ',')
			{
				if(curNode != null)
				{
					curNode.data = s.substring((Integer) curNode.data, i).trim();
					curBranch.add(curNode);
					curNode = null;
				}
			}
			else
			{
				if(curNode == null)
					curNode = new Node(i);
			}
		}
		
		return resultBranch;
	}
}

class Node
{
	public BranchNode parent;
	public Object data;
	
	public Node(Object data)
	{
		this.data = data;
	}
	
	/*
	 * Typically used only after typechecking data.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get()
	{
		return (T) data;
	}

	public Node copy()
	{
		return new Node(data);
	}

	public void dispose()
	{
		parent = null;
		data = null;
	}
	
	@Override
	public String toString()
	{
		return "" + data;
	}
}

class BranchNode extends Node
{
	public ArrayList<Node> children;
	
	public BranchNode(Object data)
	{
		super(data);
		children = new ArrayList<Node>();
	}
	
	public boolean has(Node n)
	{
		return children.contains(n);
	}
	
	public void add(Node n)
	{
		n.parent = this;
		children.add(n);
	}
	
	public void add(Node n, int i)
	{
		n.parent = this;
		children.add(i, n);
	}
	
	public void remove(Node n)
	{
		n.parent = null;
		children.remove(n);
	}
	
	public BranchNode copy()
	{
		BranchNode newNode = new BranchNode(data);
		for(Node n : children)
			newNode.add(n.copy());
		return newNode;
	}

	@Override
	public void dispose()
	{
		super.dispose();
		for(Node n : children)
			n.dispose();
		children.clear();
		children = null;
	}
	
	@Override
	public String toString()
	{
		String s = "[";
		for(Node n : children)
			s += n + ", ";
		s += "]:" + data;
		
		return s;
	}
}