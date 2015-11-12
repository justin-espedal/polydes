package com.polydes.datastruct.ui.page;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import com.polydes.common.nodes.DefaultNodeCreator;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.ui.filelist.TreePage;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureFolder;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.ui.list.ListUtils;

import stencyl.sw.SW;
import stencyl.sw.util.UI;

public class StructurePage extends TreePage<DataItem,Folder>
{
	private static StructurePage _instance;
	
	private JComponent sidebar;
	private Boolean listEditEnabled;
	
	public StructurePage(Folder rootFolder)
	{
		super(new HierarchyModel<>(rootFolder, DataItem.class, Folder.class));
		
		setListEditEnabled(true);
		getFolderModel().setUniqueLeafNames(true);
		
		getTree().enablePropertiesButton();
		getFolderModel().setNodeCreator(new DefaultNodeCreator<DataItem,Folder>()
		{
			//For our purposes here, the object these folders point to is a type limiter.
			
			@Override
			public ArrayList<CreatableNodeInfo> getCreatableNodeList(Folder branchNode)
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
			public DataItem createNode(CreatableNodeInfo selected, String nodeName)
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
			public ArrayList<NodeAction<DataItem>> getNodeActions(DataItem[] targets)
			{
				return null;
			}
			
			@Override
			public boolean attemptRemove(List<DataItem> toRemove)
			{
				int numStructuresToRemove = 0;
				for(DataItem item : toRemove)
					if(!(item instanceof Folder))
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
			public void nodeRemoved(DataItem toRemove)
			{
				if(toRemove.getObject() instanceof Structure)
				{
					Structure s = (Structure) toRemove.getObject();
					Structures.structures.get(s.getTemplate()).remove(s);
					Structures.structuresByID.remove(s.getID());
					s.dispose();
				}
			}
			
			@Override
			public void editNode(DataItem toEdit)
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
			_instance = new StructurePage(Structures.root);

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
