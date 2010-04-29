/**
 * 
 */
package oozeWars;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Keeps track of a player score and draws it
 * @author Nick
 *
 */
public class Score implements Sprite 
{
	static final Font font = initFont();
	static final float  FONT_SIZE = 40;
	int x, y;
	long score;
	Color color;
	
	private static Font initFont()
	{
		InputStream fontStream = Score.class.getResourceAsStream("Splats_Unsplatted.ttf");

		try 
		{
			Font f = Font.createFont(Font.TRUETYPE_FONT, fontStream);
			fontStream.close();
			return f.deriveFont(FONT_SIZE);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Score( int x, int y, Color color, long score )
	{
		this.x = x;
		this.y = y;
		this.color = color;
		this.score = score;
	}
	
	/* (non-Javadoc)
	 * @see oozeWars.Sprite#draw(java.awt.Graphics2D, oozeWars.Game)
	 */
	@Override
	public void draw(Graphics2D graphics, Game game) 
	{
		graphics.setPaint(color);
		graphics.setFont(font);
		graphics.drawString( Long.toString(score), x, y);
	}

	public void setScore( long s )
	{
		score = s;
	}

}
