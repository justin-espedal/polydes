package com.polydes.paint.app.editors.bitmapfont;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.polydes.paint.app.editors.bitmapfont.tools.GlyphBounds;
import com.polydes.paint.app.editors.bitmapfont.tools.GlyphSpacing;
import com.polydes.paint.app.editors.bitmapfont.tools.LineSpacing;
import com.polydes.paint.app.editors.image.DrawTools;
import com.polydes.paint.data.BitmapFont;
import com.polydes.paint.res.Resources;

import stencyl.sw.SW;

@SuppressWarnings("serial")
public class FontTools extends DrawTools
{
	private ToolButton glyphBoundsButton;
	private ToolButton glyphSpacingButton;
	private ToolButton lineSpacingButton;
	private JButton packGlyphsButton;
	
	public FontTools()
	{
		super();
		
		glyphBoundsButton = createToolButton(Resources.loadIcon("draw/glyph_bounds.png"), new GlyphBounds());
		glyphSpacingButton = createToolButton(Resources.loadIcon("draw/glyph_spacing.png"), new GlyphSpacing());
		lineSpacingButton = createToolButton(Resources.loadIcon("draw/line_spacing.png"), new LineSpacing());
		packGlyphsButton = createButton(Resources.loadIcon("draw/pack_glyphs.png"));
		packGlyphsButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				FontDrawArea area = (FontDrawArea) getDrawArea();
				BitmapFont font = area.font;
				PackGlyphsDialog.showPackGlyphsDialog(font, area, SW.get());
			}
		});
		
		add(createButtonPair(glyphBoundsButton, glyphSpacingButton));
		add(createButtonPair(lineSpacingButton, null));
		
		createButtonPair(null, null);
		
		add(packGlyphsButton);
	}
}
