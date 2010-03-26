/**
	SPRITE.JAVA
	
	A simple interface for an object capable of drawing on the screen.  Sprites are registered with the View to be
	drawn in a specific order when the View calls upon them.
*/

package oozeWars;
import java.awt.*;

public interface Sprite
{
	public void draw(Graphics2D graphics, Game game);
}