package oozeWars;

import javax.sound.midi.*;
import java.io.*;
import java.util.*;
import java.net.*;

/** 
	Plays a MIDI file either once or in a loop.

	<p>You set up the MIDI class by specifying the file, either as a File, URL/Resource, or InputStream.
	Then you start playing by calling start, and stop playing by pressing stop.  Once you are playing this
	MIDI file, you must stop it before playing it again.

	<p>You can play a MIDI file in any of three ways:

	<ul>
	<li>A one-shot MIDI file that can be paused via <b>stop()</b> and can be resumed.
	This is done by calling <b>play(false, true). 
	<li>A repeating MIDI file that can be paused via <b>stop()</b>.
	This is done by calling <b>play(true, true).
	<li>A one-shot MIDI file that can be stopped via <b>stop()</b> and reset from the start if play(...) is called again.
	This is done by calling <b>play(false, false). 
	<li>A repeating MIDI file that can be stopped via <b>stop()</b> and reset from the start if play(...) is called again.
	This is done by calling <b>play(true, false). 
	</ul>
**/

public class Midi
	{
	Sequence sequence;
	Sequencer sequencer;
	Synthesizer synthesizer;

	/** Constructs a MIDI player from the given MIDI file. */
	public Midi(URL url) throws InvalidMidiDataException, MidiUnavailableException, IOException
		{
		sequence = MidiSystem.getSequence(url);
		build();
		}

	/** Constructs a MIDI player from the given MIDI file. */
	public Midi(File file) throws InvalidMidiDataException, MidiUnavailableException, IOException
		{
		sequence = MidiSystem.getSequence(file);
		build();
		}

	/** Constructs a MIDI player from the given MIDI file. */
	public Midi(InputStream stream) throws InvalidMidiDataException, MidiUnavailableException, IOException
		{
		sequence = MidiSystem.getSequence(stream);
		build();
		}

	// loads the sequencer and attaches a synthesizer
	void build() throws InvalidMidiDataException, MidiUnavailableException
		{
		sequencer = MidiSystem.getSequencer(false);
		sequencer.open();
		synthesizer = MidiSystem.getSynthesizer();
		synthesizer.open();
		sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
		sequencer.setSequence(sequence);
		}

	boolean playing = false;

	/** Starts playing the MIDI file.  If <i>loop</i> is true, will play continuously.  If <i>reset</i> is false,
		will play from last place left off after the previous stop(), else will restart playing from scratch.  */
	public void play(boolean loop, boolean reset) throws InvalidMidiDataException
		{
		if (playing) return;
		playing = true;
		if (reset) sequencer.setMicrosecondPosition(0L);
		sequencer.setLoopCount(loop ? Sequencer.LOOP_CONTINUOUSLY : 0);
		sequencer.start();
		}

	/** Stops playing the MIDI file.  */
	public void stop()
		{
		if (!playing) return;
		playing = false;
		sequencer.stop();
		}
	}
