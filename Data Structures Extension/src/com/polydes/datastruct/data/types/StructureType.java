package com.polydes.datastruct.data.types;

import java.util.function.Predicate;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

import com.polydes.common.comp.UpdatingCombo;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.common.util.PredicateUtil;
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
	
	public static final String SOURCE_FILTER = "sourceFilter";
	public static final String ALLOW_SUBTYPES = "allowSubtypes";
	
	@Override
	public DataEditor<Structure> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		return new StructureEditor(props, null);
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new StructureEditorBuilder();
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
	
	
	public class StructureEditorBuilder extends DataEditorBuilder
	{
		public StructureEditorBuilder()
		{
			super(StructureType.this, new EditorProperties());
		}
		
		public StructureEditorBuilder filter(StructureCondition filter)
		{
			props.put(SOURCE_FILTER, filter);
			return this;
		}
		
		public StructureEditorBuilder allowSubtypes()
		{
			props.put(ALLOW_SUBTYPES, true);
			return this;
		}
	}
	
	public class StructureEditor extends DataEditor<Structure>
	{
		private UpdatingCombo<Structure> editor;
		
		public StructureEditor(EditorProperties props, Structure currentStructure)
		{
			StructureCondition condition = props.get(SOURCE_FILTER);
			Predicate<Structure> predicate = condition == null ? null : new StructurePredicate(condition, currentStructure);
			
			boolean allowSubtypes = props.get(ALLOW_SUBTYPES) == Boolean.TRUE;
			
			if(allowSubtypes)
				predicate = PredicateUtil.and(predicate, (s) -> s.getTemplate().is(def));
			
			editor = new UpdatingCombo<Structure>(allowSubtypes ? Structures.structuresByID.values() : Structures.getList(def), predicate);
			editor.addActionListener(event -> updated());
		}
		
		public StructureEditor()
		{
			editor = new UpdatingCombo<Structure>(Structures.getList(def), null);
			editor.addActionListener(event -> updated());
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
	
	class StructurePredicate implements Predicate<Structure>
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