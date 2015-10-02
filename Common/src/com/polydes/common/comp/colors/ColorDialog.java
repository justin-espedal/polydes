package com.polydes.common.comp.colors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import stencyl.sw.SW;

public class ColorDialog extends JWindow implements ActionListener
{
	public static final int WIDTH = 340;
	public static final int HEIGHT = 195;
	
	public static final int SLIDE_WIDTH = 120;
	public static final int SLIDE_HEIGHT = 14;
	
	HueModel hueModel;
	SaturationModel saturationModel;
	ValueModel valueModel;
	BaseColorModel redModel;
	BaseColorModel greenModel;
	BaseColorModel blueModel;
	AlphaModel alphaModel;
	
	HSVModel hsvModel;
	RGBModel rgbModel;
	
	ColorValueSlider hueSlider;
	ColorValueSlider saturationSlider;
	ColorValueSlider valueSlider;
	ColorValueSlider redSlider;
	ColorValueSlider greenSlider;
	ColorValueSlider blueSlider;
	AlphaValueSlider alphaSlider;
	
	ColorValueSlider colorSlider;
	ColorValueGrid colorGrid;
	
	ColorDisplay display;
	
	ArrayList<ChangeListener> listeners;
	
	public ColorDialog(ColorDisplay display, Window window)
	{
		super(window != null ? window : SW.get());
		setFocusable(true);
		
		setSize(WIDTH, HEIGHT);
		JPanel main = new JPanel();
		main.setBorder(BorderFactory.createRaisedBevelBorder());
		
		this.display = display;
		
		JPanel center = new JPanel();
		JPanel gridPanel = new JPanel();
		JPanel slidersPanel = new JPanel();
		JPanel south = new JPanel();
		
		slidersPanel.setLayout(new BoxLayout(slidersPanel, BoxLayout.Y_AXIS));
		south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
		
		center.add(gridPanel, BorderLayout.WEST);
		center.add(slidersPanel, BorderLayout.EAST);
		main.add(center, BorderLayout.CENTER);
		main.add(south, BorderLayout.SOUTH);
		
		hsvModel = new HSVModel();
		rgbModel = new RGBModel();
		hueModel = new HueModel();
		saturationModel = new SaturationModel();
		valueModel = new ValueModel();
		redModel = new BaseColorModel(BaseColorModel.RED);
		greenModel = new BaseColorModel(BaseColorModel.GREEN);
		blueModel = new BaseColorModel(BaseColorModel.BLUE);
		alphaModel = new AlphaModel();
		
		redModel.setValues(0, 0);
		greenModel.setValues(0, 0);
		blueModel.setValues(0, 0);
		
		colorSlider = new ColorValueSlider(15, 159, hueModel);
		colorGrid = new ColorValueGrid(150, 159, hsvModel);
		
		gridPanel.add(colorGrid, BorderLayout.CENTER);
		gridPanel.add(colorSlider, BorderLayout.EAST);
		
		hueSlider = new ColorValueSlider(SLIDE_WIDTH, SLIDE_HEIGHT, hueModel);
		saturationSlider = new ColorValueSlider(SLIDE_WIDTH, SLIDE_HEIGHT, saturationModel);
		valueSlider = new ColorValueSlider(SLIDE_WIDTH, SLIDE_HEIGHT, valueModel);
		redSlider = new ColorValueSlider(SLIDE_WIDTH, SLIDE_HEIGHT, redModel);
		greenSlider = new ColorValueSlider(SLIDE_WIDTH, SLIDE_HEIGHT, greenModel);
		blueSlider = new ColorValueSlider(SLIDE_WIDTH, SLIDE_HEIGHT, blueModel);
		alphaSlider = new AlphaValueSlider(SLIDE_WIDTH, SLIDE_HEIGHT, alphaModel);
		
		alphaSlider.value = 1;
		
		slidersPanel.add(hueSlider);
		slidersPanel.add(Box.createRigidArea(new Dimension(1, 5)));
		slidersPanel.add(saturationSlider);
		slidersPanel.add(Box.createRigidArea(new Dimension(1, 5)));
		slidersPanel.add(valueSlider);
		slidersPanel.add(Box.createRigidArea(new Dimension(1, 15)));
		slidersPanel.add(redSlider);
		slidersPanel.add(Box.createRigidArea(new Dimension(1, 5)));
		slidersPanel.add(greenSlider);
		slidersPanel.add(Box.createRigidArea(new Dimension(1, 5)));
		slidersPanel.add(blueSlider);
		slidersPanel.add(Box.createRigidArea(new Dimension(1, 15)));
		slidersPanel.add(alphaSlider);
		
		listeners = new ArrayList<ChangeListener>();
		
		hueSlider.addActionListener(this);
		saturationSlider.addActionListener(this);
		valueSlider.addActionListener(this);
		redSlider.addActionListener(this);
		greenSlider.addActionListener(this);
		blueSlider.addActionListener(this);
		alphaSlider.addActionListener(this);
		colorSlider.addActionListener(this);
		colorGrid.addActionListener(this);
		/*
		JButton confirmButton = new JButton("OK");
		confirmButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//setVisible(false);
				dispose();
			}
		});
		*/
		requestFocus();
		
		addWindowFocusListener(new WindowAdapter()
		{
			@Override
			public void windowLostFocus(WindowEvent e)
			{
				dispose();
				setVisible(false);
			}
		});
		
		//south.add(confirmButton);
		
		add(main);
	}
	
	public void updateSliders(int red, int green, int blue, int alpha)
	{
		float[] hsv = Color.RGBtoHSB(red, green, blue, null);
		updateSliders(hsv[0], hsv[1], hsv[2], red, green, blue, alpha);
	}
	
	public void updateSliders(float hue, float saturation, float value, float alpha)
	{
		int color = Color.HSBtoRGB(hue, saturation, value);
		int red = 0xFF & (color >> 16);
		int green = 0xFF & (color >> 8);
		int blue = 0xFF & (color);
		
		updateSliders(hue, saturation, value, red, green, blue, (int) (alpha * 255)); 
	}
	
	public void updateSliders(float hue, float saturation, float value, int red, int green, int blue, int alpha)
	{
		float rv = red / 255f;
		float gv = green / 255f;
		float bv = blue / 255f;
		float av = alpha / 255f;
		
		hueSlider.value = hue;
		saturationSlider.value = saturation;
		valueSlider.value = value;
		redSlider.value = rv;
		greenSlider.value = gv;
		blueSlider.value = bv;
		alphaSlider.value = av;
		
		if(colorSlider.model == hueModel)
			colorSlider.value = hue;
		else if(colorSlider.model == saturationModel)
			colorSlider.value = saturation;
		else if(colorSlider.model == valueModel)
			colorSlider.value = value;
		else if(colorSlider.model == redModel)
			colorSlider.value = rv;
		else if(colorSlider.model == greenModel)
			colorSlider.value = gv;
		else if(colorSlider.model == blueModel)
			colorSlider.value = bv;
		
		if(colorGrid.model == hsvModel)
		{
			if(hsvModel.mode == HSVModel.HUE)
			{
				colorGrid.yValue = saturation;
				colorGrid.xValue = value;
			}
			else if(hsvModel.mode == HSVModel.SATURATION)
			{
				colorGrid.yValue = hue;
				colorGrid.xValue = value;
			}
			else if(hsvModel.mode == HSVModel.VALUE)
			{
				colorGrid.yValue = hue;
				colorGrid.xValue = saturation;
			}
		}
		else if(colorGrid.model == rgbModel)
		{
			if(hsvModel.mode == RGBModel.RED)
			{
				colorGrid.yValue = bv;
				colorGrid.xValue = gv;
			}
			else if(hsvModel.mode == RGBModel.GREEN)
			{
				colorGrid.yValue = bv;
				colorGrid.xValue = rv;
			}
			else if(hsvModel.mode == RGBModel.BLUE)
			{
				colorGrid.yValue = gv;
				colorGrid.xValue = rv;
			}
		}
		
		saturationModel.updateHue(hue);
		saturationModel.updateValue(value);
		valueModel.updateHue(hue);
		valueModel.updateSaturation(saturation);
		redModel.setValues(green, blue);
		greenModel.setValues(red, blue);
		blueModel.setValues(red, green);
		alphaModel.updateHue(hue);
		alphaModel.updateSaturation(saturation);
		alphaModel.updateValue(value);
		hsvModel.setPrimary(HSVModel.HUE, hue);
		
		hueSlider.updateImg();
		saturationSlider.updateImg();
		valueSlider.updateImg();
		redSlider.updateImg();
		greenSlider.updateImg();
		blueSlider.updateImg();
		alphaSlider.updateImg();
		colorSlider.updateImg();
		colorGrid.updateImg();
		
		display.setColor(new Color(rv, gv, bv, av));
		
		hueSlider.repaint();
		saturationSlider.repaint();
		valueSlider.repaint();
		redSlider.repaint();
		greenSlider.repaint();
		blueSlider.repaint();
		alphaSlider.repaint();
		colorSlider.repaint();
		colorGrid.repaint();
		
		display.repaint();
		
		for(ChangeListener l : listeners)
		{
			l.stateChanged(new ChangeEvent(this));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		if(src == redSlider || src == greenSlider || src == blueSlider)
		{
			updateSliders((int) (redSlider.value * 255), (int) (greenSlider.value * 255), (int) (blueSlider.value * 255), (int) (alphaSlider.value * 255));
		}
		else if(src == hueSlider || src == saturationSlider || src == valueSlider)
		{
			updateSliders(hueSlider.value, saturationSlider.value, valueSlider.value, alphaSlider.value);
		}
		else if(src == colorGrid)
		{
			if(colorGrid.model == hsvModel)
			{
				switch(hsvModel.mode)
				{
					case HSVModel.HUE:
						updateSliders(hueSlider.value, colorGrid.yValue, colorGrid.xValue, alphaSlider.value);
						break;
					case HSVModel.SATURATION:
						updateSliders(colorGrid.yValue, saturationSlider.value, colorGrid.xValue, alphaSlider.value);
						break;
					case HSVModel.VALUE:
						updateSliders(colorGrid.yValue, colorGrid.xValue, valueSlider.value, alphaSlider.value);
						break;
				}
			}
		}
		else if(src == colorSlider)
		{
			updateSliders(colorSlider.value, saturationSlider.value, valueSlider.value, alphaSlider.value);
		}
		else if(src == alphaSlider)
		{
			display.setColor(new Color(redSlider.value, greenSlider.value, blueSlider.value, alphaSlider.value));
			display.repaint();
			alphaSlider.repaint();
			
			for(ChangeListener l : listeners)
			{
				l.stateChanged(new ChangeEvent(this));
			}
		}
	}

	public void setDisplayColor(Color color)
	{
		float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		hueSlider.value = hsv[0];
		saturationSlider.value = hsv[1];
		valueSlider.value = hsv[2];
		colorSlider.value = hsv[0];
		alphaSlider.value = (color.getAlpha() / 255f);
		
		updateSliders(hsv[0], hsv[1], hsv[2], (color.getAlpha() / 255f));
	}
	
	public Color getColor()
	{
		return display.getColor();
	}
	
	public void addChangeListener(ChangeListener l)
	{
		listeners.add(l);
	}
	
	public void removeChangeListneer(ChangeListener l)
	{
		listeners.remove(l);	
	}
}
