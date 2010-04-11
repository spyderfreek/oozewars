package com.jhlabs.image;

public class FadeFilter extends PointFilter {
	private float fadeAmount;
	
	/**
	 * Takes a number between 0 and 1, where 0 sets
	 * the image's output alpha to 0, and 1 leaves it unchanged
	 * @param fadeAmount
	 */
	public FadeFilter( float fadeAmount ) {
		setFadeAmount(fadeAmount);
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		int a = (rgb >> 24) & 0xff;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		a *= fadeAmount;
		r *= fadeAmount;
		g *= fadeAmount;
		b *= fadeAmount;
		return (a << 24 ) | ( r << 16 ) | ( g << 8 ) | b;
	}

	/**
	 * @return the fadeAmount
	 */
	public float getFadeAmount() {
		return fadeAmount;
	}

	/**
	 * @param fadeAmount the fadeAmount to set
	 */
	public void setFadeAmount(float fadeAmount) {
		this.fadeAmount = fadeAmount;
	}

}
