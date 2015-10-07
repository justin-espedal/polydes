package com.polydes.common.ext;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.MethodUtils;

import stencyl.sw.SW;
import stencyl.sw.ext.ExtensionWrapper;

public class ExtensionInterface
{
	public static Object sendMessage(String extensionID, String message, Object... args)
	{
		ExtensionWrapper ext = SW.get().getExtensionManager().getExtensions().get(extensionID);
		if(!ext.isActivated())
		{
			return null;
		}
		
		try
		{
			return MethodUtils.invokeMethod(ext.getExtension(), message, args);
		}
		catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e2)
		{
			e2.printStackTrace();
		}
		
		return null;
	}
}
