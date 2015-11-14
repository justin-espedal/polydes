package com.polydes.common.ui.propsheet;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.builtin.ResourceFolderType.ResourceFolderEditorBuilder;
import com.polydes.common.data.types.builtin.basic.ArrayType.ArrayEditorBuilder;
import com.polydes.common.data.types.builtin.basic.BoolType.BoolEditorBuilder;
import com.polydes.common.data.types.builtin.basic.DynamicType.DynamicEditorBuilder;
import com.polydes.common.data.types.builtin.basic.FloatType.FloatEditorBuilder;
import com.polydes.common.data.types.builtin.basic.IntType.IntEditorBuilder;
import com.polydes.common.data.types.builtin.basic.StringType.StringEditorBuilder;
import com.polydes.common.data.types.builtin.extra.SelectionType.SelectionEditorBuilder;
import com.polydes.common.data.types.builtin.extra.SetType.SetEditorBuilder;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport.FieldInfo;

public class PropertiesSheetBuilder
{
	private final PropertiesSheetSupport support;
	private final PropertiesSheetWrapper wrapper;
	private final PropertiesSheetStyle style;
	
	private FieldBuilder activeBuilder;
	
	public PropertiesSheetBuilder(PropertiesSheetSupport support, PropertiesSheetWrapper wrapper, PropertiesSheetStyle style)
	{
		this.support = support;
		this.wrapper = wrapper;
		this.style = style;
	}
	
	public PropertiesSheetBuilder header(String title)
	{
		wrapper.addHeader(title);
		return this;
	}
	
	public PropertiesSheetStyle getStyle()
	{
		return style;
	}
	
	public void createEditor(DataEditor<?> editor)
	{
		FieldInfo newField = new FieldInfo(activeBuilder.varname, activeBuilder.type, activeBuilder.label, activeBuilder.hint, activeBuilder.optional);
		wrapper.addField(newField, editor);
		support.fieldAdded(newField, editor);
	}
	
	public FieldBuilder field(String varname)
	{
		return activeBuilder = new FieldBuilder(varname);
	}
	
	public void finish()
	{
		activeBuilder = null;
		wrapper.finish();
	}
	
	public class FieldBuilder
	{
		private String varname;
		private DataType<?> type;
		private String label;
		private String hint;
		private boolean optional;
		
		public FieldBuilder(String varname)
		{
			this.varname = varname;
			type = null;
			label = varname;
			hint = null;
			optional = false;
		}
		
		public FieldBuilder label(String label)
		{
			this.label = label;
			return this;
		}
		
		public FieldBuilder hint(String hint)
		{
			this.hint = hint;
			return this;
		}
		
		public FieldBuilder optional()
		{
			this.optional = true;
			return this;
		}
		
		public ArrayEditorBuilder _array()
		{
			this.type = Types._Array;
			return Types._Array.new ArrayEditorBuilder();
		}
		
		public BoolEditorBuilder _boolean()
		{
			this.type = Types._Bool;
			return Types._Bool.new BoolEditorBuilder();
		}
		
		public DynamicEditorBuilder _dynamic()
		{
			this.type = Types._Dynamic;
			return Types._Dynamic.new DynamicEditorBuilder();
		}
		
		public FloatEditorBuilder _float()
		{
			this.type = Types._Float;
			return Types._Float.new FloatEditorBuilder();
		}
		
		public IntEditorBuilder _int()
		{
			this.type = Types._Int;
			return Types._Int.new IntEditorBuilder();
		}
		
		public StringEditorBuilder _string()
		{
			this.type = Types._String;
			return Types._String.new StringEditorBuilder();
		}
		
//		public ColorEditorBuilder _color()
//		{
//			this.type = Types._Color;
//			return Types._Color.new ColorEditorBuilder();
//		}
		
//		public ExtrasImageEditorBuilder _extrasImage()
//		{
//			this.type = Types._ExtrasImage;
//			return Types._ExtrasImage.new ExtrasImageEditorBuilder();
//		}
		
//		public IControlEditorBuilder _control()
//		{
//			this.type = Types._Control;
//			return Types._Control.new ControlEditorBuilder();
//		}
		
		public SelectionEditorBuilder _selection()
		{
			this.type = Types._Selection;
			return Types._Selection.new SelectionEditorBuilder();
		}
		
		public SetEditorBuilder _set()
		{
			this.type = Types._Set;
			return Types._Set.new SetEditorBuilder();
		}
		
		public ResourceFolderEditorBuilder _folder()
		{
			this.type = Types._ResourceFolder;
			return Types._ResourceFolder.new ResourceFolderEditorBuilder();
		}
		
		public DataEditorBuilder _editor(DataType<?> type)
		{
			this.type = type;
			return type.createEditorBuilder();
		}
	}
}
