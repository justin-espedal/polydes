package stencyl.ext.polydes.datastruct.data.types.general;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import stencyl.core.lib.Game;
import stencyl.core.lib.Resource;
import stencyl.ext.polydes.datastruct.data.types.DataEditor;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.UpdateListener;
import stencyl.ext.polydes.datastruct.ui.comp.UpdatingCombo;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class StencylResourceType<T extends Resource> extends DataType<T>
{
	public StencylResourceType(Class<T> javaType, String haxeType, String stencylType, String xml)
	{
		super(javaType, haxeType, stencylType, xml);
	}
	
	@Override
	public List<String> generateHaxeClass()
	{
		return null;
	}

	@Override
	public List<String> generateHaxeReader()
	{
		List<String> toReturn = new ArrayList<String>();
		toReturn.add("\tpublic static function r" + xml + "(s:String):" + haxeType);
		toReturn.add("\t{");
		toReturn.add("\t\tif(s == \"\")");
		toReturn.add("\t\t\treturn null;");
		toReturn.add("\t\t");
		toReturn.add("\t\treturn cast(Data.get().resources.get(Std.parseInt(s)), " + haxeType + ");");
		toReturn.add("\t}");
		return toReturn;
	}

	@Override
	public DataEditor<T> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new DropdownResourceEditor();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T decode(String s)
	{
		try
		{
			int id = Integer.parseInt(s);
			Resource r = Game.getGame().getResource(id);
			if(r != null && javaType.isAssignableFrom(r.getClass()))
				return (T) r;
			
			return null;
		}
		catch(NumberFormatException ex)
		{
			return null;
		}
	}

	@Override
	public String encode(T r)
	{
		if(r == null)
			return "";
		
		return "" + r.getID();
	}
	
	@Override
	public String toDisplayString(T data)
	{
		return String.valueOf(data);
	}
	
	@Override
	public T copy(T t)
	{
		return t;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		@SuppressWarnings("unchecked")
		final Extras e = (Extras) panel.getExtras();
		
		//=== Default Value
		
		final DataEditor<T> defaultField = new DropdownResourceEditor();
		defaultField.setValue(e.defaultValue);
		defaultField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.defaultValue = defaultField.getValue();
			}
		});
		
		panel.addGenericRow(expansion, "Default", defaultField);
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		e.defaultValue = extras.get(DEFAULT_VALUE, this, null);
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		@SuppressWarnings("unchecked")
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		if(e.defaultValue != null)
			emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		return emap;
	}
	
	class Extras extends ExtraProperties
	{
		public T defaultValue;
		
		@Override
		public Object getDefault()
		{
			return defaultValue;
		}
	}
	
	public class DropdownResourceEditor extends DataEditor<T>
	{
		UpdatingCombo<T> editor;
		
		public DropdownResourceEditor()
		{
			editor = new UpdatingCombo<T>(Game.getGame().getResources().getResourcesByType(javaType), null);
			
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
		public void set(T t)
		{
			editor.setSelectedItem(t);
		}
		
		@Override
		public T getValue()
		{
			return editor.getSelected();
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return comps(editor);
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			editor.dispose();
			editor = null;
		}
	}
}