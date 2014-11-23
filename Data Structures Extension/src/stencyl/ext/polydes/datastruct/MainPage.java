package stencyl.ext.polydes.datastruct;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import stencyl.ext.polydes.datastruct.ui.MiniSplitPane;
import stencyl.ext.polydes.datastruct.ui.UIConsts;
import stencyl.ext.polydes.datastruct.ui.page.StructureDefinitionPage;
import stencyl.ext.polydes.datastruct.ui.page.StructureDefinitionsWindow;
import stencyl.ext.polydes.datastruct.ui.page.StructurePage;
import stencyl.ext.polydes.datastruct.ui.tree.DTree;

public class MainPage extends JPanel
{
	private static MainPage _instance;
	private JPanel pageView;
	private MiniSplitPane splitPane;
	
	private MainPage()
	{
		super(new BorderLayout());
		
		add(splitPane = new MiniSplitPane(), BorderLayout.CENTER);
		
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(createSidebar());
		splitPane.setRightComponent(pageView = new JPanel(new BorderLayout()));
		splitPane.setDividerLocation(DTree.DEF_WIDTH);
		
		pageView.add(StructurePage.get(), BorderLayout.CENTER);
	}
	
	private JPanel createSidebar()
	{
		JPanel sidebar = new JPanel();
		sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
		
		JPanel bWrapper = new JPanel();
		bWrapper.setBackground(UIConsts.SIDEBAR_COLOR);
		
		JButton defButton = new JButton("Open Structure Editor");
		defButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				StructureDefinitionsWindow.get();
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						StructureDefinitionsWindow.get().setVisible(true);
					}
				});
			}
		});
		
		bWrapper.add(defButton);
		bWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		sidebar.add(bWrapper);
		sidebar.add(StructurePage.get().getSidebar());
		
		return sidebar;
	}
	
	public static MainPage get()
	{
		if(_instance == null)
			_instance = new MainPage();
		
		return _instance;
	}
	
	public static void disposePages()
	{
		StructurePage.dispose();
		StructureDefinitionPage.dispose();
		StructureDefinitionsWindow.disposeWindow();
		_instance = null;
	}

	public void gameSaved()
	{
		revalidate();
		repaint();
	}
}