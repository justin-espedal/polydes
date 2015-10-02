package com.polydes.paint.app.editors.bitmapfont;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;

import com.polydes.paint.app.editors.image.ImageUtils;
import com.polydes.paint.data.BitmapFont;
import com.polydes.paint.data.BitmapGlyph;

public class GlyphLayoutTransformer
{
	public static final int UP = 0;
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	
	public static final int HORZ = 0;
	public static final int VERT = 1;
	
	public static BufferedImage pack(BufferedImage src, BitmapFont font, int width, int[] borderPadding, int[] glyphPadding, int[] glyphSpacing)
	{
		Collections.sort(font.chars, new GlyphHeightComparator());
		
		int glyphCount = font.chars.size();
		Rectangle[] sourceBounds = new Rectangle[glyphCount];
		Point[] insertPoints = new Point[glyphCount];
		Dimension[] newDimensions = new Dimension[glyphCount];
		boolean[] skip = new boolean[glyphCount];
		
		int[] oldPadding = font.padding.clone();
		
		font.padding = glyphPadding.clone();
		font.spacing = glyphSpacing.clone();
		
		//positive change is growing bounds
		int[] paddingChange = new int[4];
		paddingChange[UP] = font.padding[UP] - oldPadding[UP];
		paddingChange[RIGHT] = font.padding[RIGHT] - oldPadding[RIGHT];
		paddingChange[DOWN] = font.padding[DOWN] - oldPadding[DOWN];
		paddingChange[LEFT] = font.padding[LEFT] - oldPadding[LEFT];
		
		boolean upShrink = paddingChange[UP] < 0;
		boolean rightShrink = paddingChange[RIGHT] < 0;
		boolean downShrink = paddingChange[DOWN] < 0;
		boolean leftShrink = paddingChange[LEFT] < 0;
		boolean upGrow = paddingChange[UP] > 0;
		boolean leftGrow = paddingChange[LEFT] > 0;
		
		//x/y offset of image from top-left bound.
		int xOffset = leftGrow ? paddingChange[LEFT] : 0;
		int yOffset = upGrow ? paddingChange[UP] : 0;
		
		int paddingChangeX = paddingChange[LEFT] + paddingChange[RIGHT];
		int paddingChangeY = paddingChange[UP] + paddingChange[DOWN];
		
		int paddingX = font.padding[LEFT] + font.padding[RIGHT];
		int paddingY = font.padding[UP] + font.padding[DOWN];
		
		int i = 0;
		for(BitmapGlyph g : font.chars)
		{
			g.updateRect();
			Rectangle r = (Rectangle) g.r.clone();
			
			if(upShrink)
			{
				r.y -= paddingChange[UP];
				r.height += paddingChange[UP];
			}
			if(rightShrink)
				r.width += paddingChange[RIGHT];
			if(downShrink)
				r.height += paddingChange[DOWN];
			if(leftShrink)
			{
				r.x -= paddingChange[LEFT];
				r.width += paddingChange[LEFT];
			}
			
			sourceBounds[i] = r;
			newDimensions[i] = new Dimension(g.width + paddingChangeX, g.height + paddingChangeY);
			++i;
		}
		
		int currentX = borderPadding[LEFT];
		int currentY = borderPadding[UP];
		
		int lowestY = 0;
		
		i = 0;
		for(BitmapGlyph g : font.chars)
		{
			if(newDimensions[i].height - paddingY <= 0 || newDimensions[i].width - paddingX <= 0)
			{
				insertPoints[i] = new Point(currentX + xOffset, currentY + yOffset);
				skip[i++] = true;
				continue;
			}
			skip[i] = false;
			
			if(currentX + g.width + paddingChangeX > width - borderPadding[RIGHT])
			{
				currentX = borderPadding[LEFT];
				currentY = lowestY + font.spacing[VERT];
			}
			
			lowestY = Math.max(lowestY, currentY + g.height + paddingChangeY);
			
			insertPoints[i] = new Point(currentX + xOffset, currentY + yOffset);
			
			currentX += g.width + paddingChangeX + font.spacing[HORZ];
			
			++i;
		}
		
		int height = lowestY + borderPadding[DOWN];
		
		BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		i = 0;
		for(BitmapGlyph g : font.chars)
		{
			if(!skip[i])
				ImageUtils.drawImage(target, insertPoints[i], src, sourceBounds[i]);
			
			g.x = insertPoints[i].x - xOffset;
			g.y = insertPoints[i].y - yOffset;
			g.width = newDimensions[i].width;
			g.height = newDimensions[i].height;
			g.updateRect();
			
			++i;
		}
		
		font.scaleW = width;
		font.scaleH = height;
		
		return target;
	}
	
	public static BufferedImage unpack(BufferedImage src, BitmapFont font, int width, int[] borderPadding, int[] glyphPadding, int[] glyphSpacing)
	{
		Collections.sort(font.chars, new GlyphIDComparator());
		
		int glyphCount = font.chars.size();
		Rectangle[] sourceBounds = new Rectangle[glyphCount];
		Point[] insertPoints = new Point[glyphCount];
		Dimension[] newDimensions = new Dimension[glyphCount];
		boolean[] skip = new boolean[glyphCount];
		
		int[] oldPadding = font.padding.clone();
		
		font.padding = glyphPadding.clone();
		font.spacing = glyphSpacing.clone();
		
		//positive change is growing bounds
		int[] paddingChange = new int[4];
		paddingChange[UP] = font.padding[UP] - oldPadding[UP];
		paddingChange[RIGHT] = font.padding[RIGHT] - oldPadding[RIGHT];
		paddingChange[DOWN] = font.padding[DOWN] - oldPadding[DOWN];
		paddingChange[LEFT] = font.padding[LEFT] - oldPadding[LEFT];
		
		boolean upShrink = paddingChange[UP] < 0;
		boolean rightShrink = paddingChange[RIGHT] < 0;
		boolean downShrink = paddingChange[DOWN] < 0;
		boolean leftShrink = paddingChange[LEFT] < 0;
		boolean upGrow = paddingChange[UP] > 0;
		boolean leftGrow = paddingChange[LEFT] > 0;
		
		//x/y offset of image from top-left bound.
		int xOffset = leftGrow ? paddingChange[LEFT] : 0;
		int yOffset = upGrow ? paddingChange[UP] : 0;
		
		int paddingChangeX = paddingChange[LEFT] + paddingChange[RIGHT];
		int paddingChangeY = paddingChange[UP] + paddingChange[DOWN];
		
		int paddingX = font.padding[LEFT] + font.padding[RIGHT];
		int paddingY = font.padding[UP] + font.padding[DOWN];
		
		int i = 0;
		for(BitmapGlyph g : font.chars)
		{
			g.updateRect();
			Rectangle r = (Rectangle) g.r.clone();
			
			if(upShrink)
			{
				r.y -= paddingChange[UP];
				r.height += paddingChange[UP];
			}
			if(rightShrink)
				r.width += paddingChange[RIGHT];
			if(downShrink)
				r.height += paddingChange[DOWN];
			if(leftShrink)
			{
				r.x -= paddingChange[LEFT];
				r.width += paddingChange[LEFT];
			}
			
			sourceBounds[i] = r;
			newDimensions[i] = new Dimension(g.width + paddingChangeX, g.height + paddingChangeY);
			++i;
		}
		
		int currentX = borderPadding[LEFT];
		int currentY = borderPadding[UP];
		
		int lowestY = 0;
		
		i = 0;
		for(BitmapGlyph g : font.chars)
		{
			if(newDimensions[i].height - paddingY <= 0 || newDimensions[i].width - paddingX <= 0)
			{
				insertPoints[i] = new Point(currentX + xOffset, currentY + yOffset + g.yoffset);
				skip[i++] = true;
				continue;
			}
			skip[i] = false;
			
			if(currentX + g.width + paddingChangeX > width - borderPadding[RIGHT])
			{
				currentX = borderPadding[LEFT];
				currentY = lowestY + font.spacing[VERT];
			}
			
			lowestY = Math.max(lowestY, currentY + g.height + paddingChangeY + g.yoffset);
			
			insertPoints[i] = new Point(currentX + xOffset, currentY + yOffset + g.yoffset);
			
			currentX += g.width + paddingChangeX + font.spacing[HORZ];
			
			++i;
		}
		
		int height = lowestY + borderPadding[DOWN];
		
		BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		i = 0;
		for(BitmapGlyph g : font.chars)
		{
			if(!skip[i])
				ImageUtils.drawImage(target, insertPoints[i], src, sourceBounds[i]);
			
			g.x = insertPoints[i].x - xOffset;
			g.y = insertPoints[i].y - yOffset;
			g.width = newDimensions[i].width;
			g.height = newDimensions[i].height;
			g.updateRect();
			
			++i;
		}
		
		font.scaleW = width;
		font.scaleH = height;
		
		return target;
	}
	
	public static class GlyphIDComparator implements Comparator<BitmapGlyph>
	{
	    @Override
	    public int compare(BitmapGlyph g1, BitmapGlyph g2)
	    {
	    	return g1.id - g2.id;
	    }
	}
	
	public static class GlyphHeightComparator implements Comparator<BitmapGlyph>
	{
	    @Override
	    public int compare(BitmapGlyph g1, BitmapGlyph g2)
	    {
	    	return g2.height - g1.height;
	    }
	}
}
