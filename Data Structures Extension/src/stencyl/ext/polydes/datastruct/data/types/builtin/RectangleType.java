package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.awt.Rectangle;
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

public class RectangleType extends BuiltinType<Rectangle>
{
	public RectangleType()
	{
		super(Rectangle.class, "nme.geom.Rectangle", "OBJECT", "Rectangle");
	}

	@Override
	public JComponent[] getEditor(final DataUpdater<Rectangle> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		final JTextField x = style.createTextField();
		final JTextField y = style.createTextField();
		final JTextField w = style.createTextField();
		final JTextField h = style.createTextField();
		((PlainDocument) x.getDocument()).setDocumentFilter(new IntegerFilter());
		((PlainDocument) y.getDocument()).setDocumentFilter(new IntegerFilter());
		((PlainDocument) w.getDocument()).setDocumentFilter(new IntegerFilter());
		((PlainDocument) h.getDocument()).setDocumentFilter(new IntegerFilter());
		
		Rectangle r = updater.get();
		if(r == null)
			r = new Rectangle(0, 0, 0, 0);
		
		x.setText("" + r.x);
		y.setText("" + r.y);
		w.setText("" + r.width);
		h.setText("" + r.height);

		DocumentAdapter updateRectangle = new DocumentAdapter(true)
		{
			@Override
			protected void update()
			{
				updater.set(new Rectangle(
						Integer.parseInt(x.getText()),
						Integer.parseInt(y.getText()),
						Integer.parseInt(w.getText()),
						Integer.parseInt(h.getText())));
			}
		};
		
		x.getDocument().addDocumentListener(updateRectangle);
		y.getDocument().addDocumentListener(updateRectangle);
		w.getDocument().addDocumentListener(updateRectangle);
		h.getDocument().addDocumentListener(updateRectangle);

		return comps(x, y, w, h);
	}
	
	@Override
	public Rectangle decode(String s)
	{
		int[] ints = StringData.getInts(s);
		if(ints == null)
			return new Rectangle(0, 0, 0, 0);
		
		return new Rectangle(ints[0], ints[1], ints[2], ints[3]);
	}

	@Override
	public String encode(Rectangle r)
	{
		return "[" + r.x + ", " + r.y + ", " + r.width + ", " + r.height + "]";
	}
	
	@Override
	public String toDisplayString(Rectangle data)
	{
		return encode(data);
	}
	
	@Override
	public ArrayList<Definition> getBlocks()
	{
		ArrayList<Definition> blocks = new ArrayList<Definition>();
		
		//---
		
		String spec = "rectangle x: %0 y: %1 w: %2 h: %3";
		
		Definition blockDef = new Definition
		(
			Category.CUSTOM,
			"ds-rectangle-new",
			new Type[] { Type.NUMBER, Type.NUMBER, Type.NUMBER, Type.NUMBER },
			new BasicCodeMap().setCode(CodeMap.HX, "new nme.geom.Rectangle(~, ~, ~, ~)"),
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
			"ds-rectangle-set",
			new Type[] { Type.OBJECT, Type.DROPDOWN, Type.NUMBER },
			new BasicCodeMap().setCode(CodeMap.HX, "~.~ = ~;"),
			spec,
			BlockType.ACTION,
			Type.VOID
		);
		
		blockDef.guiTemplate = spec;
		blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");
		blockDef.dropdowns = new DropdownData[] {Definitions.blank, Blocks.createGenericDropdown(new String[] {"x",  "y", "width", "height"}, new String[] {"x",  "y", "width", "height"}), Definitions.blank};
		
		blocks.add(blockDef);
		
		//---
		
		spec = "get %1 of %0";
		
		blockDef = new Definition
		(
			Category.CUSTOM,
			"ds-rectangle-get",
			new Type[] { Type.OBJECT, Type.DROPDOWN },
			new BasicCodeMap().setCode(CodeMap.HX, "~.~"),
			spec,
			BlockType.NORMAL,
			Type.NUMBER
		);
		
		blockDef.guiTemplate = spec;
		blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");
		blockDef.dropdowns = new DropdownData[] {Definitions.blank, Blocks.createGenericDropdown(new String[] {"x",  "y", "width", "height"}, new String[] {"x",  "y", "width", "height"}), Definitions.blank};
		
		blocks.add(blockDef);
		
		//---
		
		return blocks;
	}
	
	@Override
	public Rectangle copy(Rectangle t)
	{
		return new Rectangle(t);
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap extras)
	{
		Extras e = new Extras();
		e.defaultValue = extras.get(DEFAULT_VALUE, Types._Rectangle, null);
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
		public Rectangle defaultValue;
	}
}
