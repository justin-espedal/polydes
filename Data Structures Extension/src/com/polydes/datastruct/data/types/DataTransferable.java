package com.polydes.datastruct.data.types;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class DataTransferable implements Transferable
{
	static DataFlavor serialized;
	
	public static DataFlavor getFlavor()
	{
		if(serialized == null)
		{
			try
			{
				String mimeType = "application/x-java-serialized-object;class=" + SerializedData.class.getName();
				serialized = new DataFlavor(mimeType, null, SerializedData.class.getClassLoader());
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		
		return serialized;
	}
	
	DataFlavor[] flavors = new DataFlavor[2];
	SerializedData sd;
	
	public DataTransferable(DataType<?> type, Object value)
	{
		flavors[0] = getFlavor();
		flavors[1] = DataFlavor.stringFlavor;
		
		sd = new SerializedData(type.haxeType, type.checkEncode(value));
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		DataFlavor[] flavors = getTransferDataFlavors();
		for (int i = 0; i < flavors.length; i++)
			if (flavors[i].equals(flavor))
				return true;
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if(flavor.equals(flavors[0]))
			return sd;
		else if(flavor.equals(flavors[1]))
			return sd.data;
		return null;
	}
}
