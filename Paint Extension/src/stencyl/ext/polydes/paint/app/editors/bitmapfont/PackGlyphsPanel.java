package stencyl.ext.polydes.paint.app.editors.bitmapfont;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import stencyl.ext.polydes.paint.data.BitmapFont;
import stencyl.sw.lnf.Theme;
import stencyl.sw.util.UI;
import stencyl.sw.util.Util;
import stencyl.sw.util.comp.text.AutoVerifyField;
import stencyl.sw.util.comp.text.FieldVerifier;
import stencyl.sw.util.dg.DialogPanel;

@SuppressWarnings("serial")
public final class PackGlyphsPanel extends JPanel implements ActionListener, FieldVerifier
{    
	/*-------------------------------------*\
	 * Globals
	\*-------------------------------------*/ 
    
	public static final int WIDTH = 440;
	public static final int HEIGHT = 420;
	
	public static final int PACKED = 0;
	public static final int UNPACKED = 1;
	
	public static final int UP = 0;
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	
	public static final int HORZ = 0;
	public static final int VERT = 1;
	
	private BitmapFont font;
	private FontDrawArea area;
	
	private JComboBox packChooser;
	
	private AutoVerifyField[] avfs;
	
	private AutoVerifyField widthField;
	
	//Boundary Padding Fields
	private AutoVerifyField bpfUp;
	private AutoVerifyField bpfRight;
	private AutoVerifyField bpfDown;
	private AutoVerifyField bpfLeft;
	
	//Glyph Padding Fields
	private AutoVerifyField gpfUp;
	private AutoVerifyField gpfRight;
	private AutoVerifyField gpfDown;
	private AutoVerifyField gpfLeft;
	
	//Glyph Spacing Fields
	private AutoVerifyField gsfHorz;
	private AutoVerifyField gsfVert;
	
	private AbstractButton okButton;
	
	/*-------------------------------------*\
	 * Constructor
	\*-------------------------------------*/ 
	
	public PackGlyphsPanel
	(
		BitmapFont font,
		FontDrawArea area,
		AbstractButton okButton
	)
	{
		this.okButton = okButton;
		this.font = font;
		this.area = area;

		setLayout(new BorderLayout());
		add(createContentPanel(), BorderLayout.CENTER);
		
		init();

		//---

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
	
	public void init()
	{
		if(font != null && area != null)
		{
			packChooser.setSelectedIndex(PACKED);
			
			widthField.setText("" + font.scaleW);
			
			if(packChooser.getSelectedIndex() == PACKED)
			{
				bpfUp.setText("0");
				bpfRight.setText("0");
				bpfDown.setText("0");
				bpfLeft.setText("0");
			}
			else
			{
				bpfUp.setText("10");
				bpfRight.setText("10");
				bpfDown.setText("10");
				bpfLeft.setText("10");
			}
			
			gpfUp.setText("" + font.padding[UP]);
			gpfRight.setText("" + font.padding[RIGHT]);
			gpfDown.setText("" + font.padding[DOWN]);
			gpfLeft.setText("" + font.padding[LEFT]);
			
			gsfHorz.setText("" + font.spacing[HORZ]);
			gsfVert.setText("" + font.spacing[VERT]);
		}
		
		verify();
		repaint();
	}
	
	private int getWidth(JLabel l, String s)
	{
		return (int) (1.2 * l.getFontMetrics(l.getFont()).stringWidth(s));
	}
	
	/*-------------------------------------*\
	 * Construct UI
	\*-------------------------------------*/ 
	
	public void assignFocus()
	{
		packChooser.requestFocusInWindow();
	}
	
	public JComponent createContentPanel()
	{
		DialogPanel panel = new DialogPanel(Theme.LIGHT_BG_COLOR);
		
		if(Util.isMacOSX())
		{
			panel.finishBlock();
		}
		
		JLabel packedLabel = UI.createBoldLabel("compression");
		JLabel widthLabel = UI.createBoldLabel("width");
		
		JLabel boundaryPaddingLabel = UI.createBoldLabel("boundary");
		JLabel glyphPaddingLabel = UI.createBoldLabel("glyph");
		
		JLabel glyphSpacingLabel = UI.createBoldLabel("glyph spacing");
		
		int packedWidth = getWidth(packedLabel, "compression");
		int widthWidth = getWidth(widthLabel, "width");
		int boundaryWidth = getWidth(boundaryPaddingLabel, "boundary");
		int glyphWidth = getWidth(glyphPaddingLabel, "glyph");
				
		normalizeLabels(packedLabel, widthLabel, packedWidth, widthWidth);
		normalizeLabels(boundaryPaddingLabel, glyphPaddingLabel, boundaryWidth, glyphWidth);
		
		panel.addHeader("Image");
		createWidthPanel(panel, widthLabel);
		createPackedPanel(panel, packedLabel);
		panel.finishBlock();
		
		//---
		
		panel.addHeader("Padding");
		createBoundaryPaddingPanel(panel, boundaryPaddingLabel);
		createGlyphPaddingPanel(panel, glyphPaddingLabel);
		createGlyphSpacingPanel(panel, glyphSpacingLabel);
		panel.finishBlock();

		//---
		
		panel.addFinalRow(new JLabel(""));

		avfs = new AutoVerifyField[11];
		avfs[0] = widthField;
		avfs[1] = bpfUp;
		avfs[2] = bpfRight;
		avfs[3] = bpfDown;
		avfs[4] = bpfLeft;
		avfs[5] = gpfUp;
		avfs[6] = gpfRight;
		avfs[7] = gpfDown;
		avfs[8] = gpfLeft;
		avfs[9] = gsfHorz;
		avfs[10] = gsfVert;
		
		return panel;
	}
	
	private void normalizeLabels(JLabel one, JLabel two, int w1, int w2)
	{
		one.setMinimumSize(new Dimension(Math.max(w1, w2), 10));
		one.setPreferredSize(new Dimension(Math.max(w1, w2), 10));
		one.setHorizontalAlignment(SwingConstants.TRAILING);
		one.setHorizontalTextPosition(SwingConstants.TRAILING);
		
		two.setMinimumSize(new Dimension(Math.max(w1, w2), 10));
		two.setPreferredSize(new Dimension(Math.max(w1, w2), 10));
		two.setHorizontalAlignment(SwingConstants.TRAILING);
		two.setHorizontalTextPosition(SwingConstants.TRAILING);
	}
	
	private void createWidthPanel(DialogPanel panel, JLabel widthLabel)
	{
		JPanel widthPanel = new JPanel();
		widthPanel.setBackground(Theme.LIGHT_BG_COLOR);
		widthPanel.setLayout(new BoxLayout(widthPanel, BoxLayout.X_AXIS));
		
		widthField = createField(4);
		
		widthPanel.add(widthLabel);
		widthPanel.add(Box.createHorizontalStrut(10));
		widthPanel.add(widthField);
		widthPanel.add(Box.createHorizontalGlue());
		
		panel.addGenericRow(widthPanel);
	}
	
	private void createPackedPanel(DialogPanel panel, JLabel packedLabel)
	{
		JPanel packedPanel = new JPanel();
		packedPanel.setBackground(Theme.LIGHT_BG_COLOR);
		packedPanel.setLayout(new BoxLayout(packedPanel, BoxLayout.X_AXIS));
		
		packChooser = new JComboBox(new String[] {"Packed", "Unpacked"});
		packChooser.setBackground(null);
		packChooser.setOpaque(false);
		
		packedPanel.add(packedLabel);
		packedPanel.add(Box.createHorizontalStrut(10));
		packedPanel.add(packChooser);
		packedPanel.add(Box.createHorizontalGlue());
		
		panel.addGenericRow(packedPanel);
	}
	
	private void createBoundaryPaddingPanel(DialogPanel panel, JLabel boundaryLabel)
	{
		JPanel boundaryPanel = new JPanel();
		boundaryPanel.setBackground(Theme.LIGHT_BG_COLOR);
		boundaryPanel.setLayout(new BoxLayout(boundaryPanel, BoxLayout.X_AXIS));
		
		bpfUp = createField(4);
		bpfRight = createField(4);
		bpfDown = createField(4);
		bpfLeft = createField(4);
		
		boundaryPanel.add(boundaryLabel);
		boundaryPanel.add(Box.createHorizontalStrut(10));
		boundaryPanel.add(bpfUp);
		boundaryPanel.add(Box.createHorizontalStrut(5));
		boundaryPanel.add(bpfRight);
		boundaryPanel.add(Box.createHorizontalStrut(5));
		boundaryPanel.add(bpfDown);
		boundaryPanel.add(Box.createHorizontalStrut(5));
		boundaryPanel.add(bpfLeft);
		boundaryPanel.add(Box.createHorizontalGlue());
		
		panel.addGenericRow(boundaryPanel);
	}
	
	private void createGlyphPaddingPanel(DialogPanel panel, JLabel glyphLabel)
	{
		JPanel glyphPanel = new JPanel();
		glyphPanel.setBackground(Theme.LIGHT_BG_COLOR);
		glyphPanel.setLayout(new BoxLayout(glyphPanel, BoxLayout.X_AXIS));
		
		gpfUp = createField(4);
		gpfRight = createField(4);
		gpfDown = createField(4);
		gpfLeft = createField(4);
		
		glyphPanel.add(glyphLabel);
		glyphPanel.add(Box.createHorizontalStrut(10));
		glyphPanel.add(gpfUp);
		glyphPanel.add(Box.createHorizontalStrut(5));
		glyphPanel.add(gpfRight);
		glyphPanel.add(Box.createHorizontalStrut(5));
		glyphPanel.add(gpfDown);
		glyphPanel.add(Box.createHorizontalStrut(5));
		glyphPanel.add(gpfLeft);
		glyphPanel.add(Box.createHorizontalGlue());
		
		panel.addGenericRow(glyphPanel);
	}
	
	private void createGlyphSpacingPanel(DialogPanel panel, JLabel glyphLabel)
	{
		JPanel glyphPanel = new JPanel();
		glyphPanel.setBackground(Theme.LIGHT_BG_COLOR);
		glyphPanel.setLayout(new BoxLayout(glyphPanel, BoxLayout.X_AXIS));
		
		gsfHorz = createField(4);
		gsfVert = createField(4);
		
		glyphPanel.add(glyphLabel);
		glyphPanel.add(Box.createHorizontalStrut(10));
		glyphPanel.add(gsfHorz);
		glyphPanel.add(Box.createHorizontalStrut(5));
		glyphPanel.add(gsfVert);
		glyphPanel.add(Box.createHorizontalGlue());
		
		panel.addGenericRow(glyphPanel);
	}
	
	private AutoVerifyField createField(int cols)
	{
		AutoVerifyField avf = new AutoVerifyField(cols, this, "");
		avf.setText("");
		avf.setBackground(null);
		avf.setOpaque(false);
		return avf;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		JComboBox box = (JComboBox) e.getSource();
		
		if(box.equals(packChooser))
		{
			if(box.getSelectedIndex() == PACKED)
			{
				bpfUp.setText("0");
				bpfRight.setText("0");
				bpfDown.setText("0");
				bpfLeft.setText("0");
			}
			else
			{
				bpfUp.setText("10");
				bpfRight.setText("10");
				bpfDown.setText("10");
				bpfLeft.setText("10");
			}
		}
	}
	
	public void applyChanges()
	{
		int[] borderPadding = new int[]
		{
			Integer.parseInt(bpfUp.getText()),
			Integer.parseInt(bpfRight.getText()),
			Integer.parseInt(bpfDown.getText()),
			Integer.parseInt(bpfLeft.getText())
		};
		int[] glyphPadding = new int[]
		{
			Integer.parseInt(gpfUp.getText()),
			Integer.parseInt(gpfRight.getText()),
			Integer.parseInt(gpfDown.getText()),
			Integer.parseInt(gpfLeft.getText())
		};
		int[] glyphSpacing = new int[]
		{
			Integer.parseInt(gsfHorz.getText()),
			Integer.parseInt(gsfVert.getText())
		};
		int width = Integer.parseInt(widthField.getText());
		
		BufferedImage result = null;
		if(packChooser.getSelectedIndex() == PACKED)
			result = GlyphLayoutTransformer.pack(area.img, font, width, borderPadding, glyphPadding, glyphSpacing);
		else
			result = GlyphLayoutTransformer.unpack(area.img, font, width, borderPadding, glyphPadding, glyphSpacing);
		
		area.img = result;
		area.width = result.getWidth();
		area.height = result.getHeight();
		area.repaint();
		
		setVisible(false);
	}
	
	public boolean verify()
	{
		boolean ok = (widthField.verify() && 
				      bpfUp.verify() && bpfRight.verify() && bpfDown.verify() && bpfLeft.verify() &&
				      gpfUp.verify() && gpfRight.verify() && gpfDown.verify() && gpfLeft.verify());
		
		okButton.setEnabled(ok);
		return ok;
	}
	
	public boolean verifyText(JTextField field, String text)
	{
		boolean result = false;
		boolean ok = false;
		
		result = verifyNumber(text);
		
		if(avfs == null)
			return result;
		
		for(AutoVerifyField avf : avfs)
		{
			if(field.equals(avf.getTextField()))
				avf.setState(result);
		}
		
		ok = true;
		
		for(AutoVerifyField avf : avfs)
		{
			if(avf.isBadInput())
				ok = false;
		}
		
		okButton.setEnabled(ok);
		return result;
	}
	
	private boolean verifyNumber(String text)
	{
		if(isEmpty(text))
		{
			return false;
		}
		
		text = text.trim();
		
		try
		{
			int i = Integer.parseInt(text);
			
			if(i < 0)
			{
				return false;
			}
		}
		
		catch(NumberFormatException e)
		{
			return false;
		}
		
		return true;
	}
	
	private boolean isEmpty(String text)
	{
		if(text == null)
		{
			return true;
		}
		
		text = text.trim();
		
		return (text.length() <= 0);
	}
	
	public void dispose()
	{
		removeAll();
		
		widthField.dispose();
		packChooser = null;
		bpfUp.dispose();
		bpfRight.dispose();
		bpfDown.dispose();
		bpfLeft.dispose();
		gpfUp.dispose();
		gpfRight.dispose();
		gpfDown.dispose();
		gpfLeft.dispose();
		
		avfs = null;
		font = null;
		area = null;
		okButton = null;
	}
}