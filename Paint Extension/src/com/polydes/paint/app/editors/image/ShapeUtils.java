package com.polydes.paint.app.editors.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

public class ShapeUtils
{
	public static int cX = 0;
	public static int cY = 0;
	public static int cW = 1;
	public static int cH = 1;
	public static Rectangle clip = new Rectangle(0, 0, 1, 1);
	
	public static void clip(int x, int y, int w, int h)
	{
		cX = x;
		cY = y;
		cW = w;
		cH = h;
		clip.x = x;
		clip.y = y;
		clip.width = w;
		clip.height = h;
	}
	
	public static Point[] getLine(int x1, int y1, int x2, int y2)
	{
		int xDif = Math.abs(x1 - x2) + 1;
		int yDif = Math.abs(y1 - y2) + 1;
		double numPixels = Math.max(xDif, yDif);
		
		ArrayList<Point> points = new ArrayList<Point>();
		
		if(numPixels == 1)
		{
			Point p = new Point(x1, y1);
			if(checkPoint(p))
				points.add(p);
		}
		else
		{
			double xStep = (x2 - x1) / (numPixels - 1);
			double yStep = (y2 - y1) / (numPixels - 1);
			
			for(int i = 0; i < numPixels; ++i)
			{
				Point p = new Point(x1 + (int)(xStep * i), y1 + (int)(yStep * i));
				if(checkPoint(p))
					points.add(p);
			}
		}
		
		return points.toArray(blank);
	}
	
	public static Point[] getRectangle(int x1, int y1, int x2, int y2)
	{
		int x = Math.min(x1, x2);
		int y = Math.min(y1, y2);
		int w = Math.abs(x1 - x2) + 1;
		int h = Math.abs(y1 - y2) + 1;
		
		int i;
		int j;
		
		ArrayList<Point> points = new ArrayList<Point>();
		
		int from;
		int to;
		
		j = y;
		if(j >= cY && j < cY + cH)
		{			
			from = Math.max(cX, x);
			to = Math.min(cX + cW, x + w); 
			for(i = from; i < to; ++i)
				points.add(new Point(i, j));
		}
		j = y + h - 1;
		if(j >= cY && j < cY + cH)
		{
			from = Math.max(cX, x);
			to = Math.min(cX + cW, x + w); 
			for(i = from; i < to; ++i)
				points.add(new Point(i, j));
		}
		i = x;
		if(i >= cX && i < cX + cW)
		{
			from = Math.max(cY, y);
			to = Math.min(cY + cH, y + h); 
			for(j = from; j < to; ++j)
				points.add(new Point(i, j));
		}
		i = x + w - 1;
		if(i >= cX && i < cX + cW)
		{
			from = Math.max(cY, y);
			to = Math.min(cY + cH, y + h); 
			for(j = from; j < to; ++j)
				points.add(new Point(i, j));
		}
		
		return points.toArray(blank);
	}
	
	public static Point[] getFilledRectangle(int x1, int y1, int x2, int y2)
	{
		Rectangle r = new Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2) + 1, Math.abs(y1 - y2) + 1);
		r = r.intersection(clip);
		
		Point[] points = new Point[r.width * r.height];
		int k = 0;
		
		for(int i = r.x; i < r.x + r.width; ++i)
		{
			for(int j = r.y; j < r.y + r.height; ++j)
			{
				points[k++] = new Point(i, j);
			}
		}
		
		return points;
	}
	
	public static boolean checkPoint(Point p)
	{
		return clip.contains(p);
	}
	
	public static final Point[] blank = new Point[0];
	
	public static Point[] getEllipse(int x1, int y1, int x2, int y2)
	{
		int x = Math.min(x1, x2);
		int y = Math.min(y1, y2);
		int w = Math.abs(x1 - x2) + 1;
		int h = Math.abs(y1 - y2) + 1;
		
		int xr = w / 2;
		int yr = h / 2;
		int xc = x + xr;
		int yc = y + yr;
		
		if (xr == 0 || yr == 0)
			return blank;
		
		int a2 = 2*xr*xr;
		int b2 = 2*yr*yr;
		int error = xr*xr*yr;
		int xo = 0;
		int yo = yr;
		int stopy = 0;
		int stopx = a2*yr;
		
		ArrayList<Point> points = new ArrayList<Point>();
		Point p;
		
		boolean contained = new Rectangle(cX, cY, cW, cH).contains(new Rectangle(x, y, w, h));
		
		while(stopy <= stopx)
		{
			if(contained)
			{
				points.add(new Point(xc + xo, yc + yo));
				points.add(new Point(xc - xo, yc + yo));
				points.add(new Point(xc - xo, yc - yo));
				points.add(new Point(xc + xo, yc - yo));
			}
			else
			{
				p = new Point(xc + xo, yc + yo);
				if(p.x >= cX && p.x < cX + cW && p.y >= cY && p.y < cY + cH)
					points.add(p);
				p = new Point(xc - xo, yc + yo);
				if(p.x >= cX && p.x < cX + cW && p.y >= cY && p.y < cY + cH)
					points.add(p);
				p = new Point(xc - xo, yc - yo);
				if(p.x >= cX && p.x < cX + cW && p.y >= cY && p.y < cY + cH)
					points.add(p);
				p = new Point(xc + xo, yc - yo);
				if(p.x >= cX && p.x < cX + cW && p.y >= cY && p.y < cY + cH)
					points.add(p);
			}
			
			++xo;
			error -= b2 * (xo - 1);
			stopy += b2;
			if (error <= 0)
			{
				error += a2 * (yo - 1);
				--yo;
				stopx -= a2;
			}
		}
		
		error = yr*yr*xr;
		xo = xr;
		yo = 0;
		stopy = b2*xr;
		stopx = 0;
		while (stopy >= stopx)
		{
			if(contained)
			{
				points.add(new Point(xc + xo, yc + yo));
				points.add(new Point(xc - xo, yc + yo));
				points.add(new Point(xc - xo, yc - yo));
				points.add(new Point(xc + xo, yc - yo));
			}
			else
			{
				p = new Point(xc + xo, yc + yo);
				if(p.x >= cX && p.x < cX + cW && p.y >= cY && p.y < cY + cH)
					points.add(p);
				p = new Point(xc - xo, yc + yo);
				if(p.x >= cX && p.x < cX + cW && p.y >= cY && p.y < cY + cH)
					points.add(p);
				p = new Point(xc - xo, yc - yo);
				if(p.x >= cX && p.x < cX + cW && p.y >= cY && p.y < cY + cH)
					points.add(p);
				p = new Point(xc + xo, yc - yo);
				if(p.x >= cX && p.x < cX + cW && p.y >= cY && p.y < cY + cH)
					points.add(p);
			}
			
			++yo;
			error -= a2 * (yo - 1);
			stopx += a2;
			if (error < 0)
			{
				error += b2 * (xo - 1);
				--xo;
				stopy -= b2;
			}
		}
		
		return points.toArray(blank);
	}
	
	public static Point[] getFilledEllipse(int x1, int y1, int x2, int y2)
	{
		int h1 = Math.abs(y1 - y2) + 1;
		int w1 = Math.abs(x1 - x2) + 1;
		
		int height = (h1 - 1) / 2;
		int width = (w1 - 1) / 2;
		
		int hh = height * height;
		int ww = width * width;
		int hhww = hh * ww;
		int x0 = width;
		int dx = 0;

		int xc = (x1 + x2) / 2; //center x
		int yc = (y1 + y2) / 2; //center y
		
		ArrayList<Point> points = new ArrayList<Point>();
		Point p;
		
		boolean contained = clip.contains(new Rectangle(Math.min(x1, x2), Math.min(y1, y2), width, height));
		
		// do the horizontal diameter
		if(contained)
		{
			for (int x = -width; x <= width; x++)
			{
				points.add(new Point(xc + x, yc));
			}
		}
		else if(yc >= cY && yc < cY + cH)
		{
			int start = Math.max(xc - width, cX);
			int end = Math.min(xc + width, cX + cW - 1);
			for (int x = start; x <= end; x++)
			{
				points.add(new Point(x, yc));
			}
		}

		if(contained)
		{
			// now do both halves at the same time, away from the diameter
			for (int y = 1; y <= height; y++)
			{
			    x1 = x0 - (dx - 1);  // try slopes of dx - 1 or more
			    for ( ; x1 > 0; x1--)
			        if (x1*x1*hh + y*y*ww <= hhww)
			            break;
			    dx = x0 - x1;  // current approximation of the slope
			    x0 = x1;
	
			    for (int x = -x0; x <= x0; x++)
			    {
			    	points.add(new Point(xc + x, yc - y));
			    	points.add(new Point(xc + x, yc + y));
			    }
			}
		}
		else
		{
			// now do both halves at the same time, away from the diameter
			for (int y = 1; y <= height; y++)
			{
			    x1 = x0 - (dx - 1);  // try slopes of dx - 1 or more
			    for ( ; x1 > 0; x1--)
			        if (x1*x1*hh + y*y*ww <= hhww)
			            break;
			    dx = x0 - x1;  // current approximation of the slope
			    x0 = x1;
	
			    int start = Math.max(xc - x0, cX);
				int end = Math.min(xc + x0, cX + cW - 1);
			    for (int x = start; x <= end; x++)
			    {
			    	p = new Point(x, yc - y);
					if(p.y >= cY && p.y < cY + cH)
						points.add(p);
					p = new Point(x, yc + y);
					if(p.y >= cY && p.y < cY + cH)
						points.add(p);
			    }
			}
		}
		
		//maybe? if even horizontally or vertically, just remove middle and translate bottom/right half.
		
		return points.toArray(blank);
	}
	
	public static Point[] translatePoints(Point[] p1, int dx, int dy)
	{
		Point[] p2 = new Point[p1.length];
		
		for(int i = 0; i < p1.length; ++i)
		{
			p2[i] = ((Point) p1[i].clone());
			p2[i].translate(dx, dy);
		}
		
		return p2;
	}
	
	public static Point[] getClipped(Point[] p1)
	{
		ArrayList<Point> points = new ArrayList<Point>(p1.length);
		
		for(int i = 0; i < p1.length; ++i)
		{
			if(checkPoint(p1[i]))
				points.add(p1[i]);
		}
		
		return points.toArray(blank);
	}
}
