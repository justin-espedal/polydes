package com.polydes.extrasmanager.app.list;

import com.polydes.extrasmanager.data.folder.SysFolder;

public interface FileListModelListener
{
	public void viewUpdated(FileListModel src, SysFolder currView);
}
