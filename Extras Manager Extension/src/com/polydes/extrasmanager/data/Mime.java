package com.polydes.extrasmanager.data;

import java.io.File;

import javax.activation.MimetypesFileTypeMap;

public class Mime
{
	public static MimetypesFileTypeMap typemap = new MimetypesFileTypeMap();
	
	static
	{
		typemap.addMimeTypes("text/plain txt xml fnt style anim");
		typemap.addMimeTypes("image/png png");
	}
	
	public static String get(File f)
	{
		return typemap.getContentType(f);
	}
}
