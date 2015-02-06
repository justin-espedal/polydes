package stencyl.ext.polydes.points.comp;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class CyclingSpinnerListModel extends SpinnerNumberModel
{
	Object firstValue, lastValue;
	SpinnerModel linkedModel = null;
	
	public CyclingSpinnerListModel()
	{
		super();
		
		firstValue = 0;
		lastValue = 0;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setMaximum(Comparable maximum)
	{
		super.setMaximum(maximum);
		lastValue = maximum;
	}
	
	public void setLinkedModel(SpinnerModel linkedModel)
	{
		this.linkedModel = linkedModel;
	}

	@Override
	public Object getNextValue()
	{
		Object value = super.getNextValue();
		if (value == null)
		{
			value = firstValue;
		}
		return value;
	}

	@Override
	public Object getPreviousValue()
	{
		Object value = super.getPreviousValue();
		if (value == null)
		{
			value = lastValue;
		}
		return value;
	}
}