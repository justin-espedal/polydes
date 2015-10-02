package com.polydes.dialog.types;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.ExtraProperties;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.utils.DocumentAdapter;
import com.polydes.dialog.app.utils.RatioIntegerFilter;
import com.polydes.dialog.data.RatioPoint;
import com.polydes.dialog.io.Text;
import com.polydes.dialog.res.Resources;

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
	public DataEditor<RatioPoint> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new RatioPointEditor(style);
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
	
	public static class RatioPointEditor extends DataEditor<RatioPoint>
	{
		private JTextField xField;
		private JTextField yField;
		
		public RatioPointEditor(PropertiesSheetStyle style)
		{
			xField = style.createTextField();
			yField = style.createTextField();
			
			((PlainDocument) xField.getDocument()).setDocumentFilter(new RatioIntegerFilter());
			((PlainDocument) yField.getDocument()).setDocumentFilter(new RatioIntegerFilter());
			
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
		public void set(RatioPoint t)
		{
			if(t == null)
				t = new RatioPoint("0", "0");
			xField.setText(t.getX());
			yField.setText(t.getY());
		}
		
		@Override
		public RatioPoint getValue()
		{
			return new RatioPoint(xField.getText(), yField.getText());
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return comps(xField, yField);
		}
	}
}
