package stencyl.ext.polydes.datastruct.data.structuregui;

public class GuiStructure
{
	//TODO: No node refreshing yet
//	@Override
//	public void propertyChange(PropertyChangeEvent e)
//	{
//		String def = getTemplate().name;
//		String prop = e.getPropertyName();
//		
//		if(def.equals("tab"))
//		{
//			if(prop.equals("label"))
//			{
//				StructureTab tab = gui.get();
//				tab.label = "" + e.getNewValue();
//				setName(tab.label);
//				sheet.lightRefreshNode(gui);
//			}
//		}
//		else if(def.equals("header"))
//		{
//			if(prop.equals("label"))
//			{
//				StructureHeader header = gui.get();
//				header.label = "" + e.getNewValue();
//				setName(header.label);
//				sheet.lightRefreshNode(gui);
//			}
//		}
//		else if(def.equals("field"))
//		{
//			StructureField f = gui.get();
//			if(prop.equals("name"))
//			{
//				sheet.model.getTemplate().setFieldName(f, "" + e.getNewValue());
//				setName(f.getName());
//			}
//			else if(prop.equals("label"))
//			{
//				f.setLabel("" + e.getNewValue());
//				sheet.lightRefreshNode(gui);
//			}
//			else if(prop.equals("hint"))
//			{
//				f.setHint("" + e.getNewValue());
//				if(e.getOldValue().equals("") || e.getNewValue().equals(""))
//					sheet.refreshNode(gui);
//				else
//					sheet.lightRefreshNode(gui);
//			}
//			else
//			{
//				StructureFieldEditor sfe = (StructureFieldEditor) editor;
//				if(prop.equals("type"))
//				{
//					sheet.model.getTemplate().setFieldType(f, sheet.model, (DataType<?>) e.getNewValue());
//					sfe.setType(((DataType<?>) e.getNewValue()).xml);
//				}
//				else if(prop.equals("optional"))
//					f.setOptional((Boolean) e.getNewValue());
//				else
//				{
//					//TODO this whole area will be rewritten
//					//f.optionalArgs.put(prop, e.getNewValue());
//				}
//				sheet.refreshNode(gui);
//			}
//		}
//		else if(def.equals("condition"))
//		{
//			setName("" + e.getNewValue());
//			sheet.lightRefreshNode(gui);
//			sheet.refreshVisibleComponents();
//		}
//	}
}