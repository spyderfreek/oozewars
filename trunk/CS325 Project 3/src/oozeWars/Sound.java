package oozeWars;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;
import java.net.*;

/** 
	Plays a Sound file either once or in a loop.

	<p>You set up the Sound class by specifying the file, either as a File, URL/Resource, or InputStream.
	Then you start playing by calling start, and stop playing by pressing stop.

	<p>There are two options for playing your Sound file: you can load it from disk every single time that
	it is played (including reloading when a loop iterates), or you can preload it from disk into an array
	in memory and use that array every time instead.  The first case is useful if you have a long sound that
	you're playing.  The second case is best if you have a short sound that you're playing or intend on
	playing the sound many times.  If you sound is very long, do not load it into a memory buffer.

	<p>You can play a Sound in any of three ways:

	<ul>
	<li>A one-shot sound that can be played again (overlaid on the original) before the first is completed.
	This is done by calling <b>play()</b>,
	<li>A one-shot sound that can be stopped via <b>stop()</b>.  You cannot only play one stoppable sound
	at a time.  This is done by calling <b>play(false).
	<li>A repeating sound that can be stopped via <b>stop()</b>.  You cannot only play one stoppable sound
	at a time.  This is done by calling <b>play(true). 
	</ul>

	<p><b>Important Note: </b>
	The default sound-playing capacity of javax.sound is very limited.  It typically can't play MP3 files,
	Microsoft ADPCM files (often found in WAV containers), AAC files, etc., without more gizmos available
	on the internet.  You're probably stuck with linear 16-bit WAV files.
**/


public class Sound
	{
	// default buffer size.  If larger, then we're probably more efficient but stopping a sound takes longer.
	final int DEFAULT_BUFFER_SIZE = 64 * 1024;  // seems reasonable

	// Requests the play thread to die
	boolean pleaseDie = false;

	// Preloaded sound.  If null, we're not preloading
	byte[] buffer = null;

	// Input stream, used if we've preloaded our sound
	AudioInputStream stream;

	// The play thread
	Thread playThread = null;

	// We're loading a sound from a file, else null
	File file;
	// We're loading a sound from a URL, else null
	URL url;
	// We're loading a sound from an InputStream, else null
	InputStream input;

	// Produces an AudioInputStream based on how we're loading the sound (file, url, or input)
	AudioInputStream getStream() throws UnsupportedAudioFileException, IOException
		{
		if (file != null) return AudioSystem.getAudioInputStream(file);
		else if (input != null) return AudioSystem.getAudioInputStream(input);
		else return AudioSystem.getAudioInputStream(url);
		}

	/** Constructs a sound player from the given sound file.   If <i>bufferInMemory</i> is true, then
		the sound is loaded once into memory and never loaded again even on repeats.  Do this if
		you have a short sound you intend to play more than once.   If you have a very long sound,
		never do this.*/
	public Sound(File file, boolean bufferInMemory) throws UnsupportedAudioFileException, LineUnavailableException, IOException
		{
		this.file = file;
		if (bufferInMemory)
			preloadFile();
		}

	/** Constructs a sound player from the given sound file.    If <i>bufferInMemory</i> is true, then
		the sound is loaded once into memory and never loaded again even on repeats.  Do this if
		you have a short sound you intend to play more than once.   If you have a very long sound,
		never do this. */
	public Sound(URL url, boolean bufferInMemory) throws UnsupportedAudioFileException, LineUnavailableException, IOException
		{
		this.url = url;
		if (bufferInMemory)
			preloadFile();
		}

	/** Constructs a sound player from the given sound file.    If <i>bufferInMemory</i> is true, then
		the sound is loaded once into memory and never loaded again even on repeats.  Do this if
		you have a short sound you intend to play more than once.   If you have a very long sound,
		never do this. */
	public Sound(InputStream input, boolean bufferInMemory) throws UnsupportedAudioFileException, LineUnavailableException, IOException
		{
		this.input = input;
		if (bufferInMemory)
			preloadFile();
		}

	// preloads the sound file into the buffer
	void preloadFile() throws UnsupportedAudioFileException, LineUnavailableException, IOException
		{
		stream = getStream();
		AudioFormat format = stream.getFormat( );
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine line = (SourceDataLine)(AudioSystem.getLine(info));
		line.open(format);
		line.start( );

		// build the buffer using a linked list of little buffers
		byte[] b = new byte[DEFAULT_BUFFER_SIZE];
		LinkedList<byte[]> l = new LinkedList<byte[]>();

		int totalLen = 0;
		while(true) 
			{
			int len = stream.read(b, 0, b.length);
			if (len < 0) break;
			if (len < b.length)  // truncate it, it's the last one probably
				{
				byte[] b2 = new byte[len];
				System.arraycopy(b, 0, b2, 0, len);
				b = b2;
				}
			totalLen += len;
			l.add(b);
			b = new byte[DEFAULT_BUFFER_SIZE];
			}

		// now our linked list has lots of buffer arrays, concatenate them.
		buffer = new byte[totalLen];
		int pos = 0;
		Iterator i = l.iterator();
		while(i.hasNext())
			{
			b = (byte[])(i.next());
			System.arraycopy(b, 0, buffer, pos, b.length); 
			pos += b.length;
			}
		}

	/** Plays the sound once in a non-stoppable fashion.  If you call this method again while this sound is
		still playing, both sounds will overlap.  You dont' have to wait for the sound to complete first.  */
	public synchronized void play()
		{
		play(false, true);
		}

	/** Plays the sound in a stoppable fashion.  If <i>loop</i> is true, loops the sound indefinitely.
		This sound may not be played again via this method until it has completed or until you have called stop().   */
	public void play(final boolean loop)
		{
		play(loop, false);
		}

	synchronized void play(final boolean loop, final boolean oneShot)
		{
		if (!oneShot && playThread !=null) return;

		Thread thread = new Thread(new Runnable()
			{
			public void run()
				{
				SourceDataLine line = null;
				try
					{
					if (buffer == null)
						{
						stream = getStream();
						}
					AudioFormat format = stream.getFormat( );
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
					line = (SourceDataLine)(AudioSystem.getLine(info));
					line.open(format);
					line.start( );

					while(true)
						{
						if (buffer==null)  // gotta load from the file
							{
							byte[ ] buffer = new byte[DEFAULT_BUFFER_SIZE];
							while(!pleaseDie)
								{
								int len = stream.read(buffer, 0, buffer.length);
								if (len < 0) break;
								line.write(buffer, 0, len);
								}
							stream.close();
							stream = getStream();  // get the next go-round
							}
						else  // preloaded
							{
							for(int pos = 0; pos < buffer.length; pos+= DEFAULT_BUFFER_SIZE)
								{
								if (pleaseDie) break;
								int desiredLen = DEFAULT_BUFFER_SIZE;
								if (pos + DEFAULT_BUFFER_SIZE > buffer.length)
									desiredLen = buffer.length - pos;
								line.write(buffer, pos, desiredLen);
								}
							}
						if (!loop) break;
						if (pleaseDie) break;
						}
					}
				catch (Exception e) { e.printStackTrace(); }

				// clean up
				try 
					{
					line.drain( );
					line.close( ); 
					if (buffer != null) stream.close();
					} 
				catch (Exception e) {  }

				// let the user play again if he wants to
				synchronized(Sound.this) { playThread = null; }
				}
			});

		thread.setDaemon(true);
		thread.start();
		if (!oneShot) playThread = thread;
		}


	/** Stops playing the sound.  If the default buffer size is large, this may take a little bit. */
	public void stop()
		{
		synchronized(this)
			{
			if (playThread == null) return;
			pleaseDie = true;
			playThread.interrupt();
			}
		try { playThread.join(); } catch (InterruptedException e) { /* oh well */ }
		}
	}