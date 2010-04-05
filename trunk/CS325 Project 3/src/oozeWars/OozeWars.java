package oozeWars;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class OozeWars extends Game 
{
	private int numPlayers;
	private PlayerControls[] controls;
	private LinkedHashMap<Integer, Blob> hBlobs;
	private ArrayList<Particle> allParticles;
	private HashMap<Particle, Location> locations;
	private HashMap<Location, ArrayList<Particle>> particles;
	private ParticleManager manager;
	private final double CELL_WIDTH = 40;
	private int MAX_X, MAX_Y;
	
	/**
	 * The constructor for the game OozeWars.  Calls the constructor for Game.java.  
	 * Initializes the number of players, sets up the controls for each player, initializes 
	 * the number of blobs needed, and sets up the Sparse Grid.
	 * @param maximumFrameRate
	 * :  The number of frames per second that the game will run at.
	 * @param numPlayers
	 * :  The number of players that this game will contain.
	 */
	public OozeWars(double maximumFrameRate, int numPlayers) 
	{
		// only need 2 schedule priority levels for now (?)
		super(2, maximumFrameRate);
		this.numPlayers = numPlayers;
		controls = new PlayerControls[numPlayers];
		hBlobs = new LinkedHashMap<Integer, Blob>();
		allParticles = new ArrayList<Particle>();
		locations = new HashMap<Particle, Location>();
		particles = new HashMap<Location, ArrayList<Particle>>();
		
		for(int i = 0; i < numPlayers; i++)
			controls[i] = setPlayerControls(i);
		
		while(numPlayers-- > 0)
		{
			int id = numPlayers + 1;
			switch (id)
			{
				case(1):
					Blob newBlob = new Blob(100, 100, 0, 4, id, Color.GREEN);
					hBlobs.put(id, newBlob);
					break;
				case(2):
					Blob newerBlob = new Blob(500, 500, 0, 4, id, Color.BLUE);
					hBlobs.put(id, newerBlob);
					break;
				default:
					System.out.println("Invalid number of players");
			}
		}
		
		//adds all the particles currently in game to the Sparse Grid
		for(Blob b: getBlobs())
		{
			for(Particle p: b.getParticles())
				addParticle(p);
		}
		
		manager = new ParticleManager();
	}

	/* (non-Javadoc)
	 * @see oozeWars.Game#start()
	 */
	@Override
	protected void start() 
	{
		for(Blob b : getBlobs())
		{
			view.addSprite(b, 0);
		}
		
		MAX_X = (int)(view.getWidth() / CELL_WIDTH);
		MAX_Y = (int)(view.getHeight() / CELL_WIDTH);
		
		queue.schedule(0, manager);
		super.start();
	}

	/*
	 * (non Java-doc)
	 * Sets the keys that the players will be using to control their Blobs.
	 * As of now, this is only set up for two players.
	 * 
	 * @param player
	 * :  The player number that the controls will be set up for.
	 */
	private PlayerControls setPlayerControls(int player)
	{
		switch( player )
		{
			case 0:
				return new PlayerControls(KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A,
						KeyEvent.VK_D, KeyEvent.VK_SPACE);
			case 1:
				return new PlayerControls(KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD5, 
						KeyEvent.VK_NUMPAD4,KeyEvent.VK_NUMPAD6, KeyEvent.VK_NUMPAD0);
			default:
				throw new IllegalArgumentException("Invalid Player Count");
		}
		

	}
	
	/**
	 * Removes the indicated player from the game and checks for win/loss/draw conditions.
	 * @param player
	 * :  The player number that will be removed from the game.
	 */
	public void removePlayer(int player)
	{
		//controls[player] = null;
		Blob b = hBlobs.remove(player+1);
		ArrayList<Particle> theList = b.getParticles();
		
		Particle head = theList.remove(0);
		removeParticle(head);
		
		b = new Blob(theList);
		
		hBlobs.put(b.getBlobID(), b);

		// TODO: remove event listeners for dead player, check for win / loss conditions.
		int playerLeft = 0;
		if(--numPlayers == 1)
		{
			for(int i = 0; i < controls.length; i++)
			{
				if(controls[i] != null)
					playerLeft = i+1;
			}
			JOptionPane.showMessageDialog(null, "Congratulations player " + playerLeft + ", you won!");
		}
		else if(numPlayers == 0) //There was a draw
		{
			JOptionPane.showMessageDialog(null, "DRAW!");
		}
	}
	
	/**
	 * @return 
	 * The current number of players.
	 */
	public int getNumPlayers() 
	{
		return numPlayers;
	}

	/**
	 * @return 
	 * The controls
	 */
	public PlayerControls[] getControls() 
	{
		return controls;
	}

	/**
	 * @return 
	 * The Blobs currently in the game.
	 */
	public Collection<Blob> getBlobs() 
	{
		return hBlobs.values();
	}

	/**
	 * @param blobs 
	 * :  The new list of blobs in the game.
	 */
	public void setBlobs(ArrayList<Blob> blobs) 
	{
	}
	
	/**
	 * Method to create a Location for the Particle in question.  This is for use in the
	 * implementation of a Sparse Grid.
	 * @param aParticle
	 * :  The Particle for which we wish to find the location.
	 * @return
	 * A Location of the Particle in question.
	 */
	public Location getLocation(Particle aParticle)
	{
		double divX = aParticle.getX()/CELL_WIDTH;
		double divY = aParticle.getY()/CELL_WIDTH;
		int x = (int)divX;
		int y = (int)divY;
		boolean isLeft = divX - x < .5;
		boolean isTop = divY - y < .5;
		return new Location(x, y, allParticles.size(), isLeft, isTop);
	}
	
	/**
	 * A method used to remove a Particle from the Sparse Grid.
	 * @param aParticle
	 * :  The particle that will be removed from the Sparse Grid.
	 */
	public void removeParticle(Particle aParticle)
	{
		if(locations.containsKey(aParticle))
		{
			Location theLocation = locations.remove(aParticle);
			ArrayList<Particle> theParticles = particles.get(theLocation);
			theParticles.remove(aParticle);
			
			if( theParticles.isEmpty() )
				particles.remove(theLocation);
			
			Particle anotherParticle = allParticles.remove(allParticles.size()-1);
			allParticles.set(theLocation.index, anotherParticle);
			locations.get(anotherParticle).index = theLocation.index;
		}
	}
	
	/**
	 * A method to add a Particle to the Sparse Grid.  Can be used to add the same particle to
	 * a different Location on the Sparse Grid.
	 * @param aParticle
	 * :  The Particle that will be added to (or moved in) the Sparse Grid
	 */
	public void addParticle(Particle aParticle)
	{
		if(locations.containsKey(aParticle))
			removeParticle(aParticle);
		
		Location theLocation = getLocation(aParticle);
		
		allParticles.add(aParticle);
		locations.put(aParticle, theLocation);
		
		if( ! particles.containsKey(theLocation) )
		{
			particles.put(theLocation, new ArrayList<Particle>());
		}
		
		particles.get(theLocation).add(aParticle);
	}

	/**
	 * A method used to add Listeners to the game's current View.
	 * @param view
	 * :  The view that the Listeners will be added to.
	 */
	@Override
	protected void registerListeners(View view) 
	{
		view.addKeyListener( new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				for(PlayerControls pc : controls)
				{
					if(e.getKeyCode() == pc.getDownKey())
					{
						pc.setDown(true);
						return;
					}
					else if(e.getKeyCode() == pc.getUpKey())
					{
						pc.setUp(true);
						return;
					}
					else if(e.getKeyCode() == pc.getLeftKey())
					{
						pc.setLeft(true);
						return;
					}
					else if(e.getKeyCode() == pc.getRightKey())
					{
						pc.setRight(true);
						return;
					}
					else if(e.getKeyCode() == pc.getFireKey())
					{
						pc.setFire(true);
						return;
					}
				}
				
				if(e.getKeyCode() == KeyEvent.VK_P )
					togglePaused();
			}
			
			public void keyReleased(KeyEvent e)
			{
				for(PlayerControls pc : controls)
				{
					if(e.getKeyCode() == pc.getDownKey())
					{
						pc.setDown(false);
						return;
					}
					else if(e.getKeyCode() == pc.getUpKey())
					{
						pc.setUp(false);
						return;
					}
					else if(e.getKeyCode() == pc.getLeftKey())
					{
						pc.setLeft(false);
						return;
					}
					else if(e.getKeyCode() == pc.getRightKey())
					{
						pc.setRight(false);
						return;
					}
					else if(e.getKeyCode() == pc.getFireKey())
					{
						pc.setFire(false);
						return;
					}
				}
			}
		});

	}

	/**
	 * The method that runs the game.
	 * @param args
	 */
	public static void main(String[] args) 
	{
		String answer = JOptionPane.showInputDialog("How many players?");
		int n = Integer.parseInt(answer);
		OozeWars game = new OozeWars(30, n);
		View view = new View(game, 1, 800, 600);
		JFrame frame = view.createFrame("Ooze Wars");
		view.setKeystrokeFocus(frame);
		game.reset();
	}
	
	/**
	 * Sets up the key bindings necessary for the controls that the player will be using.
	 * @author Nick Kitten
	 */
	protected static class PlayerControls
	{
		private boolean up, down, left, right, fire;
		private int upKey, downKey, leftKey, rightKey, fireKey;
		
		/**
		 * Constructs the controls for the player. 
		 * @param upKey
		 * :  The key that will be used to make the player go up.
		 * @param downKey
		 * :  The key that will be used to make the player go down.
		 * @param leftKey
		 * :  The key that will be used to make the player go left.
		 * @param rightKey
		 * :  The key that will be used to make the player go right.
		 * @param fireKey
		 * :  The key that will be used to make the player shoot a Bullet.
		 */
		public PlayerControls(int upKey, int downKey, int leftKey, int rightKey, int fireKey)
		{
			this.upKey = upKey;
			this.downKey = downKey;
			this.leftKey = leftKey;
			this.rightKey = rightKey;
			this.fireKey = fireKey;
			up = down = left = right = fire = false;
		}
		
		/**
		 * @return
		 * TRUE if the up key is being pressed.
		 * <p>FALSE otherwise.</p>
		 */
		public boolean isUp() 
		{
			return up;
		}

		/**
		 * Sets the value of this.up to the value provided in the parameter.
		 * @param up
		 * :  The value that this.up will be set to
		 */
		public void setUp(boolean up) 
		{
			this.up = up;
		}

		/**
		 * @return
		 * TRUE if the down key is being pressed.
		 * <p> FALSE otherwise.</p>
		 */
		public boolean isDown() 
		{
			return down;
		}

		/**
		 * Sets the value of this.down to the value provided in the parameter.
		 * @param down
		 * :  The value that this.down will be set to
		 */
		public void setDown(boolean down) 
		{
			this.down = down;
		}

		/**
		 * @return
		 * TRUE if the left key is being pressed.
		 * <p> FALSE otherwise.</p>
		 */
		public boolean isLeft() 
		{
			return left;
		}

		/**
		 * Sets the value of this.left to the value provided in the parameter.
		 * @param left
		 * :  The value that this.left will be set to
		 */
		public void setLeft(boolean left) 
		{
			this.left = left;
		}

		/**
		 * @return
		 * TRUE if the right key is being pressed.
		 * <p> FALSE otherwise.</p>
		 */
		public boolean isRight() 
		{
			return right;
		}

		/**
		 * Sets the value of this.right to the value provided in the parameter.
		 * @param right
		 * :  The value that this.right will be set to
		 */
		public void setRight(boolean right) 
		{
			this.right = right;
		}

		/**
		 * @return
		 * TRUE if the fire key is being pressed.
		 * <p> FALSE otherwise.</p>
		 */
		public boolean isFire() 
		{
			return fire;
		}

		/**
		 * Sets the value of this.fire to the value provided in the parameter.
		 * @param fire
		 * :  The value that this.fire will be set to
		 */
		public void setFire(boolean fire) 
		{
			this.fire = fire;
		}

		/**
		 * @return
		 * The integer value for the key representing up.
		 */
		public int getUpKey() 
		{
			return upKey;
		}

		/**
		 * Sets the up key to the new key represented by the parameter.
		 * @param upKey
		 * :  The integer representing the new key that will move the player up.
		 */
		public void setUpKey(int upKey) 
		{
			this.upKey = upKey;
		}

		/**
		 * @return
		 * The integer value for the key representing down.
		 */
		public int getDownKey() 
		{
			return downKey;
		}

		/**
		 * Sets the down key to the new key represented by the parameter.
		 * @param downKey
		 * :  The integer representing the new key that will move the player down.
		 */
		public void setDownKey(int downKey) 
		{
			this.downKey = downKey;
		}

		/**
		 * @return
		 * The integer value for the key representing left.
		 */
		public int getLeftKey() 
		{
			return leftKey;
		}

		/**
		 * Sets the left key to the new key represented by the parameter.
		 * @param leftKey
		 * :  The integer representing the new key that will move the player left.
		 */
		public void setLeftKey(int leftKey) 
		{
			this.leftKey = leftKey;
		}

		/**
		 * @return
		 * The integer value for the key representing right.
		 */
		public int getRightKey() 
		{
			return rightKey;
		}

		/**
		 * Sets the right key to the new key represented by the parameter.
		 * @param rightKey
		 * :  The integer representing the new key that will move the player right.
		 */
		public void setRightKey(int rightKey) 
		{
			this.rightKey = rightKey;
		}

		/**
		 * @return
		 * The integer value for the key representing fire.
		 */
		public int getFireKey() 
		{
			return fireKey;
		}

		/**
		 * Sets the fire key to the new key represented by the parameter.
		 * @param fireKey
		 * :  The integer representing the new key that will make the player fire.
		 */
		public void setFireKey(int fireKey) 
		{
			this.fireKey = fireKey;
		}
	}
	
	/**
	 * Used for the HashMaps of the Sparse Grid.  Has a lookup index for an associated Particle
	 * in <i>allParticles</i>.
	 * @author Nick Kitten & Sean Fedak
	 */
	protected static class Location
	{
		public int x, y;
		public int index;
		public final boolean isLeft, isTop; 
		
		/**
		 * Constructs a new Location for a given x and y location.  Holds the index for
		 * the associated Particle in <i>allParticles</i>.
		 * @param x
		 * :  The x location associated with this Location.
		 * @param y
		 * :  The y location associated with this Location.
		 * @param index
		 * :  The index in <i>allParticles</i> associated with the Particle in this Location.
		 */
		public Location(int x, int y, int index, boolean isLeft, boolean isTop)
		{
			this.x = x;
			this.y = y;
			this.index = index;
			this.isLeft = isLeft;
			this.isTop = isTop;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() 
		{
			return (x << 16) | (y & 0xFFFF);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) 
		{
			if (obj == null || !(obj instanceof Location)) return false;
			Location l = (Location) obj;
			return l.x == x && l.y == y;
		}
	}
	
	private class ParticleManager implements Agent
	{
		BitSet touchedSet;
		public ParticleManager()
		{
			touchedSet = new BitSet(allParticles.size() * 2);
		}

		@Override
		public void go(Game game, long timestep, int priorityLevel) 
		{
			//TODO: figure out reasonable values for range, comfydist, etc
			wipeClean();
			updateNeighbors(40);
			
			wipeClean();
			ArrayList<Particle> constituents;
			
			for(Blob b : getBlobs())
			{	
				int id = b.getBlobID();
				
				if( id == 0)
					continue;

				constituents = b.getParticles();
				getConnectivity( b.getHead(), id, constituents, true );
				
			}
			
			Blob neutral = hBlobs.get(0);
			constituents = neutral.getParticles();
			getConnectivity( constituents.get(0), 0, constituents, true );
			
			findStragglers();
			
			for(Blob b : getBlobs())
			{
				b.go(game, timestep, priorityLevel);
			}
			
			game.queue.schedule(priorityLevel, this);
			
			
		}
		
		private void wipeClean()
		{
			touchedSet.clear();
		}
		
		/*
		 * Updates the Particle's list of neighboring Particles and applies a force to them.
		 * @param range 
		 * :  The maximum distance from this Particle to a neighbor
		 */
		private void updateNeighbors( double range )
		{
			double squaredRange = range*range;
			int x1, x2, y1, y2;
			ArrayList<Particle> neighborhood;
			Location sector = new Location(0,0,0,false,false);
			
			for(Particle p:  allParticles)
			{
				p.clearNeighbors();
				
				Location theLocation = locations.get(p);
				if( theLocation.isLeft )
				{
					x1 = Math.max(0, theLocation.x - 1);
					x2 = theLocation.x;
				}
				else
				{
					x1 = theLocation.x;
					x2 = Math.min(MAX_X, theLocation.x);
				}
				
				if( theLocation.isTop )
				{
					y1 = Math.max(0, theLocation.y - 1);
					y2 = theLocation.y;
				}
				else
				{
					y1 = theLocation.y;
					y2 = Math.min(MAX_Y, theLocation.y);
				}
				
				for(int x = x1; x <= x2; x++)
					for(int y = y1; y <= y2; y++)
					{
						sector.x = x;
						sector.y = y;

						neighborhood = particles.get(sector);
						for(Particle op:  neighborhood)
						{
							if( touchedSet.get(locations.get(op).index) )
								continue;
							
							double dx = op.getX() - p.getX();
							double dy = op.getY() - p.getY();
							double squaredDistance = dx*dx + dy*dy;
							if(squaredDistance < squaredRange)
							{
								p.addNeighbor(op);
								
								double distance = Math.sqrt(squaredDistance);
								Blob blob = hBlobs.get( p.getBlobID() );
								double bForce = blob.getBlobForce();
								double comfy = blob.getComfyDistance();
								
								Blob oBlob = hBlobs.get( op.getBlobID() );
								double obForce = oBlob.getBlobForce();
								double oComfy = oBlob.getComfyDistance();
								
								p.applyForce(op, bForce, distance, dx, dy, comfy);						
								op.applyForce(p, obForce, distance, dx, dy, oComfy);
								
								
							}
						}
					}
				
				touchedSet.set(theLocation.index);
			}
		}
		
		/**
		 * Does a BFS (starting at seed particle)
		 * through all neighbors to decide which ones are still
		 * part of the blob.
		 * 
		 * @param seed The particle from which to start the search
		 * @return
		 * The Linked List of Particles that are still part of the Blob.
		 */
		public void getConnectivity( Particle seed, int blobID, ArrayList<Particle> connected, boolean clear )
		{
			// need to check headless blobs in case they have been absorbed,
			// so they won't just persist forever
			if(clear)
				connected.clear();
			
			if( seed.getBlobID() != blobID )
			{
				return;
			}
			
			LinkedList<Particle> queue = new LinkedList<Particle>();
			ArrayList<Particle> neighbors;
			connected.add(seed);
			
			queue.add(seed);
			touchedSet.set(locations.get(seed).index);
			Particle currParticle;
			int index;
			
			while( ! queue.isEmpty() )
			{
				connected.add( currParticle = queue.pop() );
				neighbors = currParticle.getNeighbors();
				
				for( Particle p : neighbors )
				{
					// only looking for particles which can be absorbed
					// into the current blob.
					index = locations.get(p).index;
					if( touchedSet.get(index) || currParticle.isEnemy(p) )
						continue;
					
					p.setBlobID( blobID );
					touchedSet.set(index);
					queue.add(p);
				}
			}
		}
		
		/**
		 * Searches through list of particles looking for ones
		 * which haven't been touched by checkConnectivity()
		 * to add to new, neutral blobs.
		 * 
		 * @return
		 * The list of new Blobs (if any) created.
		 */
		public void findStragglers()
		{
			//TODO: need to put this function outside of blob;
			// otherwise stragglers won't be found
			ArrayList<Particle> constituents = hBlobs.get(0).getParticles();
			
			for( int i = 0; i < allParticles.size(); ++i )
			{
				if( touchedSet.get(i) )
					continue;
				//TODO: create default settings for neutral blobs (and find
				// better way to manage them)
				
				getConnectivity( allParticles.get(i), 0, constituents, false);
			}

		}
	}
}
