package stencyl.ext.polydes.scenelink.ui.colors;

public interface ColorGridModel
{
	public void setPrimary(int mode, float value);
	public int[][] getGradient(int width, int height);
}