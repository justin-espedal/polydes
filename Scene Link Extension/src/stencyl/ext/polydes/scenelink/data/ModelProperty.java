package stencyl.ext.polydes.scenelink.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ModelProperty
{
	public boolean display() default true;
	public int order() default -1;
	public String label() default "";
	public String type() default "";
	public boolean editable() default true;
	public boolean refresh() default true;
}
