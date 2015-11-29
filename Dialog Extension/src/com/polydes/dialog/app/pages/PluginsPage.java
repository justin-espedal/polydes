package com.polydes.dialog.app.pages;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.polydes.common.comp.HorizontalDivider;
import com.polydes.common.comp.TitledPanel;
import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultEditableLeaf;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.DefaultNodeCreator;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.NodeCreator.CreatableNodeInfo;
import com.polydes.common.nodes.NodeCreator.NodeAction;
import com.polydes.common.nodes.NodeSelection;
import com.polydes.common.nodes.NodeUtils;
import com.polydes.common.sw.Snippets;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.ui.page.CreateStructureDefinitionDialog;
import com.polydes.datastruct.ui.page.StructureDefinitionPage;
import com.polydes.datastruct.ui.page.StructureDefinitionsWindow;
import com.polydes.dialog.DialogExtension;
import com.polydes.dialog.app.PluginList;
import com.polydes.dialog.app.editors.text.TextArea;
import com.polydes.dialog.data.def.elements.StructureExtension;
import com.polydes.dialog.res.Resources;

import stencyl.core.engine.snippet.ISnippet;
import stencyl.core.lib.Game;
import stencyl.sw.SW;
import stencyl.sw.util.Locations;
import stencyl.sw.util.UI;
import stencyl.sw.util.dg.MessageDialog;

public class PluginsPage extends JPanel
{
	private static PluginsPage _instance;
	
	private HierarchyModel<DefaultLeaf,DefaultBranch> dialogDefsFM;
	private HierarchyModel<DefaultLeaf,DefaultBranch> userDefsFM;
	
	private PluginList dialogDefsList;
	private PluginList userDefsList;
	
	public static NodeAction<DefaultLeaf> editPluginStructure = new NodeAction<DefaultLeaf>("Edit Structure", null, leaf -> {
		SwingUtilities.invokeLater(() -> {
			StructureDefinition selectDef = (StructureDefinition) leaf.getUserData();
			StructureDefinitionPage.get().selectDefinition(selectDef);
		});
		StructureDefinitionsWindow.get().setVisible(true);
	});
	
	public static NodeAction<DefaultLeaf> editPluginCode = new NodeAction<DefaultLeaf>("Edit Code", null, leaf -> {
		StructureDefinition def = (StructureDefinition) leaf.getUserData();
		
		NodeUtils.recursiveRun(def.guiRoot, (DefaultLeaf defLeaf) -> {
			if(defLeaf.getUserData() instanceof StructureExtension)
			{
				String implementingClass = ((StructureExtension) defLeaf.getUserData()).implementation;
				implementingClass = StringUtils.substringAfter(implementingClass, Locations.SCRIPTS_PACKAGE);
				ISnippet toEdit = Game.getGame().getSnippetByClassname(implementingClass);
				if(toEdit == null)
					MessageDialog.showErrorDialog("No implementation", "Couldn't find behavior with classname \"" + implementingClass + "\".");
				SW.get().getWorkspace().openResource(toEdit, false);
			}
		});
	});
	
	public static NodeAction<DefaultLeaf> duplicatePlugin = new NodeAction<DefaultLeaf>("Duplicate", null, leaf -> {
		
	});
	
	public static CreatableNodeInfo createNewPlugin = new CreatableNodeInfo("Create New Plugin", null, null);
	
	private PluginsPage()
	{
		super(new BorderLayout());
		
		Folder root = DataStructuresExtension.get().getStructureDefinitions().root;
		
		DefaultBranch dialogDefsRoot = (DefaultBranch) root.getItemByName(DialogExtension.get().getManifest().name);
		DefaultBranch userDefsRoot = (DefaultBranch) root.getItemByName("My Structures");
		
		dialogDefsFM = new HierarchyModel<DefaultLeaf,DefaultBranch>(dialogDefsRoot, DefaultLeaf.class, DefaultBranch.class);
		userDefsFM = new HierarchyModel<DefaultLeaf,DefaultBranch>(userDefsRoot, DefaultLeaf.class, DefaultBranch.class)
		{
			@Override
			public DefaultBranch getCreationParentFolder(NodeSelection<DefaultLeaf, DefaultBranch> state)
			{
				return userDefsRoot;
			}
		};
		
		dialogDefsFM.setNodeCreator(new DefaultNodeCreator<DefaultLeaf, DefaultBranch>()
		{
			@Override
			public ArrayList<NodeAction<DefaultLeaf>> getNodeActions(DefaultLeaf[] targets)
			{
				ArrayList<NodeAction<DefaultLeaf>> actions = new ArrayList<>();
				if(targets.length == 1)
				{
					if(targets[0].getUserData() instanceof StructureDefinition)
					{
						actions.add(duplicatePlugin);
					}
				}
				return actions;
			}
		});
		
		userDefsFM.setNodeCreator(new DefaultNodeCreator<DefaultLeaf, DefaultBranch>()
		{
			@Override
			public void nodeRemoved(DefaultLeaf toRemove)
			{
				//TODO
			}
			
			@Override
			public DefaultLeaf createNode(CreatableNodeInfo selected, String nodeName)
			{
				CreateStructureDefinitionDialog dg = new CreateStructureDefinitionDialog();
				dg.setParentClass((StructureDefinition) dialogDefsRoot.getItemByName("Dialog Extension").getUserData());
				dg.setNodeName("New Plugin");
				StructureDefinition toCreate = dg.newDef;
				dg.dispose();
				
				if(toCreate == null)
					return null;
				
				String newScriptTemplate = "";
				try
				{
					newScriptTemplate = IOUtils.toString(Resources.getUrlStream("dialog-extension-template.hx"));
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				
				String newScriptName = toCreate.getName();
				String newScriptClass = toCreate.getSimpleClassname();
				String newScriptQualifiedClass = "scripts." + toCreate.getSimpleClassname();
				
				newScriptTemplate = newScriptTemplate.replaceAll("CLASSNAME", newScriptClass);
				newScriptTemplate = newScriptTemplate.replaceAll("PACKAGE", "scripts");
				newScriptTemplate = newScriptTemplate.replaceAll("NAME", "\"" + newScriptName + "\"");
				
				Snippets.createNew(newScriptName, "scripts", newScriptClass, "Implementation of Dialog plugin.", newScriptTemplate);
				
				StructureExtension newItem = new StructureExtension(newScriptQualifiedClass, "Description for " + newScriptName);
				toCreate.guiRoot.addItem(new DefaultEditableLeaf(newItem.getDisplayLabel(), newItem));
				
				DataStructuresExtension.get().getStructureDefinitions().registerItem(toCreate);
				return toCreate.dref;
			}
			
			@Override
			public ArrayList<NodeAction<DefaultLeaf>> getNodeActions(DefaultLeaf[] targets)
			{
				ArrayList<NodeAction<DefaultLeaf>> actions = new ArrayList<>();
				if(targets.length == 1)
				{
					if(targets[0].getUserData() instanceof StructureDefinition)
					{
						actions.add(editPluginStructure);
						actions.add(editPluginCode);
						actions.add(duplicatePlugin);
					}
				}
				return actions;
			}
			
			@Override
			public boolean attemptRemove(List<DefaultLeaf> toRemove)
			{
				//TODO
				return true;
			}
		});
		
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBackground(TextArea.TEXT_EDITOR_COLOR);
		
		TitledPanel dialogDefsWrapper = new TitledPanel("Builtin Plugins", null);
		dialogDefsWrapper.add(dialogDefsList = new PluginList(dialogDefsFM), BorderLayout.CENTER);
		
		TitledPanel userDefsWrapper = new TitledPanel("Custom Plugins", null);
		userDefsWrapper.add(userDefsList = new PluginList(userDefsFM), BorderLayout.CENTER);
		
		content.add(dialogDefsWrapper);
		content.add(new HorizontalDivider(2));
		content.add(userDefsWrapper);
		content.add(Box.createVerticalGlue());
		
		add(UI.createScrollPane(content), BorderLayout.CENTER);
	}
	
	public static PluginsPage get()
	{
		if (_instance == null)
			_instance = new PluginsPage();
		
		return _instance;
	}
	
	public static void dispose()
	{
		if(_instance != null)
		{
			_instance.dialogDefsList.dispose();
			_instance.userDefsList.dispose();
			_instance.dialogDefsFM.dispose();
			_instance.userDefsFM.dispose();
		}
		_instance = null;
	}
}
