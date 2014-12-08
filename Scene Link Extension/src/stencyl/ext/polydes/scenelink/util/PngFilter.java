package stencyl.ext.polydes.scenelink.util;

import java.io.File;
import java.util.Locale;

public class PngFilter extends javax.swing.filechooser.FileFilter implements java.io.FilenameFilter
{
	public final static String png = "png";
	
	// FOR javax.swing.filechooser.FileFilter
	@Override
	public boolean accept(File f)
	{
		if (f.isDirectory())
			return true;

		String extension = getExtension(f);

		return (extension != null && extension.equals(png));
	}

	// The description of this filter
	@Override
	public String getDescription()
	{
		return "Import PNG Image";
	}

	// FOR java.io.FilenameFilter
	@Override
	public boolean accept(File f, String s)
	{
		return accept(new File(f, s));
	}

	public static String getExtension(File f)
	{
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1)
		{
			ext = s.substring(i + 1).toLowerCase(Locale.ENGLISH);
		}
		return ext;
	}
}
