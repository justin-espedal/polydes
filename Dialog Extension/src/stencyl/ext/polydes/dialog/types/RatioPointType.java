package stencyl.ext.polydes.dialog.types;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.DocumentAdapter;
import stencyl.ext.polydes.dialog.app.utils.RatioIntegerFilter;
import stencyl.ext.polydes.dialog.data.RatioPoint;
import stencyl.ext.polydes.dialog.io.Text;
import stencyl.ext.polydes.dialog.res.Resources;

public class RatioPointType extends DataType<RatioPoint>
{
	public RatioPointType()
	{
		super(RatioPoint.class, "scripts.ds.dialog.RatioPoint", "OBJECT", "RatioPoint");
	}

	@Override
	public RatioPoint decode(String s)
	{
		if(s.isEmpty())
			return new RatioPoint("0", "0");
		
		//Backwards compatibility
		if(!s.startsWith("["))
		{
			String[] sa = s.split(",");
			if(sa.length == 1)
				sa = new String[] {sa[0], "0"};
			return new RatioPoint(sa[0], sa[1]);
		}
		
		String[] sa = s.substring(1, s.length() -1).split(",");
		if(sa.length == 1)
			sa = new String[] {sa[0], "0"};
		return new RatioPoint(sa[0], sa[1]);
	}

	@Override
	public String encode(RatioPoint p)
	{
		return "[" + p.getX() + "," + p.getY() + "]";
	}

	@Override
	public List<String> generateHaxeClass()
	{
		return Text.readLines(Resources.getUrlStream("code/haxe/" + xml + ".hx"));
	}

	@Override
	public List<String> generateHaxeReader()
	{
		return Text.readLines(Resources.getUrlStream("code/haxer/" + xml + ".hx"));
	}

	@Override
	public JComponent[] getEditor(final DataUpdater<RatioPoint> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		final JTextField editor1 = style.createTextField();
		final JTextField editor2 = style.createTextField();
		
		((PlainDocument) editor1.getDocument()).setDocumentFilter(new RatioIntegerFilter());
		((PlainDocument) editor2.getDocument()).setDocumentFilter(new RatioIntegerFilter());
		RatioPoint pt = updater.get();
		if (pt == null)
			pt = new RatioPoint("0", "0");

		editor1.setText(pt.getX());
		editor2.setText(pt.getY());
		
		DocumentAdapter updatePoint = new DocumentAdapter(true)
		{
			@Override
			protected void update()
			{
				updater.set(new RatioPoint(editor1.getText(), editor2.getText()));
			}
		};
		
		editor1.getDocument().addDocumentListener(updatePoint);
		editor2.getDocument().addDocumentListener(updatePoint);
		
		return comps(editor1, editor2);
	}
	
	@Override
	public String toDisplayString(RatioPoint data)
	{
		return encode(data);
	}
	
	@Override
	public RatioPoint copy(RatioPoint t)
	{
		return new RatioPoint(t.getX(), t.getY());
	}
	
	@Override
	public ExtraProperties loadExtras(ExtrasMap arg0)
	{
		return null;
	}

	@Override
	public ExtrasMap saveExtras(ExtraProperties arg0)
	{
		return null;
	}
}
