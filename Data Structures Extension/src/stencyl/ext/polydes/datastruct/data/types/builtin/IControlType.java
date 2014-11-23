package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import stencyl.core.engine.input.IControl;
import stencyl.core.lib.Game;
import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.ui.comp.UpdatingCombo;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class IControlType extends BuiltinType<IControl>
{
	public IControlType()
	{
		super(IControl.class, "String", "CONTROL", "Control");
	}

	@Override
	public JComponent[] getEditor(final DataUpdater<IControl> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		final UpdatingCombo<IControl> editor = new UpdatingCombo<IControl>(Game.getGame().getNewController().values(), null);
		editor.setSelectedItem(updater.get());
		
		editor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				updater.set(editor.getSelected());
			}
		});
		
		return comps(editor);
	}

	@Override
	public IControl decode(String s)
	{
		for(IControl c : Game.getGame().getNewController().values())
			if(c.getName().equals(s))
				return c;
		
		return null;
	}

	@Override
	public String encode(IControl c)
	{
		if(c == null)
			return "";
		
		return c.getName();
	}
	
	@Override
	public IControl copy(IControl t)
	{
		return t;
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._IControl, null);
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
		public IControl defaultValue;
	}
}
