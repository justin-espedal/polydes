package com.polydes.datastruct.ui.page;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.types.Types;
import com.polydes.datastruct.res.Resources;
import com.polydes.datastruct.ui.UIConsts;
import com.polydes.datastruct.ui.utils.ImageImporter;
import com.polydes.datastruct.ui.utils.Layout;

import stencyl.sw.ext.FileHandler;
import stencyl.sw.lnf.Theme;
import stencyl.sw.util.VerificationHelper;
import stencyl.sw.util.comp.ButtonBarFactory;
import stencyl.sw.util.comp.GroupButton;
import stencyl.sw.util.comp.text.AutoVerifyField;
import stencyl.sw.util.comp.text.FieldVerifier;
import stencyl.sw.util.dg.DialogPanel;
import stencyl.sw.util.dg.StencylDialog;

public class CreateStructureDefinitionDialog extends StencylDialog
{
	FieldVerifier nameVerifier;
	FieldVerifier classVerifier;
	FieldVerifier packageVerifier;
	
	AutoVerifyField nameField;
	AutoVerifyField classField;
	AutoVerifyField packageField;
	
	JButton okButton;
	
	boolean nameOk;
	boolean classOk;
	boolean packageOk;
	
	BufferedImage iconImg;
	
	public StructureDefinition newDef;
	public StructureDefinition editDef;
	
	public CreateStructureDefinitionDialog()
	{
		super(StructureDefinitionsWindow.get(), "Create New Structure", 450, 370, true, true, true);
	}
	
	@Override
	public JComponent createContentPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Theme.LIGHT_BG_COLOR);
		panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Theme.DIALOG_BORDER));
		
		DialogPanel dp = new DialogPanel(Theme.LIGHT_BG_COLOR);
		
		nameOk = true;
		classOk= true;
		packageOk = true;
		
		nameVerifier = new FieldVerifier()
		{
			@Override
			public boolean verifyText(JTextField field, String text)
			{
				nameOk = (editDef != null && editDef.getName().equals(text)) ||
						(isValidName(text) && !Types.typeFromXML.containsKey(text));
				verify();
				
				return nameOk;
			}
		};
		
		classVerifier = new FieldVerifier()
		{
			@Override
			public boolean verifyText(JTextField field, String text)
			{
				classOk = VerificationHelper.isValidClassName(text);
				verify();
				
				return classOk;
			}
		};
		
		packageVerifier = new FieldVerifier()
		{
			@Override
			public boolean verifyText(JTextField field, String text)
			{
				packageOk = VerificationHelper.isValidPackageName(text);
				verify();
				
				return packageOk;
			}
		};
		
		nameField = new AutoVerifyField(nameVerifier, "");
		classField = new AutoVerifyField(classVerifier, "");
		packageField = new AutoVerifyField(packageVerifier, "");
		
		dp.startBlock();
		dp.addHeader("New Structure");
		dp.addGenericRow("Name", nameField);
		dp.addGenericRow("Class", classField);
		dp.addGenericRow("Package", packageField);
		
		final JLabel iconLabel = new JLabel()
		{
			@Override
			protected void paintComponent(Graphics g)
			{
				g.setColor(Theme.LIGHT_BG_COLOR);
				g.fillRect(0, 0, getWidth(), getHeight());
				
				g.setColor(UIConsts.SIDEBAR_COLOR);
				g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				
				g.drawImage(iconImg, 16, 16, 32, 32, null);
			}
		};
		iconLabel.setBackground(null);
		iconLabel.setOpaque(true);
		iconLabel.setMinimumSize(new Dimension(64, 64));
		iconLabel.setPreferredSize(new Dimension(64, 64));
		iconLabel.setMaximumSize(new Dimension(64, 64));
		
		try
		{
			iconImg = ImageIO.read(Resources.getUrlStream("newStructureDef.png"));
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		GroupButton button = new GroupButton(4);
		button.setText("Choose an Image");
		button.addActionListener(new ImageImporter(new FileHandler()
		{
			@Override
			public void handleFile(File f)
			{
				try
				{
					iconImg = ImageIO.read(f);
					iconLabel.repaint();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		}));
		
		dp.addGenericRow("Icon", Layout.horizontalBox(button, iconLabel));
		dp.addDescriptionRow("Best size: 32x32");
		
		dp.finishBlock();
		
		packageField.getTextField().setText("scripts.ds");
		
		panel.add(dp, BorderLayout.CENTER);

		return panel;
	}
	
	public void setNodeName(String text)
	{
		nameField.getTextField().setText(text);
		classField.getTextField().setText(StringUtils.deleteWhitespace(text));
		verify();
		setVisible(true);
	}
	
	public void setDefinition(StructureDefinition def)
	{
		title.setTitle("Edit Structure");
		
		editDef = def;
		nameField.getTextField().setText(def.getName());
		int lastDot = def.getClassname().lastIndexOf('.');
		classField.getTextField().setText(def.getClassname().substring(lastDot + 1));
		packageField.getTextField().setText(def.getClassname().substring(0, lastDot));
		iconImg = editDef.getIconImg();
		
		verify();
		setVisible(true);
	}
	
	@Override
	public JPanel createButtonPanel()
	{
		okButton = new GroupButton(0);

		okButton.setAction(new AbstractAction("OK")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ok();
				setVisible(false);
			}
		});

		AbstractButton cancelButton = new GroupButton(0);

		cancelButton.setAction(new AbstractAction("Cancel")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				cancel();
			}
		});

		verify();
		
		return ButtonBarFactory.createButtonBar
				(
					this,
					new AbstractButton[] { okButton, cancelButton },
					0
				);
	}
	
	private void verify()
	{
		if(okButton != null)
			okButton.setEnabled(nameOk && classOk && packageOk);
	}
	
	private void ok()
	{
		if(editDef != null)
		{
			if(!editDef.getName().equals(nameField.getText()))
				editDef.setName(nameField.getText());
			editDef.setClassname(packageField.getText() + "." + classField.getText());
			if(editDef.getIconImg() != iconImg)
				editDef.setImage(iconImg);
			
			JLabel previewLabel = editDef.getEditor().getPreview().getEditor().label;
			previewLabel.setText(editDef.getName());
			previewLabel.setIcon(editDef.getMediumIcon());
			editDef.setDirty(true);
		}
		else
		{
			String nodeName = nameField.getText();
			String className = packageField.getText() + "." + classField.getText();
			newDef = new StructureDefinition(nodeName, className);
			newDef.setImage(iconImg);
		}
	}
	
	@Override
	public void cancel()
	{
		editDef = null;
		newDef = null;
		setVisible(false);
	}
	
	@Override
	public void dispose()
	{		
		nameField = null;
		classField = null;
		packageField = null;
		
		nameVerifier = null;
		classVerifier = null;
		packageVerifier = null;
		
		okButton = null;
		newDef = null;
		editDef = null;
		
		super.dispose();
	}
	
	private boolean isValidName(String text)
	{
		if(text.isEmpty())
			return false;
		return
			StringUtils.isAlpha("" + text.charAt(0)) &&
			/*!text.endsWith(" ") &&*/
			/*!StringUtils.contains(text, "[ ]{2,}") &&*/
			VerificationHelper.isAlphanumericUnderscoreSpace(text);
	}
}
