package com.polydes.dialog.data.def.elements;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.w3c.dom.Element;

import com.polydes.common.io.XML;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.ui.objeditors.StructureObjectPanel;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.RowGroup;

public class StructureDrawkey extends SDE
{
	private String name;
	
	public StructureDrawkey(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public String getDisplayLabel()
	{
		return name;
	}
	
	public class StructureDrawkeyPanel extends StructureObjectPanel
	{
		public StructureDrawkeyPanel(final StructureDrawkey drawkey, PropertiesSheetStyle style)
		{
			super(style, drawkey);
			
			sheet.build()
			
				.field("name")._string().add()
				
				.finish();
			
			sheet.addPropertyChangeListener(event -> {
				preview.lightRefreshDataItem(previewKey);
			});
		}
	}

	private StructureDrawkeyPanel editor = null;
	
	@Override
	public JPanel getEditor()
	{
		if(editor == null)
			editor = new StructureDrawkeyPanel(this, PropertiesSheetStyle.LIGHT);
		
		return editor;
	}

	@Override
	public void disposeEditor()
	{
		editor.dispose();
		editor = null;
	}

	@Override
	public void revertChanges()
	{
		editor.revertChanges();
	}
	
	public static class DrawkeyType extends SDEType<StructureDrawkey>
	{
		public DrawkeyType()
		{
			sdeClass = StructureDrawkey.class;
			tag = "drawkey";
			isBranchNode = false;
			icon = null;
			childTypes = null;
		}

		@Override
		public StructureDrawkey read(StructureDefinition model, Element e)
		{
			return new StructureDrawkey(XML.read(e, "name"));
		}

		@Override
		public void write(StructureDrawkey object, Element e)
		{
			e.setAttribute("name", object.getName());
		}
		
		@Override
		public StructureDrawkey create(StructureDefinition def, String nodeName)
		{
			return new StructureDrawkey(nodeName);
		}
		
		@Override
		public GuiObject psAdd(PropertiesSheet sheet, Folder parent, DataItem node, StructureDrawkey value, int i)
		{
			int offset = 1;
			
			Folder extF = ((Folder) parent.getParent());
			for(DataItem di : extF.getItems())
				if(di.getObject() instanceof StructureCommands)
					offset += ((Folder) di).getItems().size();
			
			RowGroup extGroup = (RowGroup) sheet.guiMap.get(parent.getParent());
			Card parentCard = extGroup.getSubcard();
			
			RowGroup group = new RowGroup(value);
			group.add(i == 0 ? sheet.style.createLabel("Drawkeys") : null, sheet.style.createDescriptionRow(value.name));
			group.add(sheet.style.hintgap);
			
			parentCard.addGroup(i + offset, group);
			
			if(!sheet.isChangingLayout)
				parentCard.layoutContainer();
			
			return group;
		}
		
		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureDrawkey value)
		{
			
		}
		
		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureDrawkey value)
		{
			RowGroup group = (RowGroup) gui;
			Card card = group.card;
			
			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);
			
			card.layoutContainer();
		}
		
		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureDrawkey value)
		{
			sheet.style.setDescription((JLabel) ((RowGroup) gui).rows[0].components[1], value.name);
		}
	}
}
