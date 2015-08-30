package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import stencyl.ext.polydes.datastruct.Blocks;
import stencyl.ext.polydes.datastruct.data.types.DataEditor;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.data.types.UpdateListener;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.DocumentAdapter;
import stencyl.ext.polydes.datastruct.ui.utils.IntegerFilter;
import stencyl.ext.polydes.datastruct.utils.StringData;
import stencyl.sw.editors.snippet.designer.AttributeType;
import stencyl.sw.editors.snippet.designer.Block.BlockType;
import stencyl.sw.editors.snippet.designer.BlockTheme;
import stencyl.sw.editors.snippet.designer.Definition;
import stencyl.sw.editors.snippet.designer.Definition.Category;
import stencyl.sw.editors.snippet.designer.Definitions;
import stencyl.sw.editors.snippet.designer.codemap.BasicCodeMap;
import stencyl.sw.editors.snippet.designer.dropdown.DropdownData;

public class PointType extends BuiltinType<Point>
{
	public PointType()
	{
		super(Point.class, "nme.geom.Point", "OBJECT", "Point");
	}

	@Override
	public DataEditor<Point> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new PointEditor(style);
	}
	
	@Override
	public Point decode(String s)
	{
		int[] ints = StringData.getInts(s);
		if(ints == null)
			return new Point(0, 0);
		if(ints.length == 1)
			return new Point(ints[0], 0);
		
		return new Point(ints[0], ints[1]);
	}

	@Override
	public String encode(Point p)
	{
		return "[" + p.x + "," + p.y + "]";
	}
	
	@Override
	public String toDisplayString(Point data)
	{
		return encode(data);
	}
	
	@Override
	public ArrayList<Definition> getBlocks()
	{
		ArrayList<Definition> blocks = new ArrayList<Definition>();
		
		//---
		
		String spec = "point x: %0 y: %1";
		
		Definition blockDef = new Definition
		(
			Category.CUSTOM,
			"ds-point-new",
			new AttributeType[] { AttributeType.NUMBER, AttributeType.NUMBER },
			new BasicCodeMap("new nme.geom.Point(~, ~)"),
			spec,
			BlockType.NORMAL,
			AttributeType.OBJECT
		);
		
		blockDef.guiTemplate = spec;
		blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");
		
		blocks.add(blockDef);
		
		//---
		
		spec = "set %1 of %0 to %2";
		
		blockDef = new Definition
		(
			Category.CUSTOM,
			"ds-point-set",
			new AttributeType[] { AttributeType.OBJECT, AttributeType.DROPDOWN, AttributeType.NUMBER },
			new BasicCodeMap("~.~ = ~;"),
			spec,
			BlockType.ACTION,
			AttributeType.VOID
		);
		
		blockDef.guiTemplate = spec;
		blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");
		blockDef.dropdowns = new DropdownData[] {Definitions.blank, Blocks.createGenericDropdown(new String[] {"x",  "y"}, new String[] {"x",  "y"}), Definitions.blank};
		
		blocks.add(blockDef);
		
		//---
		
		spec = "get %1 of %0";
		
		blockDef = new Definition
		(
			Category.CUSTOM,
			"ds-point-get",
			new AttributeType[] { AttributeType.OBJECT, AttributeType.DROPDOWN },
			new BasicCodeMap("~.~"),
			spec,
			BlockType.NORMAL,
			AttributeType.NUMBER
		);
		
		blockDef.guiTemplate = spec;
		blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");
		blockDef.dropdowns = new DropdownData[] {Definitions.blank, Blocks.createGenericDropdown(new String[] {"x",  "y"}, new String[] {"x",  "y"})};
		
		blocks.add(blockDef);
		
		//---
		
		return blocks;
	}
	
	@Override
	public Point copy(Point t)
	{
		return new Point(t);
	}
	
	@Override
	public void applyToFieldPanel(StructureFieldPanel panel)
	{
		int expansion = panel.getExtraPropertiesExpansion();
		final Extras e = (Extras) panel.getExtras();
		PropertiesSheetStyle style = panel.style;
		
		//=== Default Value
		
		final DataEditor<Point> defaultField = new PointEditor(style);
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
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._Point, null);
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
		public Point defaultValue;
		
		@Override
		public Object getDefault()
		{
			return defaultValue;
		}
	}
	
	public static class PointEditor extends DataEditor<Point>
	{
		JTextField xField;
		JTextField yField;
		
		public PointEditor(PropertiesSheetStyle style)
		{
			xField = style.createTextField();
			yField = style.createTextField();
			((PlainDocument) xField.getDocument()).setDocumentFilter(new IntegerFilter());
			((PlainDocument) yField.getDocument()).setDocumentFilter(new IntegerFilter());
			
			DocumentAdapter updatePoint = new DocumentAdapter(true)
			{
				@Override
				protected void update()
				{
					updated();
				}
			};
			
			xField.getDocument().addDocumentListener(updatePoint);
			yField.getDocument().addDocumentListener(updatePoint);
		}
		
		@Override
		public void set(Point t)
		{
			if(t == null)
				t = new Point(0, 0);
			xField.setText("" + t.x);
			yField.setText("" + t.y);
		}
		
		@Override
		public Point getValue()
		{
			return new Point(Integer.parseInt(xField.getText()), Integer.parseInt(yField.getText()));
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return comps(xField, yField);
		}
	}
}
