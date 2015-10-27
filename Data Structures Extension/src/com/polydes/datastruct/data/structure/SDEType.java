package com.polydes.datastruct.data.structure;

import java.util.Collection;

import javax.swing.Icon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;

/** StructureDefinitionElementType **/
public abstract class SDEType<T extends SDE>
{
	public Class<T> sdeClass;
	public String tag;
	public Icon icon;
	public boolean isBranchNode;
	public Collection<Class<SDEType<?>>> childTypes;
	
	public abstract T read(StructureDefinition model, Element e);
	public abstract Element write(T object, Document doc);
	public abstract T create(StructureDefinition def, String nodeName);
	
	public abstract GuiObject psAdd(PropertiesSheet sheet, Folder parent, DataItem node, T value, int i);
	public abstract void psRemove(PropertiesSheet sheet, GuiObject gui, DataItem node, T value);
	public abstract void psRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, T value);
	public abstract void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, T value);
	
	public void genCode(T value, StringBuilder builder) { }
}
