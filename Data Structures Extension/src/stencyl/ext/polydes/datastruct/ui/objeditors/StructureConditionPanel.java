package stencyl.ext.polydes.datastruct.ui.objeditors;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import stencyl.ext.polydes.datastruct.data.structure.cond.StructureCondition;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;
import stencyl.ext.polydes.datastruct.ui.utils.DocumentAdapter;
import stencyl.ext.polydes.datastruct.ui.utils.PopupUtil;

public class StructureConditionPanel extends JPanel
{
	private JTextField field;
	
	public StructureConditionPanel(final StructureCondition condition)
	{
		super(new BorderLayout());
		
		field = PropertiesSheetStyle.LIGHT.createTextField();
		field.setText(condition.getText());
		field.getDocument().addDocumentListener(new DocumentAdapter(false)
		{
			@Override
			protected void update()
			{
				condition.setText(field.getText());
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
		
		add(field);
	}
	
	private JPopupMenu buildNewSubconditionPopup()
	{
		return PopupUtil.buildPopup(new String[] {"=",  "And", "Or", "Not"}, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String text = e.getActionCommand();
				
				if(text.equals("="))
					;//setModel(new IsCondition(null, ""));
				else if(text.equals("And"))
					;//setModel(new AndCondition(null, null));
				else if(text.equals("Or"))
					;//setModel(new OrCondition(null, null));
				else if(text.equals("Not"))
					;//setModel(new NotCondition(null));
			}
		});
	}
	
	private JPopupMenu buildEditPopup()
	{
		return PopupUtil.buildPopup(new String[] {"Remove"}, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				;//setModel(null);
			}
		});
	}
}