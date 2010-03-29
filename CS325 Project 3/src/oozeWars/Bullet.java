package oozeWars;

public class Bullet extends Particle 
{
	private double damage;
	private double orientation;
	// TODO get graphics here
	
	/**
	 * Creates a new bullet at a given location with a specific radius and orientation.
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
	public Bullet(double x, double y, double radius, double orientation) 
	{
		super(x, y, radius);
		this.orientation = orientation;
	}
	
	public void explode()
	{
		
	}

}
