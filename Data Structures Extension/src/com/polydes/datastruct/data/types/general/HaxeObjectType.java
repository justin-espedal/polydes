package com.polydes.datastruct.data.types.general;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.polydes.common.util.Lang;
import com.polydes.datastruct.data.core.HaxeObject;
import com.polydes.datastruct.data.core.HaxeObjectDefinition;
import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.ExtraProperties;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.UpdateListener;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.utils.Layout;

import stencyl.sw.editors.snippet.designer.Definition;

public class HaxeObjectType extends DataType<HaxeObject>
{
	private HaxeObjectDefinition def;
	
	public HaxeObjectType(HaxeObjectDefinition def)
	{
		super(HaxeObject.class, def.haxeClass, "OBJECT");
		this.def = def;
	}
	
	public HaxeObjectDefinition getDef()
	{
		return def;
	}

	@Override
	public DataEditor<HaxeObject> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new HaxeObjectEditor(style);
	}
	
	@Override
	public HaxeObject decode(String s)
	{
		String[] parts = s.length() <= 2 ?
				ArrayUtils.EMPTY_STRING_ARRAY :
				s.substring(1, s.length() - 1).split(",");
		Object[] values = new Object[def.fields.length];
		for(int i = 0; i < def.fields.length; ++i)
			values[i] = def.fields[i].type.decode(parts.length > i ? parts[i] : def.fields[i].defaultValue);
		
		return new HaxeObject(def, values);
	}

	@Override
	public String encode(HaxeObject o)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		for(int i = 0; i < o.values.length; ++i)
		{
			sb.append(def.fields[i].type.checkEncode(o.values[i]));
			if(i + 1 < o.values.length)
				sb.append(",");
		}
		sb.append("]");
		
		return sb.toString();
	}
	
	@Override
	public String toDisplayString(HaxeObject data)
	{
		return encode(data);
	}
	
	@Override
	public ArrayList<Definition> getBlocks()
	{
		return null;
	}
	
	@Override
	public HaxeObject copy(HaxeObject t)
	{
		return new HaxeObject(t);
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		
		//=== Default Value
		
		final DataEditor<HaxeObject> defaultField = new HaxeObjectEditor(panel.style);
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
		Extras e = (Extras) extras;
		ExtrasMap emap = new ExtrasMap();
		if(e.defaultValue != null)
			emap.put(DEFAULT_VALUE, encode(e.defaultValue));
		return emap;
	}
	
	class Extras extends ExtraProperties
	{
		public HaxeObject defaultValue;
		
		@Override
		public Object getDefault()
		{
			return defaultValue;
		}
	}
	
	public class HaxeObjectEditor extends DataEditor<HaxeObject>
	{
		JLabel[] labels;
		DataEditor<?>[] editors;
		
		public HaxeObjectEditor(PropertiesSheetStyle style)
		{
			editors = new DataEditor[def.fields.length];
			if(def.showLabels)
				labels = new JLabel[def.fields.length];
			
			UpdateListener updater = () -> updated();
			
			for(int i = 0; i < def.fields.length; ++i)
			{
				editors[i] = def.fields[i].type.createEditor(def.fields[i].editorData, style);
				editors[i].addListener(updater);
				if(labels != null)
				{
					labels[i] = style.createLabel(def.fields[i].name);
					labels[i].setHorizontalAlignment(SwingConstants.CENTER);
				}
			}
		}
		
		@Override
		public void set(HaxeObject o)
		{
			if(o == null)
			{
				System.out.println("Setting editor to null HaxeObject.");
				o = new HaxeObject(def, new Object[def.fields.length]);
			}
			for(int i = 0; i < def.fields.length; ++i)
			{
				editors[i].setValueUnchecked(o.values[i]);
			}
		}
		
		@Override
		public HaxeObject getValue()
		{
			Object[] values = new Object[editors.length];
			for(int i = 0; i < editors.length; ++i)
				values[i] = editors[i].getValue();
			return new HaxeObject(def, values);
		}
		
		@Override
		public JComponent[] getComponents()
		{
			List<JComponent> jcomps = new ArrayList<JComponent>();
			for(int i = 0; i < def.fields.length; ++i)
			{
				JComponent c = new JPanel(new BorderLayout());
				c.setBackground(null);
				if(labels != null)
					c.add(labels[i], BorderLayout.NORTH);
				c.add(Layout.horizontalBox(editors[i].getComponents()), BorderLayout.CENTER);
				jcomps.add(c);
			}
			
			return jcomps.toArray(new JComponent[jcomps.size()]);
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			for(DataEditor<?> editor : editors)
			{
				editor.dispose();
			}
			editors = null;
		}
	}

	@Override
	public List<String> generateHaxeReader()
	{
		if(def.haxereaderExpression != null)
		{
			return Lang.arraylist(String.format("StringData.registerReader(\"%s\", function(s) return %s);", haxeType, def.haxereaderExpression));
		}
		else
		{
			String[] types = Lang.map(def.fields, String.class, (field) -> "\"" + field.type.haxeType + "\"");
			return Lang.arraylist(String.format("StringData.registerHaxeObjectReader(\"%s\", %s, [%s]);", haxeType, haxeType, StringUtils.join(types, ",")));
		}
	}
}
