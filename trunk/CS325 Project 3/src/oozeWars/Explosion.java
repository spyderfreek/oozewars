package oozeWars;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

//Moves particles around (naturally)
class Explosion extends Entity
{
	// determines how quickly the falloff function goes to zero:
	// higher values indicate a steeper drop-off
	static final double SCALE_FACTOR = 5;
	// Shape used to draw explosion animation
	Shape star = createStar();
	// max radius of the explosion
	double radius;
	// max acceleration to apply to a particle
	double accel;
	// time for the explosion to reach max radius, in ms
	int duration;
	// time for the image to stay on the screen
	int bangDuration;
	double damage;
	// elapsed time from start of the explosion
	int time; 
	// Have all particles been moved?
	boolean particlesPushed;
	// min and max squared distance of particles pushed in the current timestep
	double minR2, maxR2;
	// current transformation of the visual bang
	AffineTransform transform;
	// transparency of the image
	float alpha;
	
	ArrayList<Particle> targets;
	
	// create a multi-pointed star
	Shape createStar()
	{
		Polygon poly = new Polygon();
		Point p0 = new Point(30,0);
		Point p1 = new Point(10, 5);
		poly.addPoint( p0.x, p0.y );
		poly.addPoint(p1.x, p1.y);
	
		// rotate and add more points
		AffineTransform rot = new AffineTransform();
		rot.rotate(Math.PI / 5);
		
		// create 10 points
		for(int i = 0; i < 9; i++)
		{
			rot.transform(p0, p0);
			rot.transform(p1, p1);
			poly.addPoint( p0.x, p0.y );
    		poly.addPoint(p1.x, p1.y);
		}
		
		return poly;
	}
	
	public Explosion( double x, double y, double rad, double acc, int dur, double damage, ArrayList<Particle> particles)
	{
		super(x, y);
		radius = rad;
		accel = acc;
		duration = dur;
		this.damage = damage;
		bangDuration = 8 * dur;
		particlesPushed = false;
		minR2 = maxR2 = 0;
		time = 0;
		targets = particles;
		
		// initialize visual's scale and position
		transform = new AffineTransform();
		transform.translate( x, y );
		transform.scale(0.01, 0.01);
	}
	
	@Override
	public void go(Game game, long timestep, int priorityLevel)
	{
		time++;
		OozeWars g = (OozeWars)game;
		super.go(game, timestep, priorityLevel);
		updateVisual();
		
		// while particles get updated rapidly to avoid multiple pushes,
		// the visual needs to stay on the screen longer to be noticed
		if( particlesPushed )
		{
			return;
		}
		
		// determine area of influence
		minR2 = maxR2;
		maxR2 = lerp(time, duration, 0, radius);
		maxR2 *= maxR2;
		
		/**
		 * Move particles
		 */
		// velocity to add
		double vx, vy;
		// magnitude of added velocity
		double speed;
		// distance from epicenter to particle
		double dist, dist2;
		
		for( Particle p : targets )
		{
			vx = p.getX() - x;
			vy = p.getY() - y;
			dist2 = vx * vx + vy * vy;
			
			// ignore particles outisde the zone of influence
			if( dist2 > maxR2 || dist2 <= minR2 )
				continue;
			
			dist = Math.sqrt(dist2);
			speed = falloff( dist, radius, accel);
			p.damage(speed * damage);
			
			speed /= dist;
			vx *= speed;
			vy *= speed;
			
			p.push(vx, vy);  			
		}
		
		
		if( time >= duration )
		{
			particlesPushed = true;
		}
		
		
	}
	
	/**
	 * Update the image transformation
	 */
	private void updateVisual()
	{
		// scale relative to the bomb's power
		double scale = 1.1 + falloff( time, bangDuration, accel / 16 );
		transform.scale(scale, scale);
		alpha = (float)lerp( time, bangDuration, 1, 0 );
		
		// once visual is done, stop updating this sprite
		if( time >= bangDuration )
		{
			setDead(true);
		}
	}
	
	// returns the result of an inverse-square falloff curve
	double falloff(double t, double maxT, double maxF)
	{
		double st2 = SCALE_FACTOR * t / maxT;
		st2 *= st2;
		double stm2 = SCALE_FACTOR;
		stm2 *= stm2;
		return maxF / (st2 + 1) - maxF / (stm2 + 1);
	}
	
	// returns a linear interpolation between minF and maxF for the
	// function ranging from t = 0 to maxT
	double lerp(double t, double maxT, double minF, double maxF)
	{
		double range = maxF - minF;
		return t * range / maxT + minF;
	}
	
	// draw the explosion graphics
	@Override
	public void draw(Graphics2D g, Game game)
	{
		// inner red star is half the size of outer yellow
		AffineTransform inner = (AffineTransform)transform.clone();
		inner.scale(0.5, 0.5);
		
		g.setColor( new Color(0.8f, 0.2f, 0f, alpha));
		g.fill(transform.createTransformedShape(star));
		g.setColor(new Color(0.8f, 0.8f, 0f, alpha));
		g.fill(inner.createTransformedShape(star));
	}
}