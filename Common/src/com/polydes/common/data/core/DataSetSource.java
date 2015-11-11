package com.polydes.common.data.core;

import java.util.Collection;
import java.util.function.Supplier;

import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.Types;
import com.polydes.common.ext.RegistryObject;

public class DataSetSource implements RegistryObject
{
	public final String id; //registry key
	public final DataType<?> type;
	public Supplier<Collection<?>> collectionSupplier;
	
	public DataSetSource(String id, DataType<?> type, Supplier<Collection<?>> collectionSupplier)
	{
		this.id = id;
		this.type = type;
		this.collectionSupplier = collectionSupplier;
	}
	
	public void setCollectionSupplier(Supplier<Collection<?>> collectionSupplier)
	{
		this.collectionSupplier = collectionSupplier;
	}
	
	//===
	
	public static class CustomDataSetSource extends DataSetSource implements Supplier<Collection<?>>
	{
		public DataList list;
		
		public CustomDataSetSource(DataList list)
		{
			super("custom", Types._String, null);
			this.list = list;
			setCollectionSupplier(this);
		}
		
		@Override
		public Collection<?> get()
		{
			return list;
		}
	}
	
	@Override
	public String getKey()
	{
		return id;
	}
	
	@Override
	public void setKey(String newKey)
	{
		throw new IllegalAccessError();
	}
}
