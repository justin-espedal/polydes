package com.polydes.dialog.data;

import stencyl.core.engine.actor.IActorType;

public class Animation
{
	public Animation(IActorType actorType, String anim)
	{
		this.actorType = actorType;
		this.anim = anim;
	}
	
	public IActorType actorType;
	public String anim;
}
