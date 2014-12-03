package stencyl.ext.polydes.datastruct.ui.comp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.apache.commons.lang3.ArrayUtils;

import stencyl.ext.polydes.datastruct.data.core.CollectionObserver;
import stencyl.ext.polydes.datastruct.data.core.CollectionPredicate;
import stencyl.ext.polydes.datastruct.data.core.CollectionUpdateListener;

/**
 * An UpdatingCombo automatically begins to observe the list it's passed.
 * When finished with an UpdatingCombo, it's important to call {@code dispose()} on it.
 */
public class UpdatingCombo<T> extends JComboBox
{
	UpdatingModel<T> model;
	
	@SuppressWarnings("unchecked")
	public UpdatingCombo(Collection<T> list, CollectionPredicate<T> filter)
	{
		super(new UpdatingModel<T>(list, filter));
		model = (UpdatingModel<T>) getModel();
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

	public void setFilter(CollectionPredicate<T> filter)
	{
		model.setFilter(filter);
	}
}

class UpdatingModel<T> extends DefaultComboBoxModel implements CollectionUpdateListener
{
	public static Map<Collection<?>, CollectionObserver> observers = new IdentityHashMap<Collection<?>, CollectionObserver>();
	
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
	
	public void setFilter(CollectionPredicate<T> filter)
	{
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
		objects = null;
	}
}