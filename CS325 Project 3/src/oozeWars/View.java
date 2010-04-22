/**
	VIEW.JAVA

	<p>
	A JPanel which displays the game model: that is, does all the drawing.  This is also the place to handle GUI events
	such as keystrokes or mouse events.
	
	<p>The View does its drawing by asking one or more Sprites to draw on the screen.  The Sprites can draw on the screen
	any way they like.  The only thing the View controls is the <i>order</i> in which the Sprites are called.  You do this
	by stating some number n of <i>layers</i> in the View, and then registering a Sprite with a layer (from 0 to n-1).
	When the View is repainting, it will call all the Sprites in layer 0 (in arbitrary order), then all the sprites in
	layer 1, then all the Sprites in layer 2, and so on.  This means that Sprites in higher layers will be drawn on top
	of Sprites in lower layers.  You can add and delete Sprites from the view at any time.
	
	 <p>The View has a convenience method called
	 setKeystrokeFocus(...), which causes a JFrame (notionally the one holding the View) to cause all keystrokes to
	 be routed to the View properly. 
	 
	 <p>The class also comes with a convenience method to provide a JFrame for the View and set both up properly.
	 By default the View and its JFrame are set up with a stated dimension, in the center of the screen, and not
	 resizable.
*/

package oozeWars;
import java.util.*;
import java.lang.reflect.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

@SuppressWarnings({"unchecked"})		// Java's generics handling of arrays is absolutely abysmal.  I chose to ignore it entirely.
public class View extends JPanel
{
	// internal variables
	Game game;					// backpointer to the game
	int preferredWidth;			// preferred view width
	int preferredHeight;		// preferred view height
	LinkedHashSet<Sprite>[] sprites;	// An array of Sets of sprites, each array element corresponding to a sprite layer

	/** Constructs a View with a given game, number of sprite layers, and preferred width and height.
		Obviously the game must have already been constructed.  Sets the game's 'view' variable to this
		View. Also calls game.registerListeners(this).  */
	public View(Game game, int layers, int preferredWidth, int preferredHeight)
	{
		this.game = game;
		this.preferredWidth = preferredWidth;
		this.preferredHeight = preferredHeight;
		sprites = new LinkedHashSet[layers];
		for(int i = 0; i < layers; i++)
			sprites[i] = new LinkedHashSet();
		setOpaque(false);  // I will redraw everything
		game.setView(this);
		//game.registerListeners(this);
	}
		
	/** Sets up the JFrame and this View so that the View receives all the keystroke requests.  The JFrame must
		have already been set as owning this View.  */
	public void setKeystrokeFocus(JFrame frame)
	{
		setFocusable(true);
		requestFocusInWindow();
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowActivated(WindowEvent e)
			{
				requestFocusInWindow();
			}
		});
	}
		
	/** Returns the preferred width and height.  */
	public Dimension getPreferredSize()	
	{
		return new Dimension(preferredWidth, preferredHeight);
	}
	
	/** Adds a sprite for the given layer if it doesn't already exist for that layer. */
	public void addSprite(Sprite sprite, int layer)
	{
		sprites[layer].add(sprite);
	}

	/** Removes a sprite from the given layer. */
	public void removeSprite(Sprite sprite, int layer)
	{
		sprites[layer].remove(sprite);
	}

	/** Removes all sprites. */
	public void clear()
	{
		for(int i = 0; i < sprites.length; i++)
			sprites[i] = new LinkedHashSet();
	}
		
	/** Paints the View by drawing all the sprites in order.  Painting is done antialiased. */
	public void paintComponent(Graphics g)
	{
		Graphics2D graphics = (Graphics2D) g;
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for(int i = 0; i < sprites.length; i++)
		{
			Iterator<Sprite> iterator = sprites[i].iterator();
			while(iterator.hasNext())
			{
				Sprite sprite = iterator.next();
				sprite.draw(graphics, game);
			}
		}
		graphics.dispose();
	}
	
	/** A convenience method for creating a JFrame appropriate for this View, placing the View in it,
		setting the keystroke focus of the View, and displaying the JFrame on-screen. 
	*/
	
	public JFrame createFrame(String title)
	{
		final JFrame frame = new JFrame(title);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(this);
		frame.getContentPane().setBackground(Color.BLACK);
		setKeystrokeFocus(frame);
		frame.pack();
		frame.setLocationRelativeTo(null);		// a magic trick to center the window on-screen
		frame.setVisible(true);
		frame.setResizable(false);
		
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				if (game.quit())
					frame.dispose();
			}
		});
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		return frame;
	}
}