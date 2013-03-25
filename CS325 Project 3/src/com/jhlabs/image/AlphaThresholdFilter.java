package com.jhlabs.image;

public class AlphaThresholdFilter extends PointFilter 
{
	private int cutOff;
	
	public AlphaThresholdFilter( int cutOff )
	{
		this.cutOff = cutOff;
	}
	
	@Override
	public int filterRGB(int x, int y, int rgb) 
	{
		int a = (rgb >> 24) & 0xff;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		int brightness = a;
		a = brightness < cutOff ? 0 : a;
		
		return (rgb & 0xffffff) | (a << 24);
	}

	/**
	 * @return the cutOff
	 */
	public int getCutOff() {
		return cutOff;
	}

	/**
	 * @param cutOff the cutOff to set
	 */
	public void setCutOff(int cutOff) {
		this.cutOff = cutOff;
	}

}
