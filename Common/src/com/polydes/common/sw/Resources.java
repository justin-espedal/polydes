package com.polydes.common.sw;

import stencyl.core.lib.AbstractResource;
import stencyl.core.lib.Folder;

public class Resources
{
	public static boolean isUnderFolder(AbstractResource r, Folder f)
	{
		//since Stencyl doesn't have nested folders, null folder represents the root folder.
		return f == null || r.getParentFolder() == f;
	}
}
