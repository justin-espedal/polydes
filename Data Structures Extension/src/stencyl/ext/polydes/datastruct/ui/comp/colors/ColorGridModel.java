package stencyl.ext.polydes.datastruct.ui.comp.colors;

public interface ColorGridModel
{
	public void setPrimary(int mode, float value);
	public int[][] getGradient(int width, int height);
}