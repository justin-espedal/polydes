package com.polydes.dialog.types;

import java.io.File;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import stencyl.sw.util.Locations;

import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.ExtraProperties;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.utils.DocumentAdapter;
import com.polydes.dialog.app.utils.RatioIntegerFilter;
import com.polydes.dialog.data.RatioInt;
import com.polydes.dialog.io.Text;

public class RatioIntType extends DataType<RatioInt>
{
	public RatioIntType()
	{
		super(RatioInt.class, "scripts.ds.dialog.RatioInt", "OBJECT", "RatioInt");
	}

	@Override
	public RatioInt decode(String s)
	{
		return new RatioInt(s);
	}

	@Override
	public String encode(RatioInt i)
	{
		return i.get();
	}

	@Override
	public List<String> generateHaxeClass()
	{
		return null;
	}

	@Override
	public List<String> generateHaxeReader()
	{
		File f = new File(Locations.getGameExtensionLocation("com.polydes.dialog"), "types/" + xml + ".hx");
		return Text.readLines(f);
	}

	@Override
	public DataEditor<RatioInt> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
	{
		return new RatioIntEditor(style);
	}
	
	@Override
	public String toDisplayString(RatioInt data)
	{
		return encode(data);
	}
	
	@Override
	public RatioInt copy(RatioInt t)
	{
		return new RatioInt(t.get());
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
	
	public static class RatioIntEditor extends DataEditor<RatioInt>
	{
		JTextField editor;
		
		public RatioIntEditor(PropertiesSheetStyle style)
		{
			editor = style.createTextField();
			
			((PlainDocument) editor.getDocument()).setDocumentFilter(new RatioIntegerFilter());
			
			editor.getDocument().addDocumentListener(new DocumentAdapter(true)
			{
				@Override
				protected void update()
				{
					updated();
				}
			});
		}
		
		@Override
		public void set(RatioInt t)
		{
			if(t == null)
				t = new RatioInt("0");
			editor.setText(t.get());			
		}
		
		@Override
		public RatioInt getValue()
		{
			return new RatioInt(editor.getText());
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return comps(editor);
		}
	}
}
