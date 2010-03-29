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

	/**
	 * Used to create a new Particle at a given location with a defined radius.
	 * @param x
	 * :  The x location of the center of the Particle
	 * @param y
	 * :  The y location of the center of the Particle
	 * @param radius
	 * :  The radius of the Particle
	 */
	public Particle(double x, double y, double radius) 
	{
		super(x, y);
		neighbors = new LinkedList<Particle>();
		this.radius = radius;
	}

	/**
	 * Used to draw the Particle to the screen.  Uses Entity's draw() method to do so.
	 * @param graphics
	 * :  The graphics that will be used to draw the Particle.
	 * @param game
	 * :  The game that the Particle needs to be drawn for.
	 * @param color
	 * :  The color that the Particle will be.
	 */
	public void draw(Graphics2D graphics, Game game, Color color) 
	{
		this.color = color;
		graphics.setPaint(this.color);
		super.draw(graphics, game);
	}

	@Override
	public void go(Game game, long timestep, int priorityLevel) 
	{
		

	}
	
	/**
	 * Applies a force to the Particle's neighbor, pulling its neighbor closer to it.
	 * @param neighbor
	 * :  The particle in which the force will be applied.
	 */
	public void applyForce(Particle neighbor)
	{
		
	}
	
	/**
	 * Updates the Particle's list of neighboring Particles.
	 */
	public void getNeighbors()
	{
		
	}
	
	/**
	 * Sets the Particle's touched variable to the given value.
	 * @param val
	 * :  The value to which the Particle's touched variable will be set.
	 */
	public void setTouched(boolean val)
	{
		touched = val;
	}
	
	/**
	 * Gets whether the Particle has been touched or not.
	 * @return
	 * :  The current value of touched.
	 */
	public boolean isTouched()
	{
		return touched;
	}
}
