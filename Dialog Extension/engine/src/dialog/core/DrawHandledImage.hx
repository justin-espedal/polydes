package dialog.core;

class DrawHandledImage
{
	public var handler:DrawHandler;
	public var id:Int;
	
	public function new(handler:DrawHandler, id:Int)
	{
		this.handler = handler;
		this.id = id;
	}
	
	public function moveImgTo(x:Int, y:Int):Void
	{
		handler.moveImgTo(id, x, y);
	}
	
	public function moveImgBy(x:Int, y:Int):Void
	{
		handler.moveImgBy(id, x, y);
	}
	
	public function removeImg():Void
	{
		handler.removeImg(id);
	}
}