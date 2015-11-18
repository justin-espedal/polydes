package com.polydes.common.comp;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import com.polydes.common.data.types.DataEditor;

public class EnumEditor<T extends Enum<T>> extends DataEditor<T>
{
	final JComboBox<T> combo;
	final JComponent[] comps;
	
	public EnumEditor(Class<T> cls)
	{
		combo = new JComboBox<>(cls.getEnumConstants());
		combo.setBackground(null);
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
