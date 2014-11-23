package stencyl.ext.polydes.datastruct.data.structure;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.ImageIcon;

import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.folder.EditableObject;
import stencyl.ext.polydes.datastruct.data.structure.cond.StructureCondition;
import stencyl.ext.polydes.datastruct.data.structure.cond.StructureConditionVerifier;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureEditor;

public class Structure extends EditableObject implements StructureConditionVerifier
{
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
		}
		
		allStructures.get(template).add(this);
		
		dref = new DataItem(name, this);
		dref.setIcon(template.smallIcon);
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
		setDirty(true);
	}
	
	public void setProperty(StructureField field, Object value)
	{
		Object oldValue = fieldData.get(field);
		if(oldValue == (Integer) 50)
			new Exception().printStackTrace();
		fieldData.put(field, value);
		pcs.firePropertyChange(field.getVarname(), oldValue, value);
		setDirty(true);
	}
	
	public void clearProperty(StructureField field)
	{
		Object oldValue = fieldData.get(field);
		if(oldValue == (Integer) 50)
			new Exception().printStackTrace();
		fieldData.put(field, null);
		enabledFields.put(field, !field.isOptional());
		pcs.firePropertyChange(field.getVarname(), oldValue, null);
		setDirty(true);
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
	public boolean verify(StructureField field, String value)
	{
		if(field == null || getProperty(field) == null)
			return false;
		
		return getProperty(field).equals(field.getType().decode(value));
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
		return template.name;
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
	
	public void dispose()
	{
		allStructures.get(template).remove(this);
		fieldData.clear();
		enabledFields.clear();
		disposeEditor();
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
		//TODO: revert Structure editor changes
	}
}