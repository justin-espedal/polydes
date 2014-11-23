package stencyl.ext.polydes.datastruct.data.structure;

import javax.swing.JPanel;

import stencyl.ext.polydes.datastruct.data.folder.EditableObject;
import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.ExtraProperties;
import stencyl.ext.polydes.datastruct.data.types.ExtrasMap;
import stencyl.ext.polydes.datastruct.ui.objeditors.StructureFieldPanel;
import stencyl.ext.polydes.datastruct.ui.table.PropertiesSheetStyle;

public class StructureField extends EditableObject
{
	private String varname;
	private DataType<?> type;
	private String label;
	private String hint;
	private boolean optional;
	private ExtraProperties extras;
	
	public StructureField(String varname, DataType<?> type, String label, String hint, boolean optional, ExtrasMap extras)
	{
		this.varname = varname;
		this.type = type;
		this.label = label;
		this.hint = hint;
		this.optional = optional;
		if(type != null)
			this.extras = type.loadExtras(extras);
	}
	
	public void loadExtras(ExtrasMap extras)
	{
		this.extras = type.loadExtras(extras);
	}
	
	public ExtraProperties getExtras()
	{
		return extras;
	}
	
	public void setExtras(ExtraProperties extras)
	{
		this.extras = extras;
	}
	
	public String getHint()
	{
		return hint;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public String getVarname()
	{
		return varname;
	}
	
	public DataType<?> getType()
	{
		return type;
	}
	
	public boolean isOptional()
	{
		return optional;
	}
	
	public void setHint(String hint)
	{
		this.hint = hint;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public void setVarname(String varname)
	{
		this.varname = varname;
	}
	
	public void setOptional(boolean optional)
	{
		this.optional = optional;
	}
	
	public void setType(DataType<?> type)
	{
		this.type = type;
		if(editor != null)
			type.applyToFieldPanel(editor);
	}
	
	@Override
	public String toString()
	{
		return varname + ":" + type;
	}
	
	private StructureFieldPanel editor;
	
	@Override
	public JPanel getEditor()
	{
		if(editor == null)
		{
			editor = new StructureFieldPanel(this, PropertiesSheetStyle.LIGHT);
			type.applyToFieldPanel(editor);
		}
		
		return editor;
	}
	
	@Override
	public void disposeEditor()
	{
		editor.dispose();
	}
	
	@Override
	public void revertChanges()
	{
		editor.revert();
	}
}