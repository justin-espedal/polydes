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
	public JComponent[] getEditor(final DataUpdater<RatioInt> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		final JTextField editor = style.createTextField();
		
		((PlainDocument) editor.getDocument()).setDocumentFilter(new RatioIntegerFilter());
		RatioInt i = updater.get();
		if (i == null)
			i = new RatioInt("0");

		editor.setText(i.get());
		
		editor.getDocument().addDocumentListener(new DocumentAdapter(true)
		{
			@Override
			protected void update()
			{
				updater.set(new RatioInt(editor.getText()));
			}
		});
		
		return comps(editor);
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
}
