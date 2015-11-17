package com.polydes.datastruct.data.types;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.ArrayUtils;

import com.polydes.common.comp.utils.Layout;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.data.core.HaxeObject;
import com.polydes.datastruct.data.core.HaxeObjectDefinition;

public class HaxeObjectType extends DataType<HaxeObject>
{
	private HaxeObjectDefinition def;
	
	public HaxeObjectType(HaxeObjectDefinition def)
	{
		super(HaxeObject.class, def.haxeClass);
		this.def = def;
	}
	
	public HaxeObjectDefinition getDef()
	{
		return def;
	}

	@Override
	public DataEditor<HaxeObject> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		return new HaxeObjectEditor(style);
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new DataEditorBuilder(this, new EditorProperties());
	}
	
	@Override
	public HaxeObject decode(String s)
	{
		String[] parts = s.length() <= 2 ?
				ArrayUtils.EMPTY_STRING_ARRAY :
				s.substring(1, s.length() - 1).split(",");
		Object[] values = new Object[def.fields.length];
		for(int i = 0; i < def.fields.length; ++i)
			values[i] = def.fields[i].type.dataType.decode(parts.length > i ? parts[i] : def.fields[i].defaultValue);
		
		return new HaxeObject(def, values);
	}

	@Override
	public String encode(HaxeObject o)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		for(int i = 0; i < o.values.length; ++i)
		{
			sb.append(def.fields[i].type.dataType.checkEncode(o.values[i]));
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
	public HaxeObject copy(HaxeObject t)
	{
		return new HaxeObject(t);
	}
	
	public class HaxeObjectEditor extends DataEditor<HaxeObject>
	{
		final JLabel[] labels;
		final DataEditor<?>[] editors;
		final JComponent[] comps;
		
		public HaxeObjectEditor(PropertiesSheetStyle style)
		{
			editors = new DataEditor[def.fields.length];
			labels = def.showLabels ?
				new JLabel[def.fields.length] : null;
			
			UpdateListener updater = () -> updated();
			
			for(int i = 0; i < def.fields.length; ++i)
			{
				HaxeDataType htype = def.fields[i].type;
				editors[i] = htype.dataType.createEditor(htype.loadExtras(def.fields[i].editorData), style);
				editors[i].addListener(updater);
				if(labels != null)
				{
					labels[i] = style.createLabel(def.fields[i].name);
					labels[i].setHorizontalAlignment(SwingConstants.CENTER);
				}
			}
			
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
			
			comps = jcomps.toArray(new JComponent[jcomps.size()]);
		}
		
		@Override
		public void set(HaxeObject o)
		{
			if(o == null)
				o = new HaxeObject(def, new Object[def.fields.length]);
			for(int i = 0; i < def.fields.length; ++i)
				editors[i].setValueUnchecked(o.values[i]);
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
			return comps;
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			for(DataEditor<?> editor : editors)
				editor.dispose();
		}
	}
}
