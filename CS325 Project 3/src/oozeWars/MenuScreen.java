package oozeWars;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

@SuppressWarnings("serial")
public class MenuScreen extends JPanel implements ActionListener
{
	private OozeView view;
	private Canvas background;
	private JPanel foreground;
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
	private JButton[] returnToMenu;
	private JButton deleteHS;

	public MenuScreen(OozeWars game, OozeView view) 
	{
		super(new BorderLayout());
		
		this.game = game;
		this.view = view;
		view.setMenu(this);
		
		returnToMenu = new JButton[4];
		for(int i = 0; i < returnToMenu.length; i++)
		{
			returnToMenu[i] = new JButton("Return to Menu");
			returnToMenu[i].setActionCommand("Return to Menu");
			returnToMenu[i].addActionListener(this);
		}
		
		title = new JLabel("OozeWars", JLabel.CENTER);
		this.add(title, BorderLayout.NORTH);
		
		mainBox = new Box(BoxLayout.Y_AXIS);
		JLabel mainLabel = new JLabel("Main Menu", JLabel.CENTER);
		mainBox.add(mainLabel);
		mainLabel.setAlignmentX(.5f);
		mainBox.add(Box.createVerticalStrut(20));
		
		twoPlayer = initializeButton("2 Players", mainBox);
		instructions = initializeButton("Instructions", mainBox);
		highScores = initializeButton("High Scores", mainBox);
		about = initializeButton("About", mainBox);
		quit = initializeButton("Quit", mainBox);
		
		//mainBox.add(Box.createGlue());
		mainMenu = new JPanel();
		mainMenu.add(mainBox);
		this.add(mainMenu, BorderLayout.CENTER);
		
		//************Initialize Pause Menu*****************
		pauseBox = new Box(BoxLayout.Y_AXIS);
		JLabel pauseLabel = new JLabel("PAUSED", JLabel.CENTER);
		pauseBox.add(pauseLabel);
		pauseLabel.setAlignmentX(.5f);
		pauseBox.add(Box.createVerticalStrut(20));
		
		resume = initializeButton("Resume", pauseBox);
		
		pauseBox.add(returnToMenu[0]);
		returnToMenu[0].setAlignmentX(.5f);
		pauseMenu = new JPanel();
		pauseMenu.add(pauseBox);
		//**************************************************
		
		//************Initialize HighScores Menu*************
		//TODO:  Add more to this
		scores = new JPanel();
		deleteHS = new JButton("Delete High Score");
		deleteHS.addActionListener(this);
		deleteHS.setActionCommand("Delete High Score");
		scores.add(deleteHS);
		scores.add(returnToMenu[1]);
		//scores.add(scoresBox);
		//***************************************************
		
		//*************Initialize About Menu*****************
		//TODO:  Add more to this
		aboutGame = new JPanel();
		aboutGame.add(returnToMenu[2]);
		//***************************************************
		
		//***********Initialize Instructions Menu************
		//TODO:  Add more to this
		howToPlay = new JPanel();
		howToPlay.add(returnToMenu[3]);
		//***************************************************
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String action = e.getActionCommand();
		
		if(action.equals("2 Players"))
		{
			game.setNumPlayers(2);
			swapToGame();
			game.reset();
		}
		else if(action.equals("Instructions"))
			switchToInstructions();
		else if(action.equals("High Scores"))
			switchToHighScores();
		else if(action.equals("About"))
			switchToAbout();
		else if(action.equals("Return to Menu"))
			switchToMain();
		else if(action.equals("Delete High Score"))
		{
			game.deleteHighScore();
			//TODO:  Make a method to do this, need to remember to call updateUI();
		}
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
		button.setAlignmentX(.5f);
		box.add(Box.createVerticalStrut(20));
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
		try{this.remove(mainMenu);}
		catch(NullPointerException e){}
		try{this.remove(scores);}
		catch(NullPointerException e){}
		try{this.remove(howToPlay);}
		catch(NullPointerException e){}
		try{this.remove(aboutGame);}
		catch(NullPointerException e){}
		
		this.add(pauseMenu, BorderLayout.CENTER);
		this.requestFocus();
		this.updateUI();
	}
	
	public void switchToMain()
	{
		try{this.remove(pauseMenu);}
		catch(NullPointerException e){}
		try{this.remove(scores);}
		catch(NullPointerException e){}
		try{this.remove(howToPlay);}
		catch(NullPointerException e){}
		try{this.remove(aboutGame);}
		catch(NullPointerException e){}
		
		this.add(mainMenu, BorderLayout.CENTER);
		this.requestFocus();
		this.updateUI();
	}
	
	private void switchToInstructions()
	{
		try
		{
			this.remove(mainMenu);
		}
		catch(NullPointerException e){}
		this.add(howToPlay, BorderLayout.CENTER);
		this.requestFocus();
		this.updateUI();
	}
	
	private void switchToHighScores()
	{
		try
		{
			this.remove(mainMenu);
		}
		catch(NullPointerException e){}
		this.add(scores, BorderLayout.CENTER);
		this.requestFocus();
		this.updateUI();
	}
	
	private void switchToAbout()
	{
		try
		{
			this.remove(mainMenu);
		}
		catch(NullPointerException e){}
		this.add(aboutGame, BorderLayout.CENTER);
		this.requestFocus();
		this.updateUI();
	}
	
	private void resumeGame()
	{
		swapToGame();
		game.togglePaused();
	}
}
