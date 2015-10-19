package com.polydes.datastruct.ui.comp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import stencyl.sw.actions.Actions;
import stencyl.sw.actions.SAction;
import stencyl.sw.lnf.Theme;
import stencyl.sw.loc.LanguagePack;
import stencyl.sw.util.Fonts;
import stencyl.sw.util.UI;
import stencyl.sw.util.comp.GroupButton;

import com.jidesoft.swing.JidePopupMenu;
import com.jidesoft.swing.PaintPanel;
import com.polydes.common.util.Lang;
import com.polydes.datastruct.data.core.DataList;
import com.polydes.datastruct.data.core.Dynamic;
import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.ExtrasMap;
import com.polydes.datastruct.data.types.Types;
import com.polydes.datastruct.data.types.builtin.basic.DynamicType;
import com.polydes.datastruct.data.types.builtin.basic.DynamicType.DynamicEditor;
import com.polydes.datastruct.ui.MiniDialog;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.utils.Layout;

public class DataListEditor extends JPanel implements ActionListener, MouseListener, ListSelectionListener
{
	private static final LanguagePack lang = LanguagePack.get();

	private final DefaultListModel<Object> listModel;
	private final JList<Object> list;
	private DataList model;
	private final ArrayList<ActionListener> extraListeners;
	private final DataListEditorRenderer renderer;

	private final SAction newAction;
	private final SAction deleteAction;
	private final SAction upAction;
	private final SAction downAction;

	public DataListEditor(DataList model)
	{
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

		listModel = new DefaultListModel<Object>();
		renderer = new DataListEditorRenderer();
		setList(model);
		
		extraListeners = new ArrayList<ActionListener>();

		newAction = Actions.getAction("list.add").getCopy();
		deleteAction = Actions.getAction("list.remove").getCopy();
		upAction = Actions.getAction("list.up").getCopy();
		downAction = Actions.getAction("list.down").getCopy();

		newAction.setListener(this);
		deleteAction.setListener(this);
		upAction.setListener(this);
		downAction.setListener(this);

		// ---

		setLayout(new BorderLayout());

		list = new JList<Object>(listModel);
		list.setBackground(Theme.LIGHT_BG_COLOR);
		list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		list.setForeground(Theme.TEXT_COLOR);
		list.setFont(Fonts.getNormalFont());
		list.addListSelectionListener(this);
		list.addMouseListener(this);
		list.setCellRenderer(renderer);
		
		JScrollPane scrollPane = UI.createScrollPane(list);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getViewport().setBackground(Theme.EDITOR_BG_COLOR);
		scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Theme.BORDER_COLOR));

		PaintPanel tableHeader = new PaintPanel();
		tableHeader.setVertical(true);
		tableHeader.setStartColor(Theme.BUTTON_BAR_START);
		tableHeader.setEndColor(Theme.BUTTON_BAR_END);

		tableHeader.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, Theme.BORDER_COLOR));

		tableHeader.setPreferredSize(new Dimension(1, 20));
		tableHeader.setLayout(new BoxLayout(tableHeader, BoxLayout.X_AXIS));

		tableHeader.add(Box.createHorizontalStrut(5));

		JLabel l = new JLabel(lang.get("list.index"));
		tableHeader.add(l);

		tableHeader.add(Box.createHorizontalStrut(35));

		l = new JLabel(lang.get("globals.value"));
		tableHeader.add(l);

		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setOpaque(false);
		wrapper.add(scrollPane, BorderLayout.CENTER);
		wrapper.add(tableHeader, BorderLayout.NORTH);

		JPanel bar = createBar();
		add(wrapper, BorderLayout.CENTER);
		add(bar, BorderLayout.WEST);

		refreshActions();
	}

	public void setList(DataList model)
	{
		if(this.model != model)
		{
			this.model = model;
			
			listModel.removeAllElements();
			
			for (Object o : model)
				listModel.addElement(o);
			
			renderer.setModel(model);
		}
	}

	private static final class DataListEditorRenderer extends JPanel implements ListCellRenderer<Object>
	{
		DataList model;
		JLabel l;
		JLabel r;

		public DataListEditorRenderer()
		{
			super(new BorderLayout());
			
			setBackground(Theme.LIGHT_BG_COLOR);

			setOpaque(true);

			l = new JLabel();
			r = new JLabel();

			l.setPreferredSize(new Dimension(65, 1));

			l.setBackground(null);
			r.setBackground(null);
			l.setForeground(Theme.TEXT_COLOR);
			r.setForeground(Theme.TEXT_COLOR);

			add(l, BorderLayout.WEST);
			add(r, BorderLayout.CENTER);
		}
		
		public void setModel(DataList model)
		{
			this.model = model;
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends Object> list, Object value,	int index, boolean isSelected, boolean cellHasFocus)
		{
			if (isSelected)
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}

			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			l.setText("" + index);
			r.setText(model.genType.checkToDisplayString(value));

			return this;
		}
	}

	public DataList getModel()
	{
		return model;
	}

	public JList<Object> getList()
	{
		return list;
	}

	private JPanel createBar()
	{
		PaintPanel buttonPanel = new PaintPanel();
		buttonPanel.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(1, 1, 1, 1, Theme.BORDER_COLOR),
						BorderFactory.createEmptyBorder(5, 4, 5, 4)
					)
				);
		buttonPanel.setVertical(false);
		buttonPanel.setStartColor(Theme.BUTTON_BAR_START);
		buttonPanel.setEndColor(Theme.BUTTON_BAR_END);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

		buttonPanel.add(Box.createHorizontalGlue());

		Dimension d = new Dimension(23, 23);

		for (SAction action : new SAction[] { newAction, deleteAction, upAction, downAction })
		{
			final GroupButton act = new GroupButton(0);
			act.setAction(action);
			act.setText("");
			act.setTargetHeight(23);
			act.setMargin(new Insets(0, 0, 0, 0));
			act.setMinimumSize(d);
			act.setPreferredSize(d);
			act.setMaximumSize(d);

			buttonPanel.add(act);
			buttonPanel.add(Box.createVerticalStrut(5));
		}

		buttonPanel.add(Box.createHorizontalGlue());

		return buttonPanel;
	}

	public void refreshActions()
	{
		if (list.getModel().getSize() >= 1 && list.getSelectedIndex() != -1)
		{
			deleteAction.setEnabled(true);
		}

		else
		{
			deleteAction.setEnabled(false);
		}

		upAction.setEnabled(list.getSelectedIndex() > 0);
		downAction.setEnabled(list.getModel().getSize() > 0	&& list.getSelectedIndex() < list.getModel().getSize() - 1);
	}

	private static HashSet<DataType<?>> excludedTypes = Lang.hashset(Types._Array, Types._DataType, Types._Dynamic, Types._Selection, Types._Set);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals(lang.get("list.add")))
		{
			if (model.genType.haxeType.equals("Dynamic"))
			{
				DynamicEditor deditor = new DynamicType.DynamicEditor(PropertiesSheetStyle.DARK);
				deditor.excludeTypes(excludedTypes);
				deditor.setValue(new Dynamic("", "String"));
				MiniDialog dg = new MiniDialog(deditor.createMiniPage(), "Add Item", 400, 170);
				
				if (!dg.canceled)
				{
					Dynamic newElement = deditor.getValue();
					((DefaultListModel) list.getModel()).addElement(newElement);
					model.add(newElement);
				}
				
				deditor.dispose();
			}
			else
			{
				Object newItem = model.genType.decode("");
				
				DataEditor editor = model.genType.createEditor(new ExtrasMap(), PropertiesSheetStyle.DARK);
				editor.setValue(newItem);
				MiniDialog dg = new MiniDialog(Layout.horizontalBox(editor.getComponents()), "Add Item", 400, 100);
				
				if (!dg.canceled)
				{
					newItem = editor.getValue();
					((DefaultListModel) list.getModel()).addElement(newItem);
					model.add(newItem);
				}
				
				editor.dispose();
			}
		}

		else if (e.getActionCommand().equals(lang.get("list.remove")))
		{
			UI.Choice result = UI.showYesNoPrompt(lang.get("globals.confirmdelete",
					new String[] { getSelected() }), "", lang
					.get("globals.remove"), lang.get("globals.noremove"));

			if (result == UI.Choice.YES)
			{
				model.remove(list.getSelectedIndex());
				((DefaultListModel) list.getModel()).remove(list
						.getSelectedIndex());

				selectFirst();
			}
		}

		else if (e.getActionCommand().equals(lang.get("list.up")))
		{
			Object above = list.getModel().getElementAt(
					list.getSelectedIndex() - 1);
			Object curr = list.getModel().getElementAt(list.getSelectedIndex());

			model.set(list.getSelectedIndex(), above);
			model.set(list.getSelectedIndex() - 1, curr);

			((DefaultListModel) list.getModel()).setElementAt(above,
					list.getSelectedIndex());
			((DefaultListModel) list.getModel()).setElementAt(curr,
					list.getSelectedIndex() - 1);

			list.setSelectedIndex(list.getSelectedIndex() - 1);

			refreshActions();
		}

		else if (e.getActionCommand().equals(lang.get("list.down")))
		{
			Object below = list.getModel().getElementAt(
					list.getSelectedIndex() + 1);
			Object curr = list.getModel().getElementAt(list.getSelectedIndex());

			model.set(list.getSelectedIndex(), below);
			model.set(list.getSelectedIndex() + 1, curr);

			((DefaultListModel) list.getModel()).setElementAt(below,
					list.getSelectedIndex());
			((DefaultListModel) list.getModel()).setElementAt(curr,
					list.getSelectedIndex() + 1);

			list.setSelectedIndex(list.getSelectedIndex() + 1);

			refreshActions();
		}

		for (ActionListener l : extraListeners)
		{
			l.actionPerformed(e);
		}
	}

	public void addActionListener(ActionListener listener)
	{
		extraListeners.add(listener);
	}

	public String getSelected()
	{
		return list.getSelectedValue().toString();
	}

	public void selectFirst()
	{
		if (list.getModel().getSize() > 0)
		{
			list.setSelectedIndex(0);
		}
	}

	/*-------------------------------------*\
	 * Mouse
	\*-------------------------------------*/

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void mousePressed(MouseEvent e)
	{
		int selectedRow = list.getSelectedIndex();

		if (e.getClickCount() == 2 && selectedRow != -1)
		{
			if (model.genType.haxeType.equals("Dynamic"))
			{
				Dynamic o = Types._Dynamic.copy((Dynamic) model.get(selectedRow));
				DynamicEditor deditor = new DynamicType.DynamicEditor(PropertiesSheetStyle.DARK);
				deditor.excludeTypes(excludedTypes);
				
				deditor.setValue(o);
				MiniDialog dg = new MiniDialog(deditor.createMiniPage(), "Edit Item", 400, 170);
				
				if (!dg.canceled)
				{
					o = deditor.getValue();
					Dynamic original = (Dynamic) model.get(selectedRow);
					original.type = o.type;
					original.value = o.value;
				}
			}
			else
			{
				Object newItem = model.genType.decode("");
				
				DataEditor editor = model.genType.createEditor(new ExtrasMap(), PropertiesSheetStyle.DARK);
				editor.setValue(newItem);
				MiniDialog dg = new MiniDialog(Layout.horizontalBox(editor.getComponents()), "Edit Item", 400, 100);

				if (!dg.canceled)
				{
					int i = list.getSelectedIndex();
					Object o = editor.getValue();
					model.set(i, o);
					listModel.setElementAt(o, i);
				}
			}
		}

		else if (selectedRow != -1)
		{
			if (e.getButton() == MouseEvent.BUTTON3 || e.isPopupTrigger())
			{
				int locationX = e.getX();
				int locationY = e.getY();

				JPopupMenu popupMenu = new JidePopupMenu();

				JMenuItem add = UI.createMenuItem(newAction);
				JMenuItem delete = UI.createMenuItem(deleteAction);

				popupMenu.add(add);
				popupMenu.add(delete);

				list.add(popupMenu);

				popupMenu.show(list, locationX, locationY);
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		refreshActions();
	}

	/*-------------------------------------*\
	 * Unused
	\*-------------------------------------*/

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}
}
