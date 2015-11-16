package com.polydes.common.data.types.builtin.basic;

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
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

import stencyl.sw.util.VerificationHelper;

public class FloatType extends DataType<Float>
{
	public FloatType()
	{
		super(Float.class);
	}
	
	public static final String MIN = "min";
	public static final String MAX = "max";
	public static final String STEP = "step";
	public static final String DECIMAL_PLACES = "decimalPlaces";

	@Override
	public DataEditor<Float> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		float min1 = or(props.<Float>get(MIN), -Float.MAX_VALUE);
		final float max = or(props.<Float>get(MAX), Float.MAX_VALUE);
		final float min = min1 > max ? max : min1;
		
		FloatEditor editor = null;
		
		switch(or(props.<Editor>get(EDITOR), Editor.Plain))
		{
			case Slider:
				editor = new SliderFloatEditor(props, style);
				break;
			case Spinner:
				editor = new SpinnerFloatEditor(props, style);
				break;
			default:
				editor = new PlainFloatEditor(style);
				break;
		}
		
		editor.setRange(min, max);
		
		return editor;
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new FloatEditorBuilder();
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
	
	public class FloatEditorBuilder extends DataEditorBuilder
	{
		public FloatEditorBuilder()
		{
			super(FloatType.this, new EditorProperties(){{
				put(EDITOR, Editor.Plain);
			}});
		}
		
		public FloatEditorBuilder spinnerEditor()
		{
			props.put(EDITOR, Editor.Spinner);
			return this;
		}
		
		public FloatEditorBuilder sliderEditor()
		{
			props.put(EDITOR, Editor.Slider);
			return this;
		}
		
		public FloatEditorBuilder min(float min)
		{
			props.put(MIN, min);
			return this;
		}
		
		public FloatEditorBuilder max(float max)
		{
			props.put(MAX, max);
			return this;
		}
		
		public FloatEditorBuilder step(float step)
		{
			props.put(STEP, step);
			return this;
		}
		
		public FloatEditorBuilder decimalPlaces(int places)
		{
			props.put(DECIMAL_PLACES, places);
			return this;
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
			return new JComponent[] {field};
		}
	}
	
	public static class SpinnerFloatEditor extends FloatEditor
	{
		private final JSpinner spinner;
		private SpinnerNumberModel model;
		
		public SpinnerFloatEditor(EditorProperties props, PropertiesSheetStyle style)
		{
			float step = or(props.<Float>get(STEP), .01f);
			
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
			return new JComponent[] {spinner};
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			model = null;
		}
	}
	
	public static class SliderFloatEditor extends FloatEditor
	{
		private final JSlider slider;
		private float factor;
		
		public SliderFloatEditor(EditorProperties props, PropertiesSheetStyle style)
		{
			field = style.createTextField();
			
			int decimalPlaces = or(props.<Integer>get(DECIMAL_PLACES), 2);
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
			return new JComponent[] {slider, field};
		}
	}
}
