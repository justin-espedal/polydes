/**
 * @author Justin Espedal
 */
import com.stencyl.models.Actor;

class GlobalActorID
{
	public static var actors:Map<String, Actor> = new Map<String, Actor>();
	
	public static function get(name:String):Actor
	{
		return actors.get(name);
	}
	
	public static function set(name:String, actor:Actor):Void
	{
		actors.set(name, actor);
	}
}