package com.polydes.datastruct.ui.table;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
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
import com.polydes.datastruct.data.structure.StructureDefinitionElement;
import com.polydes.datastruct.data.structure.StructureDefinitionElementType;
import com.polydes.datastruct.data.structure.elements.StructureCondition;
import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.data.structure.elements.StructureTab;
import com.polydes.datastruct.data.structure.elements.StructureTabset;
import com.polydes.datastruct.data.types.DataEditor;
import com.polydes.datastruct.data.types.DataType;
import com.polydes.datastruct.data.types.UpdateListener;
import com.polydes.datastruct.data.types.builtin.extra.ColorType;
import com.polydes.datastruct.data.types.builtin.extra.ColorType.ColorEditor;
import com.polydes.datastruct.data.types.general.StructureType;
import com.polydes.datastruct.ui.page.StructureDefinitionsWindow;
import com.polydes.datastruct.ui.utils.Layout;

public class PropertiesSheet extends JPanel implements HierarchyRepresentation<DataItem>
{
	public void addDataItem(Folder parent, DataItem n, int i)
	{
		Card parentCard = getFirstCardParent(parent);
		
		if(isRowGroupDataItem(n))
		{
			RowGroup group = loadRows(null, n);
			guiMap.put(n, group);
			
			parentCard.addGroup(i, group);
			
			if(n.getObject() instanceof StructureTabset)
			{
				Deck deck = getDeck(group);
				deck.setCard(parentCard);
			}
			else if(n.getObject() instanceof StructureCondition)
			{
				Card card = getConditionalCard(group);
				card.setCard(parentCard);
				card.setCondition((StructureCondition) n.getObject());
				conditionalCards.add(card);
			}
			
			if(!isChangingLayout)
				parentCard.layoutContainer();
		}
		else if(n.getObject() instanceof StructureTab)
		{
			StructureTab tab = (StructureTab) n.getObject();
			
			Deck deckParent = getFirstDeckParent(parent);
			
			Card card = new Card(tab.getLabel(), true);
			deckParent.addCard(card, i);
			
			guiMap.put(n, card);
		}
		
		if(!isChangingLayout)
			revalidate();
	}
	
	private boolean isRowGroupDataItem(DataItem n)
	{
		Object o = n.getObject();
		return !(o instanceof StructureTab);
	}
	
	private Card getFirstCardParent(DataItem n)
	{
		while(!((n.getObject() instanceof StructureTab) || (n.getObject() instanceof StructureCondition)))
			n = (DataItem) n.getParent();
		
		if(n.getObject() instanceof StructureTab)
			return (Card) guiMap.get(n);
		else
			return getConditionalCard((RowGroup) guiMap.get(n));
	}
	
	private Deck getFirstDeckParent(DataItem n)
	{
		while(!(n.getObject() instanceof StructureTabset))
			n = (DataItem) n.getParent();
		
		return getDeck((RowGroup) guiMap.get(n));
	}
	
	private Deck getDeck(RowGroup group)
	{
		JPanel wrapper = (JPanel) group.rows[3].components[0];
		return (Deck) wrapper.getComponent(0);
	}
	
	public Card getConditionalCard(RowGroup group)
	{
		return (Card) group.rows[0].components[0];
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
	
	public void removeDataItem(DataItem n)
	{
		if(isRowGroupDataItem(n))
		{
			RowGroup group = (RowGroup) guiMap.get(n);
			Card card = group.card;
			
			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);
			
			if(n.getObject() instanceof StructureTabset)
			{
				Deck deck = getDeck(group);
				deck.setCard(null);
			}
			else if(n.getObject() instanceof StructureCondition)
			{
				Card subcard = getConditionalCard((RowGroup) guiMap.get(n));
				subcard.setCard(null);
				conditionalCards.remove(subcard);
			}
			else if(n.getObject() instanceof StructureField)
			{
				fieldEditorMap.remove((StructureField) n.getObject()).dispose();
			}
			
			card.layoutContainer();
		}
		else if(n.getObject() instanceof StructureTab)
		{
			Card card = (Card) guiMap.get(n);
			if(card.deck != null)
				card.deck.removeCard(card);
		}
		
		guiMap.remove(n);
		
		revalidate();
	}
	
	public void refreshDataItem(DataItem n)
	{
		if(!guiMap.containsKey(n))
			return;
		
		if(n.getObject() instanceof StructureField)
		{
			RowGroup group = (RowGroup) guiMap.get(n);
			Card card = group.card;
			
			int groupIndex = card.indexOf(group);
			card.removeGroup(groupIndex);
			
			loadRows(group, n);
			
			card.addGroup(groupIndex, group);
			card.layoutContainer();
		}
		
		highlightElement(n);
		
		revalidate();
	}
	
	@SuppressWarnings("unchecked")
	public <S extends StructureDefinitionElement> RowGroup loadRows(RowGroup group, DataItem n)
	{
		S data = (S) n.getObject();
		
		if(group == null)
			group = new RowGroup(data);
		
		StructureDefinitionElementType<S> type = (StructureDefinitionElementType<S>) SDETypes.fromClass(data.getClass());
		type.psLoad(this, group, n, data);
		
		return group;
	}
	
	@SuppressWarnings("unchecked")
	public <S extends StructureDefinitionElement> void lightRefreshDataItem(DataItem n)
	{
		S data = (S) n.getObject();
		
		StructureDefinitionElementType<S> type = (StructureDefinitionElementType<S>) SDETypes.fromClass(data.getClass());
		type.psLightRefresh(this, guiMap.get(n), n, data);
		
		highlightElement(n);
		
		repaint();
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
		{
			if(n.getObject() instanceof StructureTab)
				((Card) guiMap.get(n)).layoutContainer();
		}
		
		refreshVisibleComponents();
		model.setDirty(false);
		revalidate();
		repaint();
	}
	/*
	public void setStructure(Structure model)
	{
		guiMap.clear();
		conditionalCards.clear();
		root.removeAll();
		for(DataEditor<?> editor : fieldEditorMap.values())
			editor.dispose();
		fieldEditorMap.clear();
		
		if(this.model != null)
			removeDataItem(this.model.getTemplate().guiRoot);
		
		this.model = model;
		
		if(model != null)
			guiMap.put(model.getTemplate().guiRoot, root);
		
		isChangingLayout = true;
	}
	*/
	
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JComponent createEditor(final StructureField f)
	{
		JComponent editPanel = null;
		
		DataType type = f.getType();
		
		if(fieldEditorMap.containsKey(f))
			fieldEditorMap.get(f).dispose();
		
		final DataEditor deditor;
		
		//special case for "Structure" editors, because they may need to know which Structure they're in for filtering.
		if(type instanceof StructureType)
			deditor = ((StructureType) type).new StructureEditor((StructureType.Extras) f.getExtras(), model);
		else
			deditor = type.createEditor(f.getExtras(), style);
		
		//special case for Color editors inside preview structures. Need to make sure the popup window works.
		if(type instanceof ColorType && model.getID() == -1)
			((ColorEditor) deditor).setOwner(StructureDefinitionsWindow.get());
		
		deditor.setValue(model.getProperty(f));
		deditor.addListener(new UpdateListener()
		{
			@Override
			public void updated()
			{
				model.setProperty(f, deditor.getValue());
				refreshVisibleComponents();
			}
		});
		
		fieldEditorMap.put(f, deditor);
		
		editPanel = Layout.horizontalBox(style.fieldDimension, deditor.getComponents());
		
		if(f.isOptional())
			return constrict(createEnabler(editPanel, f), editPanel);
		else
			return editPanel;
	}
	
	private JCheckBox createEnabler(final JComponent component, final StructureField f)
	{
		final JCheckBox enabler = new JCheckBox();
		enabler.setSelected(model.isPropertyEnabled(f));
		enabler.setBackground(null);
		
		enabler.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(model.isPropertyEnabled(f) != enabler.isSelected())
				{
					component.setVisible(enabler.isSelected());
					model.setPropertyEnabled(f, enabler.isSelected());
					if(!enabler.isSelected())
						model.clearProperty(f);
					model.setDirty(true);
				}
			}
		});
		
		component.setVisible(model.isPropertyEnabled(f));
		
		return enabler;
	}
	
	public JPanel constrict(JComponent... comps)
	{
		return Layout.horizontalBox(style.fieldDimension, comps);
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
}
