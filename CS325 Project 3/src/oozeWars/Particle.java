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
	protected double radius, inverseMass, scaleFactor;
	protected double friction;
	protected ArrayList<Particle> neighbors;
	Color color;
	protected int blobID;
	protected BufferedImage image, colored;
	protected RescaleOp colorFilt;
	protected float[] scales = {1f,1f,1f,1f};
	protected final float[] offsets = {0f,0f,0f,0f};
	public static final int BLUR_WIDTH = 15;
	protected int index;
	protected int halfWidth;

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
		setRadius( radius );
		
		this.color = color;
		index = -1;
		image = createImage();
		scaleFactor = 1.0 / halfWidth;
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
		
		//double adjScale = Math.ceil( radius * scaleFactor );
		
		graphics.drawRenderedImage( colored, getTransform( scale ));
		//graphics.setColor(this.color);
		//graphics.fillOval((int)x - halfWidth, (int)y - halfWidth, 2 * halfWidth, 2 * halfWidth);
	}
	
	protected AffineTransform getTransform( double scale )
	{
		double adjScale = halfWidth * scaleFactor;
		AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
		at.translate((int)x - halfWidth, (int)y - halfWidth);
		at.scale(adjScale, adjScale);
		return at;
	}
	
	protected BufferedImage createImage()
	{
		halfWidth = (int)(radius + BLUR_WIDTH);
		int size =  halfWidth * 2;
		//BufferedImage img = new BufferedImage( size, size, BufferedImage.TYPE_BYTE_GRAY );
		BufferedImage img = new BufferedImage( size, size, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = img.createGraphics();

		
		g.setColor(Color.white);
                // replacing blur with calculated alpha
                BoxBlurFilter blur = new BoxBlurFilter(BLUR_WIDTH/3f, BLUR_WIDTH/3f, 3);
		//g.fillOval(BLUR_WIDTH, BLUR_WIDTH, (int)radius * 2, (int)radius * 2);
                g.fillOval(0, 0, size, size);
		g.dispose();
                
                int[] pixels = new int[size*size];
                blur.getRGB( img, 0, 0, size, size, pixels );
                float invWidth = 1.f / (float)(halfWidth);
                for( int y = 0; y < size; ++y)
                    for( int x = 0; x < size; ++x){
                        float dx = invWidth*(halfWidth - x);
                        float dy = invWidth*(halfWidth - y);
                        int alpha;
                        float r2 = dx*dx + dy*dy;
                        if(r2 > 1.f) {
                            alpha = 0;
                        }
                        else {
                            float q = (1.f - (dx*dx + dy*dy));
                            alpha = (int)(q*q*255.f);
                        }
                        int px = pixels[y*size+x];
                        pixels[y*size+x] = px & 0xffffff | (alpha << 24);
                    }
		
                blur.setRGB( img, 0, 0, size, size, pixels );
		return img;
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
			double minSpeed, double maxSpeed, double frictn, double maxRadius, double growth )
	{
		//applyFriction(minSpeed, maxSpeed, friction);
		friction = frictn;
		if( radius < maxRadius )
			setRadius( Math.min(maxRadius, radius + growth) );
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
		double cushion = 15;

		double x = distance / comfyDistance;
		if(Math.abs(distance - comfyDistance) < cushion)
			return;
		
		double attraction, repulsion;
		//double alpha = Math.atan2(dy, dx);
		
		attraction = -k * x;
		repulsion = 2 / (x * x);
		
		double accel = neighbor.getInverseMass() * (attraction + repulsion);
		double dvx = dx * accel / (distance + .01);
		double dvy = dy * accel / (distance + .01);
		
		/*
		if(!this.isEnemy(neighbor))
		{
			force = 1.0 / (x + cushion) - x;
		}
		else
			force = 10.0 / (x + cushion);
		
		/*
		if(!this.isEnemy(neighbor))
		{
			x = comfyDistance - distance;
			if( Math.abs(x) < cushion )
				return;
		}
		else
		{
			x = range - distance;
		}
		
		double force = k * x / (distance + .01);
		double accel = k * force * neighbor.getInverseMass();
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
			setRadius( radius - amount );
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
	
	public void setRadius( double newRadius )
	{
		radius = newRadius;
		halfWidth = (int)newRadius + BLUR_WIDTH;
		inverseMass = 1 / newRadius;
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
