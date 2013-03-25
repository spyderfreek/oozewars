package oozeWars;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class PowerUp extends Particle 
{
	public static enum Type {GOD, NITRO, BOOST, GLUE, HEAL};
	private Blob ownerBlob;
	private boolean inBlob, collected;
	private static int ticksOutsideBlob = 15*30;
	private int powerUpTicks;
	private final Type type;
	private PowerupLabel label;
	private final Game game;

	public PowerUp(double x, double y, double radius, Color color, Type type, int powerUpTicks, Game game) 
	{
		super(x, y, radius, color, 0);
		inBlob = collected = false;
		this.type = type;
		this.game = game;
		
		this.powerUpTicks = powerUpTicks;
		Agent powerRemoverAgent = new Agent()
                    { 
                        boolean isScheduled = false;

                        public void go(Game game, long timestep, int priorityLevel)
                        {

                                if(!isScheduled)
                                {
                                        isScheduled = true;
                                        game.queue.scheduleIn(ticksOutsideBlob, 1, this);
                                }
                                else if(!inBlob)
                                {
                                        setDead(true);
                                }
                        }
                    };
		game.queue.schedule(1, powerRemoverAgent);
		game.view.addSprite(label = getLabel(), 2);
	}
	
	public PowerupLabel getLabel( )
	{
		switch( type )
		{
		case GOD:
			return new PowerupLabel((int)x, (int)y, Color.black, "I", this);
		case NITRO:
			return new PowerupLabel((int)x, (int)y, Color.black, "N", this);
		case BOOST:
			return new PowerupLabel((int)x, (int)y, Color.black, "P", this);
		case GLUE:
			return new PowerupLabel((int)x, (int)y, Color.black, "G", this);
		case HEAL:
			return new PowerupLabel((int)x, (int)y, Color.black, "H", this);
		default:
			throw new IllegalArgumentException("Invalid type");
		}
	}
	
	@Override
	public void draw(Graphics2D graphics, Game game, Color col, double scale)
	{
		super.draw(graphics, game, color, scale);
	}
	
	@Override
	public void go(Game game, long timestep, int priorityLevel, 
			double minSpeed, double maxSpeed, double frictn, double maxRadius, double growth )
	{
		if(blobID > 0 && !collected)
		{
			collected = true;
			inBlob = true;
			OozeWars ow = (OozeWars) game;
			for(Blob b : ow.getBlobs())
			{
				if(b.getBlobID() == blobID)
					ownerBlob = b;
			}
			
			ownerBlob.incrementPowerUpsCollected();
			game.queue.schedule(0, createAgent(type));
		}
		super.go(game, timestep, priorityLevel, minSpeed, maxSpeed, frictn, maxRadius, growth);
	}
	
	private Agent createAgent(Type type)
	{
		Agent pAgent;
		
		switch(type)
		{
			case GOD:
			{
				pAgent = new Agent()
				{   boolean isSet = false;
					public void go(Game game, long timestep, int priorityLevel)
					{
						if(!isSet)
						{
							isSet = true;
							ownerBlob.setColor(color);
							ownerBlob.setGod(true);
							
							game.queue.scheduleIn(powerUpTicks, 1, this);
						}
						else
						{
							ownerBlob.backToBaseColor();
							ownerBlob.setGod(false);
							setDead(true);
						}
					}
				};
				break;
			}
			case NITRO:
			{
				pAgent = new Agent()
						{	boolean isSet = false;
							public void go(Game game, long timestep, int priorityLevel)
							{
								if(!isSet)
								{
									isSet = true;
									ownerBlob.setColor(color);
									ownerBlob.setNitro(true);
									
									game.queue.scheduleIn(powerUpTicks, 1, this);
								}
								else
								{
									ownerBlob.backToBaseColor();
									ownerBlob.setNitro(false);
									setDead(true);
								}
							}
						};
				break;
			}
			case BOOST:
			{
				pAgent = new Agent()
						{
							public void go(Game game, long timestep, int priorityLevel)
							{
								ownerBlob.addParticles(10, (OozeWars) game);
								ownerBlob.updateHealth();
								setDead(true);
							}
						};
				break;
			}
			case GLUE:
			{
				pAgent = new Agent()
						{	boolean isSet = false;
							double oldComf = ownerBlob.getComfyDistance();
							double oldForce = ownerBlob.getBlobForce();
							
							public void go(Game game, long timestep, int priorityLevel)
							{
								if(!isSet)
								{
									isSet = true;
									ownerBlob.setColor(color);
									ownerBlob.setComfyDistance(oldComf*.5);
									ownerBlob.setBlobForce(oldForce*2);
									
									game.queue.scheduleIn(powerUpTicks, 1, this);
								}
								else
								{
									ownerBlob.backToBaseColor();
									ownerBlob.setComfyDistance(oldComf);
									ownerBlob.setBlobForce(oldForce);
									setDead(true);
								}
							}
						};
				break;
			}
			case HEAL:
			{
				pAgent = new Agent()
						{
							public void go(Game game, long timestep, int priorityLevel)
							{
								ownerBlob.fullHeal();
								setDead(true);
							}
						};
				break;
			}
			default:
				throw new IllegalArgumentException("Undefined Powerup Type");
		}
		
		return pAgent;
	}

	/* (non-Javadoc)
	 * @see oozeWars.Entity#setDead(boolean)
	 */
	@Override
	public void setDead(boolean dead) 
	{
		super.setDead(dead);
		game.view.removeSprite(label, 2);
	}

}
