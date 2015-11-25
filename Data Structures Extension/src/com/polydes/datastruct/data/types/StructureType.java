package com.polydes.datastruct.data.types;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Predicate;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.apache.log4j.Logger;

import com.polydes.common.comp.RenderedPanel;
import com.polydes.common.comp.UpdatingCombo;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.nodes.Leaf;
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
	public static final String RENDER_PREVIEW = "renderPreview";
	
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
		
		public StructureEditorBuilder rendered()
		{
			props.put(RENDER_PREVIEW, Boolean.TRUE);
			return this;
		}
	}
	
	public class StructureEditor extends DataEditor<Structure> implements PropertyChangeListener
	{
		private final RenderedPanel panel;
		private final UpdatingCombo<Structure> editor;
		private Structure oldStructure;
		
		public StructureEditor(EditorProperties props, Structure currentStructure)
		{
			StructureCondition condition = props.get(SOURCE_FILTER);
			Predicate<Structure> predicate = condition == null ? null : new StructurePredicate(condition, currentStructure);
			
			boolean allowSubtypes = props.get(ALLOW_SUBTYPES) == Boolean.TRUE;
			
			if(allowSubtypes)
				predicate = PredicateUtil.and(predicate, (s) -> s.getTemplate().is(def));
			
			editor = new UpdatingCombo<Structure>(allowSubtypes ? Structures.structuresByID.values() : Structures.getList(def), predicate);
			editor.addActionListener(event -> valueUpdated(true));
			oldStructure = null;
			
			panel = props.get(RENDER_PREVIEW) == Boolean.TRUE ?
				new RenderedPanel(90, 60, 0) : null;
		}
		
		public StructureEditor()
		{
			editor = new UpdatingCombo<Structure>(Structures.getList(def), null);
			editor.addActionListener(event -> updated());
			panel = null;
		}
		
		private void valueUpdated(boolean callUpdated)
		{
			Structure t = editor.getSelected();
			if(t == oldStructure)
				return;
			if(panel != null)
			{
				if(oldStructure != null)
					uninstallIconListener(oldStructure);
				if(t != null)
					installIconListener(t);
				else
					panel.setLabel(null);
			}
			oldStructure = t;
			if(callUpdated)
				updated();
		}
		
		private void installIconListener(Structure t)
		{
			t.dref.addListener(Leaf.ICON, this);
			setImageIcon(t.dref.getIcon());
		}
		
		private void uninstallIconListener(Structure t)
		{
			t.dref.removeListener(Leaf.ICON, this);
			panel.setLabel(t.getIcon().getImage());
		}
		
		private void setImageIcon(ImageIcon icon)
		{
			if(icon == null)
				panel.setLabel(null);
			else
				panel.setLabel(icon.getImage());
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			setImageIcon((ImageIcon) evt.getNewValue());
		}
		
		@Override
		public void set(Structure t)
		{
			editor.setSelectedItem(t);
			valueUpdated(false);
		}
		
		@Override
		public Structure getValue()
		{
			return editor.getSelected();
		}
		
		@Override
		public JComponent[] getComponents()
		{
			if(panel != null)
				return new JComponent[] {panel, editor};
			else
				return new JComponent[] {editor};
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			if(panel != null && oldStructure != null)
				uninstallIconListener(oldStructure);
			editor.dispose();
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