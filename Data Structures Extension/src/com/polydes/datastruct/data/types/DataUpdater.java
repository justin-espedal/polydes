package com.polydes.datastruct.data.types;

public class DataUpdater<T>
{
	private T data;
	public UpdateListener listener;
	
	public DataUpdater(T data, UpdateListener listener)
	{
		this.data = data;
		this.listener = listener;
	}
	
	public void set(T t)
	{
		data = t;
		updated();
	}
	
	public T get()
	{
		return data;
	}
	
	public void updated()
	{
		listener.updated();
	}
}