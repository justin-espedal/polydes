package com.polydes.datastruct.ui.objeditors;

import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.data.structure.elements.StructureText;

public class StructureTextPanel extends StructureObjectPanel
{
	public StructureTextPanel(final StructureText text, PropertiesSheetStyle style)
	{
		super(style, text);
		
		sheet.build()
		
			.field("label")._string().add().onUpdate(() -> {
				previewKey.setName(text.getLabel());
				preview.lightRefreshLeaf(previewKey);
			})
			
			.field("text")._string().expandingEditor().add().onUpdate(() -> {
				preview.lightRefreshLeaf(previewKey);
			})
			
			.finish();
	}
}
