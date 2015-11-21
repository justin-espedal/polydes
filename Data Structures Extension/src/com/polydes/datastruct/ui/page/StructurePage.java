package com.polydes.datastruct.ui.page;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.DefaultNodeCreator;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.ui.filelist.TreePage;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureFolder;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.ui.list.ListUtils;

import stencyl.sw.SW;
import stencyl.sw.util.UI;

public class StructurePage extends TreePage<DefaultLeaf,DefaultBranch>
{
	private static StructurePage _instance;
	
	private JComponent sidebar;
	private Boolean listEditEnabled;
	
	public static StructurePage newPage(Folder rootFolder)
	{
		HierarchyModel<DefaultLeaf,DefaultBranch> model = new HierarchyModel<>(rootFolder, DefaultLeaf.class, DefaultBranch.class);
		return new StructurePage(model);
	}
	
	private StructurePage(HierarchyModel<DefaultLeaf,DefaultBranch> model)
	{
		super(model);
		
		setListEditEnabled(true);
		getFolderModel().setUniqueLeafNames(true);
		
		getTree().enablePropertiesButton();
		getFolderModel().setNodeCreator(new DefaultNodeCreator<DefaultLeaf,DefaultBranch>()
		{
			//For our purposes here, the object these folders point to is a type limiter.
			
			@Override
			public ArrayList<CreatableNodeInfo> getCreatableNodeList(DefaultBranch branchNode)
			{
				StructureFolder parent = (StructureFolder) getFolderModel().getCreationParentFolder(getFolderModel().getSelection());
				
				ArrayList<CreatableNodeInfo> items = new ArrayList<CreatableNodeInfo>();
				if(parent.childType != null)
					items.add(new CreatableNodeInfo(parent.childType.getName(), parent.childType, parent.childType.getIcon()));
				else
					for(StructureDefinition def : DataStructuresExtension.get().getStructureDefinitions().values())
						items.add(new CreatableNodeInfo(def.getName(), def, def.getIcon()));
				return items;
			}
			
			@Override
			public DefaultLeaf createNode(CreatableNodeInfo selected, String nodeName)
			{
				if(selected.name.equals("Folder"))
					return new StructureFolder(nodeName);
				
				int id = Structures.newID();
				StructureDefinition type = (StructureDefinition) selected.data;
				Structure toReturn = new Structure(id, nodeName, type);
				toReturn.loadDefaults();
				Structures.structures.get(type).add(toReturn);
				Structures.structuresByID.put(id, toReturn);
				return toReturn.dref;
			}
			
			@Override
			public ArrayList<NodeAction<DefaultLeaf>> getNodeActions(DefaultLeaf[] targets)
			{
				return null;
			}
			
			@Override
			public boolean attemptRemove(List<DefaultLeaf> toRemove)
			{
				int numStructuresToRemove = 0;
				for(DefaultLeaf item : toRemove)
					if(!(item instanceof Branch))
						++numStructuresToRemove;
				
				String plural = (numStructuresToRemove > 1 ? "s" : "");
				
				UI.Choice result =
					UI.showYesCancelPrompt(
						"Remove Selected Structure" + plural,
						"Are you sure you want to remove " + numStructuresToRemove +  " structure" + plural + "?",
						"Remove", "Cancel"
					);
				
				return result == UI.Choice.YES;
			}
			
			@Override
			public void nodeRemoved(DefaultLeaf toRemove)
			{
				if(toRemove.getUserData() instanceof Structure)
				{
					Structure s = (Structure) toRemove.getUserData();
					Structures.structures.get(s.getTemplate()).remove(s);
					Structures.structuresByID.remove(s.getID());
					s.dispose();
				}
			}
			
			@Override
			public void editNode(DefaultLeaf toEdit)
			{
				if(!(toEdit instanceof StructureFolder))
					return;
				
				EditFolderDialog dg = new EditFolderDialog(SW.get());
				dg.setFolder((StructureFolder) toEdit);
				dg.dispose();
			}
		});
		
		sidebar = ListUtils.addHeader(getTree(), "Data");
	}
	
	public JComponent getSidebar()
	{
		return sidebar;
	}
	
	public static StructurePage get()
	{
		if (_instance == null)
			_instance = newPage(Structures.root);
		
		return _instance;
	}
	
	public void setListEditEnabled(boolean value)
	{
		if(listEditEnabled == null || listEditEnabled != value)
		{
			listEditEnabled = value;
			if(listEditEnabled)
			{
				getTree().setListEditEnabled(true);
			}
			else
			{
				getTree().setListEditEnabled(false);
			}
		}
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		sidebar.removeAll();
		sidebar = null;
	}
}
