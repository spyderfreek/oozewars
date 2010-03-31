package oozeWars;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JFrame;

public class OozeWars extends Game {
	private int numPlayers;
	private PlayerControls[] controls;
	private ArrayList<Blob> blobs;
	
	public OozeWars(double maximumFrameRate, int numPlayers) {
		// only need 2 schedule priority levels for now (?)
		super(2, maximumFrameRate);
		this.numPlayers = numPlayers;
		controls = new PlayerControls[numPlayers];
		blobs = new ArrayList<Blob>();
		
		for(int i = 0; i < numPlayers; i++)
			controls[i] = setPlayerControls(i);
		
		while(numPlayers-- >= 0)
		{
			blobs.add(new Blob(100, 100, 0, 4, Color.BLACK));
		}
	}

	/* (non-Javadoc)
	 * @see oozeWars.Game#start()
	 */
	@Override
	protected void start() {
		for(Blob b : blobs)
		{
			queue.schedule(0, b);
			view.addSprite(b, 0);
		}
		
		super.start();
	}

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
	
	public void removePlayer(int player)
	{
		// TODO: remove event listeners for dead player, check for win / loss conditions.
		numPlayers--;
	}
	
	/**
	 * @return the numPlayers
	 */
	public int getNumPlayers() {
		return numPlayers;
	}

	/**
	 * @return the controls
	 */
	public PlayerControls[] getControls() {
		return controls;
	}

	/**
	 * @return the blobs
	 */
	public ArrayList<Blob> getBlobs() {
		return blobs;
	}

	/**
	 * @param blobs the blobs to set
	 */
	public void setBlobs(ArrayList<Blob> blobs) {
		this.blobs = blobs;
	}

	@Override
	protected void registerListeners(View view) {

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
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		OozeWars game = new OozeWars(30, 1);
		View view = new View(game, 1, 800, 600);
		JFrame frame = view.createFrame("Ooze Wars");
		view.setKeystrokeFocus(frame);
		game.reset();
	}
	
	protected static class PlayerControls
	{
		private boolean up, down, left, right, fire;
		private int upKey, downKey, leftKey, rightKey, fireKey;
		
		public PlayerControls(int upKey, int downKey, int leftKey, int rightKey, int fireKey)
		{
			this.upKey = upKey;
			this.downKey = downKey;
			this.leftKey = leftKey;
			this.rightKey = rightKey;
			this.fireKey = fireKey;
			up = down = left = right = fire = false;
		}

		public boolean isUp() {
			return up;
		}

		public void setUp(boolean up) {
			this.up = up;
		}

		public boolean isDown() {
			return down;
		}

		public void setDown(boolean down) {
			this.down = down;
		}

		public boolean isLeft() {
			return left;
		}

		public void setLeft(boolean left) {
			this.left = left;
		}

		public boolean isRight() {
			return right;
		}

		public void setRight(boolean right) {
			this.right = right;
		}

		public boolean isFire() {
			return fire;
		}

		public void setFire(boolean fire) {
			this.fire = fire;
		}

		public int getUpKey() {
			return upKey;
		}

		public void setUpKey(int upKey) {
			this.upKey = upKey;
		}

		public int getDownKey() {
			return downKey;
		}

		public void setDownKey(int downKey) {
			this.downKey = downKey;
		}

		public int getLeftKey() {
			return leftKey;
		}

		public void setLeftKey(int leftKey) {
			this.leftKey = leftKey;
		}

		public int getRightKey() {
			return rightKey;
		}

		public void setRightKey(int rightKey) {
			this.rightKey = rightKey;
		}

		public int getFireKey() {
			return fireKey;
		}

		public void setFireKey(int fireKey) {
			this.fireKey = fireKey;
		}
		
		
	}

}
