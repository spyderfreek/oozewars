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

public class Particle extends Entity implements Comparable<Particle>
{
	private double radius, inverseMass;
	private ArrayList<Particle> neighbors;
	Color color;
	protected int blobID;
	protected BufferedImage image;
	protected static int BLUR_WIDTH = 15;

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
		inverseMass = 1/radius;
		this.color = color;
		image = createImage();
	}
	
	public int getBlobID() 
	{
		return blobID;
	}

	public void setBlobID(int blobID2) 
	{
		this.blobID = blobID2;
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
		g.setColor(color);
		g.fillOval(BLUR_WIDTH, BLUR_WIDTH, (int)radius * 2, (int)radius * 2);
		g.dispose();
		
		return blur.filter(img, null);
	}

	@Override
	public void go(Game game, long timestep, int priorityLevel) 
	{
		x += vx;
		y += vy;
		// don't reschedule because parent blob is already being scheduled
		//super.go(game, timestep, priorityLevel);
	}
	
	/**
	 * Applies a force to the Particle's neighbor, pulling its neighbor closer to it.
	 * @param neighbor
	 * :  The particle in which the force will be applied.
	 */
	public void applyForce(Particle neighbor, double k, double distance, double dx, double dy, double comfyDistance)
	{
		comfyDistance += radius;
		double x;
		if(!this.isEnemy(neighbor))
			x = comfyDistance - dx;
		else
			x = dx;
		
		double force = k * x;
		double accel = force * inverseMass;
		double dvx = dx * accel;
		double dvy = dy * accel;
		
		neighbor.push(dvx, dvy);
	}
	
	/**
	 * @return the neighbors
	 */
	public ArrayList<Particle> getNeighbors() 
	{
		return neighbors;
	}
	
	public void addNeighbor(Particle p)
	{
		neighbors.add(p);
	}
	
	public void clearNeighbors()
	{
		neighbors.clear();
	}

	/**
	 * Sets the Particle's touched variable to the given value.
	 * @param val
	 * :  The value to which the Particle's touched variable will be set.
	 *
	public void setTouched(boolean val)
	{
		touched = val;
	}
	
	/**
	 * Gets whether the Particle has been touched or not.
	 * @return
	 * :  The current value of touched.
	 *
	public boolean isTouched()
	{
		return touched;
	}
	
	/**
	 * Tells whether another particle will be repelled by this one,
	 * assuming it is close enough to have a force applied to it
	 * 
	 * @param other The particle in question
	 * @return Returns <code>true</code> if the particle will be repelled, and
	 * <code>false</code> if it will be attracted
	 */
	public boolean isEnemy(Particle other)
	{
		int id = other.getBlobID();
		return ( id != blobID && id != 0 );
	}
	
	/**
	 * @return
	 * The radius of the Particle
	 */
	public double getRadius()
	{
		return radius;
	}
	
	/**
	 * Compares this Particle's radius to the other Particle's radius.
	 * @return
	 * 1 if this Particle's radius > the other Particle's radius.
	 * <p>0 if this Particle's radius == the other Particle's radius.</p>
	 * <p>-1 if this Particle's radius < the other Particle's radius.</p>
	 */
	public int compareTo(Particle theOther)
	{
		return (this.radius == theOther.getRadius() ? 0: (this.radius < theOther.getRadius()? -1: 1));
	}
}
