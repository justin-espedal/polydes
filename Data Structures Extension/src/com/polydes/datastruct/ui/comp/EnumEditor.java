package com.polydes.datastruct.ui.comp;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import com.polydes.datastruct.data.types.DataEditor;

public class EnumEditor<T extends Enum<T>> extends DataEditor<T>
{
	JComboBox<T> combo;
	JComponent[] comps;
	
	public EnumEditor(Class<T> cls)
	{
		combo = new JComboBox<>(cls.getEnumConstants());
		comps = new JComponent[] {combo};
		
		combo.addActionListener((e) -> updated());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T getValue()
	{
		return (T) combo.getSelectedItem();
	}

	@Override
	protected void set(T t)
	{
		combo.setSelectedItem(t);
	}

	@Override
	public JComponent[] getComponents()
	{
		return comps;
	}
}
