/**
	SCHEDULE.JAVA
	
	An event queue for handling internal state events of a game.  These events are different from the Java GUI "events"
	that are called on Java's Event Listeners to handle mouse and keyboard events.  Instead the events in Schedule are
	registered by the game developer to be fired at some time in the future.  For example, if a player enters a room,
	we may register an event with the Schedule to be fired twenty ticks from now and cause a bad guy to jump out 
	of nowhere.
	
	<p>The objects which handle events of this kind are called <i>Agents</i> and are expected to have a method called
	<i>go(...)</i> which is called when the Agent's time is up (when the "event" is fired).
	
	<p>Agents are registered with a <i>timestep</i> and a <i>priority level</i>.  The timestep is the tick (game loop
	iteration) at which the event will be called on the Agent.  Agents registered for the same timestep will be
	called in the order determined by their respective priority levels: lower-level agents will be called first.  Agents
	with the same priority level and timestep will be called in arbitrary order.  This enables you to guarantee that
	in a given timestep, certain things happen before certain other things.
	
	<p>The most general registering method is <tt>schedule(<i>timestep</i>, <i>prioritylevel</i>, <i>Agent</i>)</tt>,
	which registers the Agent to be fired a specific timestep.  Often instead you want an Agent to be registered at
	a timestep relative to the current one (like "five timesteps from now").  For that, you can use the scheduleIn(...)
	method.  Last, you often want to register an Agent to be fired at the very next timestep.  For that, you can use
	the simplest schedule(...) method.
		
	<p>In the huge majority of cases a given Agent is registered to be fired each and every timestep.  To do this you
	register the Agent, then in the Agent's go() method have the Agent register himself <i>again</i> for another
	timestep in the near future.
*/

package oozeWars;
import java.util.*;

@SuppressWarnings({"unchecked"})		// Java's generics handling of arrays is absolutely abysmal.  I chose to ignore it entirely.
public class Schedule
{
	/** The first tick of the game. */
	public static final long EPOCH = 0;
	
	// private members
	private long ticks = EPOCH - 1;				// the current time in the game.  Initially "before the game starts" (EPOCH - 1)
	private PriorityQueue[] priorityQueues;		// an array of PriorityQueues, one per priority level.  Each heap stores Agents keyed with timesteps to fire them.
	
	/** Creates a Schedule from the given Game and priority level.  Typicaly this method is called from the Game constructor itself,
		and you'd not call it. */
	public Schedule(int priorityLevels, Game game)
	{
		priorityQueues = new PriorityQueue[priorityLevels];
		for(PriorityQueue q: priorityQueues)
			q = new PriorityQueue();
	}
	
	/** Returns the current time in the game. */
	public long getTicks() 
	{ 
		return ticks;
	}

	/** Steps the schedule.  This causes the schedule to increment the ticks, then check to see if any
		agents are registered to be called at this new time.  If so, they're called in priority order as
		discussed. */
    public void step(final Game game)
    {
		
		// Here's what you want to do.
		// 1. Increment the ticks.
    	ticks++;
		// 2. For each priority queue...
    	for(int i = 0; i < priorityQueues.length; i++)
    	{
    		// 3. For each Agent in the queue whose timestamp is NOW,
    		while(priorityQueues[i].peek() != null && ((QueueElement) (priorityQueues[i].peek())).getTimestamp() == ticks)
    		{
    			// 4. Remove the Agent from the queue and call go() on the Agent
    			((QueueElement) priorityQueues[i].poll()).getAgent().go(game, ticks, i);
    		}

    		// Be sure to stop hunting as soon as you find an Agent whose timestamp is later than NOW.
    		// It's a priority queue remember!
    	}
	}
		
	/** Clears out the entire Schedule, removing all Agents registered with it. */
	public void clear()
	{
		for(PriorityQueue q: priorityQueues)
			q.clear();
	}

	/** Schedules an Agent to be fired at the very next timestep, with the given priority level. */
	public void schedule(int priorityLevel, Agent agent) throws RuntimeException
	{
		scheduleIn(1L, priorityLevel, agent);
	}

	/** Schedules an Agent to be fired in <i>interval</i> ticks from now, with the given priority level. */
	public void scheduleIn(long interval, int priorityLevel, Agent agent) throws RuntimeException
	{
		schedule(ticks+interval, priorityLevel, agent);
	}
				
	/** Schedules an Agent to be fired at the specified timestep, with the given priority level.  The
		provided timestep must be greater than the current ticks.  */
	public void schedule(long timestep, int priorityLevel, Agent agent) throws RuntimeException
	{
		// Create a QueueElement for the given timestep and agent and schedule it in the appropriate
		// PriorityQueue.  There are several things you need to check for and throw an error for:
		// - is the priority level valid?
		if( priorityLevel < 0 || priorityLevel >= priorityQueues.length)
			throw new RuntimeException("Invalid priorityLevel");
		// - is the timestep greater than the current ticks?
		if(timestep <= ticks)
			throw new RuntimeException("Invalid timeStep");
		// - is the agent non-null?
		if(agent == null)
			throw new RuntimeException("Invalid agent");
		
		QueueElement qe = new QueueElement(agent, timestep);
		priorityQueues[priorityLevel].add(qe);
	}



	/** The QueueElement is what's actually stored in the Schedule's PriorityQueue.
		It holds an AGENT and a TIMESTAMP, and sorts itself in the PriorityQueue
		lowest timestamp first. */
		
	protected static class QueueElement implements Comparable
	{
		long timestamp;
		Agent agent;
		
		public long getTimestamp() 
		{
			return timestamp;
		}
		public Agent getAgent() 
		{ 
			return agent; 
		}
		public String toString() 
		{ 
			return "QE[" + timestamp + ", " + agent + "]"; 
		}

		public QueueElement(Agent agent, long timestamp)
		{
			this.timestamp = timestamp;
			this.agent = agent;
		}
		
		public int compareTo(Object other)
		{
			QueueElement qe = (QueueElement) other;
			return (qe.timestamp == timestamp ? 0 : (qe.timestamp < timestamp ? 1 : -1));
		}
	}
}



