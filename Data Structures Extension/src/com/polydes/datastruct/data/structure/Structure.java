package com.polydes.datastruct.data.structure;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.polydes.common.util.Lang;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.EditableObject;
import com.polydes.datastruct.data.structure.elements.StructureCondition;
import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.ui.objeditors.StructureEditor;

public class Structure extends EditableObject
{
	private static final Logger log = Logger.getLogger(Structure.class);
	
	private static HashMap<StructureDefinition, ArrayList<Structure>> allStructures = new HashMap<StructureDefinition, ArrayList<Structure>>();
	
	public final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private StructureDefinition template;
	private HashMap<StructureField, Object> fieldData;
	private HashMap<StructureField, Boolean> enabledFields;
	protected StructureEditor editor;
	private int id;
	
	public DataItem dref;
	
	public Structure(int id, String name, StructureDefinition template)
	{
		this.id = id;
		this.template = template;
		fieldData = new HashMap<StructureField, Object>();
		enabledFields = new HashMap<StructureField, Boolean>();
		for(StructureField f : template.getFields())
		{
			Object value = f.getType().decode("");
			fieldData.put(f, value);
			enabledFields.put(f, !f.isOptional());
			pcs.firePropertyChange(f.getVarname(), null, value);
			log.debug(name + "::" + f.getVarname() + "=" + " -> " + value + " (init by string)");
		}
		
		allStructures.get(template).add(this);
		
		dref = new DataItem(name, this);
		dref.setIcon(template.getSmallIcon());
	}
	
	public void loadDefaults()
	{
		for(StructureField f : template.getFields())
			if(f.getExtras() != null)
			{
				Object defValue = f.getExtras().getDefault();
				if(defValue != null)
				setProperty(f, f.getType().checkCopy(defValue));
			}
	}
	
	public int getID()
	{
		return id;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
	}
	
	public void setPropertyFromString(StructureField field, String value)
	{
		Object newValue = field.getType().decode(value);
		Object oldValue = fieldData.get(field);
		fieldData.put(field, newValue);
		pcs.firePropertyChange(field.getVarname(), oldValue, newValue);
		dref.setDirty(true);
		
		log.debug(dref.getName() + "::" + field.getVarname() + "=" + oldValue + " -> " + newValue + " (by string)");
	}
	
	public void setProperty(StructureField field, Object value)
	{
		Object oldValue = fieldData.get(field);
		fieldData.put(field, value);
		pcs.firePropertyChange(field.getVarname(), oldValue, value);
		dref.setDirty(true);
		
		log.debug(dref.getName() + "::" + field.getVarname() + "=" + oldValue + " -> " + value + " (by object)");
	}
	
	public void clearProperty(StructureField field)
	{
		Object oldValue = fieldData.get(field);
		fieldData.put(field, null);
		enabledFields.put(field, !field.isOptional());
		pcs.firePropertyChange(field.getVarname(), oldValue, null);
		dref.setDirty(true);
	}
	
	public boolean isPropertyEnabled(StructureField field)
	{
		return enabledFields.get(field);
	}
	
	public void setPropertyEnabled(StructureField field, boolean value)
	{
		enabledFields.put(field, value);
	}
	
	public Object getPropByName(String name)
	{
		StructureField f = template.getField(name);
		if(f == null)
			return null;
		
		return getProperty(f);
	}
	
	public Object getProperty(StructureField field)
	{
		return fieldData.get(field);
	}
	
	public StructureField getField(String name)
	{
		return template.getField(name);
	}
	
	public Collection<StructureField> getFields()
	{
		return template.getFields();
	}

	public StructureDefinition getTemplate()
	{
		return template;
	}
	
	public boolean checkCondition(StructureCondition condition)
	{
		if(condition == null)
			return true;
		
		return condition.check(this);
	}
	
	@Override
	public StructureEditor getEditor()
	{
		if(editor == null)
			editor = new StructureEditor(this);
		
		return editor;
	}
	
	public String getDefname()
	{
		return template.getName();
	}
	
	public ImageIcon getSmallIcon()
	{
		return template.getSmallIcon();
	}
	
	public ImageIcon getMediumIcon()
	{
		return template.getMediumIcon();
	}
	
	public Structure copy()
	{
		Structure newStructure = new Structure(id, dref.getName(), template);
		
		for(StructureField field : getFields())
		{
			newStructure.setProperty(field, field.getType().checkCopy(getProperty(field)));
			newStructure.enabledFields.put(field, enabledFields.get(field));
		}
		
		return newStructure;
	}
	
	public void assignTo(Structure structure)
	{
		for(StructureField field : getFields())
		{
			setProperty(field, field.getType().checkCopy(structure.getProperty(field)));
			enabledFields.put(field, structure.enabledFields.get(field));
		}
	}
	
	public static void removeType(StructureDefinition def)
	{
		for(Structure s : allStructures.remove(def))
			s.dispose();
	}
	
	public void dispose()
	{
		fieldData.clear();
		enabledFields.clear();
		disposeEditor();
		if(Structures.structures.containsKey(template))
		{
			Structures.structures.get(template).remove(this);
		}
		Structures.structuresByID.remove(getID());
	}
	
	public static ArrayList<Structure> getAllOfType(StructureDefinition def)
	{
		return allStructures.get(def);
	}

	public static void addType(StructureDefinition def)
	{
		allStructures.put(def, new ArrayList<Structure>());
	}

	@Override
	public void disposeEditor()
	{
		if(editor != null)
			editor.dispose();
		editor = null;
	}
	
	@Override
	public void revertChanges()
	{
		
	}
	
	@Override
	public String toString()
	{
		return dref.getName();
	}
	
	private Map<String, String> unknownData;
	
	public Map<String, String> getUnknownData()
	{
		return unknownData;
	}
	
	public void setUnknownProperty(String key, String value)
	{
		if(unknownData == null)
			unknownData = new HashMap<String, String>();
		unknownData.put(key, value);
	}
	
	public void realizeTemplate(StructureDefinition def)
	{
		if(unknownData == null)
			return;
		
		log.info("Realizing unknown structure " + dref.getName() + " as " + def.getClassname());
		
		StructureDefinition oldTemplate = template;
		template = def;
		dref.setIcon(template.getSmallIcon());
		
		for(StructureField f : template.getFields())
		{
			Object value = f.getType().decode("");
			fieldData.put(f, value);
			enabledFields.put(f, !f.isOptional());
			pcs.firePropertyChange(f.getVarname(), null, value);
			log.debug(dref.getName() + "::" + f.getVarname() + "=" + " -> " + value + " (init by string)");
		}
		
		for(StructureField f : getFields())
		{
			Object o = unknownData.remove(f.getVarname());
			setPropertyFromString(f, Lang.or((String) o, ""));
			setPropertyEnabled(f, o != null || !f.isOptional());
		}
		if(unknownData.isEmpty())
			unknownData = null;
		
		allStructures.get(oldTemplate).remove(this);
		allStructures.get(template).add(this);
		
		disposeEditor();
	}
}