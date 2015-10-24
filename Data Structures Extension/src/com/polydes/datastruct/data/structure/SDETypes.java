package com.polydes.datastruct.data.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import com.polydes.common.util.PopupUtil.PopupItem;
import com.polydes.datastruct.data.structure.elements.StructureCondition.ConditionType;
import com.polydes.datastruct.data.structure.elements.StructureField.FieldType;
import com.polydes.datastruct.data.structure.elements.StructureHeader.HeaderType;
import com.polydes.datastruct.data.structure.elements.StructureTab;
import com.polydes.datastruct.data.structure.elements.StructureTab.TabType;
import com.polydes.datastruct.data.structure.elements.StructureTabset.TabsetType;
import com.polydes.datastruct.data.structure.elements.StructureText.TextType;
import com.polydes.datastruct.data.structure.elements.StructureUnknown.UnknownType;

/** StructureDefinitionElementTypes **/
public class SDETypes
{
	static HashMap<String, SDEType<?>> fromStandardTags = new HashMap<>();
	static HashMap<String, HashMap<String, SDEType<?>>> fromTag = new HashMap<>();
	static HashMap<Class<? extends SDE>, SDEType<?>> fromClass = new HashMap<>();
	
	public static Collection<Class<SDEType<?>>> standardChildren = new ArrayList<>();
	public static Collection<Class<SDEType<?>>> tabsetChildren = new ArrayList<>();
	public static HashMap<Class<? extends SDE>, PopupItem> asPopupItem = new HashMap<>();
	static
	{
		addType(null, new ConditionType());
		addType(null, new FieldType());
		addType(null, new HeaderType());
		addType(null, new TabType());
		addType(null, new TabsetType());
		addType(null, new TextType());
		addType(null, new UnknownType());
		fromClass.put(StructureTable.class, fromClass.get(StructureTab.class));
	}
	
	public static SDEType<?> fromTag(String ext, String tag)
	{
		if(ext == null)
			return fromStandardTags.get(tag);
		else
			return fromTag.get(ext).get(tag);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends SDE> SDEType<T> fromClass(Class<T> c)
	{
		return (SDEType<T>) fromClass.get(c);
	}
	
	@SuppressWarnings("unchecked")
	public static void addType(String ext, SDEType<?> type)
	{
		if(ext == null)
			fromStandardTags.put(type.tag, type);
		else
		{
			if(!fromTag.containsKey(ext))
				fromTag.put(ext, new HashMap<>());
			fromTag.get(ext).put(type.tag, type);
		}
		fromClass.put(type.sdeClass, type);
		
		if(type instanceof TabType)
			tabsetChildren.add((Class<SDEType<?>>) type.sdeClass);
		else
			standardChildren.add((Class<SDEType<?>>) type.sdeClass);
		
		String capitalized = type.tag.substring(0, 1).toUpperCase(Locale.ENGLISH) + type.tag.substring(1);
		asPopupItem.put(type.sdeClass, new PopupItem(capitalized, type.sdeClass, type.icon));
	}
	
	public static void removeExtendedType(String extension, SDEType<?> type)
	{
		fromClass.remove(type.sdeClass);
		asPopupItem.remove(type.sdeClass);
	}

	public static Collection<SDEType<?>> getTypes()
	{
		return fromClass.values();
	}

	public static void disposeExtended()
	{
		for(HashMap<String, SDEType<?>> map : fromTag.values())
			for(Entry<String, SDEType<?>> entry : map.entrySet())
				removeExtendedType(entry.getKey(), entry.getValue());
		fromTag.clear();
	}
}
