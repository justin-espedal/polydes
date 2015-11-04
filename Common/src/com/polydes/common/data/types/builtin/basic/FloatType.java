package com.polydes.common.data.types.builtin.basic;

import static com.polydes.common.util.Lang.array;
import static com.polydes.common.util.Lang.or;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.PlainDocument;

import com.polydes.common.comp.OutlinelessSpinner;
import com.polydes.common.comp.utils.DocumentAdapter;
import com.polydes.common.comp.utils.FloatFilter;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.ExtraProperties;
import com.polydes.common.data.types.ExtrasMap;
import com.polydes.common.data.types.Types;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

import stencyl.sw.util.VerificationHelper;

public class FloatType extends DataType<Float>
{
	public FloatType()
	{
		super(Float.class);
	}

	@Override
	public DataEditor<Float> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		Extras e = (Extras) extras;
		float min1 = or(e.min, -Float.MAX_VALUE);
		final float max = or(e.max, Float.MAX_VALUE);
		final float min = min1 > max ? max : min1;
		
		FloatEditor editor = null;
		
		if(e.editor.equals(Editor.Slider))
			editor = new SliderFloatEditor(e, style);
		else if(e.editor.equals(Editor.Spinner))
			editor = new SpinnerFloatEditor(e, style);
		else //if(editorType.equals("Plain"))
			editor = new PlainFloatEditor(style);
		
		editor.setRange(min, max);
		
		return editor;
	}

	@Override
	public Float decode(String s)
	{
		try
		{
			return Float.parseFloat(s);
		}
		catch(NumberFormatException ex)
		{
			return 0f;
		}
	}

	@Override
	public String encode(Float f)
	{
		return "" + f;
	}
	
	@Override
	public Float copy(Float t)
	{
		return new Float(t);
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		e.editor = extras.get(EDITOR, Editor.Plain);
		e.min = extras.get("min", Types._Float, null);
		e.max = extras.get("max", Types._Float, null);
		e.decimalPlaces = extras.get("decimalPlaces", Types._Int, null);
		e.step = extras.get("step", Types._Float, 0.01f);
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._Float, 0.0f);
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		emap.put(EDITOR, "" + e.editor);
		if(e.min != null)
			emap.put("min", "" + e.min);
		if(e.max != null)
			emap.put("max", "" + e.max);
		if(e.decimalPlaces != null)
			emap.put("decimalPlaces", "" + e.decimalPlaces);
		emap.put("step", "" + e.step);
		emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		return emap;
	}
	
	public static class Extras extends ExtraProperties
	{
		public Editor editor;
		public Float min;
		public Float max;
		public Integer decimalPlaces;
		public Float step;
		public Float defaultValue;
		
		@Override
		public Object getDefault()
		{
			return defaultValue;
		}
	}
	
	public static enum Editor
	{
		Slider,
		Spinner,
		Plain
	}
	
	public static abstract class FloatEditor extends DataEditor<Float>
	{
		protected JTextField field;
		
		public void setRange(float min, float max)
		{
			((PlainDocument) field.getDocument()).setDocumentFilter(new FloatFilter(min, max));
		}
		
		@Override
		public Float getValue()
		{
			if(field.getText().isEmpty())
				return null;
			else
				return Float.parseFloat(field.getText());
		}
		
		@Override
		public void set(Float t)
		{
			field.setText(t == null ? "" : "" + t);
		}
		
		@Override
		public void dispose()
		{
			field = null;
		}
	}
	
	public static class PlainFloatEditor extends FloatEditor
	{
		public PlainFloatEditor(PropertiesSheetStyle style)
		{
			field = style.createTextField();
			field.getDocument().addDocumentListener(new DocumentAdapter(true)
			{
				@Override
				protected void update()
				{
					if(VerificationHelper.isFloat(field.getText()))
						updated();
				}
			});
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return array(field);
		}
	}
	
	public static class SpinnerFloatEditor extends FloatEditor
	{
		private JSpinner spinner;
		private SpinnerNumberModel model;
		
		public SpinnerFloatEditor(Extras e, PropertiesSheetStyle style)
		{
			float step = or(e.step, .01f);
			
			model = new SpinnerNumberModel(0f, 0f, 0f, step);
			spinner = new OutlinelessSpinner(model);
			field = ((NumberEditor) spinner.getEditor()).getTextField();
			spinner.setBorder
			(
				BorderFactory.createCompoundBorder
				(
					BorderFactory.createLineBorder(style.fieldBorder != null ? style.fieldBorder : style.fieldBg, 1),
					BorderFactory.createLineBorder(style.fieldBg, 2)
				)
			);
			field.setBackground(style.fieldBg);
			field.setForeground(style.fieldtextColor);
			spinner.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					if(VerificationHelper.isFloat(field.getText()))
						updated();
				}
			});
		}
		
		@Override
		public void setRange(float min, float max)
		{
			super.setRange(min, max);
			model.setMinimum(min);
			model.setMaximum(max);
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return array(spinner);
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			model = null;
			spinner = null;
		}
	}
	
	public static class SliderFloatEditor extends FloatEditor
	{
		private JSlider slider;
		private float factor;
		
		public SliderFloatEditor(Extras e, PropertiesSheetStyle style)
		{
			field = style.createTextField();
			
			int decimalPlaces = or(e.decimalPlaces, 2);
			factor = (float) Math.pow(10, decimalPlaces);
			
			slider = new JSlider();
			slider.setBackground(null);
			slider.setOrientation(JSlider.HORIZONTAL);
			slider.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					if(!field.getText().equals("" + (slider.getValue() / factor)))
						SwingUtilities.invokeLater(new Runnable()
						{
							@Override
							public void run()
							{
								field.setText("" + (slider.getValue() / factor));
							}
						});
				}
			});
			
			field.getDocument().addDocumentListener(new DocumentAdapter(true)
			{
				@Override
				protected void update()
				{
					if(VerificationHelper.isFloat(field.getText()))
					{
						float val = Float.parseFloat(field.getText());
						
						if(slider.getValue() != (int) (val * factor))
							slider.setValue((int) (val * factor));
						
						updated();
					}
				}
			});
		}
		
		@Override
		public void setRange(float min, float max)
		{
			super.setRange(min, max);
			slider.setMinimum((int) (min * factor));
			slider.setMaximum((int) (max * factor));	
		}
		
		@Override
		public void set(Float t)
		{
			if(t == null)
				t = 0f;
			slider.setValue((int) (t * factor));
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return array(slider, field);
		}
	}
}
