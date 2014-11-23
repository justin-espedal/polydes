package stencyl.ext.polydes.datastruct.data.core;

import stencyl.core.lib.io.read.ActorTypeReader;
import stencyl.ext.polydes.datastruct.data.types.Types;

public class Dynamic extends ActorTypeReader.ListElement
{
	public Dynamic(Object value, String type)
	{
		super(value, type);
	}

	@Override
	public String toString()
	{
		return Types.fromXML(type).checkToDisplayString(value);
	}
}