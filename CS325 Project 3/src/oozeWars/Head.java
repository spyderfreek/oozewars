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
		setBlobID((byte) 0);
	}
	
	/**
	 * The head applies a stronger force to neighboring particles
	 * than other particles.
	 */
	@Override
	public void applyForce(Particle neighbor)
	{
		
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
	public void go(Game game, long timestep, int priorityLevel) {
		PlayerControls pc = ((OozeWars)game).getControls()[blobID];
		
		
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
		{
			
		}
		
		applyFriction(1, 8, .95);
		
		super.go(game, timestep, priorityLevel);
	}
	
	

}
