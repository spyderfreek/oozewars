package oozeWars;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

@SuppressWarnings("serial")
public class MenuScreen extends JPanel implements ActionListener
{
	private OozeView view;
	private JPanel background;
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
	private Font font = Label.initFont().deriveFont(30f);

	public MenuScreen(final OozeWars game, OozeView view) 
	{
		super();
		
		this.game = game;
		this.view = view;
		view.setMenu(this);
		
		background = new JPanel()
		{
			Backdrop image = new Backdrop("RadioactiveGoop.jpg");
			
			@Override
			protected void paintComponent(Graphics g) 
			{
				image.draw( (Graphics2D)g, game );
			}
			
		};
		
		
		foreground = new JPanel(new BorderLayout());
		
		returnToMenu = new JButton[4];
		for(int i = 0; i < returnToMenu.length; i++)
		{
			returnToMenu[i] = new JButton("Return to Menu");
			returnToMenu[i].setActionCommand("Return to Menu");
			returnToMenu[i].addActionListener(this);
			returnToMenu[i].setAlignmentX(0.5f);
			returnToMenu[i].setFont(font.deriveFont(20));
			returnToMenu[i].setToolTipText("Return to the main menu.");
		}
		
		title = new JLabel("OozeWars", JLabel.CENTER);
		title.setFont(font.deriveFont(60f));
		title.setForeground(Color.GREEN);
		foreground.add(title, BorderLayout.NORTH);
		//foreground.setOpaque(false);
		
		
		
		mainBox = new Box(BoxLayout.Y_AXIS);
		JLabel mainLabel = new JLabel("Main Menu", JLabel.CENTER);
		mainLabel.setFont(font);
		mainBox.add(mainLabel);
		mainLabel.setAlignmentX(.5f);
		mainBox.add(Box.createVerticalStrut(20));
		//mainBox.setOpaque(false);
		
		twoPlayer = initializeButton("2 Players", mainBox);
		twoPlayer.setToolTipText("Start a two player game.");
		instructions = initializeButton("Instructions", mainBox);
		instructions.setToolTipText("How to play Ooze Wars.");
		highScores = initializeButton("High Scores", mainBox);
		highScores.setToolTipText("View the high scores.");
		about = initializeButton("About", mainBox);
		about.setToolTipText("Information about Ooze Wars");
		quit = initializeButton("Quit", mainBox);
		quit.setToolTipText("Quit Ooze Wars?");
		
		//mainBox.add(Box.createGlue());
		mainMenu = new JPanel();
		mainBox.setOpaque(false);
		mainMenu.add(mainBox);
		mainMenu.setOpaque(false);
		
		foreground.add(mainMenu, BorderLayout.CENTER);
		
		//************Initialize Pause Menu*****************
		pauseBox = new Box(BoxLayout.Y_AXIS);
		JLabel pauseLabel = new JLabel("PAUSED", JLabel.CENTER);
		pauseLabel.setFont(font);
		pauseBox.add(pauseLabel);
		pauseLabel.setAlignmentX(.5f);
		pauseBox.add(Box.createVerticalStrut(20));
		
		resume = initializeButton("Resume", pauseBox);
		resume.setToolTipText("Continue playing?");
		
		pauseBox.add(returnToMenu[0]);
		returnToMenu[0].setAlignmentX(.5f);
		pauseMenu = new JPanel();
		pauseBox.setOpaque(false);
		pauseMenu.add(pauseBox);
		pauseMenu.setOpaque(false);
		//**************************************************
		
		//************Initialize HighScores Menu*************
		//TODO:  Add more to this
		scores = new JPanel();
		deleteHS = new JButton("Delete High Score");
		deleteHS.addActionListener(this);
		deleteHS.setActionCommand("Delete High Score");
		deleteHS.setFont(font);
		deleteHS.setToolTipText("Delete the high scores.");
		scores.add(deleteHS);
		scores.add(returnToMenu[1]);
		scores.setOpaque(false);
		//scores.add(scoresBox);
		//***************************************************
		
		//*************Initialize About Menu*****************
		aboutGame = new JPanel();
		BoxLayout layout = new BoxLayout(aboutGame, BoxLayout.Y_AXIS);
		aboutGame.setLayout(layout);
		String info = "Game by Sean Fedak and Nick Kitten\n" + 
			"(with additional code and help from Prof. Sean Luke), 2010";
		JTextPane aboutText = new JTextPane();
		SimpleAttributeSet atts = new SimpleAttributeSet();
		StyleConstants.setAlignment(atts, StyleConstants.ALIGN_CENTER);
		aboutText.setText(info);
		StyledDocument doc = aboutText.getStyledDocument();
		doc.setParagraphAttributes(0, 300, atts, false);
		aboutText.setEnabled(false);
		aboutText.setFont(font.deriveFont(34f));
		aboutText.setForeground(Color.black);
		aboutText.setDisabledTextColor(Color.black);
		aboutText.setAlignmentX(.5f);
		//aboutText.setHorizontalAlignment(JTextField.CENTER);

		aboutText.setOpaque(false);
		
		JLabel aboutLabel = new JLabel("About The Game");
		aboutLabel.setFont(font);
		aboutLabel.setAlignmentX(.5f);
		aboutGame.add( aboutLabel );
		aboutGame.add( Box.createVerticalStrut(100) );
		aboutGame.add( aboutText );
		aboutGame.add( returnToMenu[2] );
		aboutGame.setOpaque(false);
		//***************************************************
		
		//***********Initialize Instructions Menu****************************************
		//TODO:  Add more to this
		howToPlay = new JPanel();
		BoxLayout layout2 = new BoxLayout(howToPlay, BoxLayout.Y_AXIS);
		howToPlay.setLayout(layout2);
		String instruct = "You are two blobs fighting to the death!\n Try to kill your " +
				"opponent by shooting their head.\n You can cut off your opponent's blob " +
				"by running through the trailing part of their blob.\n Be careful, shooting uses your blob up!" +
				"\n\n Player 1 Controls:\n" +
				"Left:  a\n Right:  d\n Up:  w\n Down:  s\n Shoot:  Left- or Right-Shift\n\n" +
				"Player 2 Controls:\n" +
				"Left:  j\n Right:  l\n Up:  i\n Down:  k\n Shoot:  Left- or Right-Ctrl\n\n" +
				"Power Ups:\n" +
				"Yellow (I):  Invincible! Player is invincible for 10 seconds.\n" +
				"Red (N):  Nitro!  Player's shots do double damage for 10 seconds.\n" +
				"Orange (P):  Extra Particles!  Players receives 10 extra particles instantaneously.\n" +
				"Cyan (G):  Glue!  Player's blob will be harder to remove particles from for 10 seconds.\n" +
				"Magenta (H):  Heal!  All Particles currently in player's blob will be fully healed.";
		
		JTextPane instructText = new JTextPane();
		SimpleAttributeSet attribs = new SimpleAttributeSet();
		StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
		instructText.setText(instruct);
		StyledDocument doc2 = instructText.getStyledDocument();
		doc2.setParagraphAttributes(0, 1000, attribs, false);
		instructText.setEnabled(false);
		instructText.setFont(font.deriveFont(17f));
		instructText.setForeground(Color.black);
		instructText.setDisabledTextColor(Color.black);
		instructText.setAlignmentX(.5f);
		//instructText.setHorizontalAlignment(JTextField.CENTER);
		instructText.setOpaque(false);
		
		JLabel instructLabel = new JLabel("Instructions");
		instructLabel.setFont(font);
		instructLabel.setAlignmentX(.5f);
		instructLabel.setOpaque(false);
		howToPlay.add( instructLabel );
		howToPlay.add( instructText );
		howToPlay.add( returnToMenu[3] );
		howToPlay.setOpaque(false);
		//******************************************************************************
		

		setLayout(new OverlayLayout(this)
		{
			public void paintComponent(Graphics g)
			{
				background.paint(g);
				foreground.paint(g);
			}
		});
		add( foreground, "foreground" );
		add( background, "background" );
		background.setVisible(true);
		foreground.setOpaque(false);
		foreground.setVisible(true);
		this.setVisible(true);
		validate();
		background.updateUI();
		background.repaint();
		foreground.updateUI();
		foreground.repaint();
		updateUI();
		this.repaint();
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
		button.setFont(font.deriveFont(30f));
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
		view.updateUI();
		view.repaint();
		startMusic();
	}
	
	public void switchToPause()
	{
		try{foreground.remove(mainMenu);}
		catch(NullPointerException e){}
		try{foreground.remove(scores);}
		catch(NullPointerException e){}
		try{foreground.remove(howToPlay);}
		catch(NullPointerException e){}
		try{foreground.remove(aboutGame);}
		catch(NullPointerException e){}
		
		stopMusic();
		foreground.add(pauseMenu, BorderLayout.CENTER);
		this.requestFocus();
		this.validate();
		this.updateUI();
		this.repaint();
	}
	
	public void switchToMain()
	{
		try{foreground.remove(pauseMenu);}
		catch(NullPointerException e){}
		try{foreground.remove(scores);}
		catch(NullPointerException e){}
		try{foreground.remove(howToPlay);}
		catch(NullPointerException e){}
		try{foreground.remove(aboutGame);}
		catch(NullPointerException e){}
		
		game.stop();
		foreground.add(mainMenu, BorderLayout.CENTER);
		this.requestFocus();
		this.validate();
		this.updateUI();
		this.repaint();
	}
	
	private void switchToInstructions()
	{
		try
		{
			foreground.remove(mainMenu);
		}
		catch(NullPointerException e){}
		foreground.add(howToPlay, BorderLayout.CENTER);
		this.requestFocus();
		this.updateUI();
		this.repaint();
	}
	
	private void switchToHighScores()
	{
		try
		{
			foreground.remove(mainMenu);
		}
		catch(NullPointerException e){}
		foreground.add(scores, BorderLayout.CENTER);
		this.requestFocus();
		this.validate();
		this.updateUI();
		this.repaint();
	}
	
	private void switchToAbout()
	{
		try
		{
			foreground.remove(mainMenu);
		}
		catch(NullPointerException e){}
		foreground.add(aboutGame, BorderLayout.CENTER);
		this.requestFocus();
		this.validate();
		this.updateUI();
		this.repaint();
	}
	
	private void resumeGame()
	{
		swapToGame();
		game.togglePaused();
	}
	
	private void startMusic()
	{
		game.startMusic();
	}
	
	private void stopMusic()
	{
		game.stopMusic();
	}
}
