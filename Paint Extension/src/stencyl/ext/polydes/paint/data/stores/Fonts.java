package stencyl.ext.polydes.paint.data.stores;

import java.io.File;

public class Fonts extends FontStore
{
	private static Fonts _instance;
	
	private Fonts()
	{
		super("Fonts");
	}
	
	public static Fonts get()
	{
		if(_instance == null)
			_instance = new Fonts();
		
		return _instance;
	}
	
	@Override
	public void load(File file)
	{
		super.fontsLoad(file);
	}
	
	@Override
	public void saveChanges(File file)
	{
		super.fontsSave(file);
	}
}
