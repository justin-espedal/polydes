package stencyl.ext.polydes.datastruct;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.sw.editors.snippet.designer.Block;
import stencyl.sw.editors.snippet.designer.Block.BlockType;
import stencyl.sw.editors.snippet.designer.BlockTheme;
import stencyl.sw.editors.snippet.designer.Definition;
import stencyl.sw.editors.snippet.designer.Definition.Category;
import stencyl.sw.editors.snippet.designer.Definition.Type;
import stencyl.sw.editors.snippet.designer.Definitions;
import stencyl.sw.editors.snippet.designer.codemap.BasicCodeMap;
import stencyl.sw.editors.snippet.designer.codemap.CodeMap;
import stencyl.sw.editors.snippet.designer.dropdown.DropdownData;
import stencyl.sw.editors.snippet.designer.dropdown.DropdownData.DropdownConverter;
import stencyl.sw.editors.snippet.designer.dropdown.JavaConverter;

public class Blocks
{
	private static final Logger log = Logger.getLogger(Blocks.class);
	
	public static ArrayList<String> tagCache = new ArrayList<String>();
	
	public static void addDesignModeBlocks()
	{
//		set [propname] for [object] to [value]		object.prop = value;
		
		String spec = "set %1 for %0 to %2";
		
		Definition blockDef = new Definition
		(
			Category.CUSTOM,
			"ds-set-prop1",
			new Type[] { Type.OBJECT, Type.TEXT, Type.OBJECT },
			new BasicCodeMap().setCode(CodeMap.HX, "Reflect.setField(~, ~, ~);"),
			spec,
			BlockType.ACTION,
			Type.VOID
		);
		
		blockDef.guiTemplate = spec;
		blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");
		
		Definitions.get().put(blockDef.tag, blockDef);
		tagCache.add(blockDef.tag);

//		get [propname] for [object]					object.prop
		
		spec = "get %1 from %0";
		
		blockDef = new Definition
		(
			Category.CUSTOM,
			"ds-get-prop1",
			new Type[] { Type.OBJECT, Type.TEXT },
			new BasicCodeMap().setCode(CodeMap.HX, "Reflect.field(~, ~)"),
			spec,
			BlockType.NORMAL,
			Type.OBJECT
		);
		
		blockDef.guiTemplate = spec;
		blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");
		
		Definitions.get().put(blockDef.tag, blockDef);
		tagCache.add(blockDef.tag);
		
//		set [propname] for [objectname] to [value]	DataStructures.get(objectname).propname = value;
		
		spec = "set %1 for %0 to %2";
		
		blockDef = new Definition
		(
			Category.CUSTOM,
			"ds-set-prop2",
			new Type[] { Type.TEXT, Type.TEXT, Type.OBJECT },
			new BasicCodeMap().setCode(CodeMap.HX, "Reflect.setField(DataStructures.get(~), ~, ~);"),
			spec,
			BlockType.ACTION,
			Type.VOID
		);
		
		blockDef.guiTemplate = spec;
		blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");
		
		Definitions.get().put(blockDef.tag, blockDef);
		tagCache.add(blockDef.tag);
		
//		get [propname] for [objectname]				DataStructures.get(objectname).propname
		
		spec = "get %1 from %0";
		
		blockDef = new Definition
		(
			Category.CUSTOM,
			"ds-get-prop2",
			new Type[] { Type.TEXT, Type.TEXT },
			new BasicCodeMap().setCode(CodeMap.HX, "Reflect.field(DataStructures.get(~), ~)"),
			spec,
			BlockType.NORMAL,
			Type.OBJECT
		);
		
		blockDef.guiTemplate = spec;
		blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");
		
		Definitions.get().put(blockDef.tag, blockDef);
		tagCache.add(blockDef.tag);
		
//		transformer setter: set insets for window to [(top, bottom, left, right)]
//		transformer getter: top of [get insets for window]
		
		spec = "get data with name: %0";
		
		blockDef = new Definition
		(
			Category.CUSTOM,
			"ds-get-data",
			new Type[] { Type.TEXT },
			new BasicCodeMap().setCode(CodeMap.HX, "DataStructures.get(~)"),
			spec,
			BlockType.NORMAL,
			Type.OBJECT
		);
		
		blockDef.guiTemplate = spec;
		blockDef.customBlockTheme = BlockTheme.THEMES.get("blue");
		
		Definitions.get().put(blockDef.tag, blockDef);
		tagCache.add(blockDef.tag);
		
		for(DataType<?> type : Types.typeFromXML.values())
		{
			ArrayList<Definition> blocks = type.getBlocks();
			if(blocks != null)
			{
				for(Definition def : blocks)
				{
					Definitions.get().put(def.tag, def);
					tagCache.add(def.tag);
				}
			}
		}
	}
	
	public static DropdownData createGenericDropdown(final String[] phrases, final String[] codeTexts)
	{
		return new DropdownData
		(
			new DropdownConverter()
			{
				@Override
				public int getIDForItem(Object o)
				{
					return -1;
				}
				
				@Override
				public Object[] getItems()
				{
					return phrases;
				}
			},
			new JavaConverter()
			{
				@Override
				public String toJava(int dropdownID, Block b, int index, Object o, String language)
				{
					try
					{
						return codeTexts[index];
					}
	
					catch(ArrayIndexOutOfBoundsException e)
					{
						log.error(e.getMessage(), e);
						return Definition.ERROR;
					}
				}
			}
		);
	}
	
	public static void dispose()
	{
		for(String tag : tagCache)
			Definitions.get().remove(tag);
		tagCache.clear();
	}
}
