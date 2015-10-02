package com.polydes.scenelink.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.polydes.common.comp.colors.ColorDisplay;
import com.polydes.scenelink.data.DataModel;
import com.polydes.scenelink.data.Link;
import com.polydes.scenelink.data.ModelProperty;
import com.polydes.scenelink.ui.Reflect.Property;
import com.polydes.scenelink.ui.combos.ImageReferenceComboModel;
import com.polydes.scenelink.util.FloatFilter;
import com.polydes.scenelink.util.IntegerFilter;

import stencyl.sw.SW;
import stencyl.sw.lnf.Theme;
import stencyl.sw.util.UI;
import stencyl.sw.util.dg.DialogPanel;


public class PropertiesPage extends DialogPanel
{
	public static JWindow generatePropertiesWindow(Object model)
	{
		final JWindow w = new JWindow(SW.get());
		w.setFocusable(true);
		JPanel main = new JPanel();
		main.setBorder(BorderFactory.createRaisedBevelBorder());
		main.setBackground(Theme.LIGHT_BG_COLOR);
		main.setForeground(Color.WHITE);
		
		final PropertiesPage properties = PropertiesPage.generatePropertiesPage(model, w);
		
		main.add(properties);
		
		JScrollPane scroller = UI.createScrollPane(main);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		w.add(scroller);
        
		w.addWindowListener(new WindowAdapter()
        {
            @Override
			public void windowClosed(WindowEvent e)
            {
            	properties.dispose();
            }
        });
		
		w.addWindowFocusListener(new WindowAdapter()
		{
			@Override
			public void windowLostFocus(WindowEvent e)
			{
				if(e.getOppositeWindow() != null && e.getOppositeWindow().getOwner() == properties.window)
					properties.bindWindow((JWindow) e.getOppositeWindow());
				else
					w.dispose();
			}
		});
		
		w.pack();
		w.setSize(w.getPreferredSize());
		w.setVisible(true);
		w.requestFocus();
		
		return w;
	}
	
	public void bindWindow(JWindow w)
	{
		boundWindow = w;
		w.addWindowFocusListener(new ExternalFocusListener());
	}
	
	public class ExternalFocusListener extends WindowAdapter
	{
		@Override
		public void windowLostFocus(WindowEvent e)
		{
			boundWindow.removeWindowFocusListener(this);
			boundWindow = null;
			
			if(e.getOppositeWindow() != window)
			{
				window.dispose();
			}
		}
	}
	
	public static PropertiesPage generatePropertiesPage(Object o)
	{
		return generatePropertiesPage(o, null);
	}

	public static PropertiesPage generatePropertiesPage(Object o, JWindow w)
	{
		PropertiesPage page = new PropertiesPage(o);
		page.window = w;
		ArrayList<Property> props = new ArrayList<Property>();
		Reflect.loadProperties(o, props);
		
		Map<Integer, Property> ordered = new TreeMap<Integer, Property>();
		for(int i = 0; i < props.size(); ++ i)
		{
			Property p = props.get(i);
			if(page.getEditorCreator(p) == null)
			{
				props.remove(i--);
				continue;
			}
			
			ModelProperty mp = p.annotation;
			if(mp != null)
			{
				if(!mp.display())
				{
					props.remove(i--);
					continue;
				}
				if(mp.order() != -1)
				{
					ordered.put(mp.order(), p);
					props.remove(i--);
					continue;
				}
			}
			
			++i;
		}
		props.addAll(0, ordered.values());
		
		page.add(Box.createVerticalStrut(20));
		
		for (Property p : props)
		{
			ModelProperty mp = p.annotation;
			
			String name = p.name;
			if(mp != null && !mp.label().equals(""))
				name = mp.label();
			
			JComponent component = page.createEditor(p);
			
			page.addGenericRow(name, component);
		}

		page.finishBlock();

		return page;
	}

	private Object model;
	
	private JWindow window;
	private JWindow boundWindow;

	private PropertiesPage(Object model)
	{
		super(Theme.LIGHT_BG_COLOR);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		this.model = model;
		setFocusable(true);
		
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				requestFocus();
			}
		});
	}
	
	public Method getEditorCreator(Property p)
	{
		String editorType = p.type.getSimpleName();
		if(p.annotation != null && !p.annotation.type().equals(""))
			editorType = p.annotation.type();
		
		return Reflect.getMethod(this, "create" + editorType + "Editor");
	}

	public JComponent createEditor(Property p)
	{
		Method createEditor = getEditorCreator(p);
		if (createEditor != null)
			return (JComponent) Reflect.invoke(createEditor, this, p);

		return null;
	}

	public JComponent createStringEditor(final Property p)
	{
		final JTextField editor = new JTextField(10);
		editor.setText((String) readProperty(p));

		editor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				writeProperty(p, editor.getText());
				tryRefreshWindow();
			}
		});

		return editor;
	}

	public JComponent createLargeStringEditor(final Property p)
	{
		final JTextArea editor = new JTextArea(3, 10);
		editor.setText((String) readProperty(p));

		editor.getDocument().addDocumentListener(new DocumentListener()
		{
			private void update()
			{
				writeProperty(p, editor.getText());
				tryRefreshWindow();
			}
			
			@Override public void changedUpdate(DocumentEvent e){update();}
			@Override public void insertUpdate(DocumentEvent e){update();}
			@Override public void removeUpdate(DocumentEvent e){update();}
		});

		return editor;
	}
	
	public JComponent createbooleanEditor(Property p)
	{
		return createBooleanEditor(p);
	}
	
	public JComponent createBooleanEditor(final Property p)
	{
		final JCheckBox editor = new JCheckBox();
		editor.setSelected((Boolean) readProperty(p));
		editor.setBackground(Theme.LIGHT_BG_COLOR);
		
		editor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				writeProperty(p, editor.isSelected());
			}
		});

		return editor;
	}
	
	public JComponent createintEditor(Property p)
	{
		return createIntegerEditor(p);
	}

	public JComponent createIntegerEditor(final Property p)
	{
		final JTextField editor = new JTextField(4);
		((PlainDocument) editor.getDocument())
				.setDocumentFilter(new IntegerFilter());
		editor.setText("" + (Integer) readProperty(p));

		editor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				writeProperty(p, Integer.parseInt(editor.getText()));
			}
		});

		return editor;
	}
	
	public JComponent createfloatEditor(Property p)
	{
		return createFloatEditor(p);
	}
	
	public JComponent createFloatEditor(final Property p)
	{
		final JTextField editor = new JTextField(5);
		((PlainDocument) editor.getDocument()).setDocumentFilter(new FloatFilter());
		editor.setText("" + (Float) readProperty(p));

		editor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				writeProperty(p, Float.parseFloat(editor.getText()));
			}
		});

		return editor;
	}

	public JComponent createPointEditor(final Property p)
	{
		final JTextField editor1 = new JTextField(4);
		final JTextField editor2 = new JTextField(4);
		((PlainDocument) editor1.getDocument())
				.setDocumentFilter(new IntegerFilter());
		((PlainDocument) editor2.getDocument())
				.setDocumentFilter(new IntegerFilter());
		Point pt = (Point) readProperty(p);
		if (pt == null)
			pt = new Point(0, 0);

		editor1.setText("" + pt.x);
		editor2.setText("" + pt.y);

		ActionListener updatePoint = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Point pt = new Point(Integer.parseInt(editor1.getText()),
						Integer.parseInt(editor2.getText()));
				writeProperty(p, pt);
			}
		};

		editor1.addActionListener(updatePoint);
		editor2.addActionListener(updatePoint);

		JPanel combine = new JPanel();
		combine.setLayout(new BoxLayout(combine, BoxLayout.X_AXIS));
		combine.setBackground(Theme.LIGHT_BG_COLOR);
		combine.add(editor1);
		combine.add(Box.createHorizontalStrut(10));
		combine.add(editor2);

		return combine;
	}

	public JComponent createDimensionEditor(final Property p)
	{
		final JTextField editor1 = new JTextField(4);
		final JTextField editor2 = new JTextField(4);
		((PlainDocument) editor1.getDocument())
				.setDocumentFilter(new IntegerFilter());
		((PlainDocument) editor2.getDocument())
				.setDocumentFilter(new IntegerFilter());
		Dimension d = (Dimension) readProperty(p);

		editor1.setText("" + d.width);
		editor2.setText("" + d.height);

		ActionListener updateDimension = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Dimension d = new Dimension(
						Integer.parseInt(editor1.getText()),
						Integer.parseInt(editor2.getText()));
				writeProperty(p, d);
			}
		};

		editor1.addActionListener(updateDimension);
		editor2.addActionListener(updateDimension);

		JPanel combine = new JPanel();
		combine.setLayout(new BoxLayout(combine, BoxLayout.X_AXIS));
		combine.setBackground(Theme.LIGHT_BG_COLOR);
		combine.add(editor1);
		combine.add(Box.createHorizontalStrut(10));
		combine.add(editor2);

		return combine;
	}
	
	public JComponent createRectangleEditor(final Property p)
	{
		final JTextField x = new JTextField(4);
		final JTextField y = new JTextField(4);
		final JTextField w = new JTextField(4);
		final JTextField h = new JTextField(4);
		((PlainDocument) x.getDocument()).setDocumentFilter(new IntegerFilter());
		((PlainDocument) y.getDocument()).setDocumentFilter(new IntegerFilter());
		((PlainDocument) w.getDocument()).setDocumentFilter(new IntegerFilter());
		((PlainDocument) h.getDocument()).setDocumentFilter(new IntegerFilter());
		
		Rectangle r = (Rectangle) readProperty(p);

		x.setText("" + r.x);
		y.setText("" + r.y);
		w.setText("" + r.width);
		h.setText("" + r.height);

		ActionListener updateRectangle = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Rectangle r = new Rectangle(
						Integer.parseInt(x.getText()),
						Integer.parseInt(y.getText()),
						Integer.parseInt(w.getText()),
						Integer.parseInt(h.getText()));
				writeProperty(p, r);
			}
		};

		x.addActionListener(updateRectangle);
		y.addActionListener(updateRectangle);
		w.addActionListener(updateRectangle);
		h.addActionListener(updateRectangle);

		JPanel combine = new JPanel();
		combine.setLayout(new BoxLayout(combine, BoxLayout.X_AXIS));
		combine.setBackground(Theme.LIGHT_BG_COLOR);
		combine.add(x);
		combine.add(Box.createHorizontalStrut(10));
		combine.add(y);
		combine.add(Box.createHorizontalStrut(10));
		combine.add(w);
		combine.add(Box.createHorizontalStrut(10));
		combine.add(h);
		
		return combine;
	}

	public JComponent createImageReferenceEditor(final Property p)
	{
		final JComboBox<String> editor = new JComboBox<String>(new ImageReferenceComboModel());
		editor.setSelectedItem(readProperty(p));
		editor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				writeProperty(p, ((ImageReferenceComboModel) editor.getModel()).getImageString());
			}
		});
		
		JButton imageImporter = new ImageImportButton();
		imageImporter.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				editor.setSelectedItem(ImageImportButton.lastImported);
				tryRefreshWindow();
			}
		});
		
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
		wrapper.add(editor);
		wrapper.add(imageImporter);
		wrapper.setBackground(Theme.LIGHT_BG_COLOR);
		
		return wrapper;
	}
	
	public JComponent createLinkEditor(final Property p)
	{
		final LinkDisplay editor = new LinkDisplay((Link) readProperty(p), window);
		editor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				writeProperty(p, editor.getLink());
				tryRefreshWindow();
			}
		});
		return editor;
	}
	
	public JComponent createColorEditor(final Property p)
	{
		final ColorDisplay editor = new ColorDisplay(20, 20, (Color) readProperty(p), window);
		editor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				writeProperty(p, editor.getColor());
			}
		});
		return editor;
	}
	
	public void tryRefreshWindow()
	{
		if(window != null)
			window.setSize(window.getPreferredSize());
	}
	
	private Object readProperty(Property p)
	{
		try
		{
			return FieldUtils.readField(model, p.name, true);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private void writeProperty(Property p, Object value)
	{
		try
		{
			Object oldValue = readProperty(p);
			
			FieldUtils.writeField(model, p.name, value, true);
			
			if(DataModel.class.isInstance(model))
				((DataModel) model).pcs.firePropertyChange(p.name, oldValue, value);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	public void dispose()
	{
		removeAll();
		model = null;
	}
}
