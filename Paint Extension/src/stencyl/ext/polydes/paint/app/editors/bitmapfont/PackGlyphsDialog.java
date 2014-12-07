package stencyl.ext.polydes.paint.app.editors.bitmapfont;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import stencyl.ext.polydes.paint.data.BitmapFont;
import stencyl.sw.loc.LanguagePack;
import stencyl.sw.util.Util;
import stencyl.sw.util.comp.ButtonBarFactory;
import stencyl.sw.util.comp.GroupButton;
import stencyl.sw.util.dg.StencylDialog;

@SuppressWarnings("serial")
public final class PackGlyphsDialog extends StencylDialog
{
	/*-------------------------------------*\
	 * Globals
	\*-------------------------------------*/ 
    
	private static LanguagePack lang = LanguagePack.get();
	
    public static final int WIDTH = 440;
	public static final int HEIGHT = 460;
	
	private BitmapFont font;
	private FontDrawArea area;
	
	private PackGlyphsPanel panel;
	
	private AbstractButton okButton;	
	private int result = JOptionPane.OK_OPTION;
	
	
	/*-------------------------------------*\
	 * Constructor
	\*-------------------------------------*/ 
	
	public static int showPackGlyphsDialog(BitmapFont font, FontDrawArea area, JFrame parent)
	{
		PackGlyphsDialog properties = new PackGlyphsDialog(font, area, parent);
		int result = properties.getResult();
		properties.dispose();
		
		return result;
	}
	
	private PackGlyphsDialog(BitmapFont font, FontDrawArea area, JFrame parent)
	{
		super(parent, "Pack Glyphs", WIDTH, HEIGHT, new Color(80, 80, 80), false);
		
		this.font = font;
		this.area = area;
		
		add(createContentPanel(), BorderLayout.CENTER);
		
		if(Util.isMacOSX())
		{
			setPreferredSize(new Dimension(WIDTH, HEIGHT));
		}
		
		else
		{
			setPreferredSize(new Dimension(WIDTH, HEIGHT - 5));
		}
		
		setVisible(true);
	}
	
	public int getResult()
	{
		return result;
	}
	
	public JComponent createContentPanel()
	{
		return panel = new PackGlyphsPanel(font, area, okButton);
	}
	
	public JPanel createButtonPanel() 
	{
		okButton = new GroupButton(0);
        JButton cancelButton = new GroupButton(0);
        
		okButton.setAction
        (
        	new AbstractAction(lang.get("globals.ok"))
        	{
	            public void actionPerformed(ActionEvent e) 
	            {
	            	if(panel.verify())
	            	{
	            		result = JOptionPane.OK_OPTION;
	            		panel.applyChanges();
	            		setVisible(false);
	            	}
	            }
        	}
        );
        
        cancelButton.setAction
        (
        	new AbstractAction(lang.get("globals.cancel")) 
	        {
	            public void actionPerformed(ActionEvent e) 
	            {
	            	cancel();
	            }
	        }
        );
        
		return ButtonBarFactory.createCurvedButtonBar
		(
			this,
			new AbstractButton[] {okButton, cancelButton},
			1
		); 
    }
	
	public void cancel()
	{
		result = JOptionPane.CANCEL_OPTION;
        setVisible(false);
	}
	
	public void dispose()
	{
		removeAll();
		
		panel.dispose();
		
		font = null;
		area = null;
		panel = null;
		okButton = null;
		
		super.dispose();
	}
}