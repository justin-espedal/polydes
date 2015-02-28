/**
 * @author Justin Espedal
 */
import com.stencyl.behavior.Script;
import com.stencyl.models.Sound;

class DialogBase extends DialogExtension
{
	public var window:DialogWindow;
	public var messageBegan:Bool;
	
	public function new(dg:DialogBox)
	{
		super(dg);
		
		name = "Dialog Base";
		
		window = null;
		messageBegan = false;
		
		cmds =
		[
			"br"=>br,
			"end"=>end,
			"clear"=>clear,
			"close"=>close,
			"dg"=>dgGo
		];
		
		addCallback(Dialog.WHEN_CREATED, function():Void
		{
			dg.paused = true;
			
			window = new DialogWindow(style.msgWindow);
			if(!style.fitMsgToWindow)
				window.setContentSize(dg.msgW, dg.msgH);
			window.tweenCompleteNotify.push(function():Void
			{
				dg.paused = false;
			});
			window.applyTween(style.msgWindow.createTween);
		});
		addCallback(Dialog.WHEN_MESSAGE_BEGINS, function():Void
		{
			if(!messageBegan)
			{
				messageBegan = true;
				var snd:Sound = style.msgStartSound;
				if(snd != null)
					Script.playSound(snd);
			}
			
			Script.setGameAttribute(style.controlAttribute, true);
		});
		addCallback(Dialog.WHEN_MESSAGE_ENDS, function():Void
		{
			Script.setGameAttribute(style.controlAttribute, false);
			messageBegan = false;
		});
		addCallback(Dialog.ALWAYS, function():Void
		{
			window.update();
		});
		addDrawCallback("Window Frame", function():Void
		{
			window.draw(g);
		});
		addDrawCallback("Message", function():Void
		{
			for(line in dg.lines)
			{
				G2.drawImage(line.img, line.pos.x, line.pos.y, false);
			}
		});
	}
	
	public function br():Void
	{
		dg.startNextLine();
	}
	
	public function end():Void
	{
		dg.clearMessage();
		
		var snd:Sound = style.endSound;
		if(snd != null)
			Script.playSound(snd);
		
		window.tweenCompleteNotify.push(function():Void
		{
			dg.endMessage();
		});
		window.applyTween(style.msgWindow.destroyTween);
	}
	
	public function clear():Void
	{
		dg.clearMessage();
		var snd:Sound = style.clearSound;
		if(snd != null)
			Script.playSound(snd);
	}
	
	public function close():Void
	{
		dg.closeMessage();
		var snd:Sound = style.closeSound;
		if(snd != null)
			Script.playSound(snd);
	}

	public function dgGo(toCall:String):Void
	{
		dg.goToDialog(toCall);
	}
}