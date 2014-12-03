package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import stencyl.ext.polydes.datastruct.data.types.DataEditor;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.data.types.UpdateListener;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class BooleanType extends BuiltinType<Boolean>
{
	public BooleanType()
	{
		super(Boolean.class, "Bool", "BOOLEAN", "Boolean");
	}

	@Override
	public DataEditor<Boolean> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new BooleanEditor();
	}

	@Override
	public Boolean decode(String s)
	{
		return s.equals("true");
	}

	@Override
	public String encode(Boolean b)
	{
		return b ? "true" : "false";
	}

	@Override
	public Boolean copy(Boolean t)
	{
		return new Boolean(t);
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		
		//=== Default Value
		
		final DataEditor<Boolean> defaultField = new BooleanEditor();
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
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._Boolean, false);
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
		public Boolean defaultValue;
	}
	
	public static class BooleanEditor extends DataEditor<Boolean>
	{
		JCheckBox control;
		
		public BooleanEditor()
		{
			control = new JCheckBox();
			control.setBackground(null);
			
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
		public Boolean getValue()
		{
			return control.isSelected();
		}
		
		@Override
		public void set(Boolean b)
		{
			if(b == null)
				b = false;
			control.setSelected(b);
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			control = null;
		}
	}
}
