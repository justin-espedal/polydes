package stencyl.ext.polydes.datastruct.ui.objeditors;

import javax.swing.JLabel;

import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.sw.util.comp.RoundedLabel;
import stencyl.sw.util.dg.DialogPanel;

public class StructureObjectPanel extends DialogPanel
{
	protected PropertiesSheetStyle style;
	
	public StructureObjectPanel(PropertiesSheetStyle style)
	{
		super(style.pageBg);
		this.style = style;
	}
	
	@Override
	public JLabel createLabel(String s)
	{
		return style.createLabel(s);
	}
	
	@Override
	public RoundedLabel addHeader(String name)
	{
		return style.createRoundedLabel(name);
	}
}
