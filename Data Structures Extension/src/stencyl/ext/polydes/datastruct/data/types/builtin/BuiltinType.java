package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.util.List;

import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.io.Text;
import stencyl.ext.polydes.datastruct.res.Resources;

public abstract class BuiltinType<T> extends DataType<T>
{
	public BuiltinType(Class<T> javaType, String haxeType, String stencylType, String xml)
	{
		super(javaType, haxeType, stencylType, xml);
	}
		
	@Override
	public List<String> generateHaxeClass()
	{
		return null;
	}

	@Override
	public List<String> generateHaxeReader()
	{
		return Text.readLines(Resources.getUrlStream("code/haxer/" + xml + ".hx"));
	}
	
	@Override
	public String toDisplayString(T data)
	{
		return String.valueOf(data);
	}
}