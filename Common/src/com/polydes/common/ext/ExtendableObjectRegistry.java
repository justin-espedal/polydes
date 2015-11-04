package com.polydes.common.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

public abstract class ExtendableObjectRegistry<T extends RegistryObject>
{
	public static String BASE_OWNER = StringUtils.EMPTY;
	
	private HashMap<String, HashMap<String, T>> extendedMap;
	
	public ExtendableObjectRegistry()
	{
		extendedMap = new HashMap<>();
	}
	
	public void registerItem(String owner, T object)
	{
		if(isUnknown(owner, object.getKey()))
			realizeUnknown(owner, object.getKey(), object);
		else
		{
			if(!extendedMap.containsKey(owner))
				extendedMap.put(owner, new HashMap<>());
			
			extendedMap.get(owner).put(object.getKey(), object);
		}
	}
	
	public void clearAll()
	{
		for(String key : copy(extendedMap.keySet()))
			clearExtension(key);
	}
	
	public void clearAllExtensions()
	{
		for(String key : copy(extendedMap.keySet()))
			if(!key.equals(BASE_OWNER))
				clearExtension(key);
	}
	
	public void clearExtension(String owner)
	{
		for(String key : copy(extendedMap.get(owner).keySet()))
			unregisterItem(owner, key);
		extendedMap.remove(owner);
	}
	
	public void unregisterItem(String owner, String key)
	{
		extendedMap.get(owner).remove(key);
	}
	
	public boolean hasItem(String owner, String key)
	{
		return extendedMap.get(owner).containsKey(key);
	}
	
	private static String[] copy(Collection<String> strings)
	{
		return strings.toArray(new String[strings.size()]);
	}
	
	public T getItem(String owner, String key)
	{
		return extendedMap.get(owner).get(key);
	}
	
	public void dispose()
	{
		clearAll();
		extendedMap.clear();
		unknownValues.clear();
		roRealizers.clear();
		extendedMap = null;
		unknownValues = null;
		roRealizers = null;
	}
	
	/*-------------------------------------*\
	 * Unknown Objects
	\*-------------------------------------*/ 
	
	private HashMap<String, HashMap<String, T>> unknownValues = new HashMap<>();
	private HashMap<String, HashMap<String, ArrayList<RORealizer<T>>>> roRealizers = new HashMap<>();
	
	public T requestValue(String owner, String key, RORealizer<T> ror)
	{
		if(!hasItem(owner, key))
			addUnknown(owner, key);
		
		if(isUnknown(owner, key))
			roRealizers.get(owner).get(key).add(ror);
		else
			ror.realizeRO(getItem(owner, key));
		
		return getItem(owner, key);
	}
	
	public void addUnknown(String owner, String key)
	{
		T newObject = generatePlaceholder(owner, key);
		
		if(!unknownValues.containsKey(owner))
			unknownValues.put(owner, new HashMap<>());
		unknownValues.get(owner).put(key, newObject);
		
		if(!roRealizers.containsKey(owner))
			roRealizers.put(owner, new HashMap<>());
		roRealizers.get(owner).put(key, new ArrayList<>());
		
		registerItem(owner, newObject);
	}
	
	public abstract T generatePlaceholder(String owner, String key);
	
	public boolean isUnknown(String owner, String key)
	{
		return unknownValues.containsKey(owner) && unknownValues.get(owner).containsKey(key);
	}
	
	public void realizeUnknown(String owner, String key, T value)
	{
		T unknown = unknownValues.get(owner).remove(key);
		
		if(unknown == null)
			throw new IllegalArgumentException("There is no unknown object \"" + owner + ":" + key + "\"");
		
		for(RORealizer<T> ror : roRealizers.get(owner).remove(key))
			ror.realizeRO(value);
		
		extendedMap.get(owner).put(value.getKey(), value);
	}
}