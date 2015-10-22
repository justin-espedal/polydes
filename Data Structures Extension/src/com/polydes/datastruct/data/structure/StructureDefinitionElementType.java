package com.polydes.datastruct.data.structure;

import java.util.Collection;

import javax.swing.Icon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.RowGroup;

public abstract class StructureDefinitionElementType<T extends StructureDefinitionElement>
{
	public Class<T> sdeClass;
	public String tag;
	public Icon icon;
	public boolean isBranchNode;
	public Collection<StructureDefinitionElementType<?>> childTypes;
	
	public abstract T read(StructureDefinition model, Element e);
	public abstract Element write(T object, Document doc);
	public abstract T create(StructureDefinition def, String nodeName);
	
	public abstract void psLoad(PropertiesSheet sheet, RowGroup group, DataItem node, T value);
	public abstract void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, T value);
}
