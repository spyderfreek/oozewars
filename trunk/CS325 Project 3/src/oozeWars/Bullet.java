package oozeWars;

public class Bullet extends Particle 
{
	private double damage;
	private double orientation;
	// TODO get graphics here
	
	
	public Bullet(double x, double y, double radius, double orientation) 
	{
		super(x, y, radius);
		this.orientation = orientation;
	}
	
	public void explode()
	{
		
	}

}
