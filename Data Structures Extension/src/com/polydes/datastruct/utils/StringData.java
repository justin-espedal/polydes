package com.polydes.datastruct.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.apache.commons.lang3.reflect.MethodUtils;

import com.polydes.datastruct.data.types.DataType;

public class StringData
{
	public static <T> T getStringAs(String s, DataType<T> type, T defaultValue)
	{
		if(s == null)
			return defaultValue;
		else
			return type.decode(s);
	}
	
	//first item returned is the type of the array.
	public static ArrayList<String> getEmbeddedArrayStrings(String s)
	{
		ArrayList<String> a = new ArrayList<String>();
		
		int i = s.lastIndexOf(":");
		
		char ch;
		int k = 0;
		ArrayList<Integer> commas = new ArrayList<Integer>();
		for(int j = 1; j < i; ++j)
		{
			ch = s.charAt(j);
			if(ch == '[')
				++k;
			else if(ch == ']')
				--k;
			else if(ch == ',' && k == 0)
				commas.add(j);
		}
		
		int lastComma = 0;
		for(int comma : commas)
		{
			a.add(s.substring(lastComma + 1, comma));
			lastComma = comma;
		}
		a.add(s.substring(lastComma + 1, i - 1));
		
		return a;
	}
	
	/*
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
						finishedBranch.data = s.substring(i + 2).trim();
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
	*/
	
	public static int[] getInts(String fromString)
	{
		if(fromString.length() == 0)
			return null;
		
		//Split here for backwards compatibility.
		String[] splitString = fromString.startsWith("[") ? 
				fromString.substring(1, fromString.length() - 1).split(",") :
				fromString.split(",");
		
		int[] toReturn = new int[splitString.length];
		for(int i = 0; i < splitString.length; ++i)
		{
			try
			{
				toReturn[i] = Integer.parseInt(splitString[i].trim());
			}
			catch(NumberFormatException ex)
			{
				toReturn[i] = 0;
			}
		}
		return toReturn;
	}
	
	public static ArrayList<String> rArrayList(String s)
	{
		return rArrayList(s, String.class);
	}
	
	public static String wrArrayList(ArrayList<String> a)
	{
		return wrArrayList(a, String.class);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> rArrayList(String s, Class<T> gen)
	{
		ArrayList<T> toReturn = new ArrayList<T>();
		
		String[] elements = s.split(",");
		for(String s2 : elements)
		{
			try
			{
				T val = (T) MethodUtils.invokeStaticMethod(StringData.class, "r" + gen.getSimpleName(), s2);
				toReturn.add(val);
			}
			catch (NoSuchMethodException e)
			{
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
		}
		
		return toReturn;
	}
	
	public static <T> String wrArrayList(ArrayList<T> a, Class<T> gen)
	{
		String toReturn = "";
		for(int i = 0; i < a.size(); ++i)
		{
			try
			{
				toReturn += MethodUtils.invokeStaticMethod(StringData.class, "wr" + gen.getSimpleName(), a.get(i)) + (i < a.size() - 1 ? "," : "");
			}
			catch (NoSuchMethodException e)
			{
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
		}
				
		return toReturn;
	}
}
