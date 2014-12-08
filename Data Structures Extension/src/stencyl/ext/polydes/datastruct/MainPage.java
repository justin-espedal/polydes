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

import stencyl.ext.polydes.common.comp.MiniSplitPane;
import stencyl.ext.polydes.common.ui.darktree.DarkTree;
import stencyl.ext.polydes.datastruct.ui.UIConsts;
import stencyl.ext.polydes.datastruct.ui.page.StructureDefinitionPage;
import stencyl.ext.polydes.datastruct.ui.page.StructureDefinitionsWindow;
import stencyl.ext.polydes.datastruct.ui.page.StructurePage;

public class MainPage extends JPanel
{
	private static MainPage _instance;
	private JPanel pageView;
	private MiniSplitPane splitPane;
	
	private JButton defButton;
	private final ActionListener openDefinitionsWindowAction = new ActionListener()
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
	};
	
	private MainPage()
	{
		super(new BorderLayout());
		
		add(splitPane = new MiniSplitPane(), BorderLayout.CENTER);
		
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(createSidebar());
		splitPane.setRightComponent(pageView = new JPanel(new BorderLayout()));
		splitPane.setDividerLocation(DarkTree.DEF_WIDTH);
		
		pageView.add(StructurePage.get(), BorderLayout.CENTER);
	}
	
	private JPanel createSidebar()
	{
		JPanel sidebar = new JPanel();
		sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
		
		JPanel bWrapper = new JPanel();
		bWrapper.setBackground(UIConsts.SIDEBAR_COLOR);
		
		defButton = new JButton("Open Structure Editor");
		defButton.addActionListener(openDefinitionsWindowAction);
		defButton.setBackground(null);
		
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
	
	public void dispose()
	{
		pageView.removeAll();
		splitPane.removeAll();
		defButton.removeActionListener(openDefinitionsWindowAction);
		removeAll();
	}
	
	public static void disposePages()
	{
		StructurePage.dispose();
		StructureDefinitionPage.dispose();
		StructureDefinitionsWindow.disposeWindow();
		if(_instance != null)
			_instance.dispose();
		_instance = null;
	}

	public void gameSaved()
	{
		revalidate();
		repaint();
	}
}