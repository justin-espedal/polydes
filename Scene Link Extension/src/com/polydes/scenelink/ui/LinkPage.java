package com.polydes.scenelink.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.polydes.scenelink.data.Link;
import com.polydes.scenelink.data.LinkModel;
import com.polydes.scenelink.data.LinkPageModel;
import com.polydes.scenelink.res.Resources;
import com.polydes.scenelink.util.ColorUtil;
import com.polydes.scenelink.util.CursorUtil;

import stencyl.sw.SW;


public class LinkPage extends JComponent implements MouseMotionListener, MouseListener, KeyListener, PropertyChangeListener
{
	private static int BORDER = 10;
	
	//---
	//Panel Resizing/Moving Controls
	
	private static final int NONE = -1;
	private static final int RIGHT = 0;
	private static final int UPRIGHT = 1;
	private static final int UP = 2;
	private static final int UPLEFT = 3;
	private static final int LEFT = 4;
	private static final int DOWNLEFT = 5;
	private static final int DOWN = 6;
	private static final int DOWNRIGHT = 7;
	private static final int CENTER = 8;
	
	int hoveredControl;
	int activeControl;
	
	//---
	//Current tool
	
	Tool tool;
	
	//---
	
	private LinkPageModel model;
	private JPanel absoluteWrapper;
	
	private int width;
	private int height;
	
	private Color bgColor;
	private BufferedImage bgImage;
	private Point bgImagePos;
	
	private int gridX;
	private int gridY;
	private int gridCellW;
	private int gridCellH;
	private Color gridColor;
	
	private ArrayList<LinkPanel> linkPanels;
	
	private LinkPanel hoveredPanel;
	private LinkPanel selectedPanel;
	private ArrayList<LinkPanel> selectedPanels;
	
	private Color curColor;
	private boolean creating;
	private boolean creatingSelection;
	
	private Rectangle rubberband;
	
	private Point beginPress;
	private Rectangle beginPos;
	private ArrayList<Rectangle> beginPosMulti;
	
	private LinkPanel newLink;
	private boolean alignToGrid;
	private boolean viewTable;
	
	public LinkPage(LinkPageModel model)
	{
		this.model = model;
		model.addPropertyChangeListener(this);
		
		absoluteWrapper = null;
		refresh();
		
		linkPanels = new ArrayList<LinkPanel>();
		for(LinkModel l : model.getLinks().values())
		{
			addLinkFromModel(l);
		}
		
		setFocusable(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		
		hoveredControl = NONE;
		activeControl = NONE;
		creating = false;
		creatingSelection = false;
		curColor = ColorUtil.decode("#66a57b61");
		alignToGrid = false;
		viewTable = false;
		tool = Tool.VIEW;
		selectedPanels = null;
		rubberband = null;
		beginPosMulti = null;
		
		revalidate();
		repaint();
	}
	
	public void setAbsoluteWrapper(JPanel wrapper)
	{
		absoluteWrapper = wrapper;
		refresh();
	}
	
	public void addLinkFromModel(LinkModel l)
	{
		LinkPanel newLinkPanel = new LinkPanel(l);
		newLinkPanel.setParent(this);
		linkPanels.add(newLinkPanel);
	}
	
	public void removeLinkAndModel(LinkPanel p)
	{
		repaint(p);
		linkPanels.remove(p);
		if(p == selectedPanel)
		{
			selectedPanel = null;
			hoveredControl = NONE;
		}
		if(p == hoveredPanel)
			hoveredPanel = null;
		model.getLinks().remove(p.getModel().getId());
	}
	
	public void refresh()
	{
		width = model.getSize().width;
		height = model.getSize().height;
		bgColor = model.getBgColor();
		bgImage = Resources.getImage(model.getBgImage());
		bgImagePos = model.getBgImagePos();
		gridX = model.getGridOffset().x;
		gridY = model.getGridOffset().y;
		gridCellW = model.getGridSize().width;
		gridCellH = model.getGridSize().height;
		gridColor = model.getGridColor();
		alignToGrid = model.isAutoAlign();
		viewTable = model.isViewTable();
		
		if(absoluteWrapper != null)
		{
			absoluteWrapper.removeAll();
			Dimension d = null;
			if(viewTable)
			{
				GridTable gt;
				absoluteWrapper.add(gt = new GridTable(GridTable.HORIZONTAL, (width - gridX) / gridCellW, model.getGridSize()));
				gt.setBounds(new Rectangle(gridCellW + gridX, 0, width - gridX, gridCellH));
				absoluteWrapper.add(gt = new GridTable(GridTable.VERTICAL, (height - gridY) / gridCellH, model.getGridSize()));
				gt.setBounds(new Rectangle(0, gridCellH + gridY, gridCellW, height - gridY));
				absoluteWrapper.add(this);
				setBounds(new Rectangle(gridCellW, gridCellH, width, height));
				d = new Dimension(width + gridCellW, height + gridCellH);
				absoluteWrapper.setBackground(gt.getBackground());
			}
			else
			{
				absoluteWrapper.add(this);
				this.setBounds(new Rectangle(0, 0, width, height));
				d = new Dimension(width, height);
			}
			absoluteWrapper.setPreferredSize(d);
			absoluteWrapper.setMinimumSize(d);
			absoluteWrapper.setMaximumSize(d);
			
			absoluteWrapper.repaint();
			absoluteWrapper.revalidate();
		}
		
		revalidate();
		repaint();
	}
	
	private int nextID()
	{
		int id = 0;
		for(int i : model.getLinks().keySet())
			id = Math.max(id, i);
		return ++id;
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(width + (viewTable ? 20 : 0), height + (viewTable ? 20 : 0));
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		//Background
		if(bgImage == null || bgImage.getWidth() < width || bgImage.getHeight() < height)
		{
			g.setColor(bgColor);
			g.fillRect(0, 0, width, height);
		}
		if(bgImage != null)
			g.drawImage(bgImage, bgImagePos.x, bgImagePos.y, null);
		
		//Grid
		g.setColor(gridColor);
		for(int x = gridX; x < width; x += gridCellW)
			g.drawLine(x, gridY, x, height);
		for(int y = gridY; y < height; y += gridCellH)
			g.drawLine(gridX, y, width, y);
		
		//Links
		for(LinkPanel l : linkPanels)
		{
			paintPanel(g, l);
		}
		if(newLink != null)
			paintPanel(g, newLink);
		
		//Select tool hover
		if(tool == Tool.SELECT && selectedPanel != null && (hoveredControl != NONE || activeControl != NONE))
		{
			int c = Math.max(hoveredControl, activeControl);
			g.translate(selectedPanel.getX(), selectedPanel.getY());
			selectedPanel.paintHoveredBorders
			(
				g,
				c == UPLEFT || c == UP || c == UPRIGHT,
				c == DOWNLEFT || c == DOWN || c == DOWNRIGHT,
				c == UPLEFT || c == LEFT || c == DOWNLEFT,
				c == UPRIGHT || c == RIGHT || c == DOWNRIGHT
			);
			g.translate(-selectedPanel.getX(), -selectedPanel.getY());
		}
		if(rubberband != null)
		{
			g.setColor(Color.BLUE.brighter());
			g.drawRect(rubberband.x, rubberband.y, rubberband.width - 1, rubberband.height - 1);
			g.drawRect(rubberband.x + 1, rubberband.y + 1, rubberband.width - 3, rubberband.height - 3);
		}
	}
	
	private void paintPanel(Graphics g, LinkPanel l)
	{
		g.translate(l.getX(), l.getY());
		l.paintComponent(g);
		g.translate(-l.getX(), -l.getY());
	}
	
	public LinkPageModel getModel()
	{
		return model;
	}

	private Rectangle getGridRect(Point p1, Point p2)
	{
		Rectangle r = getRect(p1, p2);
		r.translate(-gridX, -gridY);
		int x1 = r.x / gridCellW;
		int y1 = r.y / gridCellH;
		int x2 = (r.x + r.width) / gridCellW;
		int y2 = (r.y + r.height) / gridCellH;
		r.x = x1 * gridCellW;
		r.y = y1 * gridCellH;
		r.width = (x2 - x1 + 1) * gridCellW;
		r.height = (y2 - y1 + 1) * gridCellH;
		r.translate(gridX, gridY);
		
		return r;
	}
	
	private Rectangle getRect(Point p1, Point p2)
	{
		Rectangle r = new Rectangle(p1);
		r.add(p2);
		return r;
	}
	
	public void setTool(Tool t)
	{
		tool = t;
		repaint();
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		Point pt = e.getPoint();
		if(tool == Tool.SELECT && activeControl != NONE)
		{
			if(selectedPanel != null)
				updateDragResize(pt);
			else if(selectedPanels != null)
				updateDragMulti(pt);
		}
		else if(tool == Tool.SELECT && creatingSelection)
		{
			if(rubberband != null)
				repaint(rubberband);
			rubberband = /*align() ? getGridRect(beginPress, pt) :*/ getRect(beginPress, pt);
			repaint(rubberband);
		}
		else if(tool == Tool.CREATE && creating)
		{
			newLink.setDrawRect(align() ? getGridRect(beginPress, pt) : getRect(beginPress, pt));
			repaint(newLink);
		}
		else
			checkHover(pt);
	}
	
	public void repaint(LinkPanel l)
	{
		repaint(l.getBounds());
	}
	
	@Override
	public void repaint(Rectangle r)
	{
		repaint
		(
			r.x - gridCellW,
			r.y - gridCellH,
			r.width + gridCellW * 2,
			r.height + gridCellH * 2
		);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		Point pt = e.getPoint();
		
		checkHover(pt);
	}
	
	private void checkHover(Point pt)
	{
		//give priority to selectedPanel, only when in SELECT mode
		if(tool == Tool.SELECT)
		{
			boolean overSelected = checkControlHover(pt);
			
			//TODO: rules a little different here for single and multi?
			if(overSelected && hoveredPanel != selectedPanel)
			{
				changeHovered(selectedPanel);
				return;
			}
		}
		
		//update if no current hover, or the current doesn't include pt
		if(hoveredPanel == null || !hoveredPanel.getBounds().contains(pt))
			changeHovered(getLinkPanelAt(pt));
	}
	
	private boolean checkControlHover(Point pt)
	{
		int x = pt.x;
		int y = pt.y;
		
		int old = hoveredControl;
		
		hoveredControl = NONE;
		
		if(selectedPanel != null)
		{
			Rectangle r = (Rectangle) selectedPanel.getBounds();
			
			Rectangle boundsRect = new Rectangle(r.x - BORDER, r.y - BORDER, r.width + BORDER * 2, r.height + BORDER * 2);
			if(boundsRect.contains(x, y))
			{
				boolean[] bounds = new boolean[]
				{
					Math.abs(x - (r.x + r.width)) <= BORDER,
					false,
					Math.abs(y - r.y) <= BORDER,
					false,
					Math.abs(x - r.x) <= BORDER,
					false,
					Math.abs(y - (r.y + r.height)) <= BORDER,
					false
				};
				
				if(bounds[UP] && bounds[DOWN])
				{
					bounds[UP] = Math.abs(y - r.y) < Math.abs(y - (r.y + r.height));
					bounds[DOWN] = !bounds[UP];
				}
				if(bounds[LEFT] && bounds[RIGHT])
				{
					bounds[LEFT] = Math.abs(x - r.x) < Math.abs(x - (r.x + r.width));
					bounds[RIGHT] = !bounds[LEFT];
				}
				
				bounds[UPRIGHT] = bounds[UP] && bounds[RIGHT];
				bounds[UPLEFT] = bounds[UP] && bounds[LEFT];
				bounds[DOWNRIGHT] = bounds[DOWN] && bounds[RIGHT];
				bounds[DOWNLEFT] = bounds[DOWN] && bounds[LEFT];
				
				if(bounds[UPRIGHT])
					hoveredControl = UPRIGHT;
				else if(bounds[UPLEFT])
					hoveredControl = UPLEFT;
				else if(bounds[DOWNRIGHT])
					hoveredControl = DOWNRIGHT;
				else if(bounds[DOWNLEFT])
					hoveredControl = DOWNLEFT;
				else if(bounds[RIGHT])
					hoveredControl = RIGHT;
				else if(bounds[UP])
					hoveredControl = UP;
				else if(bounds[LEFT])
					hoveredControl = LEFT;
				else if(bounds[DOWN])
					hoveredControl = DOWN;
				else
					hoveredControl = CENTER;
			}
		}
		else if(selectedPanels != null)
		{
			for(LinkPanel p : selectedPanels)
			{
				if(p.getBounds().contains(pt))
				{
					hoveredControl = CENTER;
					break;
				}
			}
		}
		
		updateCursor();
		
		if(old != hoveredControl)
		{
			if(selectedPanel != null)
				repaint(selectedPanel);
			else if(selectedPanels != null)
			{
				for(LinkPanel p : selectedPanels)
				{
					repaint(p);
				}
			}
		}
		
		return hoveredControl != NONE;
	}
	
	public void updateCursor()
	{
		switch(hoveredControl)
		{
			case UP: case DOWN:
				CursorUtil.setCursor(this, "ud");
				break;
			case RIGHT: case LEFT:
				CursorUtil.setCursor(this, "rl");
				break;
			case UPRIGHT: case DOWNLEFT:
				CursorUtil.setCursor(this, "ur");
				break;
			case UPLEFT: case DOWNRIGHT:
				CursorUtil.setCursor(this, "dr");
				break;
			case CENTER:
				CursorUtil.setCursor(this, "udrl");
				break;
			case NONE:
				CursorUtil.setCursor(this, "default");
				break;
		}
	}
	
	private void changeHovered(LinkPanel p)
	{
		if(hoveredPanel != null)
		{
			repaint(hoveredPanel);
			hoveredPanel.setHovered(false);
		}
		if(p != null)
		{
			p.setHovered(true);
			repaint(p);
		}
		hoveredPanel = p;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if(e.getClickCount() == 2 && hoveredPanel != null)
		{
			hoveredPanel.openLink();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		requestFocus();
		
		Point pt = e.getPoint();
		
		if(SwingUtilities.isLeftMouseButton(e))
		{
			beginPress = (Point) pt.clone();
			
			//Pressed while a drag/resize control is available
			if(tool == Tool.SELECT && hoveredControl != NONE)
			{
				activeControl = hoveredControl;
				if(selectedPanel != null)
					beginPos = (Rectangle) selectedPanel.getBounds().clone();
				else if(selectedPanels != null)
				{
					beginPosMulti = new ArrayList<Rectangle>();
					for(LinkPanel p : selectedPanels)
					{
						beginPosMulti.add((Rectangle) p.getBounds().clone());
					}
				}
			}
			else //CHECK FOR SELECTION AT POINT
			{
				//change atPoint to just hoveredPanel?
				LinkPanel atPoint = getLinkPanelAt(pt);
				
				boolean retainSelection = false;
				if(selectedPanels != null)
				{
					for(LinkPanel p : selectedPanels)
					{
						if(p == atPoint)
						{
							retainSelection = true;
							break;
						}
					}
				}
				if(!retainSelection)
				{
					unselect();
					
					if(atPoint != null)
					{
						repaint(atPoint);
						atPoint.setSelected(true);
					}
					
					selectedPanel = atPoint;
				}
			}
			//CLICKED EMPTY AREA, USE TOOL
			if(selectedPanel == null)
			{
				if(tool == Tool.CREATE)
				{
					creating = true;
					Rectangle drawRect = align() ? getGridRect(pt, pt) : getRect(pt, pt);
					newLink = new LinkPanel(this, curColor, drawRect);
				}
				else if(tool == Tool.SELECT)
				{
					creatingSelection = true;
				}
			}
		}
	}
	
	public void unselect()
	{
		if(selectedPanels != null)
		{
			for(LinkPanel p : selectedPanels)
			{
				repaint(p);
				p.setSelected(false);
			}
			selectedPanels = null;
		}
		
		if(selectedPanel != null)
		{
			repaint(selectedPanel);
			selectedPanel.setSelected(false);
		}
	}
	
	private boolean align()
	{
		return alignToGrid ^ SW.get().isShiftDown();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		Point pt = e.getPoint();
		if(SwingUtilities.isLeftMouseButton(e))
		{
			if(tool == Tool.CREATE && creating)
			{
				creating = false;
				Rectangle pos = align() ? getGridRect(beginPress, pt) : getRect(beginPress, pt);
				repaint(newLink);
				newLink = null;
				
				int id = nextID();
				LinkModel newModel = new LinkModel(id, "", curColor, pos, Link.createBlank());
				model.getLinks().put(id, newModel);
				
				addLinkFromModel(newModel);
				
				revalidate();
			}
			else if(tool == Tool.SELECT && activeControl != NONE)
			{
				if(selectedPanel != null)
					updateDragResize(pt);
				else if(selectedPanels != null)
					updateDragMulti(pt);
				
				activeControl = NONE;
			}
			else if(tool == Tool.SELECT && creatingSelection && rubberband != null)
			{
				if(selectedPanels != null)
				{
					for(LinkPanel p : selectedPanels)
						repaint(p);
				}
				
				repaint(rubberband);
				rubberband = /*align() ? getGridRect(beginPress, pt) :*/ getRect(beginPress, pt);
				creatingSelection = false;
				
				selectedPanel = null;
				
				selectedPanels = new ArrayList<LinkPanel>();
				for(LinkPanel p : linkPanels)
				{
					if(p.getBounds().intersects(rubberband))
					{
						selectedPanels.add(p);
						p.setSelected(true);
						repaint(p);
					}
				}
				
				rubberband = null;
			}
		}
		else if(SwingUtilities.isRightMouseButton(e) && hoveredPanel != null)
			hoveredPanel.showProperties(pt);
	}
	
	public void updateDragResize(Point pt)
	{
		if(tool != Tool.SELECT || selectedPanel == null || activeControl == NONE)
			return;
		
		int x = pt.x - beginPress.x;
		int y = pt.y - beginPress.y;
		
		Rectangle r = beginPos;
		Point p = new Point(x, y);
		
		Rectangle activeBounds = selectedPanel.getBounds();
		
		repaint(activeBounds);
		
		switch(activeControl)
		{
			case RIGHT:
				activeBounds.width = r.width + p.x;
				break;
			case UPRIGHT:
				activeBounds.y = r.y + p.y;
				activeBounds.height = r.height - p.y;
				activeBounds.width = r.width + p.x;
				break;
			case UP:
				activeBounds.y = r.y + p.y;
				activeBounds.height = r.height - p.y;
				break;
			case UPLEFT:
				activeBounds.y = r.y + p.y;
				activeBounds.height = r.height - p.y;
				activeBounds.x = r.x + p.x;
				activeBounds.width = r.width - p.x;
				break;
			case LEFT:
				activeBounds.x = r.x + p.x;
				activeBounds.width = r.width - p.x;
				break;
			case DOWNLEFT:
				activeBounds.height = r.height + p.y;
				activeBounds.x = r.x + p.x;
				activeBounds.width = r.width - p.x;
				break;
			case DOWN:
				activeBounds.height = r.height + p.y;
				break;
			case DOWNRIGHT:
				activeBounds.height = r.height + p.y;
				activeBounds.width = r.width + p.x;
				break;
			case CENTER:
				activeBounds.x = r.x + p.x;
				activeBounds.y = r.y + p.y;
				break;
		}
		if(align())
		{
			Rectangle alignedBounds = (Rectangle) activeBounds.clone();
			switch(activeControl)
			{
				case RIGHT:
					alignedBounds.width = getClosestGridlineX(activeBounds.x + activeBounds.width) - activeBounds.x;
					break;
				case UPRIGHT:
					alignedBounds.y = getClosestGridlineY(activeBounds.y);
					alignedBounds.height = activeBounds.y + activeBounds.height - alignedBounds.y;
					alignedBounds.width = getClosestGridlineX(activeBounds.x + activeBounds.width) - activeBounds.x;
					break;
				case UP:
					alignedBounds.y = getClosestGridlineY(activeBounds.y);
					alignedBounds.height = activeBounds.y + activeBounds.height - alignedBounds.y;
					break;
				case UPLEFT:
					alignedBounds.y = getClosestGridlineY(activeBounds.y);
					alignedBounds.height = activeBounds.y + activeBounds.height - alignedBounds.y;
					alignedBounds.x = getClosestGridlineX(activeBounds.x);
					alignedBounds.width = activeBounds.x + activeBounds.width - alignedBounds.x;
					break;
				case LEFT:
					alignedBounds.x = getClosestGridlineX(activeBounds.x);
					alignedBounds.width = activeBounds.x + activeBounds.width - alignedBounds.x;
					break;
				case DOWNLEFT:
					alignedBounds.height = getClosestGridlineY(activeBounds.y + activeBounds.height) - activeBounds.y;
					alignedBounds.x = getClosestGridlineX(activeBounds.x);
					alignedBounds.width = activeBounds.x + activeBounds.width - alignedBounds.x;
					break;
				case DOWN:
					alignedBounds.height = getClosestGridlineY(activeBounds.y + activeBounds.height) - activeBounds.y;
					break;
				case DOWNRIGHT:
					alignedBounds.height = getClosestGridlineY(activeBounds.y + activeBounds.height) - activeBounds.y;
					alignedBounds.width = getClosestGridlineX(activeBounds.x + activeBounds.width) - activeBounds.x;
					break;
				case CENTER:
					alignedBounds.y = getClosestGridlineY(activeBounds.y);
					alignedBounds.x = getClosestGridlineX(activeBounds.x);
					break;
			}
			activeBounds = alignedBounds;
		}
		
		repaint(activeBounds);
		
		selectedPanel.setDrawRect(activeBounds);
		selectedPanel.getModel().setPos((Rectangle) activeBounds.clone());
	}
	
	public void updateDragMulti(Point pt)
	{
		if(tool != Tool.SELECT || selectedPanels == null)
			return;
		
		int x = pt.x - beginPress.x;
		int y = pt.y - beginPress.y;
		
		Point p = new Point(x, y);
		
		for(int i = 0; i < selectedPanels.size(); ++i)
		{
			Rectangle r = beginPosMulti.get(i);
			LinkPanel selected = selectedPanels.get(i);
			
			Rectangle activeBounds = selected.getBounds();
			
			repaint(activeBounds);
			
			activeBounds.x = r.x + p.x;
			activeBounds.y = r.y + p.y;
			
			if(align())
			{
				Rectangle alignedBounds = (Rectangle) activeBounds.clone();
				alignedBounds.y = getClosestGridlineY(activeBounds.y);
				alignedBounds.x = getClosestGridlineX(activeBounds.x);
				activeBounds = alignedBounds;
			}
			
			repaint(activeBounds);
			
			selected.setDrawRect(activeBounds);
			selected.getModel().setPos((Rectangle) activeBounds.clone());
		}
	}
	
	public int getClosestGridlineX(int v)
	{
		return (((v - gridX) + (gridCellW / 2)) / gridCellW) * gridCellW + gridX;
	}
	
	public int getClosestGridlineY(int v)
	{
		return (((v - gridY) + (gridCellH / 2)) / gridCellH) * gridCellH + gridY;
	}
	
	public LinkPanel getLinkPanelAt(Point pt)
	{
		for(LinkPanel p : linkPanels)
		{
			if(p.getBounds().contains(pt))
				return p;
		}
		return null;
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			if(selectedPanel != null)
			{
				removeLinkAndModel(selectedPanel);
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			if(selectedPanel != null)
			{
				selectedPanel.openLink();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
		String propName = e.getPropertyName();
		if(propName.equals("size"))
		{
			width = model.getSize().width;
			height = model.getSize().height;
		}
		else if(propName.equals("bgColor"))
			bgColor = model.getBgColor();
		else if(propName.equals("bgImage"))
			bgImage = Resources.getImage(model.getBgImage());
		else if(propName.equals("bgImagePos"))
			bgImagePos = model.getBgImagePos();
		else if(propName.equals("gridOffset"))
		{
			gridX = model.getGridOffset().x;
			gridY = model.getGridOffset().y;
		}
		else if(propName.equals("gridSize"))
		{
			gridCellW = model.getGridSize().width;
			gridCellH = model.getGridSize().height;
		}
		else if(propName.equals("gridColor"))
			gridColor = model.getGridColor();
		else if(propName.equals("autoAlign"))
			alignToGrid = model.isAutoAlign();
		else if(propName.equals("viewTable"))
		{
			viewTable = model.isViewTable();
			if(absoluteWrapper != null)
			{
				absoluteWrapper.removeAll();
				Dimension d = null;
				if(viewTable)
				{
					GridTable gt;
					absoluteWrapper.add(gt = new GridTable(GridTable.HORIZONTAL, (width - gridX) / gridCellW, model.getGridSize()));
					gt.setBounds(new Rectangle(gridCellW + gridX, 0, width - gridX, gridCellH));
					absoluteWrapper.add(gt = new GridTable(GridTable.VERTICAL, (height - gridY) / gridCellH, model.getGridSize()));
					gt.setBounds(new Rectangle(0, gridCellH + gridY, gridCellW, height - gridY));
					absoluteWrapper.add(this);
					setBounds(new Rectangle(gridCellW, gridCellH, width, height));
					d = new Dimension(width + gridCellW, height + gridCellH);
					absoluteWrapper.setBackground(gt.getBackground());
				}
				else
				{
					absoluteWrapper.add(this);
					this.setBounds(new Rectangle(0, 0, width, height));
					d = new Dimension(width, height);
				}
				absoluteWrapper.setPreferredSize(d);
				absoluteWrapper.setMinimumSize(d);
				absoluteWrapper.setMaximumSize(d);
				
				absoluteWrapper.repaint();
				absoluteWrapper.revalidate();
			}
		}
		revalidate();
		repaint();
	}
}
