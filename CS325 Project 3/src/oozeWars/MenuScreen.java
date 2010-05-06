package oozeWars;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

@SuppressWarnings("serial")
public class MenuScreen extends JPanel implements ActionListener
{
	private OozeView view;
	private JPanel mainMenu, pauseMenu;
	private JLabel title;
	private OozeWars game;
	private Box box;
	JButton twoPlayer;
	JButton instructions;
	JButton highScores;
	JButton about;
	JButton quit;

	public MenuScreen(OozeWars game, OozeView view) 
	{
		super(new BorderLayout());
		this.game = game;
		this.view = view;
		view.setMenu(this);
		
		title = new JLabel("OozeWars", JLabel.CENTER);
		this.add(title, BorderLayout.NORTH);
		
		box = new Box(BoxLayout.Y_AXIS);
		
		twoPlayer = initializeButton("2 Players");
		instructions = initializeButton("Instructions");
		highScores = initializeButton("High Scores");
		about = initializeButton("About");
		quit = initializeButton("Quit");
		
		box.add(Box.createGlue());
		mainMenu = new JPanel();
		mainMenu.add(box);
		this.add(mainMenu, BorderLayout.CENTER);
	}
	
	public void swapToGame()
	{
		Container container = getParent();
		container.remove(this);
		container.add(view, BorderLayout.CENTER);
		view.requestFocus();
	}
	
	private JButton initializeButton(String label)
	{
		JButton button = new JButton(label);
		button.setActionCommand(label);
		button.addActionListener(this);
		box.add(button);
		return button;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String action = e.getActionCommand();
		
		if(action.equals("2 Players"))
		{
			swapToGame();
			game.reset();
		}
	}
}
