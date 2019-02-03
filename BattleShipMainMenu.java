package game;

/*
 * Name: Battleship main menu
 * Author: Richard, Eric, John, Jason
 * Date: due 13/06/2018
 * Description: main menu of the battleship game
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.String;
import java.util.*;
import java.io.*;
import javax.swing.JOptionPane;

public class BattleShipMainMenu extends JFrame implements ActionListener{
	
	// GUI components
	public JLabel battleShip =  new JLabel ("BattleShip");
	public JButton startGame = new JButton ("Start");
	public JButton about = new JButton ("About");
	public JButton quit = new JButton ("Quit");
	public JButton simple  = new JButton ("Simple Ai");
	public JButton advanced = new JButton ("Advanced Ai");
	public JButton cancel = new JButton ("Cancel");
	
	// explaining the game
	String aboutString = "BattleShip, by Richard, Eric, John, and Jason\n\nThe rules of game are as follows:\n\n1. You must prepare a 10 by 10 grid"
			+ "(you will see the format as you start the game)\n2. You must place ships on the Grid(Horizontal and/or Vertical only)"
			+ "\n3. The Ships are: Carrier 5 slots, Battleship 4 slots, Submarine 3 slots, Cruiser 3 slots, Destroyer s slots"
			+ "\n4. Player and Ai takes turn choosing coordinates to hit"
			+ "\n5. You must be honest regarding hitting/sinking of ships\n6. The first to sink all of opponent's ship wins"
			+ "\n7. Have Fun!";
	
	public JPanel mainPan = new JPanel();
	
	/**
	 * constructor for the GUI class
	 */
	public BattleShipMainMenu () {
		
		//decoration
		this.setUndecorated(true);
		this.setSize(480, 360);
		this.setTitle("BATTLESHIP");
		this.setBackground(Color.WHITE);
		this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		this.setResizable(false);
		
		// finding center of screen and put frame there
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int dimX = (int) ((dimension.getWidth() - this.getWidth())/2);
		int dimY = (int) ((dimension.getHeight() - this.getHeight())/2);
		this.setLocation(dimX, dimY);
		
		// decoration
		Font title = new Font ("Serif", Font.BOLD, 46);
		Font buttons = new Font ("Serif", Font.ITALIC, 30);
		
		battleShip.setFont(title);
		startGame.setFont(buttons);
		about.setFont(buttons);
		quit.setFont(buttons);
		simple.setFont(buttons);
		advanced.setFont(buttons);
		cancel.setFont(buttons);
		
		startGame.setBackground(Color.white);
		about.setBackground(Color.white);
		quit.setBackground(Color.white);
		simple.setBackground(Color.white);
		advanced.setBackground(Color.white);
		cancel.setBackground(Color.white);
		
		// add interaction to buttons
		startGame.addActionListener(this);
		about.addActionListener(this);
		quit.addActionListener(this);
		simple.addActionListener(this);
		advanced.addActionListener(this);
		cancel.addActionListener(this);
		
		// setting layouts and alignments
		battleShip.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.setLayout(new GridBagLayout());
		BoxLayout general = new BoxLayout (mainPan, BoxLayout.Y_AXIS);
		mainPan.setLayout(general);
		
		mainPan.add(battleShip, new GridBagConstraints());
		mainPan.add(startGame, new GridBagConstraints());
		mainPan.add(about, new GridBagConstraints());
		mainPan.add(quit, new GridBagConstraints());
		mainPan.add(simple, new GridBagConstraints());
		mainPan.add(advanced, new GridBagConstraints());
		mainPan.add(cancel, new GridBagConstraints());
		
		this.add(mainPan, new GridBagConstraints());
		
		battleShip.setAlignmentX(Component.CENTER_ALIGNMENT);
		startGame.setAlignmentX(Component.CENTER_ALIGNMENT);
		about.setAlignmentX(Component.CENTER_ALIGNMENT);
		quit.setAlignmentX(Component.CENTER_ALIGNMENT);
		simple.setAlignmentX(Component.CENTER_ALIGNMENT);
		advanced.setAlignmentX(Component.CENTER_ALIGNMENT);
		cancel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		battleShip.setVisible(true);
		startGame.setVisible(true);
		about.setVisible(true);
		quit.setVisible(true);
		simple.setVisible(false);
		advanced.setVisible(false);
		cancel.setVisible(false);
		
		mainPan.setVisible(true);
		
		this.setVisible(true);
	}
	
	/**
	 * User interactions
	 */
	public void actionPerformed(ActionEvent event) {
		
		// get button pressed
		String command = event.getActionCommand();
		
		// selection to identify button pressed
		if (command.equals("Start")) {
			
			// change frames artificially
			startGame.setVisible(false);
			about.setVisible(false);
			quit.setVisible(false);
			simple.setVisible(true);
			advanced.setVisible(true);
			cancel.setVisible(true);
			
		} else if (command.equals("Simple Ai")) {
			
			//call simple Ai
			BattleShip2 game = new BattleShip2(0);
			this.dispose();
			
		} else if (command.equals("Advanced Ai")) {
			
			//call advanced Ai
			BattleShip2 game = new BattleShip2(1);
			this.dispose();
			
		} else if (command.equals("Cancel")) {
			
			// change frame artificially
			battleShip.setVisible(true);
			startGame.setVisible(true);
			about.setVisible(true);
			quit.setVisible(true);
			simple.setVisible(false);
			advanced.setVisible(false);
			cancel.setVisible(false);
			
		} else if (command.equals("About")) {
			
			// pop up panel to explain the game
			JOptionPane.showMessageDialog(null, aboutString);
			
		} else if (command.equals("Quit")) {
			
			this.dispose();
			
		}
		
	}
	
	/**
	 * main method to start GUI
	 * @param args
	 */
	public static void main (String[] args) {
		BattleShipMainMenu game = new BattleShipMainMenu();
	}
}
