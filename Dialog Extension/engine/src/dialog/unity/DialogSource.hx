package dialog.unity;

import dialog.core.*;
import dialog.unity.compat.*;
import dialog.unity.ds.*;
import unityengine.*;

using hugs.HUGSWrapper;
using dialog.unity.extension.FontUtil;
using dialog.unity.extension.StringUtil;
using StringTools;

class DialogSource extends MonoBehaviour
{
	public var message:String;
	public var style:Style;

	public function Start():Void
	{
		if(message.startsWith("#"))
		{
			Dialog.cbCall(message.substr(1), style, null, "");
		}
		else
		{
			Dialog.globalCall(message, style, null, "");
		}
	}

	public static function main():Void
	{

	}
}
