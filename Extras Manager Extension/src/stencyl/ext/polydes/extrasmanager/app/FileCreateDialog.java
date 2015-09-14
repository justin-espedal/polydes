package stencyl.ext.polydes.extrasmanager.app;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import stencyl.ext.polydes.extrasmanager.io.FileOperations;
import stencyl.sw.lnf.Theme;
import stencyl.sw.loc.LanguagePack;
import stencyl.sw.util.comp.ButtonBarFactory;
import stencyl.sw.util.comp.GroupButton;
import stencyl.sw.util.dg.StencylDialog;

public class FileCreateDialog extends StencylDialog
{
	/*-------------------------------------*\
	 * Globals
	\*-------------------------------------*/ 

	private static LanguagePack lang = LanguagePack.get();

	public static final int WIDTH = 240;
	public static final int HEIGHT = 170;
	
	private String model;
	private JPanel panel;
	private JComboBox<FileRep> selector;
	private JTextArea text;
	
	private AbstractButton okButton;	
	
	/*-------------------------------------*\
	 * Constructor
	\*-------------------------------------*/ 

	public FileCreateDialog(JFrame owner)
	{
		super
		(
			owner, 
			"Create New File", 
			WIDTH, HEIGHT, 
			false
		);
		
		model = "New File";
		
		add(createContentPanel(), BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	private class FileRep
	{
		public File file;
		
		public FileRep(File f)
		{
			file = f;
		}
		
		@Override
		public String toString()
		{
			return file.getName();
		}
	}
	
	/*-------------------------------------*\
	 * Construct UI
	\*-------------------------------------*/ 

	@Override
	public JComponent createContentPanel()
	{
		selector = new JComboBox<FileRep>();
		for(File f : FileOperations.templates)
			selector.addItem(new FileRep(f));
		
		text = new JTextArea(1, 5);
		text.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));	
		text.setText(model);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		
		panel = new JPanel(new BorderLayout());
		panel.add(selector, BorderLayout.NORTH);
		panel.add(text, BorderLayout.SOUTH);
		panel.setBackground(Theme.EDITOR_BG_COLOR);
		
		text.getDocument().addDocumentListener
		(
			new DocumentListener()
			{
				@Override
				public void insertUpdate(DocumentEvent e)
				{
					okButton.setEnabled(text.getDocument().getLength() > 0);
				}
				
				@Override
				public void removeUpdate(DocumentEvent e)
				{
					okButton.setEnabled(text.getDocument().getLength() > 0);
				}
				
				@Override
				public void changedUpdate(DocumentEvent e)
				{
					
				}
			}
		);
		
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
		p.setBackground(Theme.EDITOR_BG_COLOR);
		p.add(panel, BorderLayout.CENTER);
		
		return p;
	}

	@Override
	public JPanel createButtonPanel() 
	{
		okButton = new GroupButton(0);
		JButton cancelButton = new GroupButton(0);

		okButton.setAction
		(
			new AbstractAction(lang.get("globals.savechanges"))
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					model = text.getText();
					setVisible(false);
				}
			}
		);

		cancelButton.setAction
		(
			new AbstractAction(lang.get("globals.cancel")) 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					cancel();
				}
			}
		);

		return ButtonBarFactory.createButtonBar
		(
			this,
			new AbstractButton[] {okButton, cancelButton},
			0
		);
	}
	
	public String getString()
	{
		return model;
	}
	
	@Override
	public void cancel()
	{
		model = null;
		setVisible(false);
	}

	public File getTemplate()
	{
		return ((FileRep) selector.getSelectedItem()).file;
	}
}