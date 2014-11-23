package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.awt.Color;

import javax.swing.JComponent;

import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.utils.ColorUtil;

public class ColorType extends BuiltinType<Color>
{
	public ColorType()
	{
		super(Color.class, "Int", "COLOR", "Color");
	}

	@Override
	public JComponent[] getEditor(final DataUpdater<Color> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		//TODO: create color editor.
		return null;
	}

	@Override
	public Color decode(String s)
	{
		return ColorUtil.decode(s);
	}

	@Override
	public String encode(Color c)
	{
		return ColorUtil.encode(c);
	}

	@Override
	public Color copy(Color t)
	{
		return null;
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		return null;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		return null;
	}
}
