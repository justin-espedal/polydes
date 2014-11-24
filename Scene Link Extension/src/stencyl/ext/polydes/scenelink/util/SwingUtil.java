package stencyl.ext.polydes.scenelink.util;

import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;

public class SwingUtil
{
	/**
	 * Verifies if the given point is visible on the screen.
	 * 
	 * @author Heidi Rakels.
	 * 
 	 * @param location
	 *            The given location on the screen.
	 * @return True if the location is on the screen, false otherwise.
	 */
	public static boolean isLocationInScreenBounds(Point location)
	{
		// Check if the location is in the bounds of one of the graphics
		// devices.
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] graphicsDevices = graphicsEnvironment
				.getScreenDevices();
		Rectangle graphicsConfigurationBounds = new Rectangle();

		// Iterate over the graphics devices.
		for (int j = 0; j < graphicsDevices.length; j++)
		{

			// Get the bounds of the device.
			GraphicsDevice graphicsDevice = graphicsDevices[j];
			graphicsConfigurationBounds.setRect(graphicsDevice
					.getDefaultConfiguration().getBounds());

			// Is the location in this bounds?
			graphicsConfigurationBounds.setRect(graphicsConfigurationBounds.x,
					graphicsConfigurationBounds.y,
					graphicsConfigurationBounds.width,
					graphicsConfigurationBounds.height);
			if (graphicsConfigurationBounds.contains(location.x, location.y))
			{

				// The location is in this screengraphics.
				return true;

			}

		}

		// We could not find a device that contains the given point.
		return false;
	}
	
	public static Point getPositionWithinWindow(Component component, Component parent, Point p)
	{
		Point[] pointCheck = new Point[]{(Point)p.clone(), (Point)p.clone(), (Point)p.clone(), (Point)p.clone()};
		int w = component.getWidth();
		int h = component.getHeight();
		pointCheck[0].translate(w, h);
		pointCheck[1].translate(0, h);
		pointCheck[2].translate(w, 0);
		pointCheck[3].translate(0, 0);
		for(Point p2 : pointCheck)
		{
			if(parent.getBounds().contains(p2))
			{
				p2.translate(-w, -h);
				return p2;
			}
		}
		return p;
	}
}