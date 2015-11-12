package com.polydes.common.data.types.builtin.basic;

import static com.polydes.common.util.Lang.or;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.PlainDocument;

import com.polydes.common.comp.OutlinelessSpinner;
import com.polydes.common.comp.utils.DocumentAdapter;
import com.polydes.common.comp.utils.IntegerFilter;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.ExtraProperties;
import com.polydes.common.data.types.ExtrasMap;
import com.polydes.common.data.types.Types;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

import stencyl.sw.util.VerificationHelper;

public class IntType extends DataType<Integer>
{
	public IntType()
	{
		super(Integer.class);
	}

	@Override
	public DataEditor<Integer> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		Extras e = (Extras) extras;
		int min1 = or(e.min, Integer.MIN_VALUE);
		final int max = or(e.max, Integer.MAX_VALUE);
		final int min = min1 > max ? max : min1;
		
		IntegerEditor editor = null;
		
		if(e.editor.equals(Editor.Slider))
			editor = new SliderIntegerEditor(e, style);
		else if(e.editor.equals(Editor.Spinner))
			editor = new SpinnerIntegerEditor(e, style);
		else //if(editorType.equals("Plain"))
			editor = new PlainIntegerEditor(style);
		
		editor.setRange(min, max);
		
		return editor;
	}

	@Override
	public Integer decode(String s)
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch(NumberFormatException ex)
		{
			return 0;
		}
	}

	@Override
	public String encode(Integer i)
	{
		return "" + i;
	}
	
	@Override
	public Integer copy(Integer t)
	{
		return new Integer(t);
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		e.editor = extras.get(EDITOR, Editor.Plain);
		e.min = extras.get("min", Types._Int, null);
		e.max = extras.get("max", Types._Int, null);
		e.step = extras.get("step", Types._Int, 1);
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._Int, 0);
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
		emap.put("step", "" + e.step);
		emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		return emap;
	}
	
	public static class Extras extends ExtraProperties
	{
		public Editor editor;
		public Integer min;
		public Integer max;
		public Integer step;
		public Integer defaultValue;
		
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
	
	public static abstract class IntegerEditor extends DataEditor<Integer>
	{
		protected JTextField field;
		
		public void setRange(int min, int max)
		{
			((PlainDocument) field.getDocument()).setDocumentFilter(new IntegerFilter(min, max));
		}
		
		@Override
		public Integer getValue()
		{
			if(field.getText().isEmpty())
				return null;
			else
				return Integer.parseInt(field.getText());
		}
		
		@Override
		public void set(Integer t)
		{
			field.setText(t == null ? "" : "" + t);
		}
		
		@Override
		public void dispose()
		{
			field = null;
		}
	}
	
	public static class PlainIntegerEditor extends IntegerEditor
	{
		public PlainIntegerEditor(PropertiesSheetStyle style)
		{
			field = style.createTextField();
			field.getDocument().addDocumentListener(new DocumentAdapter(true)
			{
				@Override
				protected void update()
				{
					if(VerificationHelper.isInteger(field.getText()))
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
	
	public static class SpinnerIntegerEditor extends IntegerEditor
	{
		private JSpinner spinner;
		private SpinnerNumberModel model;
		
		public SpinnerIntegerEditor(Extras e, PropertiesSheetStyle style)
		{
			int step = or(e.step, 1);
			
			model = new SpinnerNumberModel(0, 0, 0, step);
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
					if(VerificationHelper.isInteger(field.getText()))
						updated();
				}
			});
		}
		
		@Override
		public void setRange(int min, int max)
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
			spinner = null;
		}
	}
	
	public static class SliderIntegerEditor extends IntegerEditor
	{
		private JSlider slider;
		
		public SliderIntegerEditor(Extras e, PropertiesSheetStyle style)
		{
			field = style.createTextField();
			
			slider = new JSlider();
			slider.setBackground(null);
			slider.setOrientation(JSlider.HORIZONTAL);
			slider.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					if(!field.getText().equals("" + slider.getValue()))
						field.setText("" + slider.getValue());
				}
			});
			
			field.getDocument().addDocumentListener(new DocumentAdapter(true)
			{
				@Override
				protected void update()
				{
					if(VerificationHelper.isInteger(field.getText()))
					{
						int val = Integer.parseInt(field.getText());
						
						if(slider.getValue() != val)
							slider.setValue(val);
						
						updated();
					}
				}
			});
		}
		
		@Override
		public void setRange(int min, int max)
		{
			super.setRange(min, max);
			slider.setMinimum(min);
			slider.setMaximum(max);	
		}
		
		@Override
		public void set(Integer t)
		{
			if(t == null)
				t = 0;
			slider.setValue(t);
		}
		
		@Override
		public Integer getValue()
		{
			return slider.getValue();
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return new JComponent[] {slider, field};
		}
	}
}
