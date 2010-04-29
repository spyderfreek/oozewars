package oozeWars;

import java.awt.Color;

import oozeWars.OozeWars.PlayerControls;


public class Head extends Particle 
{

	// TODO get graphics here
	private double orientation;
	
	/**
	 * Creates a Head for a blob with a specified location, radius, and orientation.
	 * Uses Particle's constructor as Head is a special type of Particle.
	 * @param x
	 * :  The x location of the center of the Head.
	 * @param y
	 * :  The y location of the center of the Head.
	 * @param radius
	 * :  The radius of the Head.
	 * @param orientation
	 * :  The angle at which the head will be facing when it first appears (in Radians).
	 */
	public Head(double x, double y, double radius, Color color, int blobid, double orientation) 
	{
		super(x, y, radius, color, blobid);
		inverseMass = 0;
		this.orientation = orientation;
	}
	
	/**
	 * The head applies a stronger force to neighboring particles
	 * than other particles.
	 */
	/*
	@Override
	public void applyForce(Particle neighbor, double k, double distance, double dx, double dy, double comfyDistance)
	{
		k *= 10;
		comfyDistance *= .5;
		super.applyForce(neighbor, k, distance, dx, dy, comfyDistance);
	}
	*/
	
	
	
	public void calcOrientation()
	{
		double vx = x - oldX;
		double vy = y - oldY;
		
		if( vx * vx + vy * vy < .001 )
			return;
		
		orientation = Math.atan2(vy, vx);
	}
	
	/* (non-Javadoc)
	 * @see oozeWars.Particle#damage(double)
	 */
	@Override
	public void damage(double amount) 
	{
		super.damage(amount * 0.2);
	}

	/* (non-Javadoc)
	 * @see oozeWars.Particle#setRadius(double)
	 */
	@Override
	public void setRadius(double newRadius) 
	{
		radius = newRadius;
		halfWidth = (int)newRadius + BLUR_WIDTH;
	}

	/* (non-Javadoc)
	 * @see oozeWars.Particle#applyStickConstraint(oozeWars.Particle, double, double, double, double, double, double)
	 */
	@Override
	public void applyStickConstraint(Particle neighbor, double k,
			double distance, double dx, double dy, double pushDist,
			double pullDist) 
	{
		super.applyStickConstraint(neighbor, k*10, distance, dx, dy, pushDist * 1.1, pullDist * .5);
	}

	/**
	 * @return
	 * The current orientation (in Radians) of the Head
	 */
	public double getOrientation()
	{
		return orientation;
	}
	/* (non-Javadoc)
	 * @see oozeWars.Particle#go(oozeWars.Game, long, int)
	 */
	@Override
	public void go(Game game, long timestep, int priorityLevel, 
			double minSpeed, double maxSpeed, double friction, double maxRadius, double growth )
	{
		OozeWars ow = (OozeWars)game;
		PlayerControls pc = ow.getControls()[blobID-1];
		
		if(pc.isDown())
		{
			y += .2;
		}
		if(pc.isUp())
		{
			y -= .2;
		}
		if(pc.isLeft())
		{
			x -= .2;
		}
		if(pc.isRight())
		{
			x += .2;
		}
		
		super.go(game, timestep, priorityLevel, minSpeed, maxSpeed, friction, maxRadius * 1.25, growth * 0.5);
	}
	
	

}
