package stencyl.ext.polydes.datastruct.ui.objeditors;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import stencyl.ext.polydes.datastruct.data.folder.DataItem;
import stencyl.ext.polydes.datastruct.data.structure.StructureField;
import stencyl.ext.polydes.datastruct.data.types.DataEditor;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.data.types.UpdateListener;
import stencyl.ext.polydes.datastruct.data.types.builtin.BooleanType.BooleanEditor;
import stencyl.ext.polydes.datastruct.data.types.builtin.StringType;
import stencyl.ext.polydes.datastruct.data.types.hidden.DataTypeType;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheet;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.DocumentAdapter;
import stencyl.ext.polydes.datastruct.ui.utils.VarNameFilter;
import stencyl.ext.polydes.datastruct.utils.Lang;

public class StructureFieldPanel extends StructureObjectPanel
{
	private static final DataTypeType.Extras excludeHiddenTypes;
	
	static
	{
		excludeHiddenTypes = new DataTypeType.Extras();
		excludeHiddenTypes.excludedTypes = Lang.hashset(Types._DataType);
	}
	
	StructureField field;
	
	String oldName;
	DataType<?> oldType;
	String oldLabel;
	String oldHint;
	boolean oldOptional;
	boolean oldDirty;
	
	JTextField nameField;
	@SuppressWarnings("rawtypes")
	DataEditor<DataType> typeEditor;
	DataEditor<String> labelEditor;
	DataEditor<String> hintEditor;
	DataEditor<Boolean> optionalEditor;
	
	int extraPropertiesExpansion;
	
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
			}
		});
		
		JLabel nameHint = style.createEditorHint
		(
			"Variable Name Format:<br/>" + 
			"A letter or underscore, followed by any<br/>" + 
			"number of letters, numbers, or underscores."
		);
		
		//=== Type
		
		typeEditor = new DataTypeType.DataTypeEditor(excludeHiddenTypes);
		typeEditor.setValue(field.getType());
		typeEditor.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				DataType<?> type = typeEditor.getValue();
				
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
		
		labelEditor = new StringType.SingleLineStringEditor(style);
		labelEditor.setValue(field.getLabel());
		labelEditor.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				field.setLabel(labelEditor.getValue());
				previewKey.setName(labelEditor.getValue());
				preview.lightRefreshDataItem(previewKey);
			}
		});
		
		//=== Hint
		
		hintEditor = new StringType.ExpandingStringEditor(style);
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
		
		addGenericRow("Name", nameField, nameHint);
		addGenericRow("Type", typeEditor);
		addGenericRow("Label", labelEditor);
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
