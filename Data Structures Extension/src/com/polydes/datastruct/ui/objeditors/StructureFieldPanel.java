package com.polydes.datastruct.ui.objeditors;

import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.HaxeDataTypeType;
import com.polydes.datastruct.ui.table.PropertiesSheet;

public class StructureFieldPanel extends StructureObjectPanel
{
	StructureField field;
	
	boolean oldDirty;
	
	PropertiesSheetSupport editorSheet;
	
	public StructureFieldPanel(final StructureField field, PropertiesSheetStyle style)
	{
		super(style, field);
		
		this.field = field;
		oldDirty = field.isDirty();

		String nameHint = 
			"Variable Name Format:<br/>" + 
			"A letter or underscore, followed by any<br/>" + 
			"number of letters, numbers, or underscores.";
		
		sheet.build()
			
			.field("label")._string().add()
			
			.field("type")._editor(new HaxeDataTypeType()).add()
			
			.field("varname").label("Name").hint(nameHint)._string().regex("([a-zA-Z_][a-z0-9A-Z_]*)?").add()
			
			.field("hint")._string().expandingEditor().add()
			
			.field("optional")._boolean().add().onUpdate(() -> preview.refreshDataItem(previewKey))
			
			.field("defaultValue").label("Default")._editor(field.getType().dataType).add()
			
			.finish();
		
		sheet.addPropertyChangeListener("label", event -> {
			previewKey.setName(field.getLabel());
			
			String oldLabel = (String) event.getOldValue();
			if(StructureField.formatVarname(oldLabel).equals(field.getVarname()))
				sheet.updateField("varname", StructureField.formatVarname(field.getLabel()));
			
			preview.lightRefreshDataItem(previewKey);
		});
		
		sheet.addPropertyChangeListener("type", event -> {
			HaxeDataType type = field.getType();
			
			field.setDefaultValue(type.dataType.decode(""));
			sheet.change().field("defaultValue")._editor(type.dataType).change().finish();
			
			field.setTypeForPreview(type);
			clearSheetExtension("extras");
			type.applyToFieldPanel(StructureFieldPanel.this);
			preview.refreshDataItem(previewKey);
			
			layoutContainer();
			revalidate();
			setSize(getPreferredSize());
		});
			
		sheet.addPropertyChangeListener("hint", event -> {
			String oldV = (String) event.getOldValue();
			String newV = (String) event.getNewValue();
			if(oldV.isEmpty() || newV.isEmpty())
				preview.refreshDataItem(previewKey);
			else
				preview.lightRefreshDataItem(previewKey);
		});
		
		//TODO ?
//		addGenericRow("Hint", hintEditor, RESIZE_FLAG);
		
		editorSheet = createSheetExtension(field.getEditorProperties(), "editor");
		editorSheet.addPropertyChangeListener(event -> preview.refreshDataItem(previewKey));
	}
	
	// === Methods for DataType extra property appliers.
	
	@Override
	public void setPreviewSheet(PropertiesSheet sheet, DataItem key)
	{
		super.setPreviewSheet(sheet, key);
		
		clearSheetExtension("editor");
		editorSheet = createSheetExtension(field.getEditorProperties(), "editor");
		editorSheet.addPropertyChangeListener(event -> preview.refreshDataItem(previewKey));
		field.getType().applyToFieldPanel(StructureFieldPanel.this);
	}
	
	public StructureField getField()
	{
		return field;
	}
	
	public EditorProperties getExtras()
	{
		return field.getEditorProperties();
	}
	
	public PropertiesSheet getPreview()
	{
		return preview;
	}
	
	public DataItem getPreviewKey()
	{
		return previewKey;
	}
	
	public PropertiesSheetSupport getSheet()
	{
		return sheet;
	}
	
	public PropertiesSheetSupport getEditorSheet()
	{
		return editorSheet;
	}
	
	// ===
	
	public void revert()
	{
		super.revertChanges();
		field.setDirty(oldDirty);
	}
}
