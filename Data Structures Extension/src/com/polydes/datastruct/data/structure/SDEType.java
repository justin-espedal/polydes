package com.polydes.datastruct.data.structure;

import java.util.Collection;

import javax.swing.ImageIcon;

import org.w3c.dom.Element;

import com.polydes.common.ext.RegistryObject;
import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;

/** StructureDefinitionElementType **/
public abstract class SDEType<T extends SDE> implements RegistryObject
{
	public Class<T> sdeClass;
	public String tag;
	public ImageIcon icon;
	public boolean isBranchNode;
	public Collection<Class<? extends SDE>> childTypes;
	
	public abstract T read(StructureDefinition model, Element e);
	public abstract void write(T object, Element e);
	public abstract T create(StructureDefinition def, String nodeName);
	
	public abstract GuiObject psAdd(PropertiesSheet sheet, DefaultBranch parent, DefaultLeaf node, T value, int i);
	public abstract void psRemove(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, T value);
	public abstract void psRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, T value);
	public abstract void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DefaultLeaf node, T value);
	
	public void genCode(T value, StringBuilder builder) { }
	
	public String owner;
	
	@Override
	public String getKey()
	{
		return tag;
	}
	
	@Override
	public void setKey(String newKey)
	{
		throw new IllegalAccessError();
	}
}
