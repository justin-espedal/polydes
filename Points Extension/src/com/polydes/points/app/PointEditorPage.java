package com.polydes.points.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FileUtils;

import com.jidesoft.swing.PaintPanel;
import com.polydes.common.comp.StatusBar;
import com.polydes.common.comp.UpdatingCombo;
import com.polydes.points.NamedPoint;
import com.polydes.points.PointsExtension;
import com.polydes.points.comp.CyclingSpinnerListModel;
import com.polydes.points.res.Resources;

import stencyl.core.engine.actor.IActorType;
import stencyl.core.lib.Game;
import stencyl.core.lib.Resource;
import stencyl.sw.data.EditableAnimation;
import stencyl.sw.lnf.Theme;
import stencyl.sw.util.Fonts;
import stencyl.sw.util.UI;
import stencyl.sw.util.comp.GroupButton;
import stencyl.sw.util.comp.GroupToggleButton;

public class PointEditorPage extends JPanel
{
	private static PointEditorPage _instance;
	
	// Sidebar
	
	private JPanel sidebar;
	
	private JTextField actorTypeFilter;
	private UpdatingCombo<IActorType> actorTypeChooser;
	private UpdatingCombo<EditableAnimation> animationChooser;
	private JSpinner frameField;
	
	private Point currentOffset;
	private Image currentView;
	private NamedPoint selectedPoint = null;
	private ArrayList<NamedPoint> displayedPoints = new ArrayList<NamedPoint>();
	
	private JTable pointList;
	private DefaultTableModel pointListModel;
	
	// Main Area
	
	private static final int CREATE_MODE = 0;
	private static final int SELECT_MODE = 1;
	private static final int MOVE_MODE = 2;
	
	private int cursorMode = SELECT_MODE;
	private int scale = 1;
	
	private Toolbar toolbar;
	private AbstractAction zoomInAction;
	private AbstractAction zoomOutAction;
	
	private JScrollPane scroller;
	private JPanel page;
	
	public static PointEditorPage get()
	{
		if (_instance == null)
			_instance = new PointEditorPage();
		
		return _instance;
	}
	
	private static final List<EditableAnimation> noAnims = new ArrayList<EditableAnimation>();
	
	public PointEditorPage()
	{
		super(new BorderLayout());
		
		actorTypeFilter = new JTextField();
		actorTypeFilter.getDocument().addDocumentListener(new DocumentListener()
		{
			private void update()
			{
				String s = actorTypeFilter.getText();
				if(s.isEmpty())
					actorTypeChooser.setFilter(null);
				else
					actorTypeChooser.setFilter(matchPredicate(s));
			}
			
			@Override public void removeUpdate (DocumentEvent e){ update(); }
			@Override public void insertUpdate (DocumentEvent e){ update(); }
			@Override public void changedUpdate(DocumentEvent e){ update(); }
		});
		
		actorTypeChooser = new UpdatingCombo<IActorType>(Game.getGame().getResources().getResourcesByType(IActorType.class), null);
		actorTypeChooser.setBackground(null);
		actorTypeChooser.addActionListener(new ActionListener()
		{
			private IActorType current;
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(actorTypeChooser.getSelected() == null)
					animationChooser.setList(noAnims);
				else
					animationChooser.setList(actorTypeChooser.getSelected().getSprite().getAnimations());
				
				if(animationChooser.getModel().getSize() > 0)
					animationChooser.setSelectedIndex(0);
				
				if(current != null)
				{
					if(dirty)
						savePoints(current);
					dirty = false;
					clearPoints();
				}
				if(actorTypeChooser.getSelected() != null)
					loadPoints(current = actorTypeChooser.getSelected());
			}
		});
		
		animationChooser = new UpdatingCombo<EditableAnimation>(noAnims, null);
		animationChooser.setBackground(null);
		animationChooser.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(animationChooser.getSelected() != null)
				{
					int numFrames = ((EditableAnimation) animationChooser.getSelected()).getNumFrames();
					if(numFrames > 0)
					{
						frameField.setEnabled(true);
						((SpinnerNumberModel) frameField.getModel()).setMaximum(numFrames - 1);
						frameField.setValue(0);
					}
					else
					{
						frameField.setEnabled(false);
					}
				}
				updatePage();
			}
		});
		
		frameField = new JSpinner(new CyclingSpinnerListModel());
		((SpinnerNumberModel) frameField.getModel()).setMinimum(0);
		frameField.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				updatePage();
			}
		});
		
		pointList = new JTable(pointListModel = new DefaultTableModel());
		pointListModel.addColumn("Points");
		pointList.setBackground(UIConsts.SIDEBAR_COLOR);
		pointList.setDefaultRenderer(Object.class, new PointListRenderer());
		pointList.setDefaultEditor(Object.class, new PointListEditor());
		//pointList.setIntercellSpacing(new Dimension(0, 0));
		pointList.setShowGrid(false);
		
		//override enter only when not editing a cell.
		Object enterKey = pointList.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).get(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		final Action oldAction = pointList.getActionMap().get(enterKey);
		pointList.getActionMap().put(enterKey, new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(pointList.isEditing())
					oldAction.actionPerformed(e);
				else
				{
					int selRow = pointList.getSelectedRow();
					int selCol = pointList.getSelectedColumn();
					if(selRow != -1 && selCol != -1)
					{
						pointList.editCellAt(selRow, selCol);
						((PointListEditor) pointList.getCellEditor()).requestFocus();
					}
				}
			}
		});
		
		pointList.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "Delete");
		pointList.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "Backspace");
		pointList.getActionMap().put("Delete", null);
		pointList.getActionMap().put("Backspace", null);
	    
		pointList.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				int selRow = pointList.getSelectedRow();
				int selCol = pointList.getSelectedColumn();
				
				if(selRow != -1 && selCol != -1)
					selectedPoint = (NamedPoint) pointList.getValueAt(pointList.getSelectedRow(), pointList.getSelectedColumn());
				else
					selectedPoint = null;
				
				page.repaint();
			}
		});
		pointList.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				
			}
			
			@Override
			public void keyReleased(KeyEvent e)
			{
				
			}
			
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(!pointList.isEditing())
				{
					if(e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
						deleteSelected();
				}
			}
		});
		
		JPanel sidebarTop = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.insets = new Insets(0, 7, 10, 0);
		addToBar(sidebarTop, c, label("Filter"), actorTypeFilter);
		addToBar(sidebarTop, c, label("Actor Type"), actorTypeChooser);
		addToBar(sidebarTop, c, label("Animation"), animationChooser);
		addToBar(sidebarTop, c, label("Frame"), frameField);
		sidebarTop.setBackground(UIConsts.SIDEBAR_COLOR);
		sidebarTop.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 7));
		
		sidebar = new JPanel(new BorderLayout());
		sidebar.setBackground(UIConsts.SIDEBAR_COLOR);
		sidebar.add(ListUtils.addHeader(sidebarTop, "Display"), BorderLayout.NORTH);
		sidebar.add(ListUtils.addHeader(pointList, "Points"), BorderLayout.CENTER);
		
		toolbar = new Toolbar();
		toolbar.create.setAction(new AbstractAction("", Resources.loadIcon("create.png"))
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				cursorMode = CREATE_MODE;
				toolbar.modeLabel.setText("Mode: Create");
			}
		});
		toolbar.select.setAction(new AbstractAction("", Resources.loadIcon("select.png"))
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				cursorMode = SELECT_MODE;
				toolbar.modeLabel.setText("Mode: Select");
			}
		});
		toolbar.move.setAction(new AbstractAction("", Resources.loadIcon("move.png"))
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				cursorMode = MOVE_MODE;
				toolbar.modeLabel.setText("Mode: Move");
			}
		});
		
		zoomInAction = new AbstractAction("", Resources.loadIcon("zoom-in.png"))
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				zoomIn();
			}
		};
		zoomOutAction = new AbstractAction("", Resources.loadIcon("zoom-out.png"))
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				zoomOut();
			}
		};
		toolbar.zoomIn.setAction(zoomInAction);
		toolbar.zoomOut.setAction(zoomOutAction);
		
		page = new JPanel(null)
		{
			@Override
			protected void paintComponent(Graphics g)
			{
				g.setColor(UIConsts.TEXT_EDITOR_COLOR);
				g.fillRect(0, 0, getWidth(), getHeight());
				
				if(currentView != null)
				{
					g.drawImage(currentView,
							currentOffset.x,
							currentOffset.y,
							currentView.getWidth(null) * scale,
							currentView.getHeight(null) * scale,
							null);
					
					int so = scale / 2 - 4; // scale offset
					
					g.setColor(Color.GREEN);
					for(NamedPoint p : displayedPoints)
					{
						g.fillOval(currentOffset.x + p.x * scale + so, currentOffset.y + p.y * scale + so, 9, 9);
					}
					
					if(selectedPoint != null)
					{
						g.setColor(Color.ORANGE);
						g.fillOval(currentOffset.x + selectedPoint.x * scale + so, currentOffset.y + selectedPoint.y * scale + so, 9, 9);
					}
				}
			}
		};
		page.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				page.requestFocus();
			}
			
			@Override
			public void mousePressed(MouseEvent e)
			{
				if(currentView == null)
					return;
				
				Point p = getLogicalPoint(e.getPoint());
				
				switch(cursorMode)
				{
					case CREATE_MODE:
						
						selectedPoint = new NamedPoint(p);
						displayedPoints.add(selectedPoint);
						pointListModel.addRow(new NamedPoint[] { selectedPoint });
						page.repaint();
						dirty = true;
						
						refreshPageSize();
						
						break;
					
					case SELECT_MODE:
						
						double closest = Double.MAX_VALUE;
						selectedPoint = null;
						
						for(NamedPoint np : displayedPoints)
						{
							double dist = p.distance(np);
							if(dist < 10 && dist < closest)
							{
								selectedPoint = np;
								closest = dist;
							}
						}
						
//						for(int i = 0; i < pointListModel.getRowCount(); ++i)
//							if(pointListModel.getValueAt(i, 0).equals(selectedName))
//								pointList.getSelectionModel().setSelectionInterval(index0, index1);
						
						page.repaint();
						
						break;
						
					case MOVE_MODE:
						
						if(selectedPoint != null)
						{
							selectedPoint.setLocation(p.x, p.y);
							
							refreshPageSize();
							
							pointList.repaint();
							page.repaint();
							dirty = true;
						}
						
						break;
				}
			}
		});
		page.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				
			}
			
			@Override
			public void keyReleased(KeyEvent e)
			{
				
			}
			
			@Override
			public void keyPressed(KeyEvent e)
			{
				switch(e.getKeyCode())
				{
					case KeyEvent.VK_DELETE:
					case KeyEvent.VK_BACK_SPACE:
						deleteSelected();
						break;
					case KeyEvent.VK_C:
						toolbar.create.doClick();
						break;
					case KeyEvent.VK_S:
						toolbar.select.doClick();
						break;
					case KeyEvent.VK_M:
						toolbar.move.doClick();
						break;
				}
			}
		});
		
		scroller = UI.createScrollPane(page);
		
		page.addMouseWheelListener(new MouseWheelListener()
		{
			JScrollBar horizontalScrollBar = scroller.getHorizontalScrollBar();
			JScrollBar verticalScrollBar = scroller.getVerticalScrollBar();

			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				int scrollAmount = e.getScrollAmount();
				JScrollBar toScroll = null;
				if(!e.isControlDown())
					toScroll = e.isShiftDown() ? horizontalScrollBar : verticalScrollBar;
				
				if (e.getWheelRotation() == 1)
				{
					if(toScroll != null)
					{
						int newValue = toScroll.getValue() + toScroll.getBlockIncrement() * scrollAmount;
						if(newValue <= toScroll.getMaximum())
							toScroll.setValue(newValue);
					}
					else
						zoomOut(getLogicalPoint(e.getPoint()));
				}
				else if (e.getWheelRotation() == -1)
				{
					if(toScroll != null)
					{
						int newValue = toScroll.getValue() - toScroll.getBlockIncrement() * scrollAmount;
						if(newValue >= 0)
							toScroll.setValue(newValue);
					}
					else
						zoomIn(getLogicalPoint(e.getPoint()));
				}
			}
		});
		page.setBackground(UIConsts.TEXT_EDITOR_COLOR);
		
		scroller.setBackground(null);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		setBackground(UIConsts.TEXT_EDITOR_COLOR);
		add(toolbar, BorderLayout.NORTH);
		add(scroller, BorderLayout.CENTER);
		add(StatusBar.createStatusBar(), BorderLayout.SOUTH);
	}
	
	private void deleteSelected()
	{
		if(selectedPoint != null)
		{
			NamedPoint sp = selectedPoint;
			selectedPoint = null;
			
			for(int i = 0; i < pointListModel.getRowCount(); ++i)
			{
				if(pointListModel.getValueAt(i, 0) == sp)
				{
					displayedPoints.remove(pointListModel.getValueAt(i, 0));
					pointListModel.removeRow(i);
					
					if(pointListModel.getRowCount() > i)
						pointList.setRowSelectionInterval(i, i);
					else if(pointListModel.getRowCount() == i && i != 0)
						pointList.setRowSelectionInterval(i - 1, i - 1);
				}
			}
			page.repaint();
			dirty = true;
			refreshPageSize();
		}
	}
	
	private Point getLogicalPoint(Point p)
	{
		p = new Point(p);
		p.x -= currentOffset.x;
		p.y -= currentOffset.y;
		p.x /= scale;
		p.y /= scale;
		return p;
	}
	
	private Point getRealPoint(Point p)
	{
		p = new Point(p);
		p.x *= scale;
		p.y *= scale;
		p.x += currentOffset.x;
		p.y += currentOffset.y;
		return p;
	}
	
	private Rectangle getLogicalRect(Rectangle r)
	{
		r = new Rectangle(r);
		r.x -= currentOffset.x;
		r.y -= currentOffset.y;
		r.x /= scale;
		r.y /= scale;
		r.width /= scale;
		r.height /= scale;
		return r;
	}
	
	//Unused
//	private Rectangle getRealRect(Rectangle r)
//	{
//		r = new Rectangle(r);
//		r.width *= scale;
//		r.height *= scale;
//		r.x *= scale;
//		r.y *= scale;
//		r.x += currentOffset.x;
//		r.y += currentOffset.y;
//		return r;
//	}
	
	private void zoomIn()
	{
		Rectangle r = getLogicalRect(scroller.getViewport().getViewRect());
		zoomIn(new Point(r.x + r.width / 2, r.y + r.height / 2));
	}
	
	private void zoomIn(Point p)
	{
		if(scale < 16)
		{
			scale *= 2;
			toolbar.zoomLabel.setText(scale + "x");
			refreshPageSize();
			
			Dimension vd = scroller.getViewport().getExtentSize();
			p = getRealPoint(p);
			p.x -= vd.width / 2;
			p.y -= vd.height / 2;
			scroller.getViewport().setViewPosition(p);
		}
		zoomInAction.setEnabled(scale < 16);
		zoomOutAction.setEnabled(scale > 1);
	}
	
	private void zoomOut()
	{
		Rectangle r = getLogicalRect(scroller.getViewport().getViewRect());
		zoomOut(new Point(r.x + r.width / 2, r.y + r.height / 2));
	}
	
	private void zoomOut(Point p)
	{
		if(scale > 1)
		{
			scale /= 2;
			toolbar.zoomLabel.setText(scale + "x");
			refreshPageSize();
			
			Dimension vd = scroller.getViewport().getExtentSize();
			p = getRealPoint(p);
			p.x -= vd.width / 2;
			p.y -= vd.height / 2;
			scroller.getViewport().setViewPosition(p);
		}
		zoomInAction.setEnabled(scale < 16);
		zoomOutAction.setEnabled(scale > 1);
	}
	
	private void addToBar(JPanel bar, GridBagConstraints c, JLabel label, JComponent toAdd)
	{
		c.gridx = 0;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		bar.add(label, c);
		
		c.gridx = 1;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		bar.add(toAdd, c);
		
		c.gridy++;
	}

	public JComponent getSidebar()
	{
		return sidebar;
	}
	
	private JLabel label(String text)
	{
		JLabel label = new JLabel(text);
		label.setFont(Fonts.getBoldFont());
		label.setForeground(Color.WHITE);
		
		return label;
	}
	
	public void updatePage()
	{
		if
		(
			actorTypeChooser.getSelected() != null &&
			animationChooser.getSelected() != null &&
			frameField.getValue() != null
		)
		{
			try
			{
				currentView = animationChooser.getSelected().getImage((Integer) frameField.getValue());
			}
			catch(IndexOutOfBoundsException ex)
			{
				currentView = null;
			}
		}
		else
			currentView = null;
		
		page.repaint();
	}
	
	private static final int POINT_BOUNDS = 10;
	
	private void refreshPageSize()
	{
		Rectangle bounds = new Rectangle(0, 0, currentView.getWidth(null), currentView.getHeight(null));
		
		Dimension vd = scroller.getViewport().getExtentSize();
		vd.width /= scale;
		vd.height /= scale;
		Point margin = new Point(vd.width / 2 - bounds.width / 2, vd.height / 2 - bounds.height / 2);
		bounds.grow(Math.max(margin.x, 0), Math.max(margin.y, 0));
		
		for(NamedPoint p : displayedPoints)
			bounds = bounds.union(new Rectangle(p.x - POINT_BOUNDS, p.y - POINT_BOUNDS, POINT_BOUNDS * 2, POINT_BOUNDS * 2));
		
		bounds.x *= scale;
		bounds.y *= scale;
		bounds.width *= scale;
		bounds.height *= scale;
		
		currentOffset = new Point(-bounds.x, -bounds.y);
		
		Dimension d = new Dimension(bounds.width, bounds.height);
		page.setSize(d);
		page.setPreferredSize(d);
		page.repaint();
	}
	
	private boolean dirty = false;
	
	private void clearPoints()
	{
		while(pointListModel.getRowCount() > 0)
			pointListModel.removeRow(0);
		displayedPoints.clear();
	}
	
	private void loadPoints(Resource r)
	{
		File f = PointsExtension.get().getExtrasFolder();
		File rf = new File(f, r.getID() + ".txt");
		if(rf.exists())
		{
			try
			{
				List<String> lines = FileUtils.readLines(rf);
				for(String s : lines)
				{
					if(s.isEmpty())
						continue;
					
					NamedPoint p = NamedPoint.fromKeyValue(s.split("="));
					if(p == null)
						continue;
					
					pointListModel.addRow(new NamedPoint[] { p });
					displayedPoints.add(p);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		refreshPageSize();
	}
	
	private void savePoints(Resource r)
	{
		File f = PointsExtension.get().getExtrasFolder();
		if(!f.exists())
			f.mkdirs();
		
		File rf = new File(f, r.getID() + ".txt");
		try
		{
			List<String> lines = new ArrayList<String>();
			for(NamedPoint p : displayedPoints)
				lines.add(NamedPoint.toKeyValue(p));
			FileUtils.writeLines(rf, lines);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void save()
	{
		if(_instance != null && _instance.dirty)
			_instance.savePoints(_instance.actorTypeChooser.getSelected());
	}
	
	public static void dispose()
	{
		if(_instance != null)
		{
			save();
			_instance.actorTypeChooser.dispose();
			_instance.animationChooser.dispose();
		}
		_instance = null;
	}

	private class Toolbar extends JPanel
	{
		public GroupButton zoomIn;
		public GroupButton zoomOut;
		public GroupToggleButton create;
		public GroupToggleButton select;
		public GroupToggleButton move;
		public JLabel modeLabel;
		public JLabel zoomLabel;
		
		public Toolbar()
		{
			super(new BorderLayout());
			
			PaintPanel b = UI.createButtonPanel
			(
				BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR),
					BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.TEXT_COLOR)
				)
			);
			
			b.setPreferredSize(new Dimension(1, 35));
			add(b, BorderLayout.CENTER);
			
			zoomIn = new GroupButton(1);
			zoomIn.setText("");
			
			zoomOut = new GroupButton(3);
			zoomOut.setText("");
			
			create = new GroupToggleButton(1);
			create.setText("");
			
			select = new GroupToggleButton(2);
			select.setText("");
			
			move = new GroupToggleButton(3);
			move.setText("");

			ButtonGroup mode = new ButtonGroup();
			mode.add(create);
			mode.add(select);
			mode.add(move);
			mode.setSelected(select.getModel(), true);
			
			Dimension maxButtonSize = new Dimension(200, 23);
			create.setMaximumSize(maxButtonSize);
			select.setMaximumSize(maxButtonSize);
			move.setMaximumSize(maxButtonSize);
			
			modeLabel = label("Mode: Select");
			zoomLabel = label("1x");
			
			b.add(Box.createHorizontalStrut(10));
			b.add(create);
			b.add(select);
			b.add(move);
			b.add(Box.createHorizontalStrut(10));
			b.add(modeLabel);
			b.add(Box.createHorizontalStrut(15));
//			b.add(Box.createHorizontalGlue());
			b.add(zoomIn);
			b.add(zoomOut);
			b.add(Box.createHorizontalStrut(10));
			b.add(zoomLabel);
			b.add(Box.createHorizontalGlue());
		}
	}

	public static class ListUtils
	{
		public static JComponent addHeader(JComponent component, String text)
		{
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(createHeader(text), BorderLayout.NORTH);
			panel.add(component, BorderLayout.CENTER);
			
			return panel;
		}
		
		public static JComponent createHeader(String text)
		{
			PaintPanel header = new PaintPanel();
			header.setVertical(true);
			header.setStartColor(Theme.BUTTON_BAR_START);
			header.setEndColor(Theme.BUTTON_BAR_END);
			
			header.setPreferredSize(new Dimension(1, 20));
			
			JLabel label = new JLabel(text);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			header.add(label);
			
			return header;
		}
		
		public static JPanel horizontalBox(Component... comps)
		{
			JPanel constricted = new JPanel();
			constricted.setLayout(new BoxLayout(constricted, BoxLayout.X_AXIS));
			constricted.setBackground(null);
			for(int i = 0; i < comps.length; ++i)
			{
				constricted.add(comps[i]);
				if(i != comps.length - 1)
					constricted.add(Box.createHorizontalStrut(10));
			}

			return constricted;
		}
		
		public static JPanel verticalBox(int gap, Component... comps)
		{
			JPanel constricted = new JPanel();
			constricted.setLayout(new BoxLayout(constricted, BoxLayout.Y_AXIS));
			constricted.setBackground(null);
			for(int i = 0; i < comps.length; ++i)
			{
				constricted.add(comps[i]);
				if(i != comps.length - 1 && gap > 0)
					constricted.add(Box.createVerticalStrut(gap));
			}

			return constricted;
		}
	}
	
	public static Predicate<IActorType> matchPredicate(final String pattern)
	{
		return new Predicate<IActorType>()
		{
			@Override
			public boolean test(IActorType t)
			{
				return wildCardMatch(t.getName(), pattern);
			}
		};
	}
	
	/**
	 * Performs a wildcard matching for the text and pattern 
	 * provided.
	 * 
	 * @param text the text to be tested for matches.
	 * 
	 * @param pattern the pattern to be matched for.
	 * This can contain the wildcard character '*' (asterisk).
	 * 
	 * @return <tt>true</tt> if a match is found, <tt>false</tt> 
	 * otherwise.
	 * 
	 * @author Adarsh Ramamurthy<br>
	 * http://www.adarshr.com/simple-implementation-of-wildcard-text-matching-using-java
	 */
	public static boolean wildCardMatch(String text, String pattern) {
	    // Create the cards by splitting using a RegEx. If more speed 
	    // is desired, a simpler character based splitting can be done.
	    String [] cards = pattern.split("\\*");

	    // Iterate over the cards.
	    for (String card : cards) {
	        int idx = text.indexOf(card);

	        // Card not detected in the text.
	        if(idx == -1) {
	            return false;
	        }

	        // Move ahead, towards the right of the text.
	        text = text.substring(idx + card.length());
	    }

	    return true;
	}
}
