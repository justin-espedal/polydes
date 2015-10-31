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

public class StructureCommands extends SDE
{
	@Override
	public String getDisplayLabel()
	{
		return "Commands";
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
	
	public static class CommandsType extends SDEType<StructureCommands>
	{
		public CommandsType()
		{
			sdeClass = StructureCommands.class;
			tag = "cmds";
			isBranchNode = true;
			icon = null;
			childTypes = Lang.arraylist(StructureCommand.class);
		}

		@Override
		public StructureCommands read(StructureDefinition model, Element e)
		{
			return new StructureCommands();
		}

		@Override
		public void write(StructureCommands object, Element e)
		{
		}

		@Override
		public StructureCommands create(StructureDefinition def, String nodeName)
		{
			return new StructureCommands();
		}

		@Override
		public GuiObject psAdd(PropertiesSheet sheet, Folder parent, DataItem node, StructureCommands value, int i)
		{
			return null;
		}
		
		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureCommands value)
		{
			
		}
		
		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureCommands value)
		{
			
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureCommands value)
		{
			
		}
	}
}
