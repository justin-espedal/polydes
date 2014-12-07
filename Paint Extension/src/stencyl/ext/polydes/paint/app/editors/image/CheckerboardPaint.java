package stencyl.ext.polydes.paint.app.editors.image;

import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import sun.awt.image.IntegerComponentRaster;

public class CheckerboardPaint implements Paint
{
	private CheckerboardPaintContext context;
	
	public CheckerboardPaint(int pixelSize)
	{
		context = new CheckerboardPaintContext(pixelSize);
	}
	
	@Override
	public int getTransparency()
	{
		return Transparency.OPAQUE;
	}

	@Override
	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints)
	{
		context.setOffset(userBounds.getBounds().x - deviceBounds.x, userBounds.getBounds().y - deviceBounds.y);
		return context;
	}
	
	class CheckerboardPaintContext implements PaintContext
	{
		private WritableRaster savedTile;
		private int pixelSize;
		private Point offset;
		
		public CheckerboardPaintContext(int pixelSize)
		{
			this.pixelSize = pixelSize;
			offset = new Point(0, 0);
		}
		
		public void setOffset(int x, int y)
		{
			offset.x = x;
			offset.y = y;
		}
		
		@Override
		public void dispose()
		{
		}
		
		@Override
		public ColorModel getColorModel()
		{
			return ColorModel.getRGBdefault();
		}
		
		@Override
		public Raster getRaster(int x, int y, int w, int h)
		{
			WritableRaster t = savedTile;
			
			if(t == null)
			{
				Rectangle r = new Rectangle(x, y, w, h);
				
				x = 0;
				y = 0;
				w = 32 + pixelSize * 2;
				h = 32 + pixelSize * 2;
				
				int gray = new Color(0xbfbfbf).getRGB();
				
				t = getColorModel().createCompatibleWritableRaster(w, h);
				IntegerComponentRaster icr = (IntegerComponentRaster) t;
				Arrays.fill(icr.getDataStorage(), Color.WHITE.getRGB());
				
				int xstart;
				
				for(int y1 = y; y1 < y + h; ++y1)
				{
					if((y1 / pixelSize) % 2 == 0)
						xstart = x / pixelSize * pixelSize;
					else
						xstart = x / pixelSize * pixelSize + pixelSize;
					
					for(int x1 = xstart; x1 < x + w; x1 += pixelSize* 2)
					{
						int from = x1;
						int to = x1 + pixelSize;
						
						from -= x;
						to -= x;
						
						if(from < 0)
							from = 0;
						if(to > w)
							to = w;
						
						from += (y1 - y) * w;
						to += (y1 - y) * w;
						
						Arrays.fill(icr.getDataStorage(), from, to, gray);
					}
				}
				
				savedTile = t;
				
				x = r.x;
				y = r.y;
				w = r.width;
				h = r.height;
			}
			
			int dx = (offset.x + x) % (pixelSize * 2);
			int dy = (offset.y + y) % (pixelSize * 2);
			
			return t.createChild(dx, dy, w, h, 0, 0, null);
		}
	}
}
