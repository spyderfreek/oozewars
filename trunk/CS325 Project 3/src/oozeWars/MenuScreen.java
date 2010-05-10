package oozeWars;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		setLayout(new OverlayLayout(this));
		
		this.game = game;
		this.view = view;
		view.setMenu(this);
		
		background = new JPanel()
		{
			Backdrop image = new Backdrop("RadioactiveGoop.jpg");
			
			@Override
			protected void paintComponent(Graphics g) {
				
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
		instructions = initializeButton("Instructions", mainBox);
		highScores = initializeButton("High Scores", mainBox);
		about = initializeButton("About", mainBox);
		quit = initializeButton("Quit", mainBox);
		
		//mainBox.add(Box.createGlue());
		mainMenu = new JPanel();
		mainMenu.add(mainBox);
		//mainMenu.setOpaque(false);
		foreground.add(mainMenu, BorderLayout.CENTER);
		
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
		pauseMenu.setOpaque(false);
		//**************************************************
		
		//************Initialize HighScores Menu*************
		//TODO:  Add more to this
		scores = new JPanel();
		deleteHS = new JButton("Delete High Score");
		deleteHS.addActionListener(this);
		deleteHS.setActionCommand("Delete High Score");
		deleteHS.setFont(font);
		scores.add(deleteHS);
		scores.add(returnToMenu[1]);
		//scores.add(scoresBox);
		//***************************************************
		
		//*************Initialize About Menu*****************
		//TODO:  Add more to this
		aboutGame = new JPanel();
		BoxLayout layout = new BoxLayout(aboutGame, BoxLayout.Y_AXIS);
		aboutGame.setLayout(layout);
		aboutGame.add(returnToMenu[2]);
		aboutGame.add( Box.createVerticalStrut(100) );
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
		
		aboutGame.add( aboutText );
		//***************************************************
		
		//***********Initialize Instructions Menu************
		//TODO:  Add more to this
		howToPlay = new JPanel();
		howToPlay.add(returnToMenu[3]);
		//***************************************************
		
		
		add( background );
		add( foreground );
		background.setVisible(true);
		foreground.setVisible(true);
		updateUI();
		validate();
		background.repaint();
		foreground.repaint();
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
		this.repaint();
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
		
		foreground.add(pauseMenu, BorderLayout.CENTER);
		this.requestFocus();
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
		
		foreground.add(mainMenu, BorderLayout.CENTER);
		this.requestFocus();
		this.updateUI();
		foreground.repaint();
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
		this.updateUI();
		this.repaint();
	}
	
	private void resumeGame()
	{
		swapToGame();
		game.togglePaused();
	}
}
