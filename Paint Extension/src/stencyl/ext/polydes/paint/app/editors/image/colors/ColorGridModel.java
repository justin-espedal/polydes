package stencyl.ext.polydes.paint.app.editors.image.colors;

public interface ColorGridModel
{
	public void setPrimary(int mode, float value);
	public int[][] getGradient(int width, int height);
}