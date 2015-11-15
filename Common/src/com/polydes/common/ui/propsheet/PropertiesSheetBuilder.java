package com.polydes.common.ui.propsheet;

import java.util.Collection;
import java.util.regex.Pattern;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.Types;
import com.polydes.common.data.types.UpdateListener;
import com.polydes.common.data.types.builtin.CollectionObjectType.CollectionObjectEditorBuilder;
import com.polydes.common.data.types.builtin.FilePathType.FilePathEditorBuilder;
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
	
	private Mode mode;
	private FieldBuilder activeBuilder;
	
	// https://github.com/mfornos/humanize/blob/master/humanize-slim/src/main/java/humanize/util/Constants.java
	public static final Pattern SPLIT_CAMEL = Pattern.compile("(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])");
	
	public PropertiesSheetBuilder(PropertiesSheetSupport support, PropertiesSheetWrapper wrapper, PropertiesSheetStyle style)
	{
		this.support = support;
		this.wrapper = wrapper;
		this.style = style;
		mode = null;
	}

	private enum Mode
	{
		BUILD,
		CHANGE
	}
	
	public PropertiesSheetBuilder startBuilding()
	{
		mode = Mode.BUILD;
		return this;
	}
	
	public PropertiesSheetBuilder startChanging()
	{
		mode = Mode.CHANGE;
		return this;
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
		if(mode != Mode.BUILD)
			throw new IllegalStateException();
		
		FieldInfo newField = new FieldInfo(activeBuilder.varname, activeBuilder.type, activeBuilder.label, activeBuilder.hint, activeBuilder.optional);
		wrapper.addField(newField, editor);
		support.fieldAdded(newField, editor);
	}
	
	public void doChange(DataEditor<?> editor)
	{
		if(mode != Mode.CHANGE)
			throw new IllegalStateException();
		
		FieldInfo newField = new FieldInfo(activeBuilder.varname, activeBuilder.type, activeBuilder.label, activeBuilder.hint, activeBuilder.optional);
		wrapper.changeField(activeBuilder.varname, newField, editor);
		support.changeField(activeBuilder.varname, newField, editor);
	}
	
	public PropertiesSheetBuilder onUpdate(UpdateListener l)
	{
		support.getField(activeBuilder.varname).getEditor().addListener(l);
		return this;
	}
	
	public FieldBuilder field(String varname)
	{
		switch(mode)
		{
			case BUILD:
				return activeBuilder = new FieldBuilder(varname, this);
			case CHANGE:
				return activeBuilder = FieldBuilder.fromFieldInfo(support.getField(varname), this);
			default:
				throw new IllegalStateException();
		}
	}
	
	public void finish()
	{
		if(mode == Mode.BUILD)
			wrapper.finish();
		activeBuilder = null;
		mode = null;
	}
	
	public static class FieldBuilder
	{
		private String varname;
		private DataType<?> type;
		private String label;
		private String hint;
		private boolean optional;
		
		private PropertiesSheetBuilder sheetBuilder;
		
		public static FieldBuilder fromFieldInfo(FieldInfo info, PropertiesSheetBuilder sheetBuilder)
		{
			FieldBuilder builder = new FieldBuilder(info.getVarname(), sheetBuilder);
			builder.type = info.getType();
			builder.label = info.getLabel();
			builder.hint = info.getHint();
			builder.optional = info.isOptional();
			return builder;
		}
		
		public FieldBuilder(String varname, PropertiesSheetBuilder sheetBuilder)
		{
			this.varname = varname;
			type = null;
			label = SPLIT_CAMEL.matcher(varname).replaceAll(" ");
			hint = null;
			optional = false;
			
			this.sheetBuilder = sheetBuilder;
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
			return Types._Array.new ArrayEditorBuilder().setSheetBuilder(sheetBuilder);
		}
		
		public BoolEditorBuilder _boolean()
		{
			this.type = Types._Bool;
			return Types._Bool.new BoolEditorBuilder().setSheetBuilder(sheetBuilder);
		}
		
		public DynamicEditorBuilder _dynamic()
		{
			this.type = Types._Dynamic;
			return Types._Dynamic.new DynamicEditorBuilder().setSheetBuilder(sheetBuilder);
		}
		
		public FloatEditorBuilder _float()
		{
			this.type = Types._Float;
			return Types._Float.new FloatEditorBuilder().setSheetBuilder(sheetBuilder);
		}
		
		public IntEditorBuilder _int()
		{
			this.type = Types._Int;
			return Types._Int.new IntEditorBuilder().setSheetBuilder(sheetBuilder);
		}
		
		public StringEditorBuilder _string()
		{
			this.type = Types._String;
			return Types._String.new StringEditorBuilder().setSheetBuilder(sheetBuilder);
		}
		
//		public ColorEditorBuilder _color()
//		{
//			this.type = Types._Color;
//			return Types._Color.new ColorEditorBuilder().setSheetBuilder(sheetBuilder);
//		}
		
//		public ExtrasImageEditorBuilder _extrasImage()
//		{
//			this.type = Types._ExtrasImage;
//			return Types._ExtrasImage.new ExtrasImageEditorBuilder().setSheetBuilder(sheetBuilder);
//		}
		
//		public IControlEditorBuilder _control()
//		{
//			this.type = Types._Control;
//			return Types._Control.new ControlEditorBuilder().setSheetBuilder(sheetBuilder);
//		}
		
		public SelectionEditorBuilder _selection()
		{
			this.type = Types._Selection;
			return Types._Selection.new SelectionEditorBuilder().setSheetBuilder(sheetBuilder);
		}
		
		public SetEditorBuilder _set()
		{
			this.type = Types._Set;
			return Types._Set.new SetEditorBuilder().setSheetBuilder(sheetBuilder);
		}
		
		public ResourceFolderEditorBuilder _folder()
		{
			this.type = Types._ResourceFolder;
			return Types._ResourceFolder.new ResourceFolderEditorBuilder().setSheetBuilder(sheetBuilder);
		}
		
		public DataEditorBuilder _enum(Class<? extends Enum<?>> cls)
		{
			this.type = Types._Enum;
			return Types._Enum.new EnumEditorBuilder().type(cls).setSheetBuilder(sheetBuilder);
		}

		public CollectionObjectEditorBuilder _collection(Collection<?> values)
		{
			this.type = Types._Collection;
			return Types._Collection.new CollectionObjectEditorBuilder().source(values).setSheetBuilder(sheetBuilder);
		}

		public FilePathEditorBuilder _filePath()
		{
			this.type = Types._FilePath;
			return Types._FilePath.new FilePathEditorBuilder().setSheetBuilder(sheetBuilder);
		}
		
		public DataEditorBuilder _editor(DataType<?> type)
		{
			this.type = type;
			return type.createEditorBuilder().setSheetBuilder(sheetBuilder);
		}
	}
}
