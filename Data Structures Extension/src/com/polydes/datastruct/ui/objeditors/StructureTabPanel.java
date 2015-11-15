package com.polydes.datastruct.ui.objeditors;

import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.data.structure.elements.StructureTab;

public class StructureTabPanel extends StructureObjectPanel
{
	public StructureTabPanel(final StructureTab tab, PropertiesSheetStyle style)
	{
		super(style, tab);
		
		sheet.build()
		
			.field("label")._string().add().onUpdate(() -> {
				previewKey.setName(tab.getLabel());
				preview.lightRefreshDataItem(previewKey);
			})
			
			.finish();
	}
}
