package stencyl.ext.polydes.datastruct.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import stencyl.sw.SW;
import stencyl.sw.lnf.Theme;
import stencyl.sw.loc.LanguagePack;
import stencyl.sw.util.Util;
import stencyl.sw.util.comp.ButtonBarFactory;
import stencyl.sw.util.comp.GroupButton;
import stencyl.sw.util.dg.StencylDialog;

public class MiniDialog extends StencylDialog
{
	private static final LanguagePack lang = LanguagePack.get();
	
	public boolean canceled;
	
	public MiniDialog(JComponent content, String title, int width, int height)
	{
		super(SW.get(), title, width, height, false, false);

		if(!Util.isMacOSX())
		{
			setBackground(Theme.APP_COLOR);
		}
		
		add(content, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	public Object getResult()
	{
		return null;
	}

	@Override
	public void cancel()
	{
		canceled = true;
		setVisible(false);
	}

	@Override
	public JComponent createContentPanel()
	{
		return null;
	}

	@Override
	public JPanel createButtonPanel()
	{
		JButton okButton = new GroupButton(0);
		JButton cancelButton = new GroupButton(0);

		okButton.setAction
		(
			new AbstractAction(lang.get("globals.ok"))
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					canceled = false;
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
			new JButton[] {okButton, cancelButton},
			0,
			false
		);
	}
}