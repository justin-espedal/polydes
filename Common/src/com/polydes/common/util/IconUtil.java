package com.polydes.common.util;

import javax.swing.ImageIcon;

import stencyl.sw.util.gfx.ImageUtil;
import stencyl.thirdparty.misc.gfx.GraphicsUtilities;

public class IconUtil
{
	public static ImageIcon getIcon(ImageIcon img, int maxSize)
	{
		if(img == null)
			return null;
		
		int large = Math.max(img.getIconWidth(), img.getIconHeight());
		if(large <= maxSize)
			return img;
		
		img = new ImageIcon(GraphicsUtilities.createThumbnail(ImageUtil.getBufferedImage(img.getImage()), maxSize));
		return img;
	}
}
