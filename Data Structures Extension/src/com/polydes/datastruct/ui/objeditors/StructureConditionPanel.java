package com.polydes.datastruct.ui.objeditors;

import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.data.structure.elements.StructureCondition;

public class StructureConditionPanel extends StructureObjectPanel
{
	public StructureConditionPanel(final StructureCondition condition, PropertiesSheetStyle style)
	{
		super(style, condition);
		
		sheet.build()
		
			.field("text").label("Condition")
			._string().add().onUpdate(() -> {
				previewKey.setName(condition.getText());
				preview.lightRefreshDefaultLeaf(previewKey);
				preview.refreshVisibleComponents();
			})
			
			.finish();
	}
}