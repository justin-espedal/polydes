package com.polydes.datastruct.data.types;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

import com.polydes.common.collections.CollectionPredicate;
import com.polydes.common.comp.UpdatingCombo;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.ExtraProperties;
import com.polydes.common.data.types.ExtrasMap;
import com.polydes.common.data.types.Types;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.data.structure.elements.StructureCondition;

public class StructureType extends DataType<Structure>
{
	private static final Logger log = Logger.getLogger(StructureType.class);
	
	public StructureDefinition def;
	
	public StructureType(StructureDefinition def)
	{
		super(Structure.class, def.getFullClassname());
		this.def = def;
	}
	
	@Override
	public DataEditor<Structure> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new StructureEditor((Extras) extras, null);
	}

	@Override
	public Structure decode(String s)
	{
		try
		{
			int id = Integer.parseInt(s);
			Structure model = Structures.getStructure(id);
			
			if(model == null)
			{
				log.warn("Couldn't load structure with id " + s + ". It no longer exists.");
				return null;
			}
			if(!model.getTemplate().is(def) && !model.getTemplate().isUnknown() && !def.isUnknown())
			{
				log.warn("Couldn't load structure with id " + s + " as type " + def.getName());
				return null;
			}
			
			return model;
		}
		catch(NumberFormatException ex)
		{
			return null;
		}
	}

	@Override
	public String encode(Structure model)
	{
		if(model == null)
			return "";
		
		return "" + model.getID();
	}

	@Override
	public String toDisplayString(Structure data)
	{
		return String.valueOf(data);
	}
	
	@Override
	public Structure copy(Structure t)
	{
		return t;
	}
	
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		String filterText = extras.get("sourceFilter", Types._String, null);
		if(filterText != null)
			e.sourceFilter = new StructureCondition(null, filterText);
		e.defaultValue = extras.get(DEFAULT_VALUE, this, null);
		e.allowSubclasses = extras.get("allowSubclasses", Types._Bool, false);
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		if(e.sourceFilter != null)
			emap.put("sourceFilter", e.sourceFilter.getText());
		if(e.defaultValue != null)
			emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		if(e.allowSubclasses)
			emap.put("allowSubclasses", "true");
		return emap;
	}
	
	public class Extras extends ExtraProperties
	{
		public StructureCondition sourceFilter;
		public Structure defaultValue;
		public boolean allowSubclasses;
		
		@Override
		public Object getDefault()
		{
			return defaultValue;
		}
	}
	
	public class StructureEditor extends DataEditor<Structure>
	{
		private UpdatingCombo<Structure> editor;
		
		public StructureEditor(Extras e, Structure currentStructure)
		{
			CollectionPredicate<Structure> filter =
				e.sourceFilter == null ? null :
				new StructurePredicate(e.sourceFilter, currentStructure);
			
			if(e.allowSubclasses)
				filter = CollectionPredicate.and(filter, (s) -> s.getTemplate().is(def));
			
			editor = new UpdatingCombo<Structure>(e.allowSubclasses ? Structures.structuresByID.values() : Structures.getList(def), filter);
			
			editor.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
		}
		
		public StructureEditor()
		{
			editor = new UpdatingCombo<Structure>(Structures.getList(def), null);
			
			editor.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
		}
		
		@Override
		public void set(Structure t)
		{
			editor.setSelectedItem(t);
		}
		
		@Override
		public Structure getValue()
		{
			return editor.getSelected();
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return new JComponent[] {editor};
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			editor.dispose();
			editor = null;
		}
	}
	
	class StructurePredicate implements CollectionPredicate<Structure>
	{
		private StructureCondition condition;
		private Structure s;
		
		public StructurePredicate(StructureCondition condition, Structure s)
		{
			this.condition = condition;
			this.s = s;
		}
		
		@Override
		public boolean test(Structure s2)
		{
			return condition.check(s, s2);
		}
	}
}