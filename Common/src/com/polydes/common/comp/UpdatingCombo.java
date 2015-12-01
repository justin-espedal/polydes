package com.polydes.common.comp;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import com.polydes.common.collections.CollectionObserver;
import com.polydes.common.collections.CollectionUpdateListener;
import com.polydes.common.util.IconUtil;

/**
 * An UpdatingCombo automatically begins to observe the list it's passed.
 * When finished with an UpdatingCombo, it's important to call {@code dispose()} on it.
 */
public class UpdatingCombo<T> extends JComboBox<T>
{
	UpdatingModel<T> model;
	
	public UpdatingCombo(Collection<T> list, Predicate<T> filter)
	{
		super(new UpdatingModel<T>(list, filter));
		model = (UpdatingModel<T>) getModel();
		setBackground(null);
	}
	
	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension(super.getMaximumSize().width, super.getPreferredSize().height);
	}
	
	public T getSelected()
	{
		return model.getSelected();
	}
	
	public void dispose()
	{
		model.dispose();
		model = null;
	}
	
	public void setList(Collection<T> list)
	{
		model.setList(list);
	}
	
	public void setFilter(Predicate<T> filter)
	{
		model.setFilter(filter);
	}
	
	public void setComparator(Comparator<T> comparator)
	{
		model.setComparator(comparator);
	}
	
	@SuppressWarnings("unchecked")
	public void setIconProvider(Function<T, ImageIcon> provider)
	{
		setRenderer(new IconComboCellRenderer<T>(provider));
	}
}

class UpdatingModel<T> extends DefaultComboBoxModel<T> implements CollectionUpdateListener
{
	public static Map<Collection<?>, CollectionObserver> observers = new IdentityHashMap<Collection<?>, CollectionObserver>();
	
	private Collection<T> list;
	private Predicate<T> filter;
	private Comparator<T> comparator;
	
	private final ArrayList<T> objects;
	
	public UpdatingModel(Collection<T> list, Predicate<T> filter)
	{
		if(!observers.containsKey(list))
			observers.put(list, new CollectionObserver(list));
		observers.get(list).addListener(this);
		this.list = list;
		this.filter = filter;
		objects = new ArrayList<T>();
		listUpdated();
	}
	
	/**
	 * Completely refreshed with a new list. Previous filter is forgotten.
	 */
	public void setList(Collection<T> list)
	{
		dispose();
		
		if(!observers.containsKey(list))
			observers.put(list, new CollectionObserver(list));
		observers.get(list).addListener(this);
		
		this.list = list;
		listUpdated();
	}
	
	public void setFilter(Predicate<T> filter)
	{
		this.filter = filter;
		listUpdated();
	}
	
	public void setComparator(Comparator<T> comparator)
	{
		this.comparator = comparator;
		listUpdated();
	}
	
	@Override
	public void listUpdated()
	{
		Collection<T> filteredList;
		
		if(filter == null)
			filteredList = list;
		else
		{
			ArrayList<T> filtered = new ArrayList<T>();
			
			for(T t : list)
				if(filter.test(t))
					filtered.add(t);
			
			filteredList = filtered;
		}
		
		objects.clear();
		objects.addAll(filteredList);
		if(comparator != null)
			objects.sort(comparator);
		
		fireContentsChanged(this, 0, objects.size());
	}
	
	@Override
	public T getElementAt(int index)
	{
		return objects.get(index);
	}
	
	@Override
	public int getIndexOf(Object anObject)
	{
		return objects.indexOf(anObject);
	}
	
	@Override
	public int getSize()
	{
		return objects.size();
	}
	
	@SuppressWarnings("unchecked")
	public T getSelected()
	{
		return (T) getSelectedItem();
	}
	
	public void dispose()
	{
		CollectionObserver observer = observers.get(list);
		observer.removeListener(this);
		if(observer.hasNoListeners())
		{
			observer.cancel();
			observers.remove(list);
		}
		
		list = null;
		filter = null;
		objects.clear();
	}
}

class IconComboCellRenderer<T> extends BasicComboBoxRenderer.UIResource
{
	private Function<T, ImageIcon> iconProvider;
	
	public IconComboCellRenderer(Function<T, ImageIcon> iconProvider)
	{
		this.iconProvider = iconProvider;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		setIcon(IconUtil.getIcon(iconProvider.apply((T) value), 14));

		if(index == -1)
		{
			setOpaque(false);
			setForeground(list.getForeground());
		}
		else
		{
			setOpaque(true);
		}

		return this;
	}
}