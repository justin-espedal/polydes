package stencyl.ext.polydes.datastruct.data.types.builtin;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.PlainDocument;

import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.ui.comp.OutlinelessSpinner;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.DocumentAdapter;
import stencyl.ext.polydes.datastruct.ui.utils.IntegerFilter;
import stencyl.sw.util.VerificationHelper;

public class IntType extends BuiltinType<Integer>
{
	public IntType()
	{
		super(Integer.class, "Int", "NUMBER", "Int");
	}

	@Override
	public JComponent[] getEditor(final DataUpdater<Integer> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		Extras e = (Extras) extras;
		int min1 = or(e.min, Integer.MIN_VALUE);
		final int max = or(e.max, Integer.MAX_VALUE);
		final int min = min1 > max ? max : min1;
		if(updater.get() == null)
			updater.set(0);
		if(min > updater.get())
			updater.set(min);
		if(max < updater.get())
			updater.set(max);
		
		if(e.editor.equals(Editor.Slider))
		{
			final JTextField text = style.createTextField();
			((PlainDocument) text.getDocument()).setDocumentFilter(new IntegerFilter());
			text.setText("" + updater.get());
			final JSlider slider = new JSlider();
			slider.setBackground(null);
			slider.setMinimum(min);
			slider.setMaximum(max);
			slider.setOrientation(JSlider.HORIZONTAL);
			slider.setValue(updater.get());
			slider.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					if(!text.getText().equals("" + slider.getValue()))
						text.setText("" + slider.getValue());
				}
			});
			
			text.getDocument().addDocumentListener(new DocumentAdapter(true)
			{
				@Override
				protected void update()
				{
					if(VerificationHelper.isInteger(text.getText()))
					{
						int val = Integer.parseInt(text.getText());
						if(val < min)
							val = min;
						if(val > max)
							val = max;
						
						if(slider.getValue() != val)
							slider.setValue(val);
						
						updater.set(val);
					}
					else
						updater.set(0);
				}
			});
			
			return comps(slider, text);
		}
		else if(e.editor.equals(Editor.Spinner))
		{
			final int step = or(e.step, 1);
			
			SpinnerNumberModel model = new SpinnerNumberModel((int) updater.get(), min, max, step); 
			final JSpinner spinner = new OutlinelessSpinner(model);
			final JFormattedTextField text = ((NumberEditor) spinner.getEditor()).getTextField();
			spinner.setBorder
			(
				BorderFactory.createCompoundBorder
				(
					BorderFactory.createLineBorder(style.fieldBorder != null ? style.fieldBorder : style.fieldBg, 1),
					BorderFactory.createLineBorder(style.fieldBg, 2)
				)
			);
			text.setBackground(style.fieldBg);
			text.setForeground(style.fieldtextColor);
			spinner.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					if(VerificationHelper.isInteger(text.getText()))
					{
						int val = Integer.parseInt(text.getText());
						updater.set(val);
					}
				}
			});
			
			return comps(spinner);
		}
		else //if(editorType.equals("Plain"))
		{
			final JTextField editor = style.createTextField();
			((PlainDocument) editor.getDocument()).setDocumentFilter(new IntegerFilter());
			editor.setText("" + updater.get());
	
			editor.getDocument().addDocumentListener(new DocumentAdapter(true)
			{
				@Override
				protected void update()
				{
					if(VerificationHelper.isInteger(editor.getText()))
						updater.set(Integer.parseInt(editor.getText()));
				}
			});
			
			return comps(editor);
		}
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
		e.min = extras.get("min", Types._Integer, null);
		e.max = extras.get("max", Types._Integer, null);
		e.step = extras.get("step", Types._Integer, 1);
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._Integer, 0);
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
	
	class Extras extends ExtraProperties
	{
		public Editor editor;
		public Integer min;
		public Integer max;
		public Integer step;
		public Integer defaultValue;
	}
	
	enum Editor
	{
		Slider,
		Spinner,
		Plain
	}
}
