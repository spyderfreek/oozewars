/**
 * 
 */
package oozeWars;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * @author Nick
 *
 */
public class PowerupLabel extends Label implements Sprite 
{
	private PowerUp target;

	/**
	 * @param x
	 * @param y
	 * @param color
	 */
	public PowerupLabel(int x, int y, Color color, String chars, PowerUp powerUp ) {
		super(x, y, color);
		text = chars;
		target = powerUp;
	}

	/* (non-Javadoc)
	 * @see oozeWars.Sprite#draw(java.awt.Graphics2D, oozeWars.Game)
	 */
	@Override
	public void draw(Graphics2D graphics, Game game) 
	{
		double r = target.getRadius();
		x = (int)( target.getX() - r );
		y = (int)( target.getY() + r );
		font = baseFont.deriveFont( (float) (1.25*(r + Particle.BLUR_WIDTH)) );
		super.draw(graphics, game);
	}

}
