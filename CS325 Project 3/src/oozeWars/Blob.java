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

/**
 * <center><b>BLOB.JAVA</b></center>
 * The wrapper class for a group of particles that have the same ID.
 * 
 * <p>A Blob is set up by giving an initial x and y coordinate that the center of the Blob's
 * Head will be placed at and facing in the direction of the orientation passed.  The Blob
 * will also contain a set number of particles, a unique ID (unless the Blob is neutral) which
 * will be applied to any particles that are currently in it, and a color that all particles
 * inside the Blob will be set to.  An instance of OozeWars will be passed to the Blob merely
 * for the utilization of its random number generator.
 * 
 * <p>  Another way a Blob can be set up is to pass it an ArrayList&#60Particle&#62 and an
 * instance of OozeWars.  This is particularly useful for creating neutral Blobs as the Blob's
 * ID will be set to that of a neutral Blob.
 * 
 * <p> A Blob holds an ArrayList of Particles that it contains (including the Head Particle), the
 * Color that its Particles will be set to, the minimum and maximum speed that each particle can go,
 * the friction that will be applied to each Particle, an acceleration constant, a number representing
 * the Blob's current health, the force constant that this Blob exerts, and other variables used to maintain
 * the state of the Blob.
 * @author Nick Kitten <br /> Sean Fedak
 *
 */

public class Blob extends Entity 
{
	//The ArrayList of Particles that are part of this Blob
	private ArrayList<Particle> particles;
	
	//The score the player currently has.  Based on the following factors:
	//1) Amount of damage dealt
	//2) Number of particles they have times the amount of health they have
	//TODO:  implement other methods of calculating score
	private long score;
	
	//The total amount of damage that this Blob has done to the other Blobs
	private long damageDealt = 0;
	
	private int powerUpsCollected = 0;
	
	//The Particle that is the Head of this Blob, will be used to control the Blob.
	private Head head;
	
	//The color that all the Particles in this Blob will take on.
	private Color color, baseColor;
	
	//minSpeed:  the minimum speed that each Particle in the Blob can have while moving
	//maxSpeed:  the maximum speed that each Particle in the Blob can have while moving
	//friction:  the amount the speed of each Particle in the Blob will be reduced when
	//			 not accelerating.
	//accel:	 the current acceleration of the Blob.
	//health:	 the sum of all the Particles' radii that are currently in the Blob,
	//			 includes the Head Particle.
	//blobForce: the amount of force that each Particle in the Blob has on one another.
	private double minSpeed = .5, maxSpeed = 10, friction = .97, accel, health = 0, blobForce = .008;
	//The maximum size a particle can grow to, and the amount incremented each time step
	private double maxRadius = 8, growth = 5.0 / 30.0 / 20;
	
	//The distance at which each Particle in the Blob will come to rest from one another.
	private double comfyDistance = 17;
	
	//The time remaining until the player can shoot again.
	private int coolDown = 13;
	
	//Tells whether or not the Blob is ready to shoot again.
	private boolean fireReady = true;
	
	//The ID unique to this Blob.  Each Particle, including the Head, will obtain this ID
	//when they become a part of this Blob.
	private int blobID;
	
	//The health bar for this Blob.  This will grow or shrink depending on the sum of the
	//radii of each Particle that is currently in this Blob's ArrayList of Particles.
	private HealthBar healthBar = null;
	
	// The object which draws the current player's score on the screen
	private Score scoreDisplay;
	
	//The number of Particles that the Blob had prior to this timestep.
	private int lastNumParticles;
	
	//The sound that the Blob will make when sucking up a new Particle.
	private static final Sound slurp = initializeSound();
	
	//Variables to know whether the blob currently has the nitro or god powerups
	private boolean nitro, god;
	
	
	/*
	 * Static method for retrieving the slurping sound that the Blob makes when 
	 * it obtains a new particle.  We use this instead of making the sound when
	 * the first Blob is created so we reduce the lag of loading the sound while
	 * the game is being played.
	 */
	private static Sound initializeSound()
	{
		try 
		{
			return new Sound( Blob.class.getResourceAsStream("slurp.wav"), true );
		} 
		catch (UnsupportedAudioFileException e) {e.printStackTrace();}
		catch (LineUnavailableException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
		
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
		this.color = baseColor = color;
		this.blobID = blobID;
		particles = new ArrayList<Particle>();
		god = nitro = false;
		
		//TODO:  Figure out default size for head
		head = new Head(x, y, 8, color, blobID, orientation);
		particles.add(head);
		
		while(numParticles-- > 0)
		{
			Particle aParticle = new Particle(x + game.random.nextDouble()*80 - 40, 
					y +	game.random.nextDouble()*80-40, game.random.nextInt(5) + 3, color, blobID);
			particles.add(aParticle);
		}

		init( game );
		
		if(blobID == 1)
			scoreDisplay = new Score(10, 25, color, score);
		else if(blobID == 2)
			scoreDisplay = new Score(game.view.getWidth() - 350, 25, color, score);
		game.view.addSprite(scoreDisplay, 2);
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
		growth *= 0.5;
		color = Color.LIGHT_GRAY;
		this.particles = particles;
		head = null;
		blobID = 0;
		god = nitro = false;
	}
	
	/**
	 * Used to initialize information for this Blob. This includes things such as
	 * the Blob's health, creating a health bar for it, and scheduling the health bar
	 * to the queue.
	 * @param game
	 * :  The game in which the Blob will be initialized for.
	 */
	public void init( OozeWars game )
	{
		updateHealth();
		if(blobID == 1)
			healthBar = new HealthBar(10, 30, 100, 10, this, color);
		else if(blobID == 2)
			healthBar = new HealthBar(game.view.getWidth() - 350, 30, 100, 10, this, color);
		game.queue.schedule(1, healthBar);
		score = (long)( health * particles.size() + .5 );
		
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
	 * <b>TRUE</b> if the two Blobs are equal.<br />
	 * <b>FALSE</b> if the two Blobs are not equal.
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
	 * <b>blobID</b> if the object is a Blob.<br />
	 * <b>0</b> if the object is not a Blob.
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
		
		if(healthBar != null)
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
				
				p.go(game, timestep, priorityLevel, minSpeed, maxSpeed, friction, maxRadius, growth);
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
			
			updateScore();
			scoreDisplay.setScore(score);
		}
	}
	
	
	/**
	 * Picks the particle in the Blob at the end of its ArrayList of Particles
	 * , removes it, places it in front of the head,and shoots it in the direction
	 * the Head is facing.
	 * 
	 * @return
	 * The new <b>Bullet</b> with the attributes of the Particle that was chosen to be shot.<br />
	 * <b>NULL</b> if only the head remains
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
		if( nitro ) bigRad *= 1.25;
		return new Bullet(head.getX(), head.getY(), bigRad, color, blobID, head.getOrientation(), 25);
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
	 * A method to retrieve the Blob's current score.
	 * @return
	 * The long int representing the Blob's current score.
	 */
	public long getScore()
	{
		return score;
	}
	
	/**
	 * @param lastNumParticles 
	 * :  The previous number of particles that this Blob contained.
	 */
	public void setLastNumParticles(int lastNumParticles) 
	{
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
	
	/**
	 * Gets the time that the player has to wait in order to shoot again.
	 * @return
	 * The current value for coolDown.
	 */
	public int getCoolDown() 
	{
		return coolDown;
	}

	/**
	 * Sets this.coolDown to the value of the parameter.
	 * @param coolDown
	 * The value that this.coolDown will be set to.
	 */
	public void setCoolDown(int coolDown) 
	{
		this.coolDown = coolDown;
	}
	
	/**
	 * Tells whether the Blob's cooldown is over.
	 * @return
	 * <b>TRUE</b> if the Blob is ready to shoot.<br />
	 * <b>FALSE</b> if the Blob is not yet ready to shoot.
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
	 * :  The value at which the current comfy distance will be set to.
	 */
	public void setComfyDistance(double comfyDistance) 
	{
		this.comfyDistance = comfyDistance;
	}

	/**
	 * @return 
	 * The distance the Particles in this Blob will try to reach.
	 */
	public double getComfyDistance() 
	{
		return comfyDistance;
	}
	
	/**
	 * Returns the blob back to the color that it started as
	 */
	public void backToBaseColor()
	{
		color = baseColor;
	}
	
	/**
	 * Sets the current color of the blob to the value passed
	 * @param color
	 * :  The color that the blob will be set to 
	 */
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	/**
	 * Returns the color that the blob currently is
	 * @return
	 * The color that the blob currently is
	 */
	public Color getColor()
	{
		return color;
	}
	
	/**
	 * Adds a specified number of particles to the blob and the game's data structure
	 * for particles
	 * @param num
	 * :  The number of particles that will be added
	 * @param game
	 * :  The game that the particles will be added to
	 */
	public void addParticles(int num, OozeWars game)
	{
		while(num-- > 0)
		{
			Particle aParticle = new Particle(head.getX() + game.random.nextDouble()*20 - 10, 
					head.getY() + game.random.nextDouble()*20-10, game.random.nextInt(5) + 3, Color.LIGHT_GRAY, blobID);
			particles.add(aParticle);
			game.addParticle(aParticle);
		}
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
	 * Uses the collection of particles in this Blob to find its new health.
	 */
	public void updateHealth()
	{
		health = (head != null) ? head.getRadius() : 0;
		for(Particle p:  particles)
			health += p.getRadius();
	}
	
	/**
	 * Uses values that this Blob is tracking to update its score
	 */
	public void updateScore()
	{
		//We're adding .5 to round to the nearest integer correctly
		score = (long)( health + .5 );
		score += (long)( damageDealt + .5 )<<2;
		score += (long)(powerUpsCollected)*100;
	}
	
	/**
	 * Adds the value passed to the total damage dealt
	 * @param damage
	 * :  The amount of damage that will be added to the total damage that this Blob dealt
	 */
	public void addDamageDealt(long damage)
	{
		damageDealt += damage;
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

	/**
	 * Sets this.nitro value to the value passed
	 * @param nitro
	 * :  The value which this.nitro will be set to
	 */
	public void setNitro(boolean nitro) 
	{
		this.nitro = nitro;
	}

	/**
	 * Returns whether this blob has the nitro powerup active
	 * @return
	 * <b>TRUE</b> if the blob currently has a nitro powerup active<br />
	 * <b>FALSE</b> if the blob does not have a nitro powerup active
	 */
	public boolean isNitro() 
	{
		return nitro;
	}

	/**
	 * Sets the value of this.god to the value passed
	 * @param god
	 * :  The value that this.god will be set to
	 */
	public void setGod(boolean god) 
	{
		this.god = god;
	}

	/**
	 * Returns the value that represents whether this blob currently has the god powerup active
	 * @return
	 * <b>TRUE</b> if the blob currently has a god powerup active<br />
	 * <b>FALSE</b> if the blob does not currently have a god powerup active
	 */
	public boolean isGod() 
	{
		return god;
	}
	
	/**
	 * Fully heals the Blob by setting all the particles currently in it, including
	 * the head, to their max radius and then updates its health
	 */
	public void fullHeal()
	{
		for(Particle p:  particles)
		{
			if(p instanceof Head)
				p.setRadius(16);
			else
				p.setRadius(maxRadius);
		}
		
		updateHealth();
	}
	
	/**
	 * Increments the variable that counts how many PowerUps have been collected
	 */
	public void incrementPowerUpsCollected()
	{
		powerUpsCollected++;
	}
	
	/**
	 * Returns the number of PowerUps that this blob has collected
	 * @return
	 * The number of PowerUps that this blob has collected
	 */
	public int getPowerUpsCollected()
	{
		return powerUpsCollected;
	}
}
