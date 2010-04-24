package oozeWars;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
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
	private Backdrop backdrop;
	private final double CELL_WIDTH = 75;
	private int MAX_X, MAX_Y;
	public final double SCALE = 0.25;
	private int width, height;
	private static Midi[] songs = initializeSongs();
	
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
		
		System.out.println(System.getProperty("user.dir"));
		String imgPath = "cells_bg.jpg";

		backdrop = new Backdrop(imgPath);
	}

	/* (non-Javadoc)
	 * @see oozeWars.Game#start()
	 */
	@Override
	protected void start() 
	{	
		width = view.preferredWidth + 20;
		height = view.preferredHeight + 20;
		
		manager = new ParticleManager(this);
		
		int numParticles = 50;
		int player = numPlayers;
		
		while( player-- > 0)
		{
			int id = player + 1;
			switch (id)
			{
				case(1):
					Blob newBlob = new Blob(200, 200, 0, numParticles, id, this , Color.GREEN);
					hBlobs.put(id, newBlob);
					break;
				case(2):
					Blob newerBlob = new Blob(500, 500, 0, numParticles, id, this, Color.BLUE);
					hBlobs.put(id, newerBlob);
					break;
				default:
					System.out.println("Invalid number of players");
			}
		}
		
		ArrayList<Particle> neutralParticles = new ArrayList<Particle>();
		
		for( int i = 0; i < numParticles; i++ )
			neutralParticles.add(new Particle(700 + random.nextInt(80) - 40, 100 + random.nextInt(80) - 40, 6, Color.WHITE, 0));
		
		hBlobs.put(0, new Blob(neutralParticles, this));
		
		for(Blob b: getBlobs())
		{
			for(Particle p: b.getParticles())
				addParticle(p);
			view.addSprite(b, 1);
		}
		
		view.addSprite(backdrop, 0);
		
		MAX_X = (int)(view.getWidth() / CELL_WIDTH);
		MAX_Y = (int)(view.getHeight() / CELL_WIDTH);
		
		
		super.start();
		queue.schedule(0, manager);
		
		try 
		{
			songs[random.nextInt(4)].play(true, true);
		} 
		catch (InvalidMidiDataException e) {e.printStackTrace();}
	}
	
	@Override
	protected void stop()
	{
		super.stop();
		
		for(Midi s: songs)
			s.stop();
		
		KeyListener[] kl = view.getKeyListeners();
		if(kl.length == 1 && kl[0] != null)
			view.removeKeyListener( kl[0] );
		
		pace.purge();
		pace = null;
		hBlobs.clear();
		allParticles.clear();
		manager = null;
		for(PlayerControls pc : controls)
			pc.resetBooleans();
	}

	public int getWidth() 
	{
		return width;
	}

	public void setWidth(int width) 
	{
		this.width = width;
	}

	public int getHeight() 
	{
		return height;
	}

	public void setHeight(int height) 
	{
		this.height = height;
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
				return new PlayerControls(KeyEvent.VK_I, KeyEvent.VK_K, 
						KeyEvent.VK_J,KeyEvent.VK_L, KeyEvent.VK_SHIFT);
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
		
		paused = true;
		
		//hBlobs.put(b.getBlobID(), b);

		// TODO: remove event listeners for dead player, check for win / loss conditions.
		int playerLeft = 0;
		Collection<Blob> blobsLeft = getBlobs();
		
		if(--numPlayers == 1)
		{
			for(Blob blob : blobsLeft)
			{
				playerLeft = blob.getBlobID();
				if(playerLeft != 0)
					break;
			}
			JOptionPane.showMessageDialog(null, "Player " + playerLeft  + " wins!");

			int answer = JOptionPane.showConfirmDialog(null, "Rematch?", "Game Over", 
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(answer == JOptionPane.YES_OPTION)
				numPlayers = 2;
			else
			{
				boolean quit = quit();
				if(quit)
					System.exit(0);
			}

			reset();
			
		}
		else if(numPlayers == 0) //There was a draw
		{
			JOptionPane.showMessageDialog(null, "DRAW!");
			
			int answer = JOptionPane.showConfirmDialog(null, "Rematch?", "Game Over", 
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if(answer == JOptionPane.YES_OPTION)
				numPlayers = 2;
			else
			{
				boolean quit = quit();
				if(quit)
					System.exit(0);
			}
			
			reset();
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
		int lastInd = allParticles.size()-1;
		int ind = aParticle.getIndex();
		Particle anotherParticle = allParticles.remove( lastInd );
		if( ind != lastInd )
		{
			anotherParticle.setIndex( ind );
			allParticles.set( ind, anotherParticle);
		}
		//view.removeSprite(aParticle, 1);
	}
	
	/**
	 * A method to add a Particle to the Sparse Grid.  Can be used to add the same particle to
	 * a different Location on the Sparse Grid.
	 * @param aParticle
	 * :  The Particle that will be added to (or moved in) the Sparse Grid
	 */
	public void addParticle(Particle aParticle)
	{
		aParticle.setIndex(allParticles.size());
		allParticles.add(aParticle);
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
		OozeWars game = new OozeWars(45, n);
		OozeView view = new OozeView(game, 3, 800, 600, 0.25);
		JFrame frame = view.createFrame("Ooze Wars");
		view.setKeystrokeFocus(frame);
		game.reset();
	}
	
	/**
	 * Sets up the key bindings necessary for the controls that the player will be using.
	 * @author Nick Kitten & Sean Fedak
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
		
		public void resetBooleans()
		{
			up = left = down = right = fire = false;
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
		private BitSet touchedSet;
		private final double RANGE = CELL_WIDTH * 0.75;
		private OozeWars ow;
		
		public ParticleManager(OozeWars ow)
		{
			touchedSet = new BitSet(allParticles.size() * 2);
			this.ow = ow;
		}

		@Override
		public void go(Game game, long timestep, int priorityLevel) 
		{

			
			//TODO: figure out reasonable values for range, comfydist, etc
			
			// avoid ConcurrentModificationException
			boolean isDead[] = new boolean[2];
			
			for( int i = 0; i < isDead.length; i++ )
			{
				isDead[i] = false;
			}
			
			for(Blob b : getBlobs())
			{
				if( b.isDead() && b.getBlobID() != 0)
					isDead[ b.getBlobID() - 1 ] = true;
				b.go(game, timestep, priorityLevel);
			}			
			
			for( int i = 0; i < isDead.length; i++ )
			{
				if( isDead[i] )
					removePlayer(i);
			}
			
			wipeClean();
			updateNeighbors( RANGE );
			
			OozeWars ow = (OozeWars)game;
			double xMax = ow.getWidth();
			double yMax = ow.getHeight();
			keepInBounds(xMax, yMax);
			
			wipeClean();
			getConnectivity();

			// 50% chance of  a new particle every 2 seconds
			// use .9772 for every second
			if( random.nextFloat() > .9885f )
				addParticle( new Particle( random.nextFloat()* width, random.nextFloat()* height, random.nextFloat()*5+3, Color.LIGHT_GRAY, 0));
				
			findStragglers();

			
			/*
			for(int i = 0; i < 1; i++)
			{
				wipeClean();
				applyConstraints( RANGE );
			}
			*/
			
			/*for(Particle p : allParticles)
				addParticle(p);*/
			
			game.queue.schedule(priorityLevel, this);
		}
		
		private void getConnectivity()
		{
			ArrayList<Particle> constituents;
			
			for(Blob b : getBlobs())
			{	
				int id = b.getBlobID();
				
				if( id == 0)
					continue;

				constituents = b.getParticles();
				
				b.setLastNumParticles( constituents.size() );
				
				Head head = b.getHead();
				getConnectivity( head, id, constituents, true );
				
				
				Particle p;
				double headX = head.getX();
				double headY = head.getY();
				int numNeighbors = constituents.size();
				
				double comfyDist = b.getComfyDistance();
				double factor = 1.0 / 100;
				double dx, dy;
				
				for( int i  = 1; i < numNeighbors; i++ )
				{	
					p = constituents.get(i);
					dx = headX - p.getX();
					dy = headY - p.getY();
					double k = .005 * (1 - i * factor);
					
					//head.applyStickConstraint( p, .001 * (1 - i * factor), Math.sqrt(comfyDist * comfyDist + 1), dx, dy, 0, comfyDist);
					p.push(k * dx, k * dy);
				}
				
			}
		}
		
		private void wipeClean()
		{
			touchedSet.clear();
		}
		
		private void clearNeighbors()
		{
			for( Particle p : allParticles )
				p.getNeighbors().clear();
		}
		
		/*
		 * Updates the Particle's list of neighboring Particles and applies a force to them.
		 * @param range 
		 * :  The maximum distance from this Particle to a neighbor
		 */
		private void updateNeighbors( double range )
		{
			double squaredRange = range*range;
			
			clearNeighbors();
			
			for(int i = 0; i < allParticles.size(); i++)
			{
				Particle p = allParticles.get(i);
				for(int j = i+1; j < allParticles.size(); j++)
				{
					Particle op = allParticles.get(j);
					
					double dx = op.getX() - p.getX();
					double dy = op.getY() - p.getY();
					double squaredDistance = dx*dx + dy*dy;
					
					if(squaredDistance < squaredRange)
					{
						p.addNeighbor(op);
						op.addNeighbor(p);
						
						double distance = Math.sqrt(squaredDistance);
						Blob blob = hBlobs.get( p.getBlobID() );
						double bForce = blob.getBlobForce();
						double comfy = blob.getComfyDistance();
						
						Blob oBlob = hBlobs.get( op.getBlobID() );
						double obForce = oBlob.getBlobForce();
						double oComfy = oBlob.getComfyDistance();
						
						p.applyStickConstraint(op, bForce, distance, dx, dy, range, comfy);	
					}
				}
			}
		}
		
		private void keepInBounds( double xMax, double yMax )
		{
			for( Particle p : allParticles )
			{
				double x = p.getX();
				double y = p.getY();
				double r = p.halfWidth;
				
				if(x < r)
					p.x = r;
				else if(x > xMax - r)
					p.x = xMax - r;
				
				if(y < r)
					p.y = r;
				else if(y > yMax - r)
					p.y = yMax - r;
			}
		}
		
		private void applyConstraints( double pushDist )
		{
			for( Particle p : allParticles )
			{
				ArrayList<Particle> neighbors = p.getNeighbors();
				Blob blob = hBlobs.get( p.getBlobID() );
				double bForce = blob.getBlobForce();
				double pullDist = blob.getComfyDistance();
				
				for( Particle q : neighbors )
				{
					if( touchedSet.get( q.getIndex() ) )
						continue;
					
					double dx = q.getX() - p.getX();
					double dy = q.getY() - p.getY();
					double distance = Math.sqrt( dx * dx + dy * dy );
					
					p.applyStickConstraint(q, bForce, distance, dx, dy, pushDist, pullDist);
				}
				
				touchedSet.set( p.getIndex() );
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
			
			if( seed.isEnemy(blobID) )
				return;
			
			LinkedList<Particle> queue = new LinkedList<Particle>();
			ArrayList<Particle> neighbors;
			
			queue.add(seed);
			touchedSet.set(seed.getIndex());
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
					index = p.getIndex();
					if( touchedSet.get(index) || currParticle.isEnemy(p) )
						continue;
					
					p.setBlobID( blobID );
					touchedSet.set(index);
					queue.add(p);
				}
				//System.out.println("Getting connectivity");
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
			Blob neutral = hBlobs.get(0);
			ArrayList<Particle> constituents = neutral.getParticles();
			constituents.clear();
			Particle seed;
			
			for( int i = 0; i < allParticles.size(); ++i )
			{
				if( touchedSet.get(i) )
					continue;
				//TODO: create default settings for neutral blobs (and find
				// better way to manage them)
				seed = allParticles.get(i);
				seed.setBlobID(0);
				
				getConnectivity( seed, 0, constituents, false);
			}
		}
	}
	
	private static Midi[] initializeSongs()
	{
		Midi[] songs = new Midi[4];
		
		try 
		{
			songs[0] = new Midi(OozeWars.class.getResourceAsStream("rtft.mid"));
			songs[1] = new Midi(OozeWars.class.getResourceAsStream("sonicsuf.mid"));
			songs[2] = new Midi(OozeWars.class.getResourceAsStream("spheare.mid"));
			songs[3] = new Midi(OozeWars.class.getResourceAsStream("absence.mid"));
		} 
		catch (InvalidMidiDataException e) {e.printStackTrace();}
		catch (MidiUnavailableException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
		
		return songs;
	}
}
