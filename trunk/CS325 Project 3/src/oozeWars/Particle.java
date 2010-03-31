package oozeWars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

import com.jhlabs.image.BoxBlurFilter;
import com.jhlabs.image.ImageMath;
import com.jhlabs.image.ImageUtils;

public class Particle extends Entity 
{
	// Indicates whether or not this particle has been touched while either
	// (a) applying forces to neighbor particles, or (b) doing a connectivity search on blobs
	private boolean touched = false;
	private double radius;
	private ArrayList<Particle> neighbors;
	Color color;
	protected byte blobID;
	// TODO get graphics here
	protected BufferedImage image;
	protected static int BLUR_WIDTH = 20;

	/**
	 * Used to create a new Particle at a given location with a defined radius.
	 * @param x
	 * :  The x location of the center of the Particle
	 * @param y
	 * :  The y location of the center of the Particle
	 * @param radius
	 * :  The radius of the Particle
	 */
	public Particle(double x, double y, double radius, Color color) 
	{
		super(x, y);
		neighbors = new ArrayList<Particle>();
		this.radius = radius;
		this.color = color;
		image = createImage();
	}
	
	public byte getBlobID() {
		return blobID;
	}

	public void setBlobID(byte blobID) {
		this.blobID = blobID;
	}

	/**
	 * Used to draw the Particle to the screen.  Uses Entity's draw() method to do so.
	 * @param graphics
	 * :  The graphics that will be used to draw the Particle.
	 * @param game
	 * :  The game that the Particle needs to be drawn for.
	 * @param color
	 * :  The color that the Particle will be.
	 */
	public void draw(Graphics2D graphics, Game game, Color color) 
	{
		int newColor = color.getRGB();
		int oldColor = this.color.getRGB();
		this.color = new Color(ImageMath.mixColors(.3f, oldColor, newColor));
		graphics.drawImage(image, (int)x, (int)y, null);
		
		//graphics.drawImage(image, (int)x, (int)y, color, null);
	}
	
	protected BufferedImage createImage()
	{
		int size = (int)((radius + BLUR_WIDTH) * 2);
		//BufferedImage img = new BufferedImage( size, size, BufferedImage.TYPE_BYTE_GRAY );
		BufferedImage img = new BufferedImage( size, size, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = img.createGraphics();

		BoxBlurFilter blur = new BoxBlurFilter(BLUR_WIDTH/3f, BLUR_WIDTH/3f, 3);
		g.setColor(Color.BLACK);
		g.fillOval(BLUR_WIDTH, BLUR_WIDTH, (int)radius * 2, (int)radius * 2);
		g.dispose();
		
		return blur.filter(img, null);
	}

	@Override
	public void go(Game game, long timestep, int priorityLevel) 
	{
		x += vx;
		y += vy;
		super.go(game, timestep, priorityLevel);
	}
	
	/**
	 * Applies a force to the Particle's neighbor, pulling its neighbor closer to it.
	 * @param neighbor
	 * :  The particle in which the force will be applied.
	 */
	public void applyForce(Particle neighbor)
	{
		
	}
	
	/**
	 * Updates the Particle's list of neighboring Particles.
	 * @param range The maximum distance from this Particle to a neighbor
	 */
	public void updateNeighbors( double range )
	{
		
	}
	
	/**
	 * @return the neighbors
	 */
	public ArrayList<Particle> getNeighbors() {
		return neighbors;
	}

	/**
	 * Sets the Particle's touched variable to the given value.
	 * @param val
	 * :  The value to which the Particle's touched variable will be set.
	 */
	public void setTouched(boolean val)
	{
		touched = val;
	}
	
	/**
	 * Gets whether the Particle has been touched or not.
	 * @return
	 * :  The current value of touched.
	 */
	public boolean isTouched()
	{
		return touched;
	}
	
	/**
	 * Tells whether another particle will be repelled by this one,
	 * assuming it is close enough to have a force applied to it
	 * @param other The particle in question
	 * @return Returns <code>true</code> if the particle will be repelled, and
	 * <code>false</code> if it will be attracted
	 */
	public boolean isEnemy(Particle other)
	{
		byte id = other.getBlobID();
		return ( id != blobID && id != 0 );
	}
}
