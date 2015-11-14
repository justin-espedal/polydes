package com.polydes.common.ui.propsheet;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport.FieldInfo;

public interface PropertiesSheetWrapper
{
	void addField(FieldInfo newField, DataEditor<?> editor);
	void addHeader(String title);
	void finish();
	void dispose();
}
