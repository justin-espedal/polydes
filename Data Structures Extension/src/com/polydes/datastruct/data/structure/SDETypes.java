package com.polydes.datastruct.data.structure;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

import com.polydes.common.ext.ExtendableObjectRegistry;
import com.polydes.common.nodes.NodeCreator.CreatableNodeInfo;
import com.polydes.datastruct.data.structure.elements.StructureCondition.ConditionType;
import com.polydes.datastruct.data.structure.elements.StructureField.FieldType;
import com.polydes.datastruct.data.structure.elements.StructureHeader.HeaderType;
import com.polydes.datastruct.data.structure.elements.StructureTab;
import com.polydes.datastruct.data.structure.elements.StructureTab.TabType;
import com.polydes.datastruct.data.structure.elements.StructureTabset.TabsetType;
import com.polydes.datastruct.data.structure.elements.StructureText.TextType;
import com.polydes.datastruct.data.structure.elements.StructureUnknown.UnknownType;

/** StructureDefinitionElementTypes **/
public class SDETypes extends ExtendableObjectRegistry<SDEType<?>>
{
	public SDETypes()
	{
		String base = ExtendableObjectRegistry.BASE_OWNER;
		registerItem(base, new ConditionType());
		registerItem(base, new FieldType());
		registerItem(base, new HeaderType());
		registerItem(base, new TabType());
		registerItem(base, new TabsetType());
		registerItem(base, new TextType());
//		registerItem(base, new UnknownType());
		
		fromClass.put(StructureTable.class, fromClass.get(StructureTab.class));
	}
	
	static HashMap<Class<? extends SDE>, SDEType<?>> fromClass = new HashMap<>();
	public static HashMap<Class<? extends SDE>, CreatableNodeInfo> asCNInfo = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public static <T extends SDE> SDEType<T> fromClass(Class<T> c)
	{
		return (SDEType<T>) fromClass.get(c);
	}
	
	@Override
	public void registerItem(String owner, SDEType<?> type)
	{
		super.registerItem(owner, type);
		fromClass.put(type.sdeClass, type);
		
		String capitalized = type.tag.substring(0, 1).toUpperCase(Locale.ENGLISH) + type.tag.substring(1);
		asCNInfo.put(type.sdeClass, new CreatableNodeInfo(capitalized, type.sdeClass, type.icon));
		
		type.owner = owner;
	}
	
	@Override
	public void unregisterItem(String owner, String key)
	{
		SDEType<?> type = super.getItem(owner, key);
		super.unregisterItem(owner, key);
		fromClass.remove(type.sdeClass);
		asCNInfo.remove(type.sdeClass);
	}
	
	public static Collection<SDEType<?>> getTypes()
	{
		return fromClass.values();
	}

	@Override
	public SDEType<?> generatePlaceholder(String owner, String key)
	{
		return new UnknownType(key);
	}
}
