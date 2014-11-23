package stencyl.ext.polydes.datastruct.data.types.hidden;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.DataUpdater;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.ui.comp.UpdatingCombo;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;

@SuppressWarnings("rawtypes")
public class StencylTypeType extends HiddenType<DataType>
{
	public StencylTypeType()
	{
		super(DataType.class, "StencylType");
	}

	@Override
	public JComponent[] getEditor(final DataUpdater<DataType> updater, ExtraProperties extras, PropertiesSheetStyle style)
	{
		final UpdatingCombo<DataType<?>> typeChooser = new UpdatingCombo<DataType<?>>(Types.stencylTypes, null);
		typeChooser.setSelectedItem(updater.get());
		
		typeChooser.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				updater.set(typeChooser.getSelected());
			}
		});
		
		return comps(typeChooser);
	}

	@Override
	public DataType decode(String s)
	{
		return Types.fromXML(s);
	}

	@Override
	public String toDisplayString(DataType data)
	{
		return data.xml;
	}

	@Override
	public String encode(DataType data)
	{
		return data.xml;
	}
	
	@Override
	public DataType copy(DataType t)
	{
		return t;
	}
}
