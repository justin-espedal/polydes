package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import stencyl.ext.polydes.datastruct.data.types.DataEditor;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.data.types.UpdateListener;
import stencyl.ext.polydes.datastruct.ui.comp.colors.ColorDisplay;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import stencyl.ext.polydes.datastruct.ui.page.StructureDefinitionsWindow;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.utils.ColorUtil;

public class ColorType extends BuiltinType<Color>
{
	public ColorType()
	{
		super(Color.class, "Int", "COLOR", "Color");
	}

	@Override
	public DataEditor<Color> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new ColorEditor();
	}

	@Override
	public Color decode(String s)
	{
		return ColorUtil.decode(s);
	}

	@Override
	public String encode(Color c)
	{
		return ColorUtil.encode(c);
	}

	@Override
	public Color copy(Color t)
	{
		return t;
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		
		//=== Default Value
		
		final ColorEditor defaultField = new ColorEditor();
		defaultField.setValue(e.defaultValue);
		defaultField.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				e.defaultValue = defaultField.getValue();
			}
		});
		
		defaultField.setOwner(StructureDefinitionsWindow.get());
		
		panel.addGenericRow(expansion, "Default", defaultField);
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._Color, null);
		return e;
	}
	
	@Override
	public ExtrasMap saveExtras(ExtraProperties extras)
	{
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		if(e.defaultValue != null)
			emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		return emap;
	}
	
	class Extras extends ExtraProperties
	{
		public Color defaultValue;
		
		@Override
		public Object getDefault()
		{
			return defaultValue;
		}
	}
	
	public static class ColorEditor extends DataEditor<Color>
	{
		ColorDisplay control;
		
		public ColorEditor()
		{
			control = new ColorDisplay(20, 20, null, null);
			
			control.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return comps(control);
		}
		
		@Override
		public Color getValue()
		{
			return control.getColor();
		}
		
		@Override
		public void set(Color c)
		{
			if(c == null)
				c = Color.BLACK;
			control.setColor(c);
		}
		
		public void setOwner(Window owner)
		{
			control.setOwner(owner);
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			control.dispose();
			control = null;
		}
	}
}
