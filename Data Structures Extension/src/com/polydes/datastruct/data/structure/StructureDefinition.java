package com.polydes.datastruct.data.structure;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.ImageIcon;

import com.polydes.datastruct.data.core.Pair;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.EditableObject;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.folder.FolderPolicy;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.ExtraProperties;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.Types;
import com.polydes.datastruct.ui.objeditors.StructureDefinitionEditor;
import com.polydes.datastruct.ui.page.StructurePage;

import stencyl.thirdparty.misc.gfx.GraphicsUtilities;

public class StructureDefinition extends EditableObject
{
	public static FolderPolicy STRUCTURE_DEFINITION_POLICY = new StructureDefinitionEditingPolicy();
	
	private BufferedImage iconImg;
	private ImageIcon smallIcon;
	private ImageIcon mediumIcon;
	
	private String name;
	private String classname;
	
	public String customCode;
	private final LinkedHashMap<String, StructureField> fields;
	public DataItem dref;
	public Folder guiRoot; //this is passed in from elsewhere.
	private StructureDefinitionEditor editor;
	
	public StructureDefinition(String name, String classname)
	{
		this.name = name;
		this.classname = classname;
		fields = new LinkedHashMap<String, StructureField>();
		customCode = "";
		
		Structure.addType(this);
		
		dref = new DataItem(name, this);
		dref.setIcon(smallIcon);
		
		guiRoot = new Folder("root", new StructureTable(this));
		guiRoot.setPolicy(STRUCTURE_DEFINITION_POLICY);
	}
	
	public void dispose()
	{
		disposeEditor();
		Structure.removeType(this);
		dref = null;
		guiRoot = null;
	}
	
	public void setImage(BufferedImage image)
	{
		this.iconImg = image;
		smallIcon = new ImageIcon(GraphicsUtilities.createThumbnail(image, 16));
		mediumIcon = new ImageIcon(image);
		dref.setIcon(smallIcon);
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getClassname()
	{
		return classname;
	}

	public void setClassname(String newClassname)
	{
		String oldClassname = classname;
		classname = newClassname;
		
		Types.typeFromXML.get(oldClassname).haxeType = newClassname;
		Types.typeFromXML.put(newClassname, Types.typeFromXML.remove(oldClassname));
		StructureDefinitions.defMap.put(newClassname, StructureDefinitions.defMap.remove(oldClassname));
		
		Structures.root.setDirty(true);
	}
	
	public BufferedImage getIconImg()
	{
		return iconImg;
	}
	
	public ImageIcon getSmallIcon()
	{
		return smallIcon;
	}
	
	public ImageIcon getMediumIcon()
	{
		return mediumIcon;
	}
	
	public StructureField getField(String name)
	{
		return fields.get(name);
	}
	
	public Collection<StructureField> getFields()
	{
		return fields.values();
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	@Override
	public StructureDefinitionEditor getEditor()
	{
		if(editor == null)
		{
			editor = new StructureDefinitionEditor(this);
			savedDefinitionDirtyState = dref.isDirty();
		}
		
		return editor;
	}
	
	public void addField(StructureField f)
	{
		fields.put(f.getVarname(), f);
	}
	
	public void removeField(StructureField f)
	{
		fields.remove(f);
	}

	//=== For runtime updating
	
	private ArrayList<StructureField> addedFields;
	private ArrayList<StructureField> removedFields;
	private HashMap<StructureField, TypeUpdate> typeUpdates;
	private HashMap<StructureField, Pair<String>> nameUpdates;
	
	private class TypeUpdate
	{
		//l is original type/optionalArgs
		//r is new type/optionalArgs
		
		Pair<DataType<?>> type;
		Pair<ExtraProperties> optArgs;
		
		public TypeUpdate(Pair<DataType<?>> type, Pair<ExtraProperties> optArgs)
		{
			this.type = type;
			this.optArgs = optArgs;
		}
	}
	
	public void addField(StructureField f, Structure s)
	{
		if(addedFields == null)
			addedFields = new ArrayList<StructureField>();
		addedFields.add(f);
		
		fields.put(f.getVarname(), f);
		s.clearProperty(f);
	}
	
	public void removeField(StructureField f, Structure s)
	{
		if(removedFields == null)
			removedFields = new ArrayList<StructureField>();
		removedFields.add(f);
		
		s.clearProperty(f);
		fields.remove(f);
	}
	
	public void setFieldTypeForPreview(StructureField f, DataType<?> type)
	{
		if(typeUpdates == null)
			typeUpdates = new HashMap<StructureField, TypeUpdate>();
		
		if(!typeUpdates.containsKey(f))
			typeUpdates.put(f,
				new TypeUpdate(
					new Pair<DataType<?>>(
						f.getType(),
						null
					),
					new Pair<ExtraProperties>(
						f.getExtras(),
						null
					)
				)
			);
		
		TypeUpdate update = typeUpdates.get(f);
		update.type.r = type;
		update.optArgs.r = type.loadExtras(new ExtrasMap());
		
		editor.preview.clearProperty(f);
		f.setExtras(update.optArgs.r);
	}
	
	public void update()
	{
		updateTypes();
		refreshFields(true);
		refreshEditors();
	}
	
	@Override
	public void revertChanges()
	{
		revertTypes();
		revertNames();
		refreshFields(false);
		
		if(!savedDefinitionDirtyState)
			dref.setDirty(false);
	}
	
	public void updateTypes()
	{
		if(typeUpdates != null)
		{
			for(StructureField field : typeUpdates.keySet())
			{
				Pair<DataType<?>> types = typeUpdates.get(field).type;
				if(types.l == types.r)
					continue;
				setFieldType(field, types.r);
			}
			typeUpdates.clear();
			typeUpdates = null;
		}
	}
	
	public void revertTypes()
	{
		if(typeUpdates != null)
		{
			for(StructureField field : typeUpdates.keySet())
			{
				field.setExtras(typeUpdates.get(field).optArgs.l);
				field.setType(typeUpdates.get(field).type.l);
			}
			typeUpdates.clear();
			typeUpdates = null;
		}
	}
	
	public void setFieldType(StructureField f, DataType<?> type)
	{
		for(Structure s : Structure.getAllOfType(this))
			s.clearProperty(f);
		if(f.getType() != type)
		{
			f.setType(type);
			f.setExtras(type.loadExtras(new ExtrasMap()));
		}
	}
	
	public void refreshFields(boolean commit)
	{
		if(commit)
		{
			//if(removeField != null) ... was in here before. Why?
			//This does nothing now.
		}
		else //revert
		{
			if(addedFields != null)
			{
				for(StructureField f : addedFields)
					removeField(f);
				addedFields.clear();
				addedFields = null;
			}
			if(removedFields != null)
			{
				for(StructureField f : removedFields)
					addField(f);
				removedFields.clear();
				removedFields = null;
			}
		}
	}
	
	private void revertNames()
	{
		if(nameUpdates != null)
		{
			for(StructureField f : nameUpdates.keySet())
				setFieldName(f, nameUpdates.get(f).l);
			nameUpdates.clear();
			nameUpdates = null;
		}
	}
	
	public void refreshEditors()
	{
		for(Structure s : Structure.getAllOfType(this))
			s.disposeEditor();
	}
	
	public void setFieldName(StructureField f, String name)
	{
		fields.remove(f.getVarname());
		fields.put(name, f);
	}
	
	//===
	
	private boolean savedDefinitionDirtyState;
	
	@Override
	public void disposeEditor()
	{
		if(editor != null)
			editor.dispose();
		editor = null;
	}
	
	@Override
	public void setDirty(boolean value)
	{
		guiRoot.setDirty(value);
		super.setDirty(value);
	}
	
	static class StructureDefinitionEditingPolicy extends FolderPolicy
	{
		public StructureDefinitionEditingPolicy()
		{
			duplicateItemNamesAllowed = false;
			folderCreationEnabled = false;
			itemCreationEnabled = true;
			itemEditingEnabled = false;
			itemRemovalEnabled = true;
		}
		
		@Override
		public boolean canAcceptItem(Folder folder, DataItem item)
		{
			boolean tabset = folder.getObject() instanceof StructureTabset;
			boolean tab = item.getObject() instanceof StructureTab;
			
			if(tabset != tab)
				return false;
			
			return super.canAcceptItem(folder, item);
		}
	}

	public void remove()
	{
		for(Structure s : Structures.structures.get(this))
			StructurePage.get().getFolderModel().removeItem(s.dref, s.dref.getParent());
		
		StructureDefinitions.defMap.remove(getClassname());
		Structures.structures.remove(this);
		Types.typeFromXML.remove(classname);
		
		dispose();
	}
}