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
	public int filterRGB(int x, int y, int rgba) {
		int a = (rgba >> 24) & 0xff;
		int rgb = rgba & 0xffffff;
		a *= fadeAmount;
		return (a << 24 ) | rgb;
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
