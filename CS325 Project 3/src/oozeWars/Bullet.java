package oozeWars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Bullet extends Particle 
{
	private double damage;
	private double orientation;
	private final double range = 400;
	private static Sound[] launch = initializeSound();
	
	private boolean played = false;
	
	/**
	 * Creates a new bullet at a given location with a specific radius and orientation.  Initializes the damage
	 * that this bullet does.
	 * @param x
	 * :  The x location of the center of the Bullet
	 * @param y
	 * :  The y location of the center of the Bullet
	 * @param radius
	 * :  The radius of the Bullet
	 * @param orientation
	 * :  The orientation of the bullet (in Degrees) relative to the orientation of
	 * the Head when it was fired.
	 */
	public Bullet(double x, double y, double radius, Color color, int blobid, double orientation, double speed) 
	{
		super(x, y, radius, color, blobid);
		this.orientation = orientation;
		friction = 1;
		push( speed*Math.cos(orientation), speed*Math.sin(orientation) );
		damage = radius*2;
	}
	
	@Override
	public void go(Game game, long timestep, int priorityLevel)
	{
		if( played == false )
		{
			played = true;
			launch[game.random.nextInt(2)].play();
		}
			
		
		if( x < 0 || y < 0 || x > game.view.getWidth() || y > game.view.getHeight() )
			setDead(true);

		if( ! isDead() )
			game.queue.schedule(priorityLevel, this);
		else
		{
			game.view.removeSprite(this, 1);
			return;
		}
		
		super.go(game, timestep, priorityLevel);
		
		ArrayList<Particle> targets;
		
		for( Blob b : ( ( OozeWars )game ).getBlobs() )
		{
			if( b.getBlobID() == blobID )
				continue;
			
			targets = b.getParticles();
			double dx, dy;
			
			for( Particle p : targets )
			{
				dx = x - p.getX();
				dy = y - p.getY();
				
				/*
				if( dx != dx)
				{
					System.out.println("Bullet.go() failed");
					System.exit(1);
				}*/
				
				if(dx * dx + dy * dy < range)
				{
					Explosion e = explode( targets );
					game.queue.schedule(priorityLevel, e );
					game.view.addSprite(e, 2);
					return;
				}
			}
			
		}	
	}
	
	public Explosion explode( ArrayList<Particle> targets )
	{
		setDead(true);
		return new Explosion(x, y, radius * 6, 5, 6, damage, targets);
	}

	/* (non-Javadoc)
	 * @see oozeWars.Particle#draw(java.awt.Graphics2D, oozeWars.Game, java.awt.Color)
	 */
	@Override
	public void draw(Graphics2D graphics, Game game ) {
		// TODO Auto-generated method stub
		super.draw(graphics, game, color, ((OozeView)game.view).SCALE);
	}
	
	private static Sound[] initializeSound()
	{
		Sound[] sounds = new Sound[3];
		
		for( int i = 0; i < 2; i++)
		{
			try 
			{
				sounds[i] = new Sound(Bullet.class.getResource("launch" + (i+1) + ".wav"), true);
			} 
			catch (UnsupportedAudioFileException e) {e.printStackTrace();} 
			catch (LineUnavailableException e) {e.printStackTrace();}
			catch (IOException e) {e.printStackTrace();}
		}
		
		return sounds;
	}

}
