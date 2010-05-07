package oozeWars;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

@SuppressWarnings("serial")
public class MenuScreen extends JPanel implements ActionListener
{
	private OozeView view;
	private JPanel mainMenu, pauseMenu, scores, aboutGame, howToPlay;
	private JLabel title;
	private OozeWars game;
	private Box mainBox, pauseBox;
	private JButton twoPlayer;
	private JButton instructions;
	private JButton highScores;
	private JButton about;
	private JButton quit;
	private JButton resume;

	public MenuScreen(OozeWars game, OozeView view) 
	{
		super(new BorderLayout());
		this.game = game;
		this.view = view;
		view.setMenu(this);
		
		title = new JLabel("OozeWars", JLabel.CENTER);
		this.add(title, BorderLayout.NORTH);
		
		mainBox = new Box(BoxLayout.Y_AXIS);
		mainBox.add(new JLabel("Main Menu", JLabel.CENTER));
		
		twoPlayer = initializeButton("2 Players", mainBox);
		instructions = initializeButton("Instructions", mainBox);
		highScores = initializeButton("High Scores", mainBox);
		about = initializeButton("About", mainBox);
		quit = initializeButton("Quit", mainBox);
		
		mainBox.add(Box.createGlue());
		mainMenu = new JPanel();
		mainMenu.add(mainBox);
		this.add(mainMenu, BorderLayout.CENTER);
		
		//************Initialize Pause Menu*****************
		pauseBox = new Box(BoxLayout.Y_AXIS);
		pauseBox.add(new JLabel("Pause", JLabel.CENTER));
		
		resume = initializeButton("Resume", pauseBox);
		pauseBox.add(quit);
		pauseMenu = new JPanel();
		pauseMenu.add(pauseBox);
		//**************************************************
		
		//************Initialize HighScores Menu*************
		//TODO:  Add more to this
		scores = new JPanel();
		//scores.add(scoresBox);
		//***************************************************
		
		//*************Initialize About Menu*****************
		//TODO:  Add more to this
		aboutGame = new JPanel();
		//***************************************************
		
		//***********Initialize Instructions Menu************
		//TODO:  Add more to this
		howToPlay = new JPanel();
		//***************************************************
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String action = e.getActionCommand();
		
		if(action.equals("2 Players"))
		{
			swapToGame();
			game.reset();
		}
		else if(action.equals("Instructions"))
			switchToInstructions();
		else if(action.equals("High Scores"))
			switchToHighScores();
		else if(action.equals("About"))
			switchToAbout();
		else if(action.equals("Resume"))
			resumeGame();
		else if(action.equals("Quit"))
		{
			boolean answer = game.quit();
			if(answer)
				System.exit(0);
		}
	}
	
	private JButton initializeButton(String label, Box box)
	{
		JButton button = new JButton(label);
		button.setActionCommand(label);
		button.addActionListener(this);
		box.add(button);
		return button;
	}
	
	public void swapToGame()
	{
		Container container = getParent();
		container.remove(this);
		container.add(view, BorderLayout.CENTER);
		view.requestFocus();
		validate();
	}
	
	public void switchToPause()
	{
		try
		{
			this.remove(mainMenu);
		}
		catch (Exception e){e.printStackTrace();}
		this.add(pauseMenu, BorderLayout.CENTER);
		validate();
	}
	
	public void switchToMain()
	{
		try
		{
			this.remove(pauseMenu);
		}
		catch(Exception e){e.printStackTrace();}
		this.add(mainMenu, BorderLayout.CENTER);
		validate();
	}
	
	private void switchToInstructions()
	{
		try
		{
			this.remove(mainMenu);
		}
		catch(NullPointerException e){}
		this.add(howToPlay, BorderLayout.CENTER);
		validate();
	}
	
	private void switchToHighScores()
	{
		try
		{
			this.remove(mainMenu);
		}
		catch(NullPointerException e){}
		this.add(scores, BorderLayout.CENTER);
		validate();
	}
	
	private void switchToAbout()
	{
		try
		{
			this.remove(mainMenu);
		}
		catch(NullPointerException e){}
		this.add(aboutGame, BorderLayout.CENTER);
		validate();
	}
	
	private void resumeGame()
	{
		
	}
}
