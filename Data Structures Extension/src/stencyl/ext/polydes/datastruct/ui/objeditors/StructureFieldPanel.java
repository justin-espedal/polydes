package stencyl.ext.polydes.datastruct.ui.objeditors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import stencyl.ext.polydes.datastruct.data.structure.StructureField;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.ext.polydes.datastruct.ui.comp.UpdatingCombo;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.DocumentAdapter;
import stencyl.ext.polydes.datastruct.ui.utils.VarNameFilter;

public class StructureFieldPanel extends StructureObjectPanel
{
	StructureField field;
	
	String oldName;
	DataType<?> oldType;
	String oldLabel;
	String oldHint;
	
	JTextField nameField;
	UpdatingCombo<DataType<?>> typeField;
	JTextField labelField;
	JTextArea hintField;
	
	public StructureFieldPanel(final StructureField field, PropertiesSheetStyle style)
	{
		super(style);
		
		this.field = field;
		
		oldName = field.getVarname();
		oldType = field.getType();
		oldLabel = field.getLabel();
		oldHint = field.getHint();
		
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
			}
		});
		
		//=== Type
		
		typeField = new UpdatingCombo<DataType<?>>(Types.typeFromXML.values(), null);
		
		typeField.setSelectedItem(field.getType());
		
		typeField.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				field.setType(typeField.getSelected());
			}
		});
		
		//=== Label
		
		labelField = style.createTextField();
		labelField.setText(field.getLabel());
	
		labelField.getDocument().addDocumentListener(new DocumentAdapter(false)
		{
			@Override
			protected void update()
			{
				field.setLabel(labelField.getText());
			}
		});
		
		//=== Hint
		
		hintField = new JTextArea();
		hintField.setBackground(style.fieldBg);
		hintField.setForeground(style.fieldtextColor);
		hintField.setCaretColor(style.fieldtextColor);
		hintField.setLineWrap(true);
		hintField.setWrapStyleWord(true);
		if(style.fieldBorder != null)
			hintField.setBorder
			(
				BorderFactory.createCompoundBorder
				(
					BorderFactory.createLineBorder(style.fieldBorder, 1),
					BorderFactory.createEmptyBorder(0, 3, 0, 3)
				)
			);
		
		hintField.setText(field.getHint());
		
		hintField.getDocument().addDocumentListener(new DocumentAdapter(false)
		{
			@Override
			protected void update()
			{
				field.setHint(hintField.getText());
			}
		});
		
		addGenericRow("Name", nameField);
		addGenericRow("Type", typeField);
		addGenericRow("Label", labelField);
		addGenericRow("Hint", hintField);
		finishBlock(5, style.rowgap, true);
	}
	
	public void revert()
	{
		field.setVarname(oldName);
		field.setType(oldType);
		field.setLabel(oldLabel);
		field.setHint(oldHint);
	}
	
	public void dispose()
	{
		removeAll();
		oldName = null;
		oldType = null;
		oldLabel = null;
		oldHint = null;
		nameField = null;
		typeField = null;
		labelField = null;
		hintField = null;
	}
}
