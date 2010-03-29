package oozeWars;

public class Head extends Particle 
{

	// TODO get graphics here
	private double orientation;
	
	/**
	 * Creates a Head for a blob with a specified location, radius, and orientation.
	 * Uses Particle's constructor as a Head is a special type of Particle.
	 * @param x
	 * :  The x location of the center of the Head.
	 * @param y
	 * :  The y location of the center of the Head.
	 * @param radius
	 * :  The radius of the Head.
	 * @param orientation
	 * :  The angle at which the head will be facing when it first appears (in Degrees).
	 */
	public Head(double x, double y, double radius, double orientation) 
	{
		super(x, y, radius);
		this.orientation = orientation;
	}
	
	/**
	 * The head applies a stronger force to neighboring particles
	 * than other particles.
	 */
	@Override
	public void applyForce(Particle neighbor)
	{
		
	}

}
