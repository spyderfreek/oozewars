/**
	AGENT.JAVA
	
	<p>A simple interface for an object capable of being registered with the Schedule to have its go(...)
	method called at some time in the future.

	<p>It's often the case that you'd like to register an object to be called by the Schedule multiple
	times, and ideally have multiple (say, two) different "go" methods called instead of just one.  You can do this
	fairly easily: just create a second agent to call the second method on the first and register that agent as well.
	Now this sounds like a cop-out but it isn't: it's quite easy to do with anonymous classes.  Let's
	say your Agent has two methods, <i>go</i> and <i>doTheOtherThing</i>, and you need to register both
	of them with the schedule at different times.  You can do it this with a single line:
	
	<pre><tt>
	final MyAgent myAgent = ... ;
	Agent secondAgent = new Agent() { public void go(Game game) { myAgent.doTheOtherThing(game); } };
	</tt></pre>
*/

package oozeWars;

public interface Agent
{
	public void go(Game game, long timestep, int priorityLevel);
}
