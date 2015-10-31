package com.polydes.dialog.data.def.elements;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.polydes.common.io.XML;
import com.polydes.common.util.Lang;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.UpdateListener;
import com.polydes.datastruct.data.types.builtin.basic.StringType;
import com.polydes.datastruct.ui.objeditors.StructureObjectPanel;
import com.polydes.datastruct.ui.table.Card;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.table.RowGroup;
import com.polydes.datastruct.utils.ColorUtil;
import com.polydes.dialog.app.editors.text.DialogHighlighter;
import com.polydes.dialog.data.def.elements.StructureArgument.StructureArgumentEditor;
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
		StructureCommand cmd;
		
		String oldName;
		ArrayList<StructureArgument> oldArgs;
		String oldDescription;
		
		DataEditor<String> nameEditor;
		ArrayList<DataEditor<StructureArgument>> argEditors;
		DataEditor<String> descriptionEditor;
		
		int argsExpander;
		
		public StructureCommandPanel(final StructureCommand cmd, PropertiesSheetStyle style)
		{
			super(style);
			
			this.cmd = cmd;
			
			oldName = cmd.name;
			oldArgs = new ArrayList<>();
			for(StructureArgument arg : cmd.args)
				oldArgs.add(new StructureArgument(arg.name, arg.type));
			
			//=== Name
	
			nameEditor = new StringType.SingleLineStringEditor(null, style);
			nameEditor.setValue(cmd.name);
		
			nameEditor.addListener(new UpdateListener()
			{
				@Override
				public void updated()
				{
					cmd.name = nameEditor.getValue();
					previewKey.setName(cmd.getDisplayLabel());
					preview.lightRefreshDataItem(previewKey);
				}
			});
			
			addGenericRow("Name", nameEditor);
			
			//=== Description
			
			descriptionEditor = new StringType.ExpandingStringEditor(null, style);
			descriptionEditor.setValue(cmd.description);
		
			descriptionEditor.addListener(new UpdateListener()
			{
				@Override
				public void updated()
				{
					cmd.description = descriptionEditor.getValue();
					preview.lightRefreshDataItem(previewKey);
				}
			});
			
			addGenericRow("Description", descriptionEditor);
			
			//=== Arguments
			
			addGenericRow("", new JComponent[] {style.createSoftLabel("Arguments")});
			
			argsExpander = newExpander();
			
			refreshArgs();
		}
		
		private void refreshArgs()
		{
			clearExpansion(argsExpander);
			
			int i = 0;
			
			for(StructureArgument arg : cmd.args)
			{
				final int argIndex = i++;
				
				DataEditor<StructureArgument> argEditor = new StructureArgumentEditor(style);
				argEditor.setValue(arg);
				
				argEditor.addListener(new UpdateListener()
				{
					@Override
					public void updated()
					{
						StructureArgument value = argEditor.getValue();
						arg.name = value.name;
						arg.type = value.type;
						previewKey.setName(cmd.getDisplayLabel());
						preview.lightRefreshDataItem(previewKey);
					}
				});
				
				addGenericRow(argsExpander, String.valueOf(argIndex), argEditor);
			}
			
			JButton addArgButton = new JButton("+");
			JButton removeArgButton = new JButton("-");
			
			final int argIndex = i;
					
			addArgButton.addActionListener((e) -> {
				cmd.args.add(new StructureArgument("newArg" + argIndex, Type.String));
				previewKey.setName(cmd.getDisplayLabel());
				preview.lightRefreshDataItem(previewKey);
				refreshArgs();
			});
			
			removeArgButton.addActionListener((e) -> {
				cmd.args.remove(cmd.args.size() - 1);
				previewKey.setName(cmd.getDisplayLabel());
				preview.lightRefreshDataItem(previewKey);
				refreshArgs();
			});
			
			removeArgButton.setEnabled(cmd.args.size() > 0);
			
			addGenericRow(argsExpander, "", new JComponent[] {addArgButton, removeArgButton});
			
			setSize(getPreferredSize());
		}
		
		public void revert()
		{
			cmd.name = oldName;
			cmd.args.clear();
			cmd.args.addAll(oldArgs);
			cmd.description = oldDescription;
		}
		
		public void dispose()
		{
			clearExpansion(0);
			clearExpansion(argsExpander);
			oldName = null;
			oldArgs = null;
			oldDescription = null;
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
		editor.revert();
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
