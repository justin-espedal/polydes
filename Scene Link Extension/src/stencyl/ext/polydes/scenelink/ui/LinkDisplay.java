package stencyl.ext.polydes.scenelink.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JWindow;

import stencyl.core.lib.scene.SceneModel;
import stencyl.ext.polydes.scenelink.data.Link;
import stencyl.ext.polydes.scenelink.data.LinkPageModel;


public class LinkDisplay extends JButton
{
	private Link link;
	private LinkReferenceChooser chooser;
	
	private ArrayList<ActionListener> listeners;
	
	public LinkDisplay(Link link, final JWindow owner)
	{
		this.link = link;
		
		listeners = new ArrayList<ActionListener>();
		
		updateText();
		
		super.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				chooser = new LinkReferenceChooser(owner, LinkDisplay.this.link);
				chooser.setLocationRelativeTo(null);
				chooser.setVisible(true);
				chooser.requestFocus();
				
				chooser.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						LinkDisplay.this.link = chooser.getLink();
						updateText();
						
						for(ActionListener l : listeners)
						{
							l.actionPerformed(new ActionEvent(this, 0, ""));
						}
					}
				});
			}
		});
	}
	
	@Override
	public void addActionListener(ActionListener l)
	{
		listeners.add(l);
	}
	
	@Override
	public void removeActionListener(ActionListener l)
	{
		listeners.remove(l);
	}
	
	public void updateText()
	{
		Object o = LinkDisplay.this.link.getModel();
		if(o instanceof SceneModel)
			setText("Scene: " + o);
		else if(o instanceof LinkPageModel)
			setText("Page: " + o);
		else
			setText("No link");
	}
	
	public Link getLink()
	{
		return chooser.getLink();
	}
}
