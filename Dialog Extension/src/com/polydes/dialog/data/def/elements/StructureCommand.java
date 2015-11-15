package com.polydes.dialog.data.def.elements;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.polydes.common.io.XML;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.common.util.ColorUtil;
import com.polydes.common.util.Lang;
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
import com.polydes.dialog.app.editors.text.DialogHighlighter;
import com.polydes.dialog.data.def.elements.StructureArgument.StructureArgumentType;
import com.polydes.dialog.data.def.elements.StructureArgument.Type;

public class StructureCommand extends SDE
{
	public String name;
	public ArrayList<StructureArgument> args;
	public String description;
	
	public StructureCommand(String name, ArrayList<StructureArgument> args, String description)
	{
		this.name = name;
		this.args = args;
		this.description = description;
	}
	
	@Override
	public String getDisplayLabel()
	{
		return "<" + name + (args.isEmpty() ? ">" :
			" " + StringUtils.join(Lang.mapCA(args, String.class, (arg) -> arg.type.name()), " ") + ">");
	}
	
	public String getFullHtmlDisplayLabel()
	{
		return String.format("<b><font color=\"%s\">%s<font color=\"%s\">%s</font>%s</font></b> - %s",
				ColorUtil.encode24(DialogHighlighter.TEXT_COLOR_TAG),
				StringEscapeUtils.escapeHtml4("<" + name),
				ColorUtil.encode24(DialogHighlighter.TEXT_COLOR_TAG_DATA),
				args.isEmpty()? "" : " " + StringUtils.join(Lang.mapCA(args, String.class, (arg) -> arg.name+":"+arg.type.name()), " "),
				StringEscapeUtils.escapeHtml4(">"),
				description);
	}
	
	public class StructureCommandPanel extends StructureObjectPanel
	{
		public StructureCommandPanel(final StructureCommand cmd, PropertiesSheetStyle style)
		{
			super(style, cmd);
			
			StructureArgumentType sat = new StructureArgumentType();
			
			sheet.build()
			
				.field("name")._string().add()
				
				.field("description")._string().expandingEditor().add()
				
				.header("Argument")
				
				.field("args").label("")._array().genType(sat).add()
				
				.finish();
			
			sheet.addPropertyChangeListener("name", event -> {
				previewKey.setName(cmd.getDisplayLabel());
				preview.lightRefreshDataItem(previewKey);
			});	
			
			sheet.addPropertyChangeListener("description", event -> {
				preview.lightRefreshDataItem(previewKey);
			});
			
			sheet.addPropertyChangeListener("args", event -> {
				previewKey.setName(cmd.getDisplayLabel());
				preview.lightRefreshDataItem(previewKey);
			});
		}
	}

	private StructureCommandPanel editor = null;
	
	@Override
	public JPanel getEditor()
	{
		if(editor == null)
			editor = new StructureCommandPanel(this, PropertiesSheetStyle.LIGHT);
		
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
	
	public static class CommandType extends SDEType<StructureCommand>
	{
		public CommandType()
		{
			sdeClass = StructureCommand.class;
			tag = "cmd";
			isBranchNode = false;
			icon = null;
			childTypes = null;
		}
		
		@Override
		public StructureCommand read(StructureDefinition model, Element e)
		{
			return new StructureCommand(XML.read(e, "name"), readArgs(e), XML.read(e, "desc"));
		}
		
		private ArrayList<StructureArgument> readArgs(Element e)
		{
			ArrayList<StructureArgument> args = new ArrayList<>();
			XML.children(e).forEach((child) ->
				args.add(new StructureArgument(XML.read(child, "name"), Type.valueOf(XML.read(child, "type"))))
			);
			return args;
		}

		@Override
		public void write(StructureCommand object, Element e)
		{
			e.setAttribute("name", object.name);
			e.setAttribute("desc", object.description);
			
			for(StructureArgument arg : object.args)
			{
				Element child = e.getOwnerDocument().createElement("arg");
				XML.write(child, "name", arg.name);
				XML.write(child, "type", arg.type.name());
				e.appendChild(child);
			}
		}

		@Override
		public StructureCommand create(StructureDefinition def, String nodeName)
		{
			return new StructureCommand(nodeName, new ArrayList<>(), "");
		}
		
		@Override
		public GuiObject psAdd(PropertiesSheet sheet, Folder parent, DataItem node, StructureCommand value, int i)
		{
			RowGroup extGroup = (RowGroup) sheet.guiMap.get(parent.getParent());
			Card parentCard = extGroup.getSubcard();
			
			RowGroup group = new RowGroup(value);
			group.add(i == 0 ? sheet.style.createLabel("Commands") : null, sheet.style.createDescriptionRow(value.getFullHtmlDisplayLabel()));
			group.add(sheet.style.hintgap);
			
			parentCard.addGroup(i + 1, group);
			
			if(!sheet.isChangingLayout)
				parentCard.layoutContainer();
			
			return group;
		}
		
		@Override
		public void psRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureCommand value)
		{
			
		}
		
		@Override
		public void psRemove(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureCommand value)
		{
			RowGroup group = (RowGroup) gui;
			Card card = group.card;
			
			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);
			
			card.layoutContainer();
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureCommand value)
		{
			sheet.style.setDescription((JLabel) ((RowGroup) gui).rows[0].components[1], value.getFullHtmlDisplayLabel());
		}
	}
}
