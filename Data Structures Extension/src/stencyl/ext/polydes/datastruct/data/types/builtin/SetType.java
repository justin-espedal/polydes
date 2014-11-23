package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.apache.commons.lang3.StringUtils;

import stencyl.core.lib.Game;
import stencyl.ext.polydes.datastruct.data.core.DataList;
import stencyl.ext.polydes.datastruct.data.core.DataSet;
import stencyl.ext.polydes.datastruct.data.core.Dynamic;
import stencyl.ext.polydes.datastruct.data.structure.StructureDefinition;
import stencyl.ext.polydes.datastruct.data.structure.Structures;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.data.types.general.StencylResourceType;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.Layout;

public class SetType extends BuiltinType<DataSet>
{
	public SetType()
	{
		super(DataSet.class, "Map", "OBJECT", "Set");
	}

	@Override
	public JComponent[] getEditor(final DataUpdater<DataSet> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		Extras e = (Extras) extras;
		String properType = "";
		Collection<?> source = null;
		if(e.sourceType.equals(SourceType.Structure))
		{
			StructureDefinition def = (StructureDefinition) e.source;
			if(def != null)
			{
				source = Structures.getList(def.name);
				properType = def.name;
			}
		}
		else if(e.sourceType.equals(SourceType.Resource))
		{
			StencylResourceType<?> dtype = (StencylResourceType<?>) e.source;
			if(dtype != null)
			{
				source = Game.getGame().getResources().getResourcesByType(dtype.javaType);
				properType = dtype.xml;
			}
		}
		else
		{
			source = (Collection<?>) e.source;
			if(source == null)
				source = new ArrayList<Dynamic>();
			properType = "String";
		}
		
		if(source == null)
			return comps(style.createSoftLabel("Select a valid data source"));
		
		if(source.isEmpty())
			return comps(style.createSoftLabel("The selected source has no items"));
		
		if(updater.get() == null || !updater.get().genType.xml.equals(properType))
			updater.set(new DataSet(Types.fromXML(properType)));
		final DataSet set = updater.get();
		
		if(e.editor.equals(Editor.Grid))
			return comps(style.createSoftLabel("Grid is unimplemented"));
		
		else// if(editorType.equals("Checklist"))
		{
			ArrayList<JCheckBox> buttons = new ArrayList<JCheckBox>();
			
			for(final Object o : source)
			{
				final JCheckBox b = new JCheckBox("" + o);
				buttons.add(b);
				if(set.contains(o))
					b.setSelected(true);
				
				b.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						if(b.isSelected())
							set.add(o);
						else
							set.remove(o);
					}
				});
				
				b.setBackground(null);
				b.setForeground(style.labelColor);
			}
			
			return comps(Layout.verticalBox(0, buttons.toArray(new JCheckBox[0])));
		}
	}

	@Override
	public DataSet decode(String s)
	{
		int typeMark = s.lastIndexOf(":");
		if(typeMark == -1)
			return new DataSet(Types.fromXML("Dynamic"));
		
		DataType<?> dtype = Types.fromXML(s.substring(typeMark + 1));
		if(dtype == null)
			return new DataSet(Types.fromXML("Dynamic"));
		
		DataSet toReturn = new DataSet(dtype);
		
		for(String s2 : StringUtils.split(s.substring(1, typeMark - 1), ","))
			toReturn.add(dtype.decode(s2));
		
		return toReturn;
	}

	@Override
	public String encode(DataSet t)
	{
		Object[] a = t.toArray(new Object[0]);
		String s = "[";
		DataType<?> type = t.genType;
		
		for(int i = 0; i < a.length; ++i)
		{
			s += type.checkEncode(a[i]);
			
			if(i < a.length - 1)
				s += ",";
		}
		
		s += "]:" + type.xml;
		
		return s;
	}
	
	//For set, because none of the data should ever actually be modified, a shallow copy is fine.
	@Override
	public DataSet copy(DataSet t)
	{
		DataSet copySet = new DataSet(t.genType);
		Iterator<Object> it = t.iterator();
		while(it.hasNext())
			copySet.add(it.next());
		return copySet;
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		e.editor = extras.get(EDITOR, Editor.Checklist);
		e.sourceType = SourceType.valueOf(extras.get("sourceType", "Structure"));
		if(e.sourceType.equals(SourceType.Structure) || e.sourceType.equals(SourceType.Resource))
			e.source = Types.fromXML(extras.get("source", ""));
		else
			e.source = extras.get("source", Types._Array, null);
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		emap.put(EDITOR, "" + e.editor);
		emap.put("sourceType", "" + e.sourceType);
		if(e.sourceType.equals(SourceType.Structure) || e.sourceType.equals(SourceType.Resource))
			emap.put("source", ((DataType<?>) e.source).xml);
		else
			emap.put("source", Types._Array.encode((DataList) e.source));
		return emap;
	}
	
	class Extras extends ExtraProperties
	{
		public Editor editor;
		public SourceType sourceType;
		public Object source; //StructureType, StencylType, or (Array, Simple editor)
	}
	
	enum Editor
	{
		Checklist,
		Grid
	}
	
	enum SourceType
	{
		Structure,
		Resource,
		Custom
	}
}
