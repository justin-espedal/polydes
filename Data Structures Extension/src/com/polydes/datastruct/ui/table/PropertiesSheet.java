package com.polydes.datastruct.ui.table;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.nodes.DefaultBranch;
import com.polydes.common.nodes.DefaultLeaf;
import com.polydes.common.nodes.HierarchyModel;
import com.polydes.common.nodes.HierarchyRepresentation;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
import com.polydes.datastruct.data.structure.SDE;
import com.polydes.datastruct.data.structure.SDEType;
import com.polydes.datastruct.data.structure.SDETypes;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.elements.StructureField;

public class PropertiesSheet extends JPanel implements HierarchyRepresentation<DefaultLeaf,DefaultBranch>
{
	public Card getFirstCardParent(DefaultLeaf n)
	{
		while(true)
		{
			GuiObject o = guiMap.get(n);
			
			if(o instanceof Card)
				return (Card) o;
			if(o instanceof RowGroup && ((RowGroup) o).hasSubcard())
				return ((RowGroup) o).getSubcard();
			
			n = n.getParent();
			if(n == null)
				break;
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> allDescendentsOfType(Class<T> cls, ArrayList<T> list, DefaultBranch n)
	{
		if(list == null)
			list = new ArrayList<T>();
		for(DefaultLeaf n2 : n.getItems())
		{
			if(n2 instanceof DefaultBranch)
				allDescendentsOfType(cls, list, (DefaultBranch) n2);
			if(cls.isAssignableFrom(n2.getUserData().getClass()))
				list.add((T) n2.getUserData());
		}
		return list;
	}
	
	public Structure model;
	
	public Table root;
	
	public PropertiesSheetStyle style;
	
	public HashMap<DefaultLeaf, GuiObject> guiMap;
	public HashMap<StructureField, DataEditor<?>> fieldEditorMap;
	public ArrayList<Card> conditionalCards = new ArrayList<Card>();
	public JScrollPane scroller;
	
	/**
	 * folderModel is null if this isn't the preview of a structure definition editor
	 */
	public PropertiesSheet(Structure model, HierarchyModel<DefaultLeaf,DefaultBranch> folderModel)
	{
		this(model, folderModel, PropertiesSheetStyle.DARK);
	}
	
	public boolean isChangingLayout;
	
	/**
	 * folderModel is null if this isn't the preview of a structure definition editor
	 */
	public PropertiesSheet(Structure model, HierarchyModel<DefaultLeaf,DefaultBranch> folderModel, PropertiesSheetStyle style)
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
		guiMap = new HashMap<DefaultLeaf, GuiObject>();
		fieldEditorMap = new HashMap<StructureField, DataEditor<?>>();
		add(root);
		
		DefaultBranch rootFolder = model.getTemplate().guiRoot;
		guiMap.put(rootFolder, root);
		
		isChangingLayout = true;
		buildSheetFromBranch(rootFolder);
		isChangingLayout = false;
		
		if(folderModel != null)
			folderModel.addRepresentation(this);
		
		for(DefaultLeaf n : guiMap.keySet())
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
		removeLeaf(model.getTemplate().guiRoot);
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
	
	public void highlightElement(DefaultLeaf n)
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
	 | Hierarchy Representation
	\*================================================*/
	
	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		
	}
	
	@Override
	public void itemAdded(DefaultBranch branch, DefaultLeaf item, int position)
	{
		addLeaf(branch, item, position);
	}

	@Override
	public void itemRemoved(DefaultBranch branch, DefaultLeaf item, int oldPosition)
	{
		removeLeaf(item);
	}
	
	public void buildSheetFromBranch(DefaultBranch branch)
	{
		for(int i = 0; i < branch.getItems().size(); ++i)
		{
			DefaultLeaf d = branch.getItemAt(i);
			addLeaf(branch, d, i);
			if(d instanceof DefaultBranch)
				buildSheetFromBranch((DefaultBranch) d); 
		}
	}
	
	@SuppressWarnings("unchecked")
	public <S extends SDE> void addLeaf(DefaultBranch parent, DefaultLeaf n, int i)
	{
		S data = (S) n.getUserData();
		
		SDEType<S> type = (SDEType<S>) SDETypes.fromClass(data.getClass());
		GuiObject newObj = type.psAdd(this, parent, n, data, i);
		guiMap.put(n, newObj);
		
		if(!isChangingLayout)
			revalidate();
	}
	
	@SuppressWarnings("unchecked")
	public <S extends SDE> void removeLeaf(DefaultLeaf n)
	{
		if(!guiMap.containsKey(n))
			return;
		
		S data = (S) n.getUserData();
		
		SDEType<S> type = (SDEType<S>) SDETypes.fromClass(data.getClass());
		type.psRemove(this, guiMap.remove(n), n, data);
		
		revalidate();
	}
	
	@SuppressWarnings("unchecked")
	public <S extends SDE> void refreshLeaf(DefaultLeaf n)
	{
		if(!guiMap.containsKey(n))
			return;
		
		S data = (S) n.getUserData();
		
		SDEType<S> type = (SDEType<S>) SDETypes.fromClass(data.getClass());
		type.psRefresh(this, guiMap.get(n), n, data);
		
		highlightElement(n);
		
		revalidate();
	}
	
	@SuppressWarnings("unchecked")
	public <S extends SDE> void lightRefreshLeaf(DefaultLeaf n)
	{
		if(!guiMap.containsKey(n))
			return;
		
		S data = (S) n.getUserData();
		
		SDEType<S> type = (SDEType<S>) SDETypes.fromClass(data.getClass());
		type.psLightRefresh(this, guiMap.get(n), n, data);
		
		highlightElement(n);
		
		repaint();
	}
}
