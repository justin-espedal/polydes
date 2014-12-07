package stencyl.ext.polydes.paint.app.editors.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

public class ImageUtils
{
	public static void drawPixel(BufferedImage img, int x, int y, int color)
	{
		img.setRGB(x, y, color);
	}
	
	public static void drawEllipse(BufferedImage img, int x1, int y1, int x2, int y2, int color, int brushSize)
	{
		ShapeUtils.clip(0, 0, img.getWidth(), img.getHeight());
		for(Point p : ShapeUtils.getEllipse(x1, y1, x2, y2))
		{
			img.setRGB(p.x, p.y, color);
		}
	}
	
	public static void drawFilledEllipse(BufferedImage img, int x1, int y1, int x2, int y2, int color, int brushSize)
	{
		ShapeUtils.clip(0, 0, img.getWidth(), img.getHeight());
		for(Point p : ShapeUtils.getFilledEllipse(x1, y1, x2, y2))
		{
			img.setRGB(p.x, p.y, color);
		}
	}
	
	public static Rectangle fillPixels(BufferedImage img, int x, int y, int color)
	{
		int x1;
		int x2;
		int y1;
		int y2;
		
		int target = img.getRGB(x, y);
		if(color == target)
			return new Rectangle(x, y, 1, 1);
		
		Queue<Point> q = new LinkedList<Point>();
		Point p;
		Point w;
		Point e;
		
		int width = img.getWidth();
		int height = img.getHeight();
		
		x1 = x2 = x;
		y1 = y2 = y;
		
		q.add(new Point(x, y));
		while(!q.isEmpty())
		{
			p = q.remove();
			
			if(img.getRGB(p.x, p.y) != target)
				continue;
			
			if(p.y < y1)
				y1 = p.y;
			if(p.y > y2)
				y2 = p.y;
			
			w = (Point) p.clone();
			e = (Point) p.clone();
			while(w.x > 0 && img.getRGB(w.x - 1, w.y) == target)
				--w.x;
			while(e.x + 1 < width && img.getRGB(e.x + 1, e.y) == target)
				++e.x;
			
			if(w.x < x1)
				x1 = w.x;
			if(e.x > x2)
				x2 = e.x;
			
			for(int i = w.x; i <= e.x; ++i)
			{
				img.setRGB(i, p.y, color);
				if(p.y > 0 && img.getRGB(i, p.y - 1) == target)
					q.add(new Point(i, p.y - 1));
				if(p.y + 1 < height && img.getRGB(i, p.y + 1) == target)
					q.add(new Point(i, p.y + 1));
			}
		}
		
		return new Rectangle(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
	}
	
	public static void drawPixelLine(BufferedImage img, int x1, int y1, int x2, int y2, int color)
	{
		ShapeUtils.clip(0, 0, img.getWidth(), img.getHeight());
		for(Point p : ShapeUtils.getLine(x1, y1, x2, y2))
		{
			img.setRGB(p.x, p.y, color);
		}
	}
	
	public static void drawRect(BufferedImage img, int x1, int y1, int x2, int y2, int color)
	{
		ShapeUtils.clip(0, 0, img.getWidth(), img.getHeight());
		for(Point p : ShapeUtils.getRectangle(x1, y1, x2, y2))
		{
			img.setRGB(p.x, p.y, color);
		}
	}
	
	public static void fillRect(BufferedImage img, int x1, int y1, int x2, int y2, int color)
	{
		ShapeUtils.clip(0, 0, img.getWidth(), img.getHeight());
		for(Point p : ShapeUtils.getFilledRectangle(x1, y1, x2, y2))
		{
			img.setRGB(p.x, p.y, color);
		}
	}

	public static void drawPoints(BufferedImage img, Point[] points, int color)
	{
		for(Point p : points)
		{
			img.setRGB(p.x, p.y, color);
		}
	}

	public static void fill(BufferedImage img, Rectangle area, int color)
	{
		int x2 = area.x + area.width;
		int y2 = area.y + area.height;
		for(int i = area.x; i < x2; ++i)
		{
			for(int j = area.y; j < y2; ++j)
			{
				img.setRGB(i, j, color);
			}
		}
	}
	
	public static BufferedImage getSubImage(BufferedImage img, Rectangle area)
	{
		BufferedImage newImg = new BufferedImage(area.width, area.height, BufferedImage.TYPE_INT_ARGB);
		int i = 0;
		int j = 0;
		int x2 = area.x + area.width;
		int y2 = area.y + area.height;
		
		for(int x = area.x; x < x2; ++x)
		{
			for(int y = area.y; y < y2; ++y)
			{
				newImg.setRGB(i, j++, img.getRGB(x, y));
			}
			
			++i;
			j = 0;
		}
		
		return newImg;
	}
	
	public static BufferedImage cutSubImage(BufferedImage img, Rectangle area)
	{
		BufferedImage newImg = getSubImage(img, area);
		fill(img, area, DrawArea.TRANSPARENT);
		return newImg;
	}
	
	public static void drawImage(BufferedImage target, Point targetPoint, BufferedImage source)
	{
		Rectangle area = new Rectangle(0, 0, source.getWidth(), source.getHeight());
		drawImage(target, targetPoint, source, area);
	}
	
	public static void drawImage(BufferedImage target, Point targetPoint, BufferedImage source, Rectangle sourceArea)
	{
		Rectangle copyFrom = (Rectangle) sourceArea.clone();
		Rectangle copyTo = new Rectangle(targetPoint.x, targetPoint.y, sourceArea.width, sourceArea.height);
		
		int d;
		
		if(copyTo.x < 0)
		{
			d = copyTo.x - 0;
			copyTo.x -= d;
			copyTo.width += d;
			copyFrom.x -= d;
			copyFrom.width += d;
		}
		if(copyTo.y < 0)
		{
			d = copyTo.y - 0;
			copyTo.y -= d;
			copyTo.height += d;
			copyFrom.y -= d;
			copyFrom.height += d;
		}
		
		if(copyTo.x + copyTo.width > target.getWidth())
		{
			d = copyTo.x + copyTo.width - target.getWidth();
			copyTo.width -= d;
			copyFrom.width -= d;
		}
		if(copyTo.y + copyTo.height > target.getHeight())
		{
			d = copyTo.y + copyTo.height - target.getHeight();
			copyTo.height -= d;
			copyFrom.height -= d;
		}
		
		int i = copyTo.x;
		int j = copyTo.y;
		int x1 = copyFrom.x;
		int y1 = copyFrom.y;
		int x2 = copyFrom.x + copyFrom.width;
		int y2 = copyFrom.y + copyFrom.height;
		
		for(int x = x1; x < x2; ++x)
		{
			for(int y = y1; y < y2; ++y)
			{
				target.setRGB(i, j++, source.getRGB(x, y));
			}
			
			++i;
			j = copyTo.y;
		}
	}
}
