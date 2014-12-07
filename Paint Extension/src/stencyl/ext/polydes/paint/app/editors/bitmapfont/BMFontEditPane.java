package stencyl.ext.polydes.paint.app.editors.bitmapfont;

import stencyl.ext.polydes.paint.app.editors.bitmapfont.tools.GlyphBounds;
import stencyl.ext.polydes.paint.app.editors.bitmapfont.tools.GlyphSpacing;
import stencyl.ext.polydes.paint.app.editors.bitmapfont.tools.LineSpacing;
import stencyl.ext.polydes.paint.app.editors.image.DrawArea;
import stencyl.ext.polydes.paint.app.editors.image.ImageEditPane;

@SuppressWarnings("serial")
public class BMFontEditPane extends ImageEditPane
{
	public BMFontEditPane()
	{
		super(new FontTools());
	}
	
	@Override
	public void installListeners(DrawArea area)
	{
		area.addMouseListener(toolbar.getTool(GlyphBounds.class));
		area.addMouseMotionListener(toolbar.getTool(GlyphBounds.class));
		area.addMouseListener(toolbar.getTool(GlyphSpacing.class));
		area.addMouseMotionListener(toolbar.getTool(GlyphSpacing.class));
		area.addMouseListener(toolbar.getTool(LineSpacing.class));
		area.addMouseMotionListener(toolbar.getTool(LineSpacing.class));
		
		super.installListeners(area);
	}
	
	@Override
	public void uninstallListeners(DrawArea area)
	{
		area.removeMouseListener(toolbar.getTool(GlyphBounds.class));
		area.removeMouseMotionListener(toolbar.getTool(GlyphBounds.class));
		area.removeMouseListener(toolbar.getTool(GlyphSpacing.class));
		area.removeMouseMotionListener(toolbar.getTool(GlyphSpacing.class));
		area.removeMouseListener(toolbar.getTool(LineSpacing.class));
		area.removeMouseMotionListener(toolbar.getTool(LineSpacing.class));
		
		super.uninstallListeners(area);
	}
}
