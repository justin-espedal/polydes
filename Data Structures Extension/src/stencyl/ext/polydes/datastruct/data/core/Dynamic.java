package stencyl.ext.polydes.datastruct.data.core;

import stencyl.core.lib.io.read.ActorTypeReader;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.sw.editors.snippet.designer.AttributeType;

public class Dynamic extends ActorTypeReader.ListElement
{
	public String type;
	
	public Dynamic(Object value, String type)
	{
		super(value, AttributeType.fromTag(type));
		this.type = type;
	}

	@Override
	public String toString()
	{
		return Types.fromXML(type).checkToDisplayString(value);
	}
}