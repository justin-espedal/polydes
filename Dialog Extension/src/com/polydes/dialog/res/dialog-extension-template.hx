package PACKAGE;

import com.stencyl.Engine;
import dialog.core.*;

class CLASSNAME extends dialog.core.DialogExtension
{
	public function new()
	{
		super();
	}

	override public function setup(dg:DialogBox, style:Dynamic)
	{
		super.setup(dg, style);

		name = NAME;

		cmds =
		[
			"someCommand" => someFunction
		];
	}

	public function someFunction():Void
	{
	}
}