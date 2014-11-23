package stencyl.ext.polydes.datastruct.data.types.hidden;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.DocumentAdapter;
import stencyl.ext.polydes.datastruct.ui.utils.VarNameFilter;

public class VarNameType extends HiddenType<String>
{
	public VarNameType()
	{
		super(String.class, "VarName");
	}
	
	@Override
	public JComponent[] getEditor(final DataUpdater<String> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		final JTextField editor = style.createTextField();
		((PlainDocument) editor.getDocument()).setDocumentFilter(new VarNameFilter());
		editor.setText(updater.get());
		
		editor.getDocument().addDocumentListener(new DocumentAdapter(false)
		{
			@Override
			protected void update()
			{
				updater.set(editor.getText());
			}
		});
		
		return comps(editor, style.createEditorHint("Variable Name Format:<br/>A letter or underscore, followed by any<br/>number of letters, numbers, or underscores."));
	}
	
	@Override
	public String decode(String s)
	{
		return s;
	}
	
	@Override
	public String encode(String s)
	{
		return s;
	}
	
	@Override
	public String toDisplayString(String data)
	{
		return data;
	}
	
	@Override
	public String copy(String t)
	{
		return t;
	}
}