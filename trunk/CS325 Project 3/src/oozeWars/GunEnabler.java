package oozeWars;

public class GunEnabler implements Agent 
{
	private Blob blob;
	
	public GunEnabler( Blob b )
	{
		blob = b;
	}
	
	@Override
	public void go(Game game, long timestep, int priorityLevel) 
	{
		blob.setFireReady(true);
	}

}
