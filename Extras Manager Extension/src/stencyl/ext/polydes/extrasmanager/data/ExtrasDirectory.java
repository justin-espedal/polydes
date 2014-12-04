package stencyl.ext.polydes.extrasmanager.data;

import java.io.File;
import java.util.HashSet;

public class ExtrasDirectory
{
	public static String extrasFolder;
	public static File extrasFolderF;
	public static HashSet<String> ownedFolderNames;
	
	public static boolean isRoot(File f)
	{
		return isSame(f, extrasFolderF);
	}
	
	public static boolean isSame(File f1, File f2)
	{
		return f1 != null && f2 != null && f1.getAbsolutePath().equals(f2.getAbsolutePath());
	}
}