/**
	GAME.JAVA

	<p>
	Holds the global state of the game, including the schedule and a simple game view.  This class
	is intended to be subclassed: typically you override the start() method and possibly the stop() method.
	A game is created by first creating an instance of your Game subclass, then creating a View instance, 
	passing it the Game instance.  Then you put the View in a JFrame
	(usually by just calling createFrame(...) ), add listeners as you see fit, then call reset() on the
	game to start the game going.
	
	<p>
	The game constructs a java.util.Timer to repeatedly issue to the Swing Schedule a small Runnable which
	does the same two things over and over again:
	
	<p>
	<ol>
	<li>The Schedule is pulsed.  If there are any Agents registered for this tick, they are fired and
	given the opportunity to manipulate the game model.  This is basically the *internal* game model.
	<li>Next we issue a repaint() on the View.
	</o>
	
	After this Runnable is called, all other queue events will be handled by the Swing Schedule, such as key events
	and mouse events; and of course the repaint() which was requested.  During that paint event, every 
	sprite registered with the View is given an opportunity to redraw themselves.
	</ol>
	
	<p>
	This Runnable loop is started with start(), and is 
	stopped and cleaned up with stop().  These methods are provided to you to override and add your own
	initialization and clean-up code (be sure to call super() ).  They are not meant to be called directly.
	Instead, to start the game initially, or to reset it, you should call reset().  Furthermore to quit the
	game entirely, you should call quit(), and if it returns true, you then exit the application.
	If it returns FALSE, you should NOT exit the application, and essentially refuse to quit.
	You should override quit() to return whether or not the program should be quit.  This should be done 
	as follows:
	
	<p><tt><pre>
	protected boolean quit()
		{
		if (<i>Figure out if I really want to quit...</i>)
			return super.quit();
		else return false;
		}
	</pre></tt>
	
	<p><b>GUI Event Handling</b>
	
	<p>Mouse and Keyboard and other event handling is not handled by the engine code proper.  However
	we recommend the following procedure.  First note that these GUI events are not the same thing as the game
	events.  The game events are created by YOU and are posted in the game's internal model Schedule.  GUI
	events arrive from the player through an entirely different means.
	
	<p>Listeners on a View are registered in the Game's registerListeners() method; this makes it such that
	there is no need to subclass View except for unusual circumstances.
		
	<p>
	You could code event handling in one of two ways.  First, every time an event arrives, the listener
	modifies the model appropriately:
	
	<p><tt><pre>
		public void keyPressed(KeyEvent e)
			{
			int c = e.getKeyCode();
			...
			if (c==KeyEvent.VK_SPACE)
				{
				// do the stuff that makes the ship fire
				}
			}
	</pre></tt>
	
	<p>
	I do not recommend this approach, because it's possible that the player could send in a whole ton
	of events at one time, and they might ALL get processed before the next time tick, so the player might
	have his game avatar suddenly do a whole lot of stuff really fast (and unfairly so).
	
	<p>
	Instead, I suggest a two-step approach: first the listener should simply change some flags in the Game subclass
	which indicate that the player has asked that something happen.  For example, if the user pressed the
	space bar, the keyPressed listener might set an instance variable <tt>gunFired = true</tt> in the
	Game subclass:
	
	<p>
	<tt><pre>
		public void keyPressed(KeyEvent e)
			{
			int c = e.getKeyCode();
			...
			if (c==KeyEvent.VK_SPACE)
				{
				// scribble down some information to clue in handleGUIEvents() next time it is called
				game.gunFired = true;
				}
			}
	</pre></tt>
	
	<p>Then some Agent in the Schedule, when his time has come, can look up this information to make some
	change in the model.  For example, if you have a PlayerAvatarAgent which
	moves the Player's avatar around, you might override the PlayerAvatarAgent's go() method like this:
	
	<p>
	<tt><pre>
		public void go(Game game, long timestep, int interval)
			{
			if (game.gunFired)
				{
				// do the stuff that makes the gun fire
				}
			...
			}
	</pre></tt>
		
	<p>The Game class contains three public top-level objects of interest to the game developer:
	
	<ul>
	<li>
	The RANDOM NUMBER GENERATOR.  This should be the only generator you use in your game.
	<li>
	The SCHEDULE.  This queue stores events to occur at future ticks.  The system starts at tick 0 and
	continues essentially forever.
	<li>
	The VIEW.  This is the primary component in which the game is drawn and which should receive 
	keystroke, mouse, and other UI events.
	</ul>
	
*/




package oozeWars;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.lang.reflect.*;

public class Game
	{
	/** Random number generator. */
	public final Random random = new Random();

	/** The game's internal event queue. */
	public final Schedule queue;

	/** The game's drawing and GUI event view. */
	public View view;
	
	// internal variables
	java.util.Timer pace;
	double frameRate;			// the target rate
	boolean paused = false;

	/** Constructs a Game with an Schedule of the provided number of priority levels, and a desired
		maximum frame rate.  The View is initially set to null.  */
	public Game(int numQueuePriorityLevels, double maximumFrameRate)
		{
		queue = new Schedule(numQueuePriorityLevels);
		frameRate = maximumFrameRate;
		}
	
	/** Sets the View.  You typically don't call this -- it's called by the View itself during initialization. */
	void setView(View view) { this.view = view; }
	
	/** Override this method to add listeners of all kinds to the provided View.
		You typically don't call this -- it's called by the View itself during initialization. */
	protected void registerListeners(View view) {  }

	/** Starts or resets the game. */
	public void reset()
		{
		if (pace != null) stop();
		start();
		}
		
	/** Called by reset() and by quit() to stop the game.  Override this to clean things up but be sure
		to call super.stop(); */
	protected void stop()
		{
		// IMPLEMENT ME

		// in addition to your preview code, make sure that you have cleared out the Schedule
		}
		
	/** Called by reset() to start the game.  Override this to initialize or reinitialize things, but be sure
		to call super.start(); */
	protected void start()
		{
		// IMPLEMENT ME

		// Your TimerTask's run() method should create a Runnable which steps the Schedule and repaints the view.
		// The Runnable should only do these things if the Game is NOT paused.  The TimerTask then
		// submits the Runnable with invokeAndWait.  You'll need to catch some spurious exceptions.
		}
		
	/** Pauses or unpauses the Game */
	public void setPaused(boolean val)
		{
		// IMPLEMENT ME
		}

	/** Returns the paused state of the Game */
	public boolean getPaused()
		{
		// IMPLEMENT ME
		}

	/** Toggles the paused state of the Game */
	public void togglePaused()
		{
		// IMPLEMENT ME
		}
	
	/** Called to determine whether or not to quit the program.  If quit() returns false, then the game has
		indicated that it doesn't want to quit.  By default the game always is fine with quitting, and
		cleans things up then returns true.  Override this to test for yourself whether or not to quit
		the program (perhaps by calling up a "Should we quit?" dialog box).  If the answer is YES, then
		call super.quit() and return the result; else simply return false.*/
	public boolean quit()
		{
		stop();
		return true;  // for now
		}
	}