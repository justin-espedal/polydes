package com.polydes.common.nodes;

import javax.swing.JPanel;

import com.polydes.common.ui.filelist.BranchPage;
import com.polydes.common.ui.object.EditableObject;
import com.polydes.common.ui.object.ViewableObject;

public class DefaultViewableBranch extends DefaultBranch implements ViewableObject
{
	public DefaultViewableBranch(String name, EditableObject object)
	{
		super(name, object);
	}

	public DefaultViewableBranch(String name)
	{
		super(name);
	}
	
	private BranchPage<DefaultLeaf, DefaultBranch> view;
	
	@SuppressWarnings("unchecked")
	@Override
	public JPanel getView()
	{
		if(view == null)
		{
			HierarchyModel<DefaultLeaf,DefaultBranch> rootModel =
				(HierarchyModel<DefaultLeaf,DefaultBranch>) HierarchyModel.rootModels.get(NodeUtils.getRoot(this));
			view = new BranchPage<DefaultLeaf,DefaultBranch>(this, rootModel);
		}
		return view;
	}
	
	@Override
	public void disposeView()
	{
		if(view != null)
			view.dispose();
		view = null;
	}

	@Override
	public boolean fillsViewHorizontally()
	{
		return true;
	}
}