package com.polydes.datastruct.ui.objeditors;

import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.data.structure.elements.StructureHeader;

public class StructureHeaderPanel extends StructureObjectPanel
{
	public StructureHeaderPanel(final StructureHeader header, PropertiesSheetStyle style)
	{
		super(style, header);
		
		sheet.build()
		
			.field("label")._string().add().onUpdate(() -> {
				previewKey.setName(header.getLabel());
				preview.lightRefreshDefaultLeaf(previewKey);
			})
			
			.finish();
	}
}
