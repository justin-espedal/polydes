package stencyl.ext.polydes.datastruct.ui.comp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.apache.commons.lang3.ArrayUtils;

import stencyl.ext.polydes.datastruct.data.core.CollectionObserver;
import stencyl.ext.polydes.datastruct.data.core.CollectionPredicate;
import stencyl.ext.polydes.datastruct.data.core.CollectionUpdateListener;

public class UpdatingCombo<T> extends JComboBox
{
	UpdatingModel<T> model;
	
	public UpdatingCombo(Collection<T> list, CollectionPredicate<T> filter)
	{
		super(new UpdatingModel<T>(list, filter));
	}
	
	public T getSelected()
	{
		return model.getSelected();
	}
}

class UpdatingModel<T> extends DefaultComboBoxModel implements CollectionUpdateListener
{
	public static HashMap<Collection<?>, CollectionObserver> observers = new HashMap<Collection<?>, CollectionObserver>();
	
	private Collection<T> list;
	private CollectionPredicate<T> filter;
	private Object[] objects;
	
	public UpdatingModel(Collection<T> list, CollectionPredicate<T> filter)
	{
		if(!observers.containsKey(list))
			observers.put(list, new CollectionObserver(list));
		observers.get(list).addListener(this);
		this.list = list;
		this.filter = filter;
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
		
		objects = new Object[filteredList.size()];
		int i = 0;
		for(Object o : filteredList)
			objects[i++] = o;
		
		fireContentsChanged(this, 0, objects.length);
	}
	
	@Override
	public Object getElementAt(int index)
	{
		return objects[index];
	}
	
	@Override
	public int getIndexOf(Object anObject)
	{
		return ArrayUtils.indexOf(objects, anObject);
	}
	
	@Override
	public int getSize()
	{
		return objects.length;
	}
	
	@SuppressWarnings("unchecked")
	public T getSelected()
	{
		Object o = getSelectedItem();
		return o == null ? null : (T) o;
	}
}