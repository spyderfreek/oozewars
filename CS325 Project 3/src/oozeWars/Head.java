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
	 * :  The angle at which the head will be facing when it first appears (in Degrees).
	 */
	public Head(double x, double y, double radius, Color color, double orientation) 
	{
		super(x, y, radius, color);
		this.orientation = orientation;
	}
	
	/**
	 * The head applies a stronger force to neighboring particles
	 * than other particles.
	 */
	@Override
	public void applyForce(Particle neighbor, double k, double distance, double dx, double dy, double comfyDistance, double range)
	{
		k *= 1.5;
		super.applyForce(neighbor, k, distance, dx, dy, comfyDistance, range);
	}
	
	/**
	 * @return
	 * The current orientation (in Degrees) of the Head
	 */
	public double getOrientation()
	{
		return orientation;
	}
	/* (non-Javadoc)
	 * @see oozeWars.Particle#go(oozeWars.Game, long, int)
	 */
	@Override
	public void go(Game game, long timestep, int priorityLevel, double minSpeed, double maxSpeed, double friction) 
	{
		OozeWars ow = (OozeWars)game;
		PlayerControls pc = ow.getControls()[blobID-1];
		
		if(pc.isDown())
		{
			vy += 5;
		}
		if(pc.isUp())
		{
			vy -= 5;
		}
		if(pc.isLeft())
		{
			vx -= 5;
		}
		if(pc.isRight())
		{
			vx += 5;
		}
		if(pc.isFire())
		{ //TODO:  Change this
			ow.removePlayer(blobID-1);
		}
		
		super.go(game, timestep, priorityLevel, minSpeed, maxSpeed, friction);
	}
	
	

}
