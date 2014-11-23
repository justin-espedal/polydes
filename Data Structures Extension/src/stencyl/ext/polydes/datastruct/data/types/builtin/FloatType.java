package stencyl.ext.polydes.datastruct.data.types.builtin;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
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
import stencyl.ext.polydes.datastruct.ui.utils.FloatFilter;
import stencyl.sw.util.VerificationHelper;

public class FloatType extends BuiltinType<Float>
{
	public FloatType()
	{
		super(Float.class, "Float", "NUMBER", "Float");
	}

	@Override
	public JComponent[] getEditor(final DataUpdater<Float> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		Extras e = (Extras) extras;
		float min1 = or(e.min, -Float.MAX_VALUE);
		final float max = or(e.max, Float.MAX_VALUE);
		final float min = min1 > max ? max : min1;
		if(updater.get() == null)
			updater.set(0f);
		if(min > updater.get())
			updater.set(min);
		if(max < updater.get())
			updater.set(max);
		
		if(e.editor.equals(Editor.Slider))
		{
			final float decimalPlaces = or(e.decimalPlaces, 2);
			final float factor = (float) Math.pow(10, decimalPlaces);
			
			final JTextField text = style.createTextField();
			((PlainDocument) text.getDocument()).setDocumentFilter(new FloatFilter());
			text.setText("" + updater.get());
			final JSlider slider = new JSlider();
			slider.setBackground(null);
			slider.setMinimum((int) (min * factor));
			slider.setMaximum((int) (max * factor));
			slider.setOrientation(JSlider.HORIZONTAL);
			slider.setValue((int) (updater.get() * factor));
			slider.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					if(!text.getText().equals("" + (slider.getValue() / factor)))
						SwingUtilities.invokeLater(new Runnable()
						{
							@Override
							public void run()
							{
								text.setText("" + (slider.getValue() / factor));
							}
						});
				}
			});
			
			text.getDocument().addDocumentListener(new DocumentAdapter(true)
			{
				@Override
				protected void update()
				{
					if(VerificationHelper.isFloat(text.getText()))
					{
						float val = Float.parseFloat(text.getText());
						if(val < min)
							val = min;
						if(val > max)
							val = max;
						
						if(slider.getValue() != (int) (val * factor))
							slider.setValue((int) (val * factor));
						
						updater.set(val);
					}
				}
			});
			
			return comps(slider, text);
		}
		else if(e.editor.equals(Editor.Spinner))
		{
			final float step = or(e.step, .01f);
			
			SpinnerNumberModel model = new SpinnerNumberModel((float) updater.get(), min, max, step); 
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
					if(VerificationHelper.isFloat(text.getText()))
					{
						float val = Float.parseFloat(text.getText());
						updater.set(val);
					}
				}
			});
			
			return comps(spinner);
		}
		else //if(editorType.equals("Plain"))
		{
			final JTextField editor = style.createTextField();
			((PlainDocument) editor.getDocument()).setDocumentFilter(new FloatFilter());
			editor.setText("" + updater.get());
	
			editor.getDocument().addDocumentListener(new DocumentAdapter(true)
			{
				@Override
				protected void update()
				{
					if(VerificationHelper.isFloat(editor.getText()))
						updater.set(Float.parseFloat(editor.getText()));
				}
			});
			
			return comps(editor);
		}
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
		e.decimalPlaces = extras.get("decimalPlaces", Types._Integer, null);
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
	
	class Extras extends ExtraProperties
	{
		public Editor editor;
		public Float min;
		public Float max;
		public Integer decimalPlaces;
		public Float step;
		public Float defaultValue;
	}
	
	enum Editor
	{
		Slider,
		Spinner,
		Plain
	}
}
