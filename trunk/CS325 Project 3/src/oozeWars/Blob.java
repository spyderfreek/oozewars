package oozeWars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;
import javax.swing.JOptionPane;

public class Blob extends Entity 
{
	private ArrayList<Particle> particles;
	private Head head;
	private Color color;
	private double orientation, minSpeed, maxSpeed, friction = .9, accel, health = 0, blobForce = 15;
	private double comfyDistance = 10;
	private int blobID;
	
	/**
	 * Used to create a new Blob at a given spot, in a specified orientation, with
	 * a specified number of particles, and with a particular color.  Also initializes
	 * the Blob's health and ID.
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
	public Blob(double x, double y, double orientation, int numParticles, int blobID, Game game, Color color) 
	{
		super(x, y);
		this.color = color;
		this.orientation = orientation;
		this.blobID = blobID;
		particles = new ArrayList<Particle>();
		
		//TODO:  Figure out default size for head
		head = new Head(x, y, 8, color, orientation);
		head.setBlobID(this.blobID);
		particles.add(head);
		
		while(numParticles-- > 0)
		{
			Particle aParticle = new Particle(x + game.random.nextDouble()*80 - 40, y + game.random.nextDouble()*80-40, 6, color);
			aParticle.setBlobID(blobID);
			particles.add(aParticle);
		}
		
		updateHealth();
	}
	
	/**
	 * Constructor for uncontrolled blobs from existing detached particles.  
	 * This Blob will be flagged as neutral and will have an ID = 0;
	 * @param particles 
	 * : An initialization list of particles that this Blob will contain.
	 */
	public Blob( ArrayList<Particle> particles )
	{
		super(0,0);
		color = Color.BLACK;
		orientation = 0;
		this.particles = particles;
		head = null;
		blobID = 0;
	}
	
	public boolean equals(Object theOther)
	{
		if (theOther == null || !(theOther instanceof Blob)) 
			return false;
		Blob other = (Blob) theOther;
		
		return (blobID == other.getBlobID());
	}
	
	public int hashCode(Object obj)
	{
		return (int)blobID;
	}
	
	public double getFriction()
	{
		return friction;
	}
	
	/**
	 * Returns the Blob's current ID number.
	 * @return
	 * The Blob's current ID number.
	 */
	public int getBlobID() 
	{
		return blobID;
	}
	
	/**
	 * Sets the Blob's ID number to the value given for blobID
	 * @param blobID
	 * The Blob's new ID number
	 */
	public void setBlobID(int blobID) 
	{
		this.blobID = blobID;
	}

	public Head getHead() 
	{
		return head;
	}

	/**
	 * A method used to draw the specified Blob.  Uses Particle's draw() method for each of
	 * the Particles in the Blob.
	 */
	@Override
	public void draw(Graphics2D graphics, Game game) 
	{
		for( Particle p : particles )
			p.draw(graphics, game, color);
	}

	
	@Override
	public void go(Game game, long timestep, int priorityLevel) 
	{		
		OozeWars g = (OozeWars)game;
		
		// ignore head for AI-less blobs
		boolean headDead = ( head == null ) ? false : head.isDead();
		
		if(particles.isEmpty() || headDead )
			setDead(true);
		else
		{
			for(Particle p : particles)
			{
				if(p instanceof Head)
					((Head)p).go(game, timestep, priorityLevel, minSpeed, maxSpeed, friction);
				else
					p.go(game, timestep, priorityLevel, minSpeed, maxSpeed, friction);
				g.addParticle(p);
			}
		}
		
		if(head != null)
			updateHealth();
	}
	
	
	/**
	 * Picks the largest particle in the Blob, removes it, places it in front of the head,
	 * and shoots it in the direction the Head is facing.
	 * 
	 * @return
	 * The new Bullet with the attributes of the Particle that was chosen to be shot.
	 */
	public Bullet shoot()
	{
		Iterator<Particle> it = particles.iterator();
		if(!it.hasNext())
			return null;
		Particle biggest = it.next();
		
		if(biggest instanceof Head && it.hasNext())
			biggest = it.next();
		else
			return null;
		
		while(it.hasNext())
		{
			Particle theOther = it.next();
			if( biggest.compareTo(theOther) == -1 )
				biggest = theOther;
		}
		//We must remove a Particle with the same sized radius as the biggest.. doesn't necessarily
		//have to be the Particle that we found as the biggest.
		it = particles.iterator();
		while(it.hasNext())
		{
			Particle theOther = it.next();
			if(theOther instanceof Head && it.hasNext())
				theOther = it.next();
			else
				return null;
			
			if(biggest.compareTo(theOther) == 0)
			{
				it.remove();
				break;
			}
		}
		
		
		double bigRad = biggest.getRadius();
		return new Bullet(head.getX()+bigRad, head.getY()+bigRad, bigRad, color, head.getOrientation());
	}
	
	/**
	 * Method to retrieve the Blob's current health.
	 * @return
	 * The Blob's current health.
	 */
	public double getHealth()
	{
		return health;
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
	 * :  The maximum speed the Blob can go.
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
	 * Sets the force that the Particles in the Blob will have on eachother.
	 * @param blobForce
	 * The value that the force of the Blob will be set to.
	 */
	public void setBlobForce(double blobForce) 
	{
		this.blobForce = blobForce;
	}
	
	/**
	 * Gets this Blob's force that all of it's Particles apply on eachother.
	 * @return
	 * The value for blobForce.
	 */
	public double getBlobForce() 
	{
		return blobForce;
	}

	/**
	 * @param comfyDistance 
	 * :  the comfyDistance to set
	 */
	public void setComfyDistance(double comfyDistance) 
	{
		this.comfyDistance = comfyDistance;
	}

	/**
	 * @return 
	 * the comfyDistance
	 */
	public double getComfyDistance() 
	{
		return comfyDistance;
	}

	
	/**
	 * Uses the collection of particles in this blob to find its new health.
	 */
	public void updateHealth()
	{
		health = head.getRadius();
		for(Particle p:  particles)
			health += p.getRadius();
	}
	
	public ArrayList<Particle> getParticles()
	{
		return particles;
	}
	
}
