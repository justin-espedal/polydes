package stencyl.ext.polydes.datastruct.data.types.builtin;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.DocumentAdapter;
import stencyl.ext.polydes.datastruct.ui.utils.IntegerFilter;
import stencyl.ext.polydes.datastruct.utils.StringData;

public class DimensionType extends BuiltinType<Dimension>
{
	public DimensionType()
	{
		super(Dimension.class, "nme.geom.Point", "OBJECT", "Dimension");
	}

	@Override
	public JComponent[] getEditor(final DataUpdater<Dimension> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		final JTextField editor1 = style.createTextField();
		final JTextField editor2 = style.createTextField();
		((PlainDocument) editor1.getDocument()).setDocumentFilter(new IntegerFilter());
		((PlainDocument) editor2.getDocument()).setDocumentFilter(new IntegerFilter());
		Dimension d = updater.get();
		if(d == null)
			d = new Dimension(0, 0);
		
		editor1.setText("" + d.width);
		editor2.setText("" + d.height);
		
		DocumentAdapter updateDimension = new DocumentAdapter(true)
		{
			@Override
			protected void update()
			{
				updater.set(new Dimension(
						Integer.parseInt(editor1.getText()),
						Integer.parseInt(editor2.getText())));
			}
		};
		
		editor1.getDocument().addDocumentListener(updateDimension);
		editor2.getDocument().addDocumentListener(updateDimension);

		return comps(editor1, editor2);
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
	}
}
