/*
 * http://www.formdev.com/blog/swing-tip-jsplitpane-with-zero-size-divider/
 * 
 * Copyright (c) <YEAR>, <OWNER>
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package stencyl.ext.polydes.paint.app;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

@SuppressWarnings("serial")
public class MiniSplitPane extends JSplitPane
{
	private int dividerDragSize = 7;
	private int dividerDragOffset = 3;

	public MiniSplitPane()
	{
		setBorder(null);
		setDividerSize(1);
		setContinuousLayout(true);
	}

	@Override
	public void doLayout()
	{
		super.doLayout();

		// increase divider width or height
		BasicSplitPaneDivider divider = ((BasicSplitPaneUI) getUI())
				.getDivider();
		Rectangle bounds = divider.getBounds();
		if (orientation == HORIZONTAL_SPLIT)
		{
			bounds.x -= dividerDragOffset;
			bounds.width = dividerDragSize;
		}
		else
		{
			bounds.y -= dividerDragOffset;
			bounds.height = dividerDragSize;
		}
		divider.setBounds(bounds);
	}

	@Override
	public void updateUI()
	{
		setUI(new SplitPaneWithZeroSizeDividerUI());
		revalidate();
	}

	private class SplitPaneWithZeroSizeDividerUI extends BasicSplitPaneUI
	{
		@Override
		public BasicSplitPaneDivider createDefaultDivider()
		{
			return new ZeroSizeDivider(this);
		}
	}

	private class ZeroSizeDivider extends BasicSplitPaneDivider
	{
		public ZeroSizeDivider(BasicSplitPaneUI ui)
		{
			super(ui);
			super.setBorder(null);
			//setBackground(UIManager.getColor("controlShadow"));
		}

		@Override
		public void setBorder(Border border)
		{
			// ignore
		}

		private Color c = new Color(0x2d2d2d);
		
		@Override
		public void paint(Graphics g)
		{
			g.setColor(c);
			if (orientation == HORIZONTAL_SPLIT)
				g.drawLine(dividerDragOffset, 0, dividerDragOffset, getHeight() - 1);
			else
				g.drawLine(0, dividerDragOffset, getWidth() - 1, dividerDragOffset);
		}

		@Override
		protected void dragDividerTo(int location)
		{
			super.dragDividerTo(location + dividerDragOffset);
		}

		@Override
		protected void finishDraggingTo(int location)
		{
			super.finishDraggingTo(location + dividerDragOffset);
		}
	}
}