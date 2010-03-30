package oozeWars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;

import javax.swing.JOptionPane;

public class Blob extends Entity 
{
	private LinkedList<Particle> particles;
	private Head head;
	private Color color;
	private double orientation, minSpeed, maxSpeed, friction = .9, accel;
	private byte blobID;
	
	/**
	 * Used to create a new Blob at a given spot, in a specified orientation, with
	 * a specified number of particles, and with a particular color.
	 * @param x
	 * :  The x location that the Head of the Blob will be placed
	 * @param y
	 * :  The y location that the Head of the Blob will be placed
	 * @param orientation
	 * :  The angle that the Head of the Blob will be facing (in Degrees)
	 * @param numParticles
	 * :  The number of particles the Blob will contain
	 * @param color
	 * :  The color the Blob will be
	 */
	public Blob(double x, double y, double orientation, int numParticles, Color color) 
	{
		super(x, y);
		this.color = color;
		this.orientation = orientation;
		particles = new LinkedList<Particle>();
		head = new Head(x, y, 10, color, orientation);
		particles.add(head);
		
		while(numParticles-- >= 0)
		{
			particles.add(new Particle(x, y, 8, color));
		}
	}
	
	public byte getBlobID() {
		return blobID;
	}

	public void setBlobID(byte blobID) {
		this.blobID = blobID;
	}

	@Override
	/**
	 * A method used to draw the specified Blob.  Uses Particle's draw() method for each of
	 * the Particles. 
	 */
	public void draw(Graphics2D graphics, Game game) 
	{
		for( Particle p : particles )
			p.draw(graphics, game, color);
	}

	@Override
	public void go(Game game, long timestep, int priorityLevel) 
	{
		
		if(particles.size() == 0 || head.isDead() )
			setDead(true);
		else
		{
			for(Particle p : particles)
			{
				p.go(game, timestep, priorityLevel);
			}
		}

	}
	
	/**
	 * Picks a particle in the Blob, removes it,
	 * and shoots it in the direction the Head is facing.
	 * 
	 * @return
	 * The particle that was chosen to be shot.
	 */
	public Bullet shoot()
	{
		return new Bullet(0,0,3, color, 0);
	}
	
	/**
	 * Sets the minimum speed that the Blob can go.  As speed is an absolute value, 
	 * any negative values will be set to positive values.  Lowest minimum speed = 0.  
	 * The minimum speed must be less than or equal to the maximum speed.
	 * @param mis
	 * :  The double value that the minimum speed will be set to.
	 */
	public void setMinSpeed(double mis)
	{
		if(mis > maxSpeed)
		{
			JOptionPane.showMessageDialog(null, "Invalid minimum speed.");
			System.exit(1);
		}
		if(mis < 0)
			mis = -mis;

			minSpeed = mis;
	}
	
	/**
	 * A method to get the minimum speed of the Blob.
	 * @return
	 * The minimum speed of the Blob.
	 */
	public double getMinSpeed()
	{
		return minSpeed;
	}
	
	/**
	 * Sets the maximum speed that the Blob can go.  As speed is an absolute value, 
	 * any negative values will be set to positive values.  The maximum speed must be 
	 * greater than zero and at least equal to the minimum speed.
	 * @param ms
	 * The maximum speed the Blob can go.
	 */
	public void setMaxSpeed(double ms)
	{
		if(ms == 0 || ms < minSpeed)
		{
			JOptionPane.showMessageDialog(null, "Invalid maximum speed.");
			System.exit(1);
		}
		if(ms < 0)
			ms = -ms;
		maxSpeed = ms;
	}
	
	/**
	 * A method to get the maximum speed of the Blob.
	 * @return
	 * The current maximum speed of the Blob.
	 */
	public double getMaxSpeed()
	{
		return maxSpeed;
	}
	
	/**
	 * Does a BFS (starting at head particle)
	 * through all neighbors to decide which ones are still
	 * part of the blob.
	 * 
	 * @return
	 * The Linked List of Particles that are still part of the Blob.
	 */
	public LinkedList<Blob> checkConnectivity()
	{
		return new LinkedList<Blob>();
	}
	
	/**
	 * Searches through list of particles looking for ones
	 * which haven't been touched by checkConnectivity()
	 * to add to new, neutral blobs.
	 * 
	 * @return
	 * The list of new Blobs (if any) created.
	 */
	public LinkedList<Blob> findStragglers()
	{
		return new LinkedList<Blob>();
	}
	
	/**
	 * Applies forces to all particles in this blob,
	 * marking them as touched.
	 */
	public void applyForces()
	{
		
	}
	
	/**
	 * Sets the "touched" variable on all constituent particles
	 * to false before a search.
	 */
	public void wipeClean()
	{
		Iterator<Particle> it = particles.iterator();
		while(it.hasNext())
		{
			Particle aParticle = it.next();
			aParticle.setTouched(false);
		}
	}
}
