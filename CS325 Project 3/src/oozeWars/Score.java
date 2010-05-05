/**
 * 
 */
package oozeWars;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;

/**
 * Keeps track of a player's score and draws it
 * @author Nick
 *
 */
public class Score extends Label
{
	static final float  FONT_SIZE = 40;
	long score;
	
	public Score( int x, int y, Color color, long score )
	{
		super(x, y, color);
		this.score = score;
		font = baseFont.deriveFont(FONT_SIZE);
	}
	
	/* (non-Javadoc)
	 * @see oozeWars.Sprite#draw(java.awt.Graphics2D, oozeWars.Game)
	 */
	@Override
	public void draw(Graphics2D graphics, Game game) 
	{
		text = Long.toString(score);
		super.draw(graphics, game);
	}

	public void setScore( long s )
	{
		score = s;
	}

}
