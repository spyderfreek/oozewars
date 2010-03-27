package oozeWars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;

public class Particle extends Entity 
{
	// Indicates whether or not this particle has been touched while either
	// (a) applying forces to neighbor particles, or (b) doing a connectivity search on blobs
	private boolean touched = false;
	private double radius;
	private LinkedList<Particle> neighbors;
	Color color;
	// TODO get graphics here

	public Particle(double x, double y, double radius) 
	{
		super(x, y);
		neighbors = new LinkedList<Particle>();
		this.radius = radius;
	}

	public void draw(Graphics2D graphics, Game game, Color color) 
	{
		super.draw(graphics, game);

	}

	@Override
	public void go(Game game, long timestep, int priorityLevel) 
	{
		

	}
	
	public void applyForce(Particle neighbor)
	{
		
	}
	
	public void getNeighbors()
	{
		
	}

}
