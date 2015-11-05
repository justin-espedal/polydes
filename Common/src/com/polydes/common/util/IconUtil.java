package com.polydes.common.util;

import javax.swing.ImageIcon;

import stencyl.sw.util.gfx.ImageUtil;
import stencyl.thirdparty.misc.gfx.GraphicsUtilities;

public class IconUtil
{
	public static ImageIcon getIcon(ImageIcon img, int maxSize)
	{
		int small = Math.min(img.getIconWidth(), img.getIconHeight());
		img = new ImageIcon(GraphicsUtilities.createThumbnail(ImageUtil.getBufferedImage(img.getImage()), Math.min(maxSize, small)));
		return img;
	}
}
