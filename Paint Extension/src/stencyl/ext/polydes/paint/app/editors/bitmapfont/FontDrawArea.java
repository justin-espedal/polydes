package stencyl.ext.polydes.paint.app.editors.bitmapfont;

import java.awt.Color;
import java.awt.Graphics;

import stencyl.ext.polydes.paint.app.editors.bitmapfont.tools.GlyphBounds;
import stencyl.ext.polydes.paint.app.editors.image.DrawArea;
import stencyl.ext.polydes.paint.data.BitmapFont;
import stencyl.ext.polydes.paint.data.BitmapGlyph;

@SuppressWarnings("serial")
public class FontDrawArea extends DrawArea
{
	public BitmapFont font;
	
	public FontDrawArea(BitmapFont font)
	{
		super();
		
		loadImg(font.pageImages.entrySet().iterator().next().getValue());
		this.font = font;
		font.setEditor(this);
		init();
		bgPaint = Color.BLACK;
	}
	
	@Override
	public void paintImage(Graphics g, int x, int y, int width, int height, boolean paintBackground)
	{
		super.paintImage(g, x, y, width, height, paintBackground);
		
		int s = (int) scale;
		
		g.setColor(Color.BLUE);
		
		BitmapGlyph skipGlyph = null;
		if(currentTool instanceof GlyphBounds)
			skipGlyph = ((GlyphBounds) currentTool).selectedGlyph;
		
		for(BitmapGlyph glyph : font.chars)
		{
			if(glyph == skipGlyph)
				continue;
			
			//if(glyph.r.intersects(g.getClipBounds()))
			g.drawRect(glyph.x * s, glyph.y * s, glyph.width * s, glyph.height * s);
		}
	}
}
