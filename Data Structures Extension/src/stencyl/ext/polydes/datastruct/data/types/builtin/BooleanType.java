package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class BooleanType extends BuiltinType<Boolean>
{
	public BooleanType()
	{
		super(Boolean.class, "Bool", "BOOLEAN", "Boolean");
	}

	@Override
	public JComponent[] getEditor(final DataUpdater<Boolean> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		final JCheckBox editor = new JCheckBox();
		if(updater.get() == null)
			updater.set(false);
		editor.setSelected(updater.get());
		editor.setBackground(null);
		
		editor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				updater.set(editor.isSelected());
			}
		});

		return comps(editor);
	}

	@Override
	public Boolean decode(String s)
	{
		return s.equals("true");
	}

	@Override
	public String encode(Boolean b)
	{
		return b ? "true" : "false";
	}

	@Override
	public Boolean copy(Boolean t)
	{
		return new Boolean(t);
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._Boolean, false);
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		if(e.defaultValue != null)
			emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		return emap;
	}
	
	class Extras extends ExtraProperties
	{
		public Boolean defaultValue;
	}
}
