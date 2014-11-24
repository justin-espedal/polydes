package stencyl.ext.polydes.extrasmanager.data;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import stencyl.ext.polydes.extrasmanager.app.list.FileListModel;
import stencyl.ext.polydes.extrasmanager.app.pages.MainPage;
import stencyl.ext.polydes.extrasmanager.res.Resources;
import stencyl.sw.lnf.Theme;
import stencyl.sw.util.FileHelper;
import stencyl.sw.util.Fonts;
import stencyl.sw.util.comp.GroupButton;

import com.explodingpixels.macwidgets.HudWidgetFactory;
import com.jidesoft.swing.PaintPanel;

public class FilePreviewer
{
	private static Color BACKGROUND_COLOR = new Color(62, 62, 62);
	private static TitlePanel previewBar = new TitlePanel();
	private static File previewFile = null;
	
	public static void preview(File f)
	{
		String type = Mime.get(f);
		JComponent toPreview = null;
		
		if(type.startsWith("image"))
			toPreview = buildImagePreview(f);
		else if(type.startsWith("text"))
			toPreview = buildTextPreview(f);
		
		if(toPreview != null)
		{
			JPanel previewPanel = new JPanel();
			previewPanel.setBackground(BACKGROUND_COLOR);
			previewPanel.add(toPreview);
			previewFile = f;
			MainPage.get().setView(previewPanel, previewBar);
		}
	}
	
	private static JComponent buildImagePreview(File f)
	{
		try
		{
			return new JLabel(new ImageIcon(ImageIO.read(f)));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return new JLabel();
		}
	}
	
	private static JComponent buildTextPreview(File f)
	{
		JPanel panel = new JPanel();
		TextArea preview = new TextArea();
		try
		{
			preview.setText(FileHelper.readFileToString(f));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		panel.add(preview);
		
		return panel;
	}
	
	@SuppressWarnings("serial")
	protected static class TitlePanel extends PaintPanel
	{
		public JLabel label;
		
		private JButton homeButton;
		private JButton upButton;
		private JButton refreshButton;
		
		private JButton backButton;
		private JButton forwardButton;
		
		private JButton editButton;
		
		public TitlePanel()
		{
			initButtons();
			setVertical(true);
	        setStartColor(Theme.COMMAND_BAR_START);
	        setEndColor(Theme.COMMAND_BAR_END);
	        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	        setPreferredSize(new Dimension(1, 35));
	        
			setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0), BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR)));
			
			add(Box.createHorizontalStrut(8));
	        add(homeButton);
	        add(upButton);
	        add(refreshButton);
	        add(Box.createHorizontalStrut(8));
	        add(backButton);
	        add(forwardButton);
	        add(Box.createHorizontalStrut(8));
	        add(editButton);
	        
			label = HudWidgetFactory.createHudLabel("Extras");
			label.setForeground(Theme.TEXT_COLOR);
			label.setFont(Fonts.getTitleBoldFont());
			
	        add(Box.createHorizontalStrut(16));
	        add(label);
			add(Box.createHorizontalStrut(8));
		}
		
		public void refreshBreadcrumb()
		{
			label.setText("Parent" +  " > " + "Child");
		}
		
		private void initButtons()
		{
			homeButton = createButton("home", 1, new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					MainPage.get().setViewedFile(ExtrasDirectory.extrasFolderF);
				}
			});
			
			upButton = createButton("back_up", 2, new ActionListener()
			{
			public void actionPerformed(ActionEvent e)
			{
				MainPage.get().setViewedFile(previewFile.getParentFile());
			}
			});
			
			refreshButton = createButton("refresh", 3, new ActionListener()
			{
			public void actionPerformed(ActionEvent e)
			{
				MainPage.get().setViewedFile(previewFile);
			}
			});
			
			//---
			
			backButton = createButton("back", 1, new ActionListener()
			{
			public void actionPerformed(ActionEvent e)
			{
				FileListModel model = MainPage.get().getFlistmodel();
				int i = model.indexOf(previewFile);
				if(i == -1)
					return;
				File next = null;
				while(next == null || next.isDirectory())
				{
					i -= 1;
					if(i < 0) i = model.getSize() - 1;
					if(i == model.getSize()) i = 0;
					next = (File) model.get(i);
				}
				if(next != null)
					MainPage.get().setViewedFile(next);
			}
			});
			
			forwardButton = createButton("forward", 3, new ActionListener()
			{
			public void actionPerformed(ActionEvent e)
			{
				FileListModel model = MainPage.get().getFlistmodel();
				int i = model.indexOf(previewFile);
				if(i == -1)
					return;
				File next = null;
				while(next == null || next.isDirectory())
				{
					i += 1;
					if(i < 0) i = model.getSize() - 1;
					if(i == model.getSize()) i = 0;
					next = (File) model.get(i);
				}
				if(next != null)
					MainPage.get().setViewedFile(next);
			}
			});
			
			//---
			
			editButton = createButton("edit", 4, new ActionListener()
			{
			public void actionPerformed(ActionEvent e)
			{
				FileEditor.edit(previewFile);
			}
			});
		}
		
		public JButton createButton(String img, int buttonType, ActionListener l)
		{
			Dimension d = new Dimension(30, 23);
			GroupButton b = new GroupButton(buttonType);
	        b.disableEtching();
	        b.setIcon(Resources.loadIcon("nav/" + img + ".png"));
	        b.setTargetHeight(23);
	        b.setMargin(new Insets(0, 0, 0, 0));
	        b.setMinimumSize(d);
	        b.setPreferredSize(d);
	        b.setMaximumSize(d);
	        b.addActionListener(l);
	        
	        return b;
		}
	}
}
