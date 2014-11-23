package stencyl.ext.polydes.datastruct.data.structure;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.ImageIcon;

import stencyl.ext.polydes.datastruct.data.core.Pair;
import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.folder.EditableObject;
import stencyl.ext.polydes.datastruct.data.folder.Folder;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureDefinitionEditor;
import stencyl.sw.util.gfx.GraphicsUtilities;

public class StructureDefinition extends EditableObject
{
	public BufferedImage iconImg;
	public ImageIcon smallIcon;
	public ImageIcon mediumIcon;
	
	public String name;
	public String classname;
	
	public String customCode;
	private LinkedHashMap<String, StructureField> fields;
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
	}
	
	public void setImage(BufferedImage image)
	{
		this.iconImg = image;
		smallIcon = new ImageIcon(GraphicsUtilities.createThumbnail(image, 16));
		mediumIcon = new ImageIcon(image);
		dref.setIcon(smallIcon);
	}
	
	public void setName(String newName)
	{
		String oldName = name;
		name = newName;
		
		Types.typeFromXML.get(oldName).xml = newName;
		Types.typeFromXML.put(newName, Types.typeFromXML.remove(oldName));
		Structures.structures.put(newName, Structures.structures.remove(oldName));
		StructureDefinitions.defMap.put(newName, StructureDefinitions.defMap.remove(oldName));
		
		Structures.root.setDirty(true);
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
			savedDefinitionDirtyState = isDirty();
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
	
	public void setFieldType(StructureField f, Structure s, DataType<?> type)
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
		
		s.clearProperty(f);
		f.setType(type);
		f.setExtras(update.optArgs.r);
	}
	
	public void update()
	{
		updateTypes();
		refreshFields(true);
		//TODO:
		//commitGui();
		refreshEditors();
	}
	
	@Override
	public void revertChanges()
	{
		revertTypes();
		revertNames();
		refreshFields(false);
		//TODO:
		//revertGui();
		
		if(!savedDefinitionDirtyState)
			setDirty(false);
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
			if(removedFields != null)
			{
				for(StructureField f : removedFields)
					addField(f);
				removedFields.clear();
				removedFields = null;
			}
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
		f.setVarname(name);
		fields.put(name, f);
	}
	
	//===
	
	//TODO: For reverting changes.
	
	
//	private Folder guiRoot2 = null;
//	private boolean guiAlreadyChanged = false;
	private boolean savedDefinitionDirtyState;
	
	/*
	public void guiChanged()
	{
		if(guiAlreadyChanged)
			return;
		
		guiAlreadyChanged = true;
		gui2 = gui.copy();
	}
	
	private void commitGui()
	{
		if(gui2 != null)
			gui2.dispose();

		gui2 = null;
		guiAlreadyChanged = false;
	}
	
	private void revertGui()
	{
		if(gui2 != null)
		{
			gui = gui2.copy();
			gui2.dispose();
		}
		gui2 = null;
		guiAlreadyChanged = false;
	}*/

	public void disposeEditor()
	{
		if(editor != null)
			editor.dispose();
		editor = null;
	}
}