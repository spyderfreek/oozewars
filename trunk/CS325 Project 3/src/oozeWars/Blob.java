package oozeWars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

import com.jhlabs.image.Colormap;
import com.jhlabs.image.FadeFilter;
import com.jhlabs.image.LinearColormap;
import com.jhlabs.image.PointFilter;
import com.jhlabs.image.PremultiplyFilter;

import oozeWars.OozeWars.PlayerControls;

public class Blob extends Entity 
{
	private ArrayList<Particle> particles;
	private Head head;
	private Color color;
	private double minSpeed = .5, maxSpeed = 10, friction = .97, accel, health = 0, blobForce = .001;
	private double comfyDistance = 20;
	private int coolDown = 10;
	private boolean fireReady = true;
	private int blobID;
	private HealthBar healthBar;
	private int lastNumParticles;
	private static Sound slurp = initializeSound();
	
	private static Sound initializeSound()
	{
		try {
			return new Sound( Blob.class.getResourceAsStream("slurp.wav"), true );
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Used to create a new Blob at a given spot, in a specified orientation, with
	 * a specified number of particles, and with a particular color.  Also initializes
	 * the Blob's health and ID.
	 * @param x
	 * :  The x location that the Head of the Blob will be placed.
	 * @param y
	 * :  The y location that the Head of the Blob will be placed.
	 * @param orientation
	 * :  The angle that the Head of the Blob will be facing (in Radians).
	 * @param numParticles
	 * :  The number of particles the Blob will contain.
	 * @param blobID
	 * :  The ID that this Blob will have as well as all of its Particles.
	 * @param game
	 * :  The game that this Blob will be added to.
	 * @param color
	 * :  The color the Blob will be.
	 */
	public Blob(double x, double y, double orientation, int numParticles, int blobID, OozeWars game, Color color) 
	{
		super(x, y);
		this.color = color;
		this.blobID = blobID;
		particles = new ArrayList<Particle>();
		
		//TODO:  Figure out default size for head
		head = new Head(x, y, 8, color, blobID, orientation);
		head.setBlobID(this.blobID);
		particles.add(head);
		
		while(numParticles-- > 0)
		{
			Particle aParticle = new Particle(x + game.random.nextDouble()*80 - 40, 
					y +	game.random.nextDouble()*80-40, game.random.nextInt(5) + 3, color, blobID);
			aParticle.setBlobID(blobID);
			particles.add(aParticle);
		}

		init( game );
		
	}
	
	/**
	 * Constructor for uncontrolled blobs from existing detached particles.  
	 * This Blob will be flagged as neutral and will have an ID = 0;
	 * @param particles 
	 * : An initialization list of particles that this Blob will contain.
	 * @param game
	 * :  The game that this Blob will be added to.
	 */
	public Blob( ArrayList<Particle> particles, OozeWars game )
	{
		super(0,0);
		color = Color.lightGray;
		this.particles = particles;
		head = null;
		blobID = 0;
		
		init( game );
		
	}
	
	public void init( OozeWars game )
	{
		updateHealth();
		healthBar = new HealthBar(10, 10 + 15 * blobID, 100, 10, this, color);
		game.queue.schedule(1, healthBar);
		/*
		final int divisor = 2;
		backBuf = new BufferedImage(game.getWidth()>>divisor, game.getHeight()>>divisor, BufferedImage.TYPE_INT_ARGB);
		frontBuf = new BufferedImage(game.getWidth()>>divisor, game.getHeight()>>divisor, BufferedImage.TYPE_INT_ARGB);
		int[] knots = { 0xffff0000, 0xff00ff00, 0xff0000ff };
		//colorMap = new Gradient(knots);
		colorMap = new LinearColormap();
		filter = new LookupFilter( colorMap );
		threshold = new ThresholdFilter(0);
		threshold.setLowerThreshold(0);
		threshold.setUpperThreshold(255);
		*/
	}
	
	/**
	 * Checks if a Blob is equal to another Object based on whether the Object is
	 * a Blob.  If it so happens that the other Object is a Blob, the two Blobs are
	 * equal if they have the same blobID.
	 * 
	 * @param theOther
	 * :  The Object that this Blob will check equality for.
	 * @return
	 * <b>TRUE</b> if the two Blobs are equal.
	 * <p><b>FALSE</b> if the two Blobs are not equal.</p>
	 */
	public boolean equals(Object theOther)
	{
		if (theOther == null || !(theOther instanceof Blob)) 
			return false;
		Blob other = (Blob) theOther;
		
		return (blobID == other.getBlobID());
	}
	
	/**
	 * Uses a simple hashing method for getting an int to represent the current Blob.
	 * @param obj
	 * :  The Object for which the hash code will be returned.
	 * @return
	 * <b>blobID</b> if the object is a Blob.
	 * <p><b>0</b> if the object is not a Blob.</p>
	 */
	public int hashCode(Object obj)
	{
		if(obj instanceof Blob)
			return ((Blob)obj).getBlobID();
		
		return 0;
	}
	
	/**
	 * A method to return the friction constant that is currently affecting this Blob.
	 * @return
	 * The friction constant currently affecting this Blob.
	 */
	public double getFriction()
	{
		return friction;
	}
	
	/**
	 * Returns the Blob's current ID number.
	 * @return
	 * The int representing the Blob's current ID number.
	 */
	public int getBlobID() 
	{
		return blobID;
	}
	
	/**
	 * Sets the Blob's ID number to the value given for blobID
	 * @param blobID
	 * The Blob's new ID number
	 */
	public void setBlobID(int blobID) 
	{
		this.blobID = blobID;
	}

	/**
	 * A method to retrieve the Particle that is the Head of this Blob.
	 * @return
	 * The Particle that is the Head of this Blob.
	 */
	public Head getHead() 
	{
		return head;
	}

	/**
	 * A method used to draw the specified Blob.  Uses Particle's draw() method for each of
	 * the Particles in the Blob.
	 * @param graphics
	 * :  The Java graphics that will be used to draw this Blob on screen.
	 * @param game
	 * :  The game that this Blob will be drawn for.
	 */
	@Override
	public void draw(Graphics2D graphics, Game game) 
	{
		OozeWars ow = (OozeWars) game;
		OozeView ov = (OozeView) ow.view;

		/*float[] scalars = {0.95f};
		float[] offsets = {0f};
		RenderingHints rh = new RenderingHints(null);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		*/
		//new RescaleOp( scalars, offsets, rh).filter(frontBuf.getAlphaRaster(), backBuf.getAlphaRaster());
		//new RescaleFilter( 0.95f ).filter(image, image);
		//new FadeFilter( 0.98f ).filter(frontBuf, backBuf);
		
		
		//Graphics2D g = backBuf.createGraphics();
		double scale = ov.SCALE;
		
		for( Particle p : particles )
			p.draw(graphics, game, color, scale );
		
		healthBar.draw(graphics, game, scale);
		
		//g.dispose();

		
		//BufferedImage temp = backBuf;
		//backBuf = frontBuf;
		//frontBuf = temp;
			
		//filter.filter(frontBuf, backBuf);
		//threshold.filter(backBuf, frontBuf);
		//graphics.drawRenderedImage(threshold.filter(new PremultiplyFilter().filter(frontBuf,null), null), AffineTransform.getScaleInstance(4, 4));
		//graphics.drawRenderedImage( frontBuf, AffineTransform.getScaleInstance(4, 4));
		
		//ScaleFilter s = new ScaleFilter(ow.getWidth(), ow.getHeight());
		//graphics.drawRenderedImage( s.filter(frontBuf, null), null);
	}

	/**
	 * Instructions for this Blob to carry out each timestep of the game.
	 * @param game
	 * :  The game this Blob will be taking action in.
	 * @param timestep
	 * :  The tick number that the Blob will be scheduled for.
	 * @param priorityLevel
	 * :  The importance the Blob carries for performing its actions.
	 */
	@Override
	public void go(Game game, long timestep, int priorityLevel) 
	{		
		OozeWars g = (OozeWars)game;
		updateHealth();
		
		//System.out.println("Blob go");
		
		// ignore head for AI-less blobs
		if( ( head != null ) && ( head.isDead() || particles.isEmpty() ) )
			setDead(true);
		else
		{
			Particle p;
			for(int i = 0; i < particles.size(); i++)
			{
				p = particles.get(i);
				
				if( p.isDead() )
				{
					g.removeParticle(p);
					removeParticle(i);
					i--;
					continue;
				}
				
				p.go(game, timestep, priorityLevel, minSpeed, maxSpeed, friction);
			}
		}
		
		if(head != null)
		{
			if( particles.size() > lastNumParticles )
				slurp.play();
			
			PlayerControls pc = g.getControls()[blobID-1];
			
			if(pc.isFire() && isFireReady())
			{
				Bullet b = shoot();
				g.queue.scheduleIn(coolDown, priorityLevel, new GunEnabler(this) );
				
				if( b == null)
					return;
				
				g.queue.schedule(1, b );
				g.view.addSprite(b, 1);
			}
		}
	}
	
	
	/**
	 * Picks the largest particle in the Blob, removes it, places it in front of the head,
	 * and shoots it in the direction the Head is facing.
	 * 
	 * @return
	 * The new Bullet with the attributes of the Particle that was chosen to be shot.
	 */
	public Bullet shoot()
	{
		setFireReady(false);
		
		Particle biggest = null;
		for(int i = particles.size() - 1; i > -1; i--)
		{
				biggest = particles.get(i);
				if( biggest != null && !biggest.isDead() )
					break;
		}
		
		if(biggest == head)
			return null;
		
		biggest.setDead(true);
		//particles.remove(particles.size() - 1);
		
		head.calcOrientation();
		double bigRad = biggest.getRadius();
		return new Bullet(head.getX(), head.getY(), bigRad, color, blobID, head.getOrientation(), 15);
	}
	
	/**
	 * Method to retrieve the Blob's current health.
	 * @return
	 * The Blob's current health.
	 */
	public double getHealth()
	{
		return health;
	}
	
	/**
	 * @param lastNumParticles the lastNumParticles to set
	 */
	public void setLastNumParticles(int lastNumParticles) {
		this.lastNumParticles = lastNumParticles;
	}

	/**
	 * Sets the minimum speed that the Blob can go.  As speed is an absolute value, 
	 * any negative values will be set to positive values.  Lowest minimum speed = 0.  
	 * The minimum speed must be less than or equal to the maximum speed.
	 * @param mis
	 * :  The double value that the minimum speed will be set to.
	 */
	public void setMinSpeed(double mis)
	{
		if(mis > maxSpeed)
		{
			JOptionPane.showMessageDialog(null, "Invalid minimum speed.");
			System.exit(1);
		}
		if(mis < 0)
			mis = -mis;

			minSpeed = mis;
	}
	
	/**
	 * A method to get the minimum speed of the Blob.
	 * @return
	 * The minimum speed of the Blob.
	 */
	public double getMinSpeed()
	{
		return minSpeed;
	}
	
	/**
	 * Sets the maximum speed that the Blob can go.  As speed is an absolute value, 
	 * any negative values will be set to positive values.  The maximum speed must be 
	 * greater than zero and at least equal to the minimum speed.
	 * @param ms
	 * :  The maximum speed the Blob can go.
	 */
	public void setMaxSpeed(double ms)
	{
		if(ms == 0 || ms < minSpeed)
		{
			JOptionPane.showMessageDialog(null, "Invalid maximum speed.");
			System.exit(1);
		}
		if(ms < 0)
			ms = -ms;
		maxSpeed = ms;
	}
	
	/**
	 * A method to get the maximum speed of the Blob.
	 * @return
	 * The current maximum speed of the Blob.
	 */
	public double getMaxSpeed()
	{
		return maxSpeed;
	}
	
	public int getCoolDown() 
	{
		return coolDown;
	}

	public void setCoolDown(int coolDown) 
	{
		this.coolDown = coolDown;
	}
	
	/**
	 * Tells whether the Blob's cooldown is over.
	 * @return
	 * <b>TRUE</b> if the Blob is ready to shoot.
	 * <p><b>FALSE</b> if the Blob is not yet ready to shoot.
	 */
	public boolean isFireReady() 
	{
		return fireReady;
	}
	
	/**
	 * Sets the Blob's fireReady variable to the given boolean value.
	 * @param fireReady
	 * :  The value that this.fireReady will be set to.
	 */
	public void setFireReady(boolean fireReady) 
	{
		this.fireReady = fireReady;
	}

	/**
	 * Sets the force that the Particles in the Blob will have on eachother.
	 * @param blobForce
	 * The value that the force of the Blob will be set to.
	 */
	public void setBlobForce(double blobForce) 
	{
		this.blobForce = blobForce;
	}
	
	/**
	 * Gets this Blob's force that all of it's Particles apply on eachother.
	 * @return
	 * The value for blobForce.
	 */
	public double getBlobForce() 
	{
		return blobForce;
	}

	/**
	 * @param comfyDistance 
	 * :  the comfyDistance to set
	 */
	public void setComfyDistance(double comfyDistance) 
	{
		this.comfyDistance = comfyDistance;
	}

	/**
	 * @return 
	 * the comfyDistance
	 */
	public double getComfyDistance() 
	{
		return comfyDistance;
	}
	
	/**
	 * Sets all the Particles in this Blob to the Blob's current color.
	 */
	public void setParticlesColor()
	{
		for(Particle p : particles)
			p.color = color;
	}
	
	/**
	 * Removes the tail Particle in this Blob's Particles and replaces it with
	 * the ith Particle.
	 * @param i
	 * The element that will now be the tail Particle.
	 */
	public void removeParticle( int i )
	{
		int size = particles.size();
		Particle last = particles.remove( size - 1);
		
		if( i != size - 1 )
			particles.set(i, last);
	}
	
	/**
	 * Uses the collection of particles in this blob to find its new health.
	 */
	public void updateHealth()
	{
		health = (head != null) ? head.getRadius() : 0;
		for(Particle p:  particles)
			health += p.getRadius();
	}
	
	/**
	 * Receives an ArrayList of Particles and replaces its current particles
	 * with the new ones in the ArrayList passed.
	 * @param particles
	 * The <code>ArrayList&#60Particle&#62</code> that this Blob's particles will
	 * be set to.
	 */
	public void setParticles(ArrayList<Particle> particles)
	{
		this.particles = particles;
	}
	
	/**
	 * Returns the <code>ArrayList&#60Particle&#62</code> that this Blob currently
	 * contains.
	 * @return
	 * The <code>ArrayList&#60Particle&#62</code> of Particles that this Blob currently
	 * contains.
	 */
	public ArrayList<Particle> getParticles()
	{
		return particles;
	}
}
