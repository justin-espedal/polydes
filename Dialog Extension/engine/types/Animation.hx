	public static function rAnimation(s:String):scripts.ds.dialog.Animation
	{
		var a:scripts.ds.dialog.Animation = new scripts.ds.dialog.Animation();
		
		var sa:Array<String> = s.split("-");
		a.actor = cast(com.stencyl.Data.get().resources.get(rInt(sa[0])), com.stencyl.models.actor.ActorType);
		a.anim = sa[1];
		
		return a;
	}