package oozeWars;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;

public class Label implements Sprite 
{

	protected static final Font baseFont = initFont();
	protected Font font = baseFont;
	protected int x;
	protected int y;
	protected Color color;
	protected String text;

	private static Font initFont() {
		InputStream fontStream = Score.class.getResourceAsStream("Splats_Unsplatted.ttf");
	
		try 
		{
			Font f = Font.createFont(Font.TRUETYPE_FONT, fontStream);
			fontStream.close();
			return f;
		} 
		catch (FontFormatException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
		
		return null;
	}

	public static Font getFont() {
		return baseFont;
	}

	public Label( int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	/* (non-Javadoc)
	 * @see oozeWars.Sprite#draw(java.awt.Graphics2D, oozeWars.Game)
	 */
	@Override
	public void draw(Graphics2D graphics, Game game) 
	{
		graphics.setPaint(color);
		graphics.setFont(font);
		graphics.drawString( text, x, y);
	}

}