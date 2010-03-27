package oozeWars;

import java.awt.Graphics2D;

public abstract class Entity implements Sprite, Agent
{
	protected double x, y, vx = 0, vy = 0;
	
	public Entity(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void draw(Graphics2D graphics, Game game)
	{
		
	}
}
