package com.polydes.datastruct.ui.objeditors;

import com.polydes.datastruct.data.structure.cond.StructureCondition;
import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.UpdateListener;
import com.polydes.datastruct.data.types.builtin.StringType;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class StructureConditionPanel extends StructureObjectPanel
{
	private DataEditor<String> field;
	private String oldText;
	
	public StructureConditionPanel(final StructureCondition condition, PropertiesSheetStyle style)
	{
		super(style);
		
		oldText = condition.getText();
		field = new StringType.SingleLineStringEditor(style);
		field.setValue(condition.getText());
		field.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				condition.setText(field.getValue());
				previewKey.setName(field.getValue());
				preview.lightRefreshDataItem(previewKey);
				preview.refreshVisibleComponents();
			}
		});
//		addMouseListener(new MouseAdapter()
//		{
//			@Override
//			public void mousePressed(MouseEvent e)
//			{
//				if(StructureConditionPanel.this.condition == null)
//				{
//					JPopupMenu menu = buildNewSubconditionPopup();
//					menu.show(StructureConditionPanel.this, e.getX(), e.getY());
//				}
//				else
//				{
//					JPopupMenu menu = buildEditPopup();
//					menu.show(StructureConditionPanel.this, e.getX(), e.getY());
//				}
//			}
//		});
		
		addGenericRow("Condition", field);
	}
	
	public String getOldText()
	{
		return oldText;
	}
	
//	private JPopupMenu buildNewSubconditionPopup()
//	{
//		return PopupUtil.buildPopup(new String[] {"=",  "And", "Or", "Not"}, new ActionListener()
//		{
//			@Override
//			public void actionPerformed(ActionEvent e)
//			{
//				String text = e.getActionCommand();
//				
//				if(text.equals("="))
//					;//setModel(new IsCondition(null, ""));
//				else if(text.equals("And"))
//					;//setModel(new AndCondition(null, null));
//				else if(text.equals("Or"))
//					;//setModel(new OrCondition(null, null));
//				else if(text.equals("Not"))
//					;//setModel(new NotCondition(null));
//			}
//		});
//	}
//	
//	private JPopupMenu buildEditPopup()
//	{
//		return PopupUtil.buildPopup(new String[] {"Remove"}, new ActionListener()
//		{
//			@Override
//			public void actionPerformed(ActionEvent e)
//			{
//				;//setModel(null);
//			}
//		});
//	}
	
	public void dispose()
	{
		clearExpansion(0);
		field = null;
	}
}