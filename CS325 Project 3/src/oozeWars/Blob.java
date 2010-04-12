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
import com.jhlabs.image.PointFilter;
import com.jhlabs.image.PremultiplyFilter;
import com.jhlabs.image.RescaleFilter;
import com.jhlabs.image.ScaleFilter;
import com.jhlabs.image.ThresholdFilter;

import oozeWars.OozeWars.PlayerControls;

public class Blob extends Entity 
{
	private ArrayList<Particle> particles;
	private ArrayList<Bullet> bullets;
	private Head head;
	private Color color;
	private double orientation, minSpeed = .5, maxSpeed = 10, friction = .97, accel, health = 0, blobForce = .001;
	private double comfyDistance = 20;
	private int coolDown = 10;
	private boolean fireReady = true;
	private int blobID;
	private BufferedImage backBuf, frontBuf;
	private Colormap colorMap;
	private LookupFilter filter;
	private ThresholdFilter threshold;
	private HealthBar healthBar;
	
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
		bullets = new ArrayList<Bullet>();
		
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
	 * @param game TODO
	 */
	public Blob( ArrayList<Particle> particles, OozeWars game )
	{
		super(0,0);
		color = Color.lightGray;
		orientation = 0;
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
		//new FadeFilter( 0.98f ).filter(frontBuf, backBuf);
		
		
		//Graphics2D g = backBuf.createGraphics();
		double scale = ((OozeView)game.view).SCALE;
		
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
			PlayerControls pc = g.getControls()[blobID-1];
			
			if(pc.isFire() && isFireReady())
			{
				Bullet b = shoot();
				if( b == null)
					return;
				
				g.queue.schedule(1, b );
				g.view.addSprite(b, 1);
				g.queue.scheduleIn(coolDown, priorityLevel, new GunEnabler(this) );
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
		
		Particle biggest = particles.get(particles.size() - 1);
		
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
	
	public void setParticles(ArrayList<Particle> particles)
	{
		this.particles = particles;
	}
	
	public ArrayList<Particle> getParticles()
	{
		return particles;
	}
	
}
