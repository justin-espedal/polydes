package stencyl.ext.polydes.datastruct.data.types.hidden;

import java.util.List;

import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;

public abstract class HiddenType<T> extends DataType<T>
{
	public HiddenType(Class<T> cls, String name)
	{
		super(cls, "", "", name);
	}
	
	@Override
	public List<String> generateHaxeClass()
	{
		return null;
	}

	@Override
	public List<String> generateHaxeReader()
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