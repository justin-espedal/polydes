package com.polydes.common.data.types.builtin.extra;

//import static com.polydes.common.util.Lang.array;
//
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import javax.swing.JComponent;
//
//import com.polydes.common.comp.UpdatingCombo;
//import com.polydes.common.data.core.ExtrasImage;
//import com.polydes.common.data.types.DataEditor;
//import com.polydes.common.data.types.DataType;
//import com.polydes.common.data.types.ExtraProperties;
//import com.polydes.common.data.types.ExtrasMap;
//import com.polydes.common.ui.propsheet.PropertiesSheetStyle;
//
//public class ExtrasImageType extends DataType<ExtrasImage>
//{
//	public ExtrasImageType()
//	{
//		super(ExtrasImage.class);
//	}
//
//	@Override
//	public DataEditor<ExtrasImage> createEditor(ExtraProperties extras, PropertiesSheetStyle style)
//	{
//		return new ExtrasImageEditor();
//	}
//
//	@Override
//	public ExtrasImage decode(String s)
//	{
//		return Images.get().getImage(s);
//	}
//
//	@Override
//	public String encode(ExtrasImage i)
//	{
//		if(i == null)
//			return "";
//		
//		return i.name;
//	}
//	
//	@Override
//	public ExtrasImage copy(ExtrasImage t)
//	{
//		return t;
//	}
//	
//	@Override
//	public ExtraProperties loadExtras(ExtrasMap extras)
//	{
//		return null;
//	}
//	
//	@Override
//	public ExtrasMap saveExtras(ExtraProperties extras)
//	{
//		return null;
//	}
//	
//	public static class ExtrasImageEditor extends DataEditor<ExtrasImage>
//	{
//		UpdatingCombo<ExtrasImage> editor;
//		
//		public ExtrasImageEditor()
//		{
//			editor = new UpdatingCombo<ExtrasImage>(Images.get().getList(), null);
//			
//			editor.addActionListener(new ActionListener()
//			{
//				@Override
//				public void actionPerformed(ActionEvent e)
//				{
//					updated();
//				}
//			});
//		}
//		
//		@Override
//		public ExtrasImage getValue()
//		{
//			return editor.getSelected();
//		}
//
//		@Override
//		public void set(ExtrasImage t)
//		{
//			editor.setSelectedItem(t);
//		}
//
//		@Override
//		public JComponent[] getComponents()
//		{
//			return array(editor);
//		}
//		
//		@Override
//		public void dispose()
//		{
//			super.dispose();
//			editor.dispose();
//			editor = null;
//		}
//	}
//}