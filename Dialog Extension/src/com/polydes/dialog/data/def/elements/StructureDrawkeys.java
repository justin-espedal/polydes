package com.polydes.dialog.data.def.elements;

import javax.swing.JPanel;

import org.w3c.dom.Element;

import com.polydes.common.util.Lang;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;

public class StructureDrawkeys extends SDE
{
	@Override
	public String getDisplayLabel()
	{
		return "Drawkeys";
	}

	@Override
	public JPanel getEditor()
	{
		return new JPanel();
	}

	@Override
	public void disposeEditor()
	{
		
	}

	@Override
	public void revertChanges()
	{
		
	}
	
	public static class DrawkeysType extends SDEType<StructureDrawkeys>
	{
		public DrawkeysType()
		{
			sdeClass = StructureDrawkeys.class;
			tag = "drawkeys";
			isBranchNode = true;
			icon = null;
			childTypes = Lang.arraylist(StructureDrawkey.class);
		}

		@Override
		public StructureDrawkeys read(StructureDefinition model, Element e)
		{
			return new StructureDrawkeys();
		}

		@Override
		public void write(StructureDrawkeys object, Element e)
		{
		}

		@Override
		public StructureDrawkeys create(StructureDefinition def, String nodeName)
		{
			return new StructureDrawkeys();
		}

		@Override
		public GuiObject psAdd(PropertiesSheet sheet, Folder parent, DataItem node, StructureDrawkeys value, int i)
		{
			return null;
		}
		
		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureDrawkeys value)
		{
			
		}

		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureDrawkeys value)
		{
			
		}
		
		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureDrawkeys value)
		{
			
		}
	}
}
