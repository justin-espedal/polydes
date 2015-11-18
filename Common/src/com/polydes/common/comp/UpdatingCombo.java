package com.polydes.common.comp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.polydes.common.collections.CollectionObserver;
import com.polydes.common.collections.CollectionUpdateListener;

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