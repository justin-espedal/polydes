package com.polydes.datastruct.data.types.builtin;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.ExtraProperties;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.Types;
import com.polydes.datastruct.data.types.UpdateListener;
import com.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.utils.DocumentAdapter;
import com.polydes.datastruct.ui.utils.IntegerFilter;
import com.polydes.datastruct.utils.StringData;

public class DimensionType extends BuiltinType<Dimension>
{
	public DimensionType()
	{
		super(Dimension.class, "nme.geom.Point", "OBJECT", "Dimension");
	}

	@Override
	public DataEditor<Dimension> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new DimensionEditor(style);
	}

	@Override
	public Dimension decode(String s)
	{
		int[] ints = StringData.getInts(s);
		if(ints == null)
			return new Dimension(0, 0);
		
		return new Dimension(ints[0], ints[1]);
	}

	@Override
	public String encode(Dimension d)
	{
		return "[" + d.width + ", " + d.height + "]";
	}
	
	@Override
	public String toDisplayString(Dimension data)
	{
		return encode(data);
	}

	@Override
	public Dimension copy(Dimension t)
	{
		return new Dimension(t);
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		final PropertiesSheetStyle style = panel.style;
		
		//=== Default Value
		
		final DataEditor<Dimension> defaultField = new DimensionEditor(style);
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
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._Dimension, null);
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
		public Dimension defaultValue;
		
		@Override
		public Object getDefault()
		{
			return defaultValue;
		}
	}
	
	public static class DimensionEditor extends DataEditor<Dimension>
	{
		private JTextField widthField;
		private JTextField heightField;
		
		public DimensionEditor(PropertiesSheetStyle style)
		{
			widthField = style.createTextField();
			heightField = style.createTextField();
			((PlainDocument) widthField.getDocument()).setDocumentFilter(new IntegerFilter());
			((PlainDocument) heightField.getDocument()).setDocumentFilter(new IntegerFilter());
			
			DocumentAdapter updateDimension = new DocumentAdapter(true)
			{
				@Override
				protected void update()
				{
					updated();
				}
			};
			
			widthField.getDocument().addDocumentListener(updateDimension);
			heightField.getDocument().addDocumentListener(updateDimension);
		}
		
		@Override
		public Dimension getValue()
		{
			return new Dimension(
					Integer.parseInt(widthField.getText()),
					Integer.parseInt(heightField.getText()));
		}
		
		@Override
		public void set(Dimension t)
		{
			if(t == null)
				t = new Dimension(0, 0);
			widthField.setText("" + t.width);
			heightField.setText("" + t.height);
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return comps(widthField, heightField);
		}
	}
}
