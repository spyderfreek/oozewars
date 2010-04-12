package oozeWars;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class HealthBar extends Entity 
{
	private Blob blob;
	private double width, height;
	private double factor, currHealth;
	private Color color, outline;
	private final double UPDATE = .5;
	private float strokeWidth;

	public HealthBar(double x, double y, double w, double h, Blob b, Color col) 
	{
		super(x, y);
		width = w;
		height = h;
		blob = b;
		color = col;
		outline = Color.white;
		factor = width / blob.getHealth();
		strokeWidth = (float)(h * 0.1);
	}

	public void draw(Graphics2D graphics, Game game, double scale) 
	{
		AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
		graphics.setColor( color );
		Shape rect = at.createTransformedShape( new Rectangle2D.Double(x, y, currHealth, height) );
		graphics.fill( rect );
		//graphics.setColor(outline);
		//graphics.setStroke(new BasicStroke(strokeWidth * (float)(scale)));
		//graphics.draw(rect);
	}

	/* (non-Javadoc)
	 * @see oozeWars.Entity#go(oozeWars.Game, long, int)
	 */
	@Override
	public void go(Game game, long timestep, int priorityLevel) {
		currHealth += UPDATE * ( blob.getHealth() * factor - currHealth );
		super.go(game, timestep, priorityLevel);
	}


}
