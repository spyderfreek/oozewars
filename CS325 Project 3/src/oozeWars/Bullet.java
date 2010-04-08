package oozeWars;

import java.awt.Color;

public class Bullet extends Particle 
{
	private double damage;
	private double orientation;
	// TODO get graphics here
	
	/**
	 * Creates a new bullet at a given location with a specific radius and orientation.  Initializes the damage
	 * that this bullet does.
	 * @param x
	 * :  The x location of the center of the Bullet
	 * @param y
	 * :  The y location of the center of the Bullet
	 * @param radius
	 * :  The radius of the Bullet
	 * @param orientation
	 * :  The orientation of the bullet (in Degrees) relative to the orientation of
	 * the Head when it was fired.
	 */
	public Bullet(double x, double y, double radius, Color color, double orientation) 
	{
		super(x, y, radius, color);
		this.orientation = orientation;
		vx = 20*Math.cos(orientation);
		vy = 20*Math.sin(orientation);
		damage = radius*1.5;
	}
	
	//TODO:  Implement move() and explode()
	@Override
	public void go(Game game, long timestep, int priorityLevel)
	{
		super.go(game, timestep, priorityLevel);
	}
	
	public void explode()
	{
		
	}

}
