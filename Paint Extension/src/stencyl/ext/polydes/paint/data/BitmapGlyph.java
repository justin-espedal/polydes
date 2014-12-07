package stencyl.ext.polydes.paint.data;

import java.awt.Rectangle;

//http://www.angelcode.com/products/bmfont/doc/file_format.html
public class BitmapGlyph
{
	public BitmapFont font;
	
	public int id;
	public int x;
	public int y;
	public int width;
	public int height;
	public int xoffset;
	public int yoffset;
	public int xadvance;
	public int page;
	public int chnl;
	
	public Rectangle r; //glyph bounds
	public Rectangle r2; //glyph extended bounds (includes x advance, -xoffset, -yoffset, -yoffset + lineHeight)
	
	public void updateRect()
	{
		if(r == null)
			r = new Rectangle(x, y, width, height);
		else
		{
			r.x = x;
			r.y = y;
			r.width = width;
			r.height = height;
		}
		if(r2 == null)
			r2 = new Rectangle(x, y, width, height);
		r2.x = x - xoffset;
		r2.y = y - yoffset;
		r2.width = Math.max(xadvance, xoffset + width);
		r2.height = Math.max(Math.max(font.base, font.lineHeight), yoffset + height);
	}
	
	public String toString()
	{
		return ""+Character.toChars(id)[0];
	}
}
