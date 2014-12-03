package stencyl.ext.polydes.dialog.types;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import stencyl.ext.polydes.datastruct.data.types.DataEditor;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.DocumentAdapter;
import stencyl.ext.polydes.dialog.app.utils.RatioIntegerFilter;
import stencyl.ext.polydes.dialog.data.RatioInt;
import stencyl.ext.polydes.dialog.io.Text;
import stencyl.ext.polydes.dialog.res.Resources;

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
		return Text.readLines(Resources.getUrlStream("code/haxe/" + xml + ".hx"));
	}

	@Override
	public List<String> generateHaxeReader()
	{
		return Text.readLines(Resources.getUrlStream("code/haxer/" + xml + ".hx"));
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
