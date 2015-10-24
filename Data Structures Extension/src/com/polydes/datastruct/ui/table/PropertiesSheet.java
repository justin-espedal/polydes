package com.polydes.datastruct.ui.table;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.HierarchyRepresentation;
import com.polydes.common.nodes.Leaf;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.data.types.DataEditor;

public class PropertiesSheet extends JPanel implements HierarchyRepresentation<DataItem>
{
	public Card getFirstCardParent(DataItem n)
	{
		while(true)
		{
			GuiObject o = guiMap.get(n);
			
			if(o instanceof Card)
				return (Card) o;
			if(o instanceof RowGroup && ((RowGroup) o).hasSubcard())
				return ((RowGroup) o).getSubcard();
			
			n = (DataItem) n.getParent();
			if(n == null)
				break;
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> allDescendentsOfType(Class<T> cls, ArrayList<T> list, Folder n)
	{
		if(list == null)
			list = new ArrayList<T>();
		for(Leaf<DataItem> n2 : n.getItems())
		{
			if(n2 instanceof Folder)
				allDescendentsOfType(cls, list, (Folder) n2);
			if(cls.isAssignableFrom(((DataItem) n2).getObject().getClass()))
				list.add((T) ((DataItem) n2).getObject());
		}
		return list;
	}
	
	public Structure model;
	
	public Table root;
	
	public PropertiesSheetStyle style;
	
	public HashMap<DataItem, GuiObject> guiMap;
	public HashMap<StructureField, DataEditor<?>> fieldEditorMap;
	public ArrayList<Card> conditionalCards = new ArrayList<Card>();
	public JScrollPane scroller;
	
	/**
	 * folderModel is null if this isn't the preview of a structure definition editor
	 */
	public PropertiesSheet(Structure model, HierarchyModel<DataItem> folderModel)
	{
		this(model, folderModel, PropertiesSheetStyle.DARK);
	}
	
	public boolean isChangingLayout;
	
	/**
	 * folderModel is null if this isn't the preview of a structure definition editor
	 */
	public PropertiesSheet(Structure model, HierarchyModel<DataItem> folderModel, PropertiesSheetStyle style)
	{
		root = new Table(style);
		this.style = style;
		
		setBorder(style.border);
		setFocusable(true);
		setBackground(style.pageBg);
		
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				requestFocus();
			}
		});
		
		this.model = model;
		guiMap = new HashMap<DataItem, GuiObject>();
		fieldEditorMap = new HashMap<StructureField, DataEditor<?>>();
		add(root);
		
		Folder rootFolder = model.getTemplate().guiRoot;
		guiMap.put(rootFolder, root);
		
		isChangingLayout = true;
		buildSheetFromFolder(rootFolder);
		isChangingLayout = false;
		
		if(folderModel != null)
			folderModel.addRepresentation(this);
		
		for(DataItem n : guiMap.keySet())
			if(guiMap.get(n) instanceof Card)
				((Card) guiMap.get(n)).layoutContainer();
		
		refreshVisibleComponents();
		model.setDirty(false);
		revalidate();
		repaint();
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		Dimension d = root.getPreferredSize();
		Insets i = getInsets();
		return new Dimension(d.width + i.left + i.right, d.height + i.top + i.bottom);
	}
	
	@Override
	public Dimension getMaximumSize()
	{
		return getPreferredSize();
	}
	
	public void refreshVisibleComponents()
	{
		for(Card card : conditionalCards)
			card.check();
	}
	
	public void dispose()
	{
		removeDataItem(model.getTemplate().guiRoot);
		guiMap.clear();
		conditionalCards.clear();
		root.removeAll();
		for(DataEditor<?> editor : fieldEditorMap.values())
			editor.dispose();
		fieldEditorMap.clear();
		
		removeAll();
		highlighter = null;
		style = null;
		tweener = null;
	}
	
	/*================================================*\
	 | Highlighting
	\*================================================*/
	
	private Highlighter highlighter = new Highlighter();
	private Timer tweener;
	
	public void highlightElement(DataItem n)
	{
		if(n == null)
			return;
		
		GuiObject go = guiMap.get(n);
		
		if(go == null)
			return;
		
		highlighter.setTargets();
		go.makeShown();
		highlightGuiObject(go);
		tween();
	}
	
	private void highlightGuiObject(GuiObject go)
	{
		if(go instanceof RowGroup)
		{
			for(Row row : ((RowGroup) go).rows)
				highlighter.addTargets(row.components);
		}
		else if(go instanceof Deck)
		{
			highlighter.addTargets(((Deck) go).buttons);
		}
		else if(go instanceof Card)
		{
			Card card = (Card) go;
			if(card == root)
				return;
			
			if(card.isTab)
				highlighter.addTargets(card.button);
			else
				highlighter.addTargets(card);
		}
	}
	
	private void tween()
	{
		tweener = new Timer();
		tweener.schedule(new HighlightTweenTask(tweener), 10, 10);
	}
	
	class HighlightTweenTask extends TimerTask
	{
		Timer tweener;
		int i = 0;
		
		public HighlightTweenTask(Timer tweener)
		{
			this.tweener = tweener;
		}
		
		@Override
		public void run()
		{
			++i;
			if(i > 10 || highlighter == null)
				tweener.cancel();
			
			if(highlighter == null || !highlighter.ready)
				return;
			
			Rectangle r = new Rectangle(highlighter.r);
			r.grow(20, 20);
			scrollRectToVisible(r);
			tweener.cancel();
		}
	}
	
	@Override
	protected void paintChildren(Graphics g)
	{
		super.paintChildren(g);
		SwingUtilities.paintComponent(g, highlighter, this, 0, 0, getWidth(), getHeight());
	}

	/*================================================*\
	 | Folder Hierarchy Representation
	\*================================================*/
	
	@Override
	public void leafStateChanged(Leaf<DataItem> source)
	{
		
	}
	
	@Override
	public void leafNameChanged(Leaf<DataItem> source, String oldName)
	{
		
	}
	
	@Override
	public void itemAdded(Branch<DataItem> folder, Leaf<DataItem> item,	int position)
	{
		addDataItem((Folder) folder, (DataItem) item, position);
	}

	@Override
	public void itemRemoved(Branch<DataItem> folder, Leaf<DataItem> item, int oldPosition)
	{
		removeDataItem((DataItem) item);
	}
	
	public void buildSheetFromFolder(Folder folder)
	{
		for(int i = 0; i < folder.getItems().size(); ++i)
		{
			DataItem d = (DataItem) folder.getItemAt(i);
			addDataItem(folder, d, i);
			if(d instanceof Folder)
				buildSheetFromFolder((Folder) d); 
		}
	}
	
	@SuppressWarnings("unchecked")
	public <S extends SDE> void addDataItem(Folder parent, DataItem n, int i)
	{
		S data = (S) n.getObject();
		
		SDEType<S> type = (SDEType<S>) SDETypes.fromClass(data.getClass());
		GuiObject newObj = type.psAdd(this, parent, n, data, i);
		guiMap.put(n, newObj);
		
		if(!isChangingLayout)
			revalidate();
	}
	
	@SuppressWarnings("unchecked")
	public <S extends SDE> void removeDataItem(DataItem n)
	{
		if(!guiMap.containsKey(n))
			return;
		
		S data = (S) n.getObject();
		
		SDEType<S> type = (SDEType<S>) SDETypes.fromClass(data.getClass());
		type.psRemove(this, guiMap.remove(n), n, data);
		
		revalidate();
	}
	
	@SuppressWarnings("unchecked")
	public <S extends SDE> void refreshDataItem(DataItem n)
	{
		if(!guiMap.containsKey(n))
			return;
		
		S data = (S) n.getObject();
		
		SDEType<S> type = (SDEType<S>) SDETypes.fromClass(data.getClass());
		type.psRefresh(this, guiMap.get(n), n, data);
		
		highlightElement(n);
		
		revalidate();
	}
	
	@SuppressWarnings("unchecked")
	public <S extends SDE> void lightRefreshDataItem(DataItem n)
	{
		if(!guiMap.containsKey(n))
			return;
		
		S data = (S) n.getObject();
		
		SDEType<S> type = (SDEType<S>) SDETypes.fromClass(data.getClass());
		type.psLightRefresh(this, guiMap.get(n), n, data);
		
		highlightElement(n);
		
		repaint();
	}
}
