package oozeWars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;
import javax.swing.JOptionPane;

import oozeWars.OozeWars.Location;

public class Blob extends Entity 
{
	private ArrayList<Particle> particles;
	private Head head;
	private Color color;
	private double orientation, minSpeed, maxSpeed, friction = .9, accel, health = 0;
	private byte blobID;
	
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
	public Blob(double x, double y, double orientation, int numParticles, byte blobID, Color color) 
	{
		super(x, y);
		this.color = color;
		this.orientation = orientation;
		this.blobID = blobID;
		particles = new ArrayList<Particle>();
		
		//TODO:  Figure out default size for head
		head = new Head(x, y, 5, color, orientation);
		head.setBlobID(blobID);
		particles.add(head);
		
		while(numParticles-- >= 0)
			particles.add(new Particle(x, y, 3, color));
		
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
		color = Color.WHITE;
		orientation = 0;
		this.particles = particles;
		head = null;
		blobID = 0x00;
		
		for(Particle p: this.particles)
			health += p.getRadius();
	}
	
	public boolean equals(Object theOther)
	{
		if (theOther == null || !(theOther instanceof Blob)) 
			return false;
		Blob other = (Blob) theOther;
		
		return (blobID == other.getBlobID());
	}
	
	/**
	 * Returns the Blob's current ID number.
	 * @return
	 * The Blob's current ID number.
	 */
	public byte getBlobID() 
	{
		return blobID;
	}
	
	/**
	 * Sets the Blob's ID number to the value given for blobID
	 * @param blobID
	 * The Blob's new ID number
	 */
	public void setBlobID(byte blobID) 
	{
		this.blobID = blobID;
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
		checkConnectivity();
		OozeWars g = (OozeWars)game;
		
		// add any blobs which have been disconnected from this one
		// to the master list
		g.getBlobs().addAll( findStragglers() );
		
		// ignore head for AI-less blobs
		boolean headDead = ( head == null ) ? false : head.isDead();
		
		if(particles.isEmpty() || headDead )
			setDead(true);
		else
		{
			for(Particle p : particles)
			{
				p.go(game, timestep, priorityLevel);
			}
		}
		
		// blobs are rescheduled, but not particles for performance
		super.go(game, timestep, priorityLevel);

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
		
		while(it.hasNext())
		{
			Particle theOther = it.next();
			if( biggest.compareTo(theOther) == -1 )
				biggest = theOther;
		}
		//We must remove a Particle with the same sized radius as the biggest.. doesn't necessarily
		//have to be the particle that we found as the biggest.
		it = particles.iterator();
		while(it.hasNext())
		{
			Particle theOther = it.next();
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
	 * Updates the blob's particle list to include only connected neighbors
	 */
	public void checkConnectivity()
	{
		wipeClean();
		// should seed head for Agent-controlled blobs, and first particle otherwise
		particles = getConnectivity( particles.get(0) );
	}
	
	/**
	 * Does a BFS (starting at seed particle)
	 * through all neighbors to decide which ones are still
	 * part of the blob.
	 * 
	 * @param seed The particle from which to start the search
	 * @return
	 * The Linked List of Particles that are still part of the Blob.
	 */
	public ArrayList<Particle> getConnectivity( Particle seed )
	{
		// need to check headless blobs in case they have been absorbed,
		// so they won't just persist forever
		if( seed.getBlobID() != getBlobID() )
		{
			return new ArrayList<Particle>();
		}
		
		LinkedList<Particle> queue = new LinkedList<Particle>();
		ArrayList<Particle> neighbors;
		ArrayList<Particle> connected = new ArrayList<Particle>();
		
		queue.add(seed);
		seed.setTouched(true);
		Particle currParticle;
		
		while( ! queue.isEmpty() )
		{
			connected.add( currParticle = queue.pop() );
			neighbors = currParticle.getNeighbors();
			
			for( Particle p : neighbors )
			{
				// only looking for particles which can be absorbed
				// into the current blob.
				if( p.isTouched() || currParticle.isEnemy(p) )
					continue;
				
				p.setBlobID(getBlobID());
				p.setTouched(true);
				queue.add(p);
			}
		}
		
		return connected;
	}
	
	/**
	 * Searches through list of particles looking for ones
	 * which haven't been touched by checkConnectivity()
	 * to add to new, neutral blobs.
	 * 
	 * @return
	 * The list of new Blobs (if any) created.
	 */
	public ArrayList<Blob> findStragglers()
	{
		//TODO: need to put this function outside of blob;
		// otherwise stragglers won't be found
		ArrayList<Blob> newBlobs = new ArrayList<Blob>();
		
		for( Particle p : particles )
		{
			if( p.isTouched() )
				continue;
			//TODO: create default settings for neutral blobs
			newBlobs.add( new Blob( getConnectivity( p ) ) );
		}

		return newBlobs;
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
		for( Particle aParticle : particles )
		{
			aParticle.setTouched(false);
		}
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
