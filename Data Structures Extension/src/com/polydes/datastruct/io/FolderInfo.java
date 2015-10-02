package com.polydes.datastruct.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

public class FolderInfo extends HashMap<String, String>
{
	public static String FOLDER_INFO_FILENAME = "folderinfo.txt";
	public static String FILE_ORDER_KEY = "fileOrder";
	
	//=== For Reading
	
	private File folder;
	
	public FolderInfo(File folder)
	{
		if(!folder.isDirectory())
			return;
		
		this.folder = folder;
		
		File info = new File(folder, FOLDER_INFO_FILENAME);
		if(!info.exists())
			return;
		
		putAll(Text.readKeyValues(info));
	}
	
	public ArrayList<String> getFileOrder()
	{
		ArrayList<String> fileOrder = new ArrayList<String>();
		
		if(containsKey(FILE_ORDER_KEY))
			fileOrder = new ArrayList<String>(Arrays.asList(StringUtils.split(get(FILE_ORDER_KEY), ",")));
		
		for(String fname : folder.list())
			if(!fileOrder.contains(fname) && !fname.equals(FOLDER_INFO_FILENAME))
				fileOrder.add(fname);
		
		return fileOrder;
	}
	
	//=== For Writing
	
	private ArrayList<String> fileOrder;
	
	public FolderInfo()
	{
		fileOrder = new ArrayList<String>();
	}
	
	public void addFilenameToOrder(String filename)
	{
		if(fileOrder == null)
			fileOrder = new ArrayList<String>();
		
		fileOrder.add(filename);
	}
	
	public void writeToFolder(File folder)
	{
		ArrayList<String> lines = new ArrayList<String>();
		
		for(String key : keySet())
			lines.add(key + "=" + get(key));
		
		if(fileOrder != null)
			lines.add(FILE_ORDER_KEY + "=" + StringUtils.join(fileOrder.toArray(new String[0]), ","));
		
		Text.writeLines(new File(folder, FOLDER_INFO_FILENAME), lines);
	}
}
