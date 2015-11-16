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
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

import stencyl.sw.util.VerificationHelper;

public class IntType extends DataType<Integer>
{
	public IntType()
	{
		super(Integer.class);
	}
	
	public static final String MIN = "min";
	public static final String MAX = "max";
	public static final String STEP = "step";
	
	@Override
	public DataEditor<Integer> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		int min1 = or(props.<Integer>get(MIN), Integer.MIN_VALUE);
		final int max = or(props.<Integer>get(MAX), Integer.MAX_VALUE);
		final int min = min1 > max ? max : min1;
		
		IntegerEditor editor = null;
		
		switch(or(props.<Editor>get(EDITOR), Editor.Plain))
		{
			case Slider:
				editor = new SliderIntegerEditor(props, style);
				break;
			case Spinner:
				editor = new SpinnerIntegerEditor(props, style);
				break;
			default:
				editor = new PlainIntegerEditor(style);
				break;
		}
		
		editor.setRange(min, max);
		
		return editor;
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new IntEditorBuilder();
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
	
	public static enum Editor
	{
		Slider,
		Spinner,
		Plain
	}
	
	public class IntEditorBuilder extends DataEditorBuilder
	{
		public IntEditorBuilder()
		{
			super(IntType.this, new EditorProperties(){{
				put(EDITOR, Editor.Plain);
			}});
		}
		
		public IntEditorBuilder spinnerEditor()
		{
			props.put(EDITOR, Editor.Spinner);
			return this;
		}
		
		public IntEditorBuilder sliderEditor()
		{
			props.put(EDITOR, Editor.Slider);
			return this;
		}
		
		public IntEditorBuilder min(int min)
		{
			props.put(MIN, min);
			return this;
		}
		
		public IntEditorBuilder max(int max)
		{
			props.put(MAX, max);
			return this;
		}
		
		public IntEditorBuilder step(int step)
		{
			props.put(STEP, step);
			return this;
		}
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
		private final JSpinner spinner;
		private SpinnerNumberModel model;
		
		public SpinnerIntegerEditor(EditorProperties props, PropertiesSheetStyle style)
		{
			int step = or(props.get(STEP), 1);
			
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
		}
	}
	
	public static class SliderIntegerEditor extends IntegerEditor
	{
		private final JSlider slider;
		
		public SliderIntegerEditor(EditorProperties props, PropertiesSheetStyle style)
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
