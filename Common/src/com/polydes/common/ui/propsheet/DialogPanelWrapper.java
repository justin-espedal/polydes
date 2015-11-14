package com.polydes.common.ui.propsheet;

import com.polydes.common.comp.utils.Layout;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport.FieldInfo;

import stencyl.sw.util.dg.DialogPanel;

public class DialogPanelWrapper implements PropertiesSheetWrapper
{
	private DialogPanel panel;
	
	public DialogPanelWrapper(DialogPanel panel)
	{
		this.panel = panel;
	}
	
	@Override
	public void addField(FieldInfo newField, DataEditor<?> editor)
	{
		panel.addGenericRow(newField.getLabel(), Layout.horizontalBox(editor.getComponents()));
		
		String hint = newField.getHint();
		if(hint != null && !hint.isEmpty())
			panel.addDescriptionRow(hint);
	}
	
	@Override
	public void addHeader(String title)
	{
		panel.addHeader(title);
	}
	
	@Override
	public void finish()
	{
		panel.finishBlock();
	}
	
	@Override
	public void dispose()
	{
		panel.removeAll();
		panel = null;
	}
}
