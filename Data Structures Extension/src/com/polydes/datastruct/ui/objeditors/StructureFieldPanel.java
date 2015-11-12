package com.polydes.datastruct.ui.objeditors;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.ExtraProperties;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.data.types.builtin.basic.BoolType.BooleanEditor;
import com.polydes.common.data.types.builtin.basic.StringType;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.data.types.HaxeDataType;
import com.polydes.datastruct.data.types.HaxeDataTypeType;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.utils.DocumentAdapter;
import com.polydes.datastruct.ui.utils.VarNameFilter;

public class StructureFieldPanel extends StructureObjectPanel
{
	StructureField field;
	
	String oldName;
	HaxeDataType oldType;
	String oldLabel;
	String oldHint;
	boolean oldOptional;
	boolean oldDirty;
	
	JTextField nameField;
	DataEditor<HaxeDataType> typeEditor;
	DataEditor<String> labelEditor;
	DataEditor<String> hintEditor;
	DataEditor<Boolean> optionalEditor;
	
	int extraPropertiesExpansion;
	
	private boolean varnameFollowsLabel;
	private boolean varnameUpdate;
	
	public StructureFieldPanel(final StructureField field, PropertiesSheetStyle style)
	{
		super(style);
		
		this.field = field;
		
		oldName = field.getVarname();
		oldType = field.getType();
		oldLabel = field.getLabel();
		oldHint = field.getHint();
		oldDirty = field.isDirty();
		oldOptional = field.isOptional();
		
		varnameFollowsLabel = StructureField.formatVarname(oldLabel).equals(oldName);
		
		//=== Name
		
		nameField = style.createTextField();
		((PlainDocument) nameField.getDocument()).setDocumentFilter(new VarNameFilter());
		nameField.setText(field.getVarname());
		
		nameField.getDocument().addDocumentListener(new DocumentAdapter(false)
		{
			@Override
			protected void update()
			{
				field.setVarname(nameField.getText());
				previewKey.setDirty(true); //Not using a DataEditor so this is done explicitly.
				if(varnameFollowsLabel && !varnameUpdate)
					varnameFollowsLabel = false;
			}
		});
		
		JLabel nameHint = style.createEditorHint
		(
			"Variable Name Format:<br/>" + 
			"A letter or underscore, followed by any<br/>" + 
			"number of letters, numbers, or underscores."
		);
		
		//=== Type
		
		typeEditor = new HaxeDataTypeType.HaxeDataTypeEditor();
		typeEditor.setValue(field.getType());
		typeEditor.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				HaxeDataType type = typeEditor.getValue();
				
				field.setTypeForPreview(type);
				clearExpansion(extraPropertiesExpansion);
				type.applyToFieldPanel(StructureFieldPanel.this);
				preview.refreshDataItem(previewKey);
				
				layoutContainer();
				revalidate();
				setSize(getPreferredSize());
			}
		});
		
		//=== Label
		
		labelEditor = new StringType.SingleLineStringEditor(null, style);
		labelEditor.setValue(field.getLabel());
		labelEditor.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				field.setLabel(labelEditor.getValue());
				previewKey.setName(labelEditor.getValue());
				if(varnameFollowsLabel)
				{
					varnameUpdate = true;
					nameField.setText(StructureField.formatVarname(labelEditor.getValue()));
					varnameUpdate = false;
				}
				
				preview.lightRefreshDataItem(previewKey);
			}
		});
		
		//=== Hint
		
		hintEditor = new StringType.ExpandingStringEditor(null, style);
		hintEditor.setValue(field.getHint());
		hintEditor.addListener(new UpdateListener()
		{
			private String oldHintValue = field.getHint();
			
			@Override
			public void updated()
			{
				field.setHint(hintEditor.getValue());
				if(oldHintValue.isEmpty() || hintEditor.getValue().isEmpty())
					preview.refreshDataItem(previewKey);
				else
					preview.lightRefreshDataItem(previewKey);
				oldHintValue = hintEditor.getValue();
			}
		});
		
		//=== Optional
		
		optionalEditor = new BooleanEditor();
		optionalEditor.setValue(field.isOptional());
		optionalEditor.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				field.setOptional(optionalEditor.getValue());
				preview.refreshDataItem(previewKey);
			}
		});
		
		addGenericRow("Label", labelEditor);
		addGenericRow("Type", typeEditor);
		addGenericRow("Name", nameField, nameHint);
		addGenericRow("Hint", hintEditor, RESIZE_FLAG);
		addGenericRow("Optional", optionalEditor);
		
		extraPropertiesExpansion = newExpander();
	}
	
	// === Methods for DataType extra property appliers.
	
	@Override
	public void setPreviewSheet(PropertiesSheet sheet, DataItem key)
	{
		super.setPreviewSheet(sheet, key);
		
		clearExpansion(extraPropertiesExpansion);
		typeEditor.getValue().applyToFieldPanel(StructureFieldPanel.this);
	}
	
	public int getExtraPropertiesExpansion()
	{
		return extraPropertiesExpansion;
	}
	
	public StructureField getField()
	{
		return field;
	}
	
	public ExtraProperties getExtras()
	{
		return field.getExtras();
	}
	
	public PropertiesSheet getPreview()
	{
		return preview;
	}
	
	public DataItem getPreviewKey()
	{
		return previewKey;
	}
	
	// ===
	
	public void revert()
	{
		field.setVarname(oldName);
		field.setType(oldType);
		field.setLabel(oldLabel);
		field.setHint(oldHint);
		field.setDirty(oldDirty);
		field.setOptional(oldOptional);
	}
	
	public void dispose()
	{
		clearExpansion(extraPropertiesExpansion);
		clearExpansion(0);
		oldName = null;
		oldType = null;
		oldLabel = null;
		oldHint = null;
		nameField = null;
		typeEditor = null;
		labelEditor = null;
		hintEditor = null;
		optionalEditor = null;
	}
}
