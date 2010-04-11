package oozeWars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.RescaleOp;
import java.util.*;
import javax.swing.JOptionPane;

import com.jhlabs.image.Colormap;
import com.jhlabs.image.FadeFilter;
import com.jhlabs.image.Gradient;
import com.jhlabs.image.LinearColormap;
import com.jhlabs.image.LookupFilter;
import com.jhlabs.image.RescaleFilter;
import com.jhlabs.image.ScaleFilter;

import oozeWars.OozeWars.PlayerControls;

public class Blob extends Entity 
{
	private ArrayList<Particle> particles;
	private Head head;
	private Color color;
	private double orientation, minSpeed = .5, maxSpeed = 10, friction = .6, accel, health = 0, blobForce = 5;
	private double comfyDistance = 30;
	private int coolDown = 60;
	private boolean fireReady = true;
	private int blobID;
	private BufferedImage backBuf, frontBuf;
	private Colormap colorMap;
	private LookupFilter filter;
	
	/**
	 * Used to create a new Blob at a given spot, in a specified orientation, with
	 * a specified number of particles, and with a particular color.  Also initializes
	 * the Blob's health and ID.
	 * @param x
	 * :  The x location that the Head of the Blob will be placed
	 * @param y
	 * :  The y location that the Head of the Blob will be placed
	 * @param orientation
	 * :  The angle that the Head of the Blob will be facing (in Degrees)
	 * @param numParticles
	 * :  The number of particles the Blob will contain
	 * @param color
	 * :  The color the Blob will be
	 */
	public Blob(double x, double y, double orientation, int numParticles, int blobID, OozeWars game, Color color) 
	{
		super(x, y);
		this.color = color;
		this.orientation = orientation;
		this.blobID = blobID;
		particles = new ArrayList<Particle>();
		
		//TODO:  Figure out default size for head
		head = new Head(x, y, 20, color, orientation);
		head.setBlobID(this.blobID);
		particles.add(head);
		
		while(numParticles-- > 0)
		{
			Particle aParticle = new Particle(x + game.random.nextDouble()*80 - 40, 
					y +	game.random.nextDouble()*80-40, game.random.nextInt(10)<<1, color);
			aParticle.setBlobID(blobID);
			particles.add(aParticle);
		}

		init( game );
		
		updateHealth();
	}
	
	/**
	 * Constructor for uncontrolled blobs from existing detached particles.  
	 * This Blob will be flagged as neutral and will have an ID = 0;
	 * @param particles 
	 * : An initialization list of particles that this Blob will contain.
	 * @param game TODO
	 */
	public Blob( ArrayList<Particle> particles, OozeWars game )
	{
		super(0,0);
		color = Color.WHITE;
		orientation = 0;
		this.particles = particles;
		head = null;
		blobID = 0;
		
		init( game );
		
	}
	
	public void init( OozeWars game )
	{
		backBuf = new BufferedImage(game.getWidth()>>2, game.getHeight()>>2, BufferedImage.TYPE_INT_ARGB);
		frontBuf = new BufferedImage(game.getWidth()>>2, game.getHeight()>>2, BufferedImage.TYPE_INT_ARGB);
		int[] knots = { 0xffff0000, 0xff00ff00, 0xff0000ff };
		//colorMap = new Gradient(knots);
		colorMap = new LinearColormap();
		filter = new LookupFilter( colorMap );
	}
	
	public boolean equals(Object theOther)
	{
		if (theOther == null || !(theOther instanceof Blob)) 
			return false;
		Blob other = (Blob) theOther;
		
		return (blobID == other.getBlobID());
	}
	
	public int hashCode(Object obj)
	{
		return (int)blobID;
	}
	
	public double getFriction()
	{
		return friction;
	}
	
	/**
	 * Returns the Blob's current ID number.
	 * @return
	 * The Blob's current ID number.
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

	public Head getHead() 
	{
		return head;
	}

	/**
	 * A method used to draw the specified Blob.  Uses Particle's draw() method for each of
	 * the Particles in the Blob.
	 */
	@Override
	public void draw(Graphics2D graphics, Game game) 
	{
		OozeWars ow = (OozeWars) game;

		/*float[] scalars = {0.95f};
		float[] offsets = {0f};
		RenderingHints rh = new RenderingHints(null);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		*/
		//new RescaleOp( scalars, offsets, rh).filter(frontBuf.getAlphaRaster(), backBuf.getAlphaRaster());
		//new RescaleFilter( 0.95f ).filter(image, image);
		new FadeFilter( 0.95f ).filter(frontBuf, backBuf);
		
		
		Graphics2D g = backBuf.createGraphics();
		
		for( Particle p : particles )
			p.draw(g, game, color);
		
		g.dispose();

		
		BufferedImage temp = backBuf;
		backBuf = frontBuf;
		frontBuf = temp;
			
		filter.filter(frontBuf, backBuf);
		graphics.drawRenderedImage(frontBuf, AffineTransform.getScaleInstance(4, 4));
		//graphics.drawRenderedImage( frontBuf, AffineTransform.getScaleInstance(1, 1));
		
		//ScaleFilter s = new ScaleFilter(ow.getWidth(), ow.getHeight());
		//graphics.drawRenderedImage( s.filter(frontBuf, null), null);
	}

	
	@Override
	public void go(Game game, long timestep, int priorityLevel) 
	{		
		OozeWars g = (OozeWars)game;
		
		// ignore head for AI-less blobs
		boolean headDead = ( head == null ) ? false : head.isDead();
		
		if(particles.isEmpty() || headDead )
			setDead(true);
		else
		{
			for(Particle p : particles)
			{
				p.go(game, timestep, priorityLevel, minSpeed, maxSpeed, friction);
			}
		}
		
		if(head != null)
		{
			updateHealth();
			PlayerControls pc = g.getControls()[blobID-1];
			
			if(pc.isFire())
			{
				shoot();
				g.queue.scheduleIn(coolDown, 0, new GunEnabler(this) );
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
		
		Iterator<Particle> it = particles.iterator();
		if( !it.hasNext() )
			return null;
		Particle biggest = it.next();
		
		if(biggest instanceof Head && it.hasNext())
			biggest = it.next();
		else
			return null;
		
		while(it.hasNext())
		{
			Particle theOther = it.next();
			if( biggest.compareTo(theOther) == -1 )
				biggest = theOther;
		}
		//We must remove a Particle with the same sized radius as the biggest.. doesn't necessarily
		//have to be the Particle that we found as the biggest.
		it = particles.iterator();
		while(it.hasNext())
		{
			Particle theOther = it.next();
			if(theOther instanceof Head && it.hasNext())
				theOther = it.next();
			else
				return null;
			
			if(biggest.compareTo(theOther) == 0)
			{
				it.remove();
				break;
			}
		}
		
		head.calcOrientation();
		double bigRad = biggest.getRadius();
		return new Bullet(head.getX()+bigRad, head.getY()+bigRad, bigRad, color, head.getOrientation());
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
	
	public int getCoolDown() {
		return coolDown;
	}

	public void setCoolDown(int coolDown) {
		this.coolDown = coolDown;
	}

	public boolean isFireReady() {
		return fireReady;
	}

	public void setFireReady(boolean fireReady) {
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
	
	public void setParticlesColor()
	{
		for(Particle p : particles)
			p.color = color;
	}
	
	/**
	 * Uses the collection of particles in this blob to find its new health.
	 */
	public void updateHealth()
	{
		health = head.getRadius();
		for(Particle p:  particles)
			health += p.getRadius();
	}
	
	public void setParticles(ArrayList<Particle> particles)
	{
		this.particles = particles;
	}
	
	public ArrayList<Particle> getParticles()
	{
		return particles;
	}
	
}
