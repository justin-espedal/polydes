package stencyl.ext.polydes.datastruct;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import stencyl.ext.polydes.datastruct.data.types.DataType;
import stencyl.ext.polydes.datastruct.data.types.Types;
import stencyl.sw.editors.snippet.designer.AttributeType;
import stencyl.sw.editors.snippet.designer.Block;
import stencyl.sw.editors.snippet.designer.Block.BlockType;
import stencyl.sw.editors.snippet.designer.BlockTheme;
import stencyl.sw.editors.snippet.designer.Definition;
import stencyl.sw.editors.snippet.designer.Definition.Category;
import stencyl.sw.editors.snippet.designer.Definitions;
import stencyl.sw.editors.snippet.designer.codebuilder.CodeBuilder;
import stencyl.sw.editors.snippet.designer.codemap.BasicCodeMap;
import stencyl.sw.editors.snippet.designer.dropdown.CodeConverter;
import stencyl.sw.editors.snippet.designer.dropdown.DropdownData;
import stencyl.sw.editors.snippet.designer.dropdown.DropdownData.DropdownConverter;

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
			new AttributeType[] { AttributeType.OBJECT, AttributeType.TEXT, AttributeType.OBJECT },
			new BasicCodeMap("Reflect.setField(~, ~, ~);"),
			spec,
			BlockType.ACTION,
			AttributeType.VOID
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
			new AttributeType[] { AttributeType.OBJECT, AttributeType.TEXT },
			new BasicCodeMap("Reflect.field(~, ~)"),
			spec,
			BlockType.NORMAL,
			AttributeType.OBJECT
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
			new AttributeType[] { AttributeType.TEXT, AttributeType.TEXT, AttributeType.OBJECT },
			new BasicCodeMap("Reflect.setField(DataStructures.get(~), ~, ~);"),
			spec,
			BlockType.ACTION,
			AttributeType.VOID
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
			new AttributeType[] { AttributeType.TEXT, AttributeType.TEXT },
			new BasicCodeMap("Reflect.field(DataStructures.get(~), ~)"),
			spec,
			BlockType.NORMAL,
			AttributeType.OBJECT
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
			new AttributeType[] { AttributeType.TEXT },
			new BasicCodeMap("DataStructures.get(~)"),
			spec,
			BlockType.NORMAL,
			AttributeType.OBJECT
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
			new CodeConverter()
			{
				@Override
				public void toCode(CodeBuilder builder, int dropdownID, Block b, int index, Object o)
				{
					try
					{
						builder.append(codeTexts[index]);
					}
	
					catch(ArrayIndexOutOfBoundsException e)
					{
						log.error(e.getMessage(), e);
						builder.error();
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
