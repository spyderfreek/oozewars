package oozeWars;

public class Head extends Particle 
{

	// TODO get graphics here
	private double orientation;
	
	public Head(double x, double y, double radius, double orientation) 
	{
		super(x, y, radius);
		this.orientation = orientation;
	}
	
	/**
	 * The head applies a stronger force to neighboring particles
	 * than other particles
	 */
	@Override
	public void applyForce(Particle neighbor)
	{
		
	}

}
