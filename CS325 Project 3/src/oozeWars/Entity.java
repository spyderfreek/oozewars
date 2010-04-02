package oozeWars;

import java.awt.Graphics2D;

public abstract class Entity implements Sprite, Agent
{
	protected double x, y, vx = 0, vy = 0;
	protected boolean dead;
	
	public Entity(double x, double y)
	{
		this.x = x;
		this.y = y;
		dead = false;
	}
	
	public void applyFriction(double minSpeed, double maxSpeed, double friction)
	{
		double speed = Math.sqrt(vx * vx + vy * vy);
		if( speed < minSpeed )
			vx = vy = 0;
		else
		{
			if( speed > maxSpeed )
			{
				double scale = maxSpeed / speed;
				vx *= scale;
				vy *= scale;
			}
			
			vx *= friction;
			vy *= friction;
		}
	}
	
	@Override
	public void draw(Graphics2D graphics, Game game)
	{
		
	}

	/* (non-Javadoc)
	 * @see oozeWars.Agent#go(oozeWars.Game, long, int)
	 */
	@Override
	public void go(Game game, long timestep, int priorityLevel) {
		// TODO Auto-generated method stub
		if(!isDead())
			game.queue.schedule(priorityLevel, this);
		else
			game.view.removeSprite(this, 0);
	}

	/**
	 * @return
	 * TRUE if the Entity is dead.<p></p>
	 * FALSE if the Entity is alive.
	 */
	public boolean isDead() 
	{
		return dead;
	}

	/**
	 * @param dead
	 * The value to set this.dead to.
	 */
	public void setDead(boolean dead) 
	{
		this.dead = dead;
	}
	
	/**
	 * @return
	 * The x value of where the Entity is in space.
	 */
	public double getX()
	{
		return x;
	}
	
	/**
	 * @return
	 * The y value of where the Entity is in space.
	 */
	public double getY()
	{
		return y;
	}
	
	public void push(double dvx, double dvy)
	{
		vx += dvx;
		vy += dvy;
	}
}
