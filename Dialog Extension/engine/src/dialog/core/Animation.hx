package scripts.ds.dialog;

import com.stencyl.models.actor.ActorType;

class Animation
{
	public var actor:ActorType;
	public var anim:String;
	
	public function new(type:ActorType, anim:String)
	{
		this.actor = type;
		this.anim = anim;
	}
}