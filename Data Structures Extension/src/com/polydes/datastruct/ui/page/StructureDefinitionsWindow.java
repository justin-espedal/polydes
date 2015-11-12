package com.polydes.datastruct.ui.page;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import com.polydes.common.comp.MiniSplitPane;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.Prefs;
import com.polydes.datastruct.data.structure.StructureDefinition;

import stencyl.sw.SW;

public class StructureDefinitionsWindow extends JDialog
{
	private static StructureDefinitionsWindow _instance;
	
	public static StructureDefinitionsWindow get()
	{
		if(_instance == null)
			_instance = new StructureDefinitionsWindow();
		
		return _instance;
	}
	
	private MiniSplitPane splitPane;
	private JPanel contents;
	private boolean initialized;
	private PropertiesWindow propsWindow;
	
	public StructureDefinitionsWindow()
	{
		super(SW.get(), "Structure Editor", true);
		
		propsWindow = new PropertiesWindow(this);
		
		contents = new JPanel(new BorderLayout());
		
		contents.add(splitPane = new MiniSplitPane(), BorderLayout.CENTER);
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		
		setContentPane(contents);
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				/*
				int result =
					UI.showYesNoCancelPrompt(
						"Apply Changes",
						"Would you like to apply or discard the changes to your structures?",
						"Apply", "Discard", "Cancel"
					);
				
				if(UI.choseYes(result))
				{
					for(StructureDefinition def : StructureDefinitions.defMap.values())
						def.update();
					StructurePage.get().refreshSelected();
					
					closeWindow();
				}
				else if(UI.choseNo(result))
				{
					for(StructureDefinition def : StructureDefinitions.defMap.values())
						def.revertChanges();
					
					closeWindow();
				}
				else
				{
					
				}
				*/
				
				for(StructureDefinition def : DataStructuresExtension.get().getStructureDefinitions().values())
					def.update();
				StructurePage.get().refreshSelected();
				
				closeWindow();
			}
		});
		
		setVisible(false);
		initialized = false;
	}
	
	private void closeWindow()
	{
		Prefs.DEFPAGE_X =  getX();
		Prefs.DEFPAGE_Y = getY();
		Prefs.DEFPAGE_WIDTH = getWidth();
		Prefs.DEFPAGE_HEIGHT = getHeight();
		Prefs.DEFPAGE_SIDEWIDTH = StructureDefinitionPage.get().splitPane.getDividerLocation();
		Prefs.DEFPAGE_SIDEDL = ((MiniSplitPane) StructureDefinitionPage.get().getSidebar()).getDividerLocation();
		
		setVisible(false);
		
		StructureDefinitionPage.get().selectNone();
		propsWindow.setObject(null);
		propsWindow.setVisible(false);
		for(StructureDefinition def : DataStructuresExtension.get().getStructureDefinitions().values())
			def.disposeEditor();
	}
	
	public PropertiesWindow getPropsWindow()
	{
		return propsWindow;
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		if(!initialized && visible)
			init();
		
		super.setVisible(visible);
	}
	
	private void init()
	{
		initialized = true;
		
		splitPane.setLeftComponent(StructureDefinitionPage.get().getSidebar());
		splitPane.setRightComponent(StructureDefinitionPage.get());
		splitPane.setDividerLocation(Prefs.DEFPAGE_SIDEDL);
		
		setSize(Prefs.DEFPAGE_WIDTH, Prefs.DEFPAGE_HEIGHT);
		
		int x = Prefs.DEFPAGE_X;
		int y = Prefs.DEFPAGE_Y;
		
		if(x == -1 || y == -1)
			setLocationRelativeTo(SW.get());
		else
			setLocation(x, y);
	}
	
	@Override
	public void dispose()
	{
		splitPane.removeAll();
		contents.removeAll();
		propsWindow.dispose();
		
		super.dispose();
	}
	
	public static void disposeWindow()
	{
		if(_instance != null)
		{
			_instance.dispose();
			_instance = null;
			for(StructureDefinition def : DataStructuresExtension.get().getStructureDefinitions().values())
				def.disposeEditor();
		}
	}
}
