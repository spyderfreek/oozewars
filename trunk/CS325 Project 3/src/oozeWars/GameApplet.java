package oozeWars;

import java.applet.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class GameApplet extends Applet
	{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init()
		{
		String gameClassName = getParameter("Game");
		setLayout(new BorderLayout());			
		try
			{
			Class c = Class.forName(gameClassName);
			final java.lang.reflect.Method m = c.getMethod("main", new String[0].getClass());
			JButton button = new JButton("Start " + getParameter("Name"));
			add(button, BorderLayout.CENTER);
			button.addActionListener(new ActionListener()
				{
                public void actionPerformed(ActionEvent evt)
					{
					Object o = new String[0];
					try { m.invoke(null, o); }
					catch (Exception e)
						{
			e.printStackTrace();
			add(new JLabel("Game could not be loaded: " + e), BorderLayout.CENTER);
						}
					}
				});
			}
		catch (Exception e) 
			{
			e.printStackTrace();
			add(new JLabel("Game could not be loaded: " + e), BorderLayout.CENTER);
			}
		}

	}