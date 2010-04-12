package oozeWars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import java.util.LinkedList;
import com.jhlabs.image.BoxBlurFilter;
import com.jhlabs.image.ImageMath;
import com.jhlabs.image.ImageUtils;

public class Particle extends Entity implements Comparable<Particle>
{
	protected double radius, inverseMass;
	protected double friction;
	protected ArrayList<Particle> neighbors;
	Color color;
	protected int blobID;
	protected BufferedImage image, colored;
	protected RescaleOp colorFilt;
	protected float[] scales = {1f,1f,1f,1f};
	protected final float[] offsets = {0f,0f,0f,0f};
	protected static int BLUR_WIDTH = 10;
	protected int index;
	private int halfWidth;

	/**
	 * Used to create a new Particle at a given location with a defined radius.
	 * @param x
	 * :  The x location of the center of the Particle
	 * @param y
	 * :  The y location of the center of the Particle
	 * @param radius
	 * :  The radius of the Particle
	 */
	public Particle(double x, double y, double radius, Color color, int blobid) 
	{
		super(x, y);
		neighbors = new ArrayList<Particle>();
		this.radius = radius;
		inverseMass = 1/(radius);
		this.color = color;
		index = -1;
		image = createImage();
		colored = new BufferedImage( halfWidth * 2, halfWidth * 2, BufferedImage.TYPE_INT_ARGB );
		blobID = blobid;
		colorFilt = new RescaleOp(offsets, scales, null);
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
	public void draw(Graphics2D graphics, Game game, Color col, double scale) 
	{
		int newColor = col.getRGB();
		int oldColor = this.color.getRGB();
		color = new Color(0xff000000 | ImageMath.mixColors(.05f, oldColor, newColor));
		float factor = (float) (1.0 / 255);
		scales[0] = color.getRed() * factor;
		scales[1] = color.getGreen() * factor;
		scales[2] = color.getBlue() * factor;
		colorFilt = new RescaleOp(scales, offsets, null);
		colorFilt.filter(image.getRaster(), colored.getRaster());
		AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
		//AffineTransform at = AffineTransform.getTranslateInstance((int)x - halfWidth, (int)y - halfWidth);
		at.translate((int)x - halfWidth, (int)y - halfWidth);
		graphics.drawRenderedImage( colored, at);
		//graphics.setColor(this.color);
		//graphics.fillOval((int)x - halfWidth, (int)y - halfWidth, 2 * halfWidth, 2 * halfWidth);
	}
	
	protected BufferedImage createImage()
	{
		halfWidth = (int)(radius + BLUR_WIDTH);
		int size =  halfWidth * 2;
		//BufferedImage img = new BufferedImage( size, size, BufferedImage.TYPE_BYTE_GRAY );
		BufferedImage img = new BufferedImage( size, size, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = img.createGraphics();

		BoxBlurFilter blur = new BoxBlurFilter(BLUR_WIDTH/3f, BLUR_WIDTH/3f, 3);
		g.setColor(color.white);
		g.fillOval(BLUR_WIDTH, BLUR_WIDTH, (int)radius * 2, (int)radius * 2);
		g.dispose();
		
		return blur.filter(img, null);
	}

	@Override
	public void go(Game game, long timestep, int priorityLevel) 
	{	
		double tmpX = x;
		double tmpY = y;
		x += friction*( x - oldX );
		y += friction * ( y - oldY );
		oldX = tmpX;
		oldY = tmpY;
	}
	
	public void go(Game game, long timestep, int priorityLevel, 
			double minSpeed, double maxSpeed, double frictn )
	{
		//applyFriction(minSpeed, maxSpeed, friction);
		friction = frictn;
		go(game, timestep, priorityLevel);
	}
	
	/**
	 * Applies a force to the Particle's neighbor, pulling its neighbor closer to it.
	 * @param neighbor
	 * :  The particle in which the force will be applied.
	 */
	/*
	public void applyForce(Particle neighbor, double k, double distance, double dx, double dy, double comfyDistance, double range)
	{
		comfyDistance += radius + neighbor.getRadius();
		double cushion = 1;

		double x;
		if(!this.isEnemy(neighbor))
		{
			x = comfyDistance - distance;
			if( Math.abs(x) < cushion )
				return;
		}
		else
			x = range - distance;
		
		double force = k * x / (distance + .01);
		double accel = force * neighbor.getInverseMass();
		double dvx = dx * accel;
		double dvy = dy * accel;
		//dvx -= neighbor.getVX() * 0.005;
		//dvy -= neighbor.getVY() * 0.005;
		
		neighbor.push(dvx, dvy);
	}
	*/
	
	/**
	 * Applies a force to the Particle's neighbor, pulling its neighbor closer to it.
	 * @param neighbor
	 * :  The particle in which the force will be applied.
	 */
	
	public void applyStickConstraint(Particle neighbor, double k, double distance, double dx, double dy, double pushDist, double pullDist)
	{
		double nInvMass = neighbor.getInverseMass();
		if( ( inverseMass + nInvMass ) == 0 )
			return;
		double dr = radius + neighbor.getRadius();
		pushDist += dr;
		pullDist += dr;
		double comfyDist = isEnemy(neighbor) ? pushDist : pullDist;
		
		double diff = k * ( distance - comfyDist ) / ( distance * (inverseMass + nInvMass ) );
		dx *= diff;
		dy *= diff;
		
		push( dx * inverseMass, dy * inverseMass );
		neighbor.push(-dx * nInvMass, -dy * nInvMass );
	}
	
	public void damage( double amount )
	{
		if( amount >= radius )
			setDead(true);
		else
			radius -= amount;
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
		return isEnemy(id);
	}
	
	public boolean isEnemy( int id )
	{
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
	
	public double getInverseMass() {
		return inverseMass;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
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
