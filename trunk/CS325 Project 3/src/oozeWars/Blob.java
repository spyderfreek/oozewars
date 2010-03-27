package oozeWars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;

public class Blob extends Entity 
{
	private LinkedList<Particle> particles;
	private Head head;
	private Color color;
	private double orientation, minSpeed, maxSpeed, friction = .9;
	
	public Blob(double x, double y, double orientation, int numParticles, Color color) 
	{
		super(x, y);
		this.color = color;
		this.orientation = orientation;
		particles = new LinkedList<Particle>();
	}

	@Override
	public void draw(Graphics2D graphics, Game game) 
	{
		

	}

	@Override
	public void go(Game game, long timestep, int priorityLevel) 
	{
		

	}
	
	public Bullet shoot()
	{
		return new Bullet(0,0,3,0);
	}
	
	public void setMinSpeed(double mis)
	{
		minSpeed = mis;
	}
	
	public double getMinSpeed()
	{
		return minSpeed;
	}
	
	public void setMaxSpeed(double ms)
	{
		maxSpeed = ms;
	}
	
	public double getMaxSpeed()
	{
		return maxSpeed;
	}
	
	/**
	 * Does a BFS (starting at head particle)
	 * through all neighbors to decide which ones are still
	 * part of the blob.
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
	 * The list of new Blobs (if any) created
	 */
	public LinkedList<Blob> findStragglers()
	{
		return new LinkedList<Blob>();
	}
	
	/**
	 * Applies forces to all particles in this blob,
	 * marking them as touched
	 */
	public void applyForces()
	{
		
	}
	
	/**
	 * Sets the "touched" variable on all constituent particles
	 * to false before a search
	 */
	public void wipeClean()
	{
		
	}
}
