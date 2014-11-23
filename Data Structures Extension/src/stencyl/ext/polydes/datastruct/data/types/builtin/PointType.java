package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import stencyl.ext.polydes.datastruct.Blocks;
import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.DocumentAdapter;
import stencyl.ext.polydes.datastruct.ui.utils.IntegerFilter;
import stencyl.ext.polydes.datastruct.utils.StringData;
import stencyl.sw.editors.snippet.designer.Block.BlockType;
import stencyl.sw.editors.snippet.designer.BlockTheme;
import stencyl.sw.editors.snippet.designer.Definition;
import stencyl.sw.editors.snippet.designer.Definition.Category;
import stencyl.sw.editors.snippet.designer.Definition.Type;
import stencyl.sw.editors.snippet.designer.Definitions;
import stencyl.sw.editors.snippet.designer.codemap.BasicCodeMap;
import stencyl.sw.editors.snippet.designer.codemap.CodeMap;
import stencyl.sw.editors.snippet.designer.dropdown.DropdownData;

public class PointType extends BuiltinType<Point>
{
	public PointType()
	{
		super(Point.class, "nme.geom.Point", "OBJECT", "Point");
	}

	@Override
	public JComponent[] getEditor(final DataUpdater<Point> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		final JTextField editor1 = style.createTextField();
		final JTextField editor2 = style.createTextField();
		((PlainDocument) editor1.getDocument()).setDocumentFilter(new IntegerFilter());
		((PlainDocument) editor2.getDocument()).setDocumentFilter(new IntegerFilter());
		Point pt = updater.get();
		if (pt == null)
			pt = new Point(0, 0);

		editor1.setText("" + pt.x);
		editor2.setText("" + pt.y);

		DocumentAdapter updatePoint = new DocumentAdapter(true)
		{
			@Override
			protected void update()
			{
				updater.set(new Point(Integer.parseInt(editor1.getText()), Integer.parseInt(editor2.getText())));
			}
		};
		
		editor1.getDocument().addDocumentListener(updatePoint);
		editor2.getDocument().addDocumentListener(updatePoint);
		
		return comps(editor1, editor2);
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
			new Type[] { Type.NUMBER, Type.NUMBER },
			new BasicCodeMap().setCode(CodeMap.HX, "new nme.geom.Point(~, ~)"),
			spec,
			BlockType.NORMAL,
			Type.OBJECT
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
			new Type[] { Type.OBJECT, Type.DROPDOWN, Type.NUMBER },
			new BasicCodeMap().setCode(CodeMap.HX, "~.~ = ~;"),
			spec,
			BlockType.ACTION,
			Type.VOID
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
			new Type[] { Type.OBJECT, Type.DROPDOWN },
			new BasicCodeMap().setCode(CodeMap.HX, "~.~"),
			spec,
			BlockType.NORMAL,
			Type.NUMBER
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
	}
}
