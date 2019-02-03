package game;

/*
 * Name: Battleship
 * Author: Richard, Eric, John, Jason
 * Date: 13/06/2018
 * Description: main battleship game GUI
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.String;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.*;
import javax.swing.Timer;

public class BattleShip extends JFrame implements ActionListener {

	// variables for keeping track of ship status
	// grid titles
	public String[] letters = { "k", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };
	// ship names for grid
	public ArrayList<String> shipNames = new ArrayList<String>();
	// ship types for calculating probability
	public ArrayList<Double> shipTypes = new ArrayList<Double>();
	// storing coordinates to be hunted
	public ArrayList<int[]> huntCoor = new ArrayList<int[]>();
	// recording ship information, used for win/lose conditions and calculating
	// probability
	public int[] computersShip = { 5, 4, 3, 3, 2 };
	public int[] playersShip = { 5, 4, 3, 3, 2 };
	// dynamic array used for temporary storage of hit location, used to access
	// last hit location
	public int[] hitting = new int[2];
	public ArrayList<Double> lastHitShip = new ArrayList<Double>();

	// variables for utility
	// gameGrid for main buttons
	public ArrayList<ArrayList<JButton>> gameGrid = new ArrayList<ArrayList<JButton>>();
	// set row and column as constant
	final int ROW = 10;
	final int COL = 10;
	// recording computer placed ships
	public int[][] placedShip = new int[ROW][COL];
	// recording probability for each cell
	public int[][] probGrid = new int[ROW][COL];

	public double[][] probKillGrid = new double[ROW][COL];
	// recording player grid information as game goes on
	public double[][] playerShip = new double[ROW][COL];
	// difficulty level, 0 = easy, 1 = advanced
	public int difficulty;
	// time recorders
	public int timeSec = 0;
	public int timeMin = 0;
	// determines whether game is started or not, used to disable unnecessary
	// button presses
	public boolean started = false;
	// determines whether it is the player's turn, used to disable unnecessary
	// button presses
	public boolean playerTurn = false;
	// variables for placing ships
	public int direction;
	public int x;
	public int y;
	public int side;
	// determine which player goes first, 0 = player, 1 = computer
	public int firstTurn;
	// temporarily recording results of a computer hit
	public double hitResult = -1;
	public int hitsOf3 = 0;
	public int[] playerHitsOf3 = { 0 };

	// GUI components
	public JPanel boxPan = new JPanel();
	public JPanel lognButton = new JPanel();
	public JButton exit = new JButton("Exit");
	public JButton start = new JButton("Start");
	public JScrollPane log = new JScrollPane();
	public JPanel logRoom = new JPanel();
	public JLabel logLabel = new JLabel("Hi, here is the log for this game.");

	// GUI components for popUp
	public JButton yes = new JButton("YES");
	public JButton no = new JButton("no");
	public JLabel confirmation = new JLabel("Was it a hit?");
	public JLabel whichShip = new JLabel("Which ship?");

	// timer
	public Timer time = null;

	/**
	 * Constructor for Main game GUI
	 * 
	 * @param diff
	 *            difficulty level for the game
	 */
	public BattleShip(int diff) {

		// initialize arrays
		for (int i = 0; i < placedShip.length; i++) {
			for (int j = 0; j < placedShip[i].length; j++) {
				placedShip[i][j] = 0;
				probGrid[i][j] = 0;
				playerShip[i][j] = 0;
			}
		}

		// initialization of ship placement by Ai
		difficulty = diff;
		ShipPlacing(difficulty);
		// create save for ship placement
		logShips();

		// initializing names of ships, cannot otherwise be done outside
		// constructor
		shipNames.add("Carrier");
		shipNames.add("Battleship");
		shipNames.add("Submarine");
		shipNames.add("Cruiser");
		shipNames.add("Destroyer");
		shipNames.add("None");

		// initializing names of ships, cannot otherwise be done outside
		// constructor
		shipTypes.add(5.0);
		shipTypes.add(4.0);
		shipTypes.add(3.1);
		shipTypes.add(3.3);
		shipTypes.add(2.0);

		// decorations
		this.setUndecorated(true);
		this.setSize(980, 460);
		this.setTitle("BATTLESHIP");
		this.setBackground(Color.WHITE);
		this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		this.setResizable(false);

		// locate center of screen and put frame in center
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int dimX = (int) ((dimension.getWidth() - this.getWidth()) / 2);
		int dimY = (int) ((dimension.getHeight() - this.getHeight()) / 2);
		this.setLocation(dimX, dimY);

		// setting layouts
		GridLayout gridLay = new GridLayout(11, 11);
		FlowLayout general = new FlowLayout();
		BoxLayout logLay = new BoxLayout(lognButton, BoxLayout.Y_AXIS);
		BoxLayout logRoomLay = new BoxLayout(logRoom, BoxLayout.Y_AXIS);

		boxPan.setLayout(gridLay);
		logRoom.setLayout(logRoomLay);
		lognButton.setLayout(logLay);
		this.setLayout(general);

		// setting component specifics
		log.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		log.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		log.setViewportView(logRoom);
		log.setPreferredSize(new Dimension(240, 360));

		exit.setPreferredSize(new Dimension(40, 40));
		exit.setBackground(Color.white);
		exit.addActionListener(this);
		start.setPreferredSize(new Dimension(40, 40));
		start.setBackground(Color.white);
		start.addActionListener(this);

		// setting component postions
		logRoom.add(logLabel);
		lognButton.add(log, new GridBagConstraints());
		lognButton.add(start, new GridBagConstraints());
		lognButton.add(exit, new GridBagConstraints());

		log.setAlignmentX(Component.CENTER_ALIGNMENT);
		start.setAlignmentX(Component.CENTER_ALIGNMENT);
		exit.setAlignmentX(Component.CENTER_ALIGNMENT);

		// initialization of game gird
		Dimension buttonDim = new Dimension(60, 40);
		for (int i = 0; i < 11; i++) {
			gameGrid.add(new ArrayList<JButton>());
			for (int j = 0; j < 11; j++) {
				if (i == 0 && j > 0) {
					gameGrid.get(i).add(new JButton(String.valueOf(j)));
					gameGrid.get(i).get(j).addActionListener(this);
				} else if (i > 0 && j == 0) {
					gameGrid.get(i).add(new JButton(letters[i]));
					gameGrid.get(i).get(j).addActionListener(this);
				} else {
					gameGrid.get(i).add(new JButton(""));
					gameGrid.get(i).get(j).addActionListener(this);
				}
				boxPan.add(gameGrid.get(i).get(j), new GridBagConstraints());
				gameGrid.get(i).get(j).setBackground(Color.white);
				gameGrid.get(i).get(j).setPreferredSize(buttonDim);
			}
		}

		// implementing timer
		time = new Timer(1000, new ActionListener() {
			public void TimeStartActionPerformed(ActionEvent e) {
			}

			// activates every second
			public void actionPerformed(ActionEvent e) {

				// incrementing time
				timeSec++;
				if (timeSec == 60) {
					timeMin++;
					timeSec = 0;
				}

				// writing time to grid
				if (timeSec < 10) {
					gameGrid.get(0).get(0).setText(String.valueOf(timeMin) + ":0" + String.valueOf(timeSec));
				} else {
					gameGrid.get(0).get(0).setText(String.valueOf(timeMin) + ":" + String.valueOf(timeSec));
				}

			}
		});

		this.add(boxPan);
		this.add(lognButton);

		boxPan.setVisible(true);
		lognButton.setVisible(true);

		this.setVisible(true);

	}

	/**
	 * User Interactions This methods will call on other methods to achieve its
	 * functions This method controls interaction with all buttons
	 */
	public void actionPerformed(ActionEvent event) {

		// gets button pressed
		String command = event.getActionCommand();

		// player starts the game
		if (command.equals("Start") && started == false) {

			// player decided whether to choose random player order or not
			int randOrNot = JOptionPane.showOptionDialog(null, "Determine randomly who goes first?", "Welcome",
					JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

			// response to previous statement
			if (randOrNot == 0) {
				firstTurn = (int) (Math.random() * 2);
			} else {
				firstTurn = JOptionPane.showOptionDialog(null, "Player goes first?", "Welcome", JOptionPane.YES_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, null, null);
			}

			// timer starts when game starts
			time.start();

			// 0 = player turn, 1 = computer turn
			if (firstTurn == 0) {

				// informs of player of who goes first
				logRoom.add(new JLabel("Player goes first!"));
				JOptionPane.showMessageDialog(null, "Player goes first!");

				// player turn starts
				playerTurn = true;

			} else {

				// informs of player of who goes first
				logRoom.add(new JLabel("Computer goes first!"));
				JOptionPane.showMessageDialog(null, "Computer goes first!");

				// not yet player turn
				playerTurn = false;

				// if choose advanced difficulty, used advanced Ai, otherwise
				// use simple Ai
				if (difficulty == 1) {
					hitResult = computerHit(hitResult);
				} else {
					hitResult = computerHitSimple();
				}

				// player turn starts after computer
				playerTurn = true;
			}

			// game status is set as started
			started = true;

		} else if (command.equals("Exit")) {

			this.dispose();

		}

		// player clicks clickable buttons when game started and current term is
		// player's turn
		if (command.equals("") && started == true && playerTurn == true) {

			// for every button in grid...
			for (int i = 1; i < gameGrid.size(); i++) {
				for (int j = 1; j < gameGrid.size(); j++) {

					// select the clicked button
					if (gameGrid.get(i).get(j) == event.getSource()) {

						// record player's hit
						logRoom.add(new JLabel("Player hits " + letters[i] + j + "!"));

						// if player hits a ship, display information and
						// determine which ship is hit
						if (placedShip[i - 1][j - 1] != 0) {

							JOptionPane.showMessageDialog(null, "You hit!");
							determinePlayerHit(placedShip[i - 1][j - 1]);
							gameGrid.get(i).get(j).setBackground(Color.RED);

						} else {

							logRoom.add(new JLabel("Player missed!"));
							JOptionPane.showMessageDialog(null, "You missed!");
							gameGrid.get(i).get(j).setBackground(Color.BLUE);

						}

					}

				}
			}

			// player turn ends after one click
			playerTurn = false;

			// computer turn starts, difficulty situation is the same as above
			if (difficulty == 1) {
				hitResult = computerHit(hitResult);
			} else {
				hitResult = computerHitSimple();
			}

			// player turn restarts after computer's turn
			playerTurn = true;

		}

	}

	/**
	 * main Ship Placing method this method will call on other methods to
	 * accomplish its function
	 * 
	 * @param newDifficulty
	 *            difficulty level of the game
	 */
	public void ShipPlacing(int newDifficulty) {

		// initialize the size of the ship to be 2
		int size = 2;

		// if chose simple Ai
		if (newDifficulty == 0) {

			// while loop used to place ships from size 2 to size 5
			while (size != 6) {

				if (size == 3) {

					for (int i = 0; i < 2; i++) {

						randomize();

						while (canPlace(x, y, direction, size) == false) {
							randomize();
						}

						place(x, y, direction, size);
					}

				} else {

					randomize();

					while (canPlace(x, y, direction, size) == false) {
						randomize();
					}

					place(x, y, direction, size);

				}

				size++;
			}

		} else if (newDifficulty == 1) {

			randomize();

			if (side == 0) {
				place(0, 0, 2, 2);
				place(0, 9, 3, 3);
				place(4, 0, 2, 3);
				place(9, 9, 0, 4);
				place(9, 0, 1, 5);

			} else if (side == 1) {

				place(0, 0, 2, 3);
				place(0, 9, 3, 3);
				place(6, 9, 0, 4);
				place(9, 9, 0, 2);
				place(9, 0, 1, 5);

			} else if (side == 2) {

				place(0, 0, 2, 3);
				place(0, 9, 3, 4);
				place(4, 9, 0, 3);
				place(9, 9, 0, 2);
				place(9, 0, 1, 5);

			} else if (side == 3) {

				place(0, 0, 2, 4);
				place(0, 9, 3, 5);
				place(4, 9, 0, 2);
				place(9, 9, 0, 3);
				place(9, 0, 1, 3);

			}

		}

	}

	/**
	 * Determines whether the row/column can be placed
	 * 
	 * @param r
	 *            row
	 * @param c
	 *            column
	 * @param d
	 *            direction
	 * @param newSize
	 *            size of ship
	 * @return whether ship can be placed
	 */
	public boolean canPlace(int r, int c, int d, int newSize) {

		boolean check = true;

		if (d == 0) {

			if ((r - (newSize - 1)) < 0) {

				check = false;

			} else {

				for (int i = 0; i < newSize; i++) {

					if (placedShip[r][c] != 0) {
						check = false;
					}
					r--;

				}

			}

		} else if (d == 1) {

			if ((c + (newSize + 1)) >= COL) {

				check = false;

			} else {

				for (int i = 0; i < newSize; i++) {

					if (placedShip[r][c] != 0) {
						check = false;
					}
					c++;

				}

			}

		} else if (d == 2) {

			if ((r + (newSize - 1)) >= ROW) {

				check = false;

			} else {

				for (int i = 0; i < newSize; i++) {

					if (placedShip[r][c] != 0) {
						check = false;
					}
					r++;

				}

			}

		} else if (d == 3) {

			if ((c - (newSize - 1)) < 0) {

				check = false;

			} else {

				for (int i = 0; i < newSize; i++) {

					if (placedShip[r][c] != 0) {
						check = false;
					}
					c--;

				}

			}

		}

		return check;

	}

	/**
	 * method for placing ship with passed in predetermined values
	 * 
	 * @param r
	 *            row
	 * @param c
	 *            column
	 * @param d
	 *            direction
	 * @param newSize
	 *            size of ship
	 */
	public void place(int r, int c, int d, int newSize) {

		if (d == 0) {

			for (int a = 0; a < newSize; a++) {
				placedShip[r][c] = newSize;
				r--;
			}

		} else if (d == 1) {

			for (int a = 0; a < newSize; a++) {
				placedShip[r][c] = newSize;
				c++;
			}

		} else if (d == 2) {

			for (int a = 0; a < newSize; a++) {
				placedShip[r][c] = newSize;
				r++;
			}

		} else if (d == 3) {

			for (int a = 0; a < newSize; a++) {
				placedShip[r][c] = newSize;
				c--;
			}

		}

	}

	/**
	 * generate random numbers
	 */
	public void randomize() {

		Random rand = new Random();

		side = (int) (rand.nextDouble() * 4);
		direction = (int) (rand.nextDouble() * 4);
		x = (int) (rand.nextDouble() * ROW);
		y = (int) (rand.nextDouble() * COL);

	}

	/**
	 * This methods determines all probable orientation of ships on all grids It
	 * is done by: Iterating through all possible ship positions checking
	 * boundaries checking game grids for collisions incrementing for all
	 * possible positions
	 */
	public void determineProbability() {

		// refresh probability grid for each calculation
		for (int i = 0; i < probGrid.length; i++) {
			for (int j = 0; j < probGrid[i].length; j++) {
				probGrid[i][j] = 0;
			}
		}

		// for each cell...
		for (int i = 0; i < playerShip.length; i++) {
			for (int j = 0; j < playerShip[i].length; j++) {

				// for each ship type...
				for (int p = 0; p < shipTypes.size(); p++) {

					// horizontal check
					// the length in x direction
					
					double shipTypeInt = shipTypes.get(p);
					
					int lX = i + (int)shipTypeInt - 1;

					// if the length does not exceed grid
					if (lX <= playerShip.length - 1) {

						// area is not occupied
						boolean areaOccupied = false;

						// check for obstacles (hit cells etc)
						for (int k = i; k <= lX; k++) {

							// if there is an obstacle than area is occupied
							if (playerShip[k][j] != 0) {
								areaOccupied = true;
								break;
							}

						}

						// if area is not occupied then increment probable
						// positions
						if (areaOccupied == false) {
							for (int k = i; k <= lX; k++) {
								probGrid[k][j] += 1;
							}
						}

					}

				}

				// same as above but in the vertical direction
				for (int p = 0; p < shipTypes.size(); p++) {

					double shipTypeInt = shipTypes.get(p);
					
					int lY = j + (int)shipTypeInt - 1;

					if (lY <= playerShip[i].length - 1) {

						boolean areaOccupied = false;

						for (int k = j; k <= lY; k++) {

							if (playerShip[i][k] != 0) {
								areaOccupied = true;
								break;
							}

						}

						if (areaOccupied == false) {
							for (int k = j; k <= lY; k++) {
								probGrid[i][k] += 1;
							}
						}

					}

				}

			}

		}
	}

	/**
	 * simple Ai for hitting player ships randomly selecting a spot to hit
	 * 
	 * @return value of position hit
	 */
	public double computerHitSimple() {

		// hit result
		double result;

		// hit coordinates
		int x, y;

		// randomize unused values
		do {
			x = (int) (Math.random() * 10);
			y = (int) (Math.random() * 10);
		} while (playerShip[x][y] != 0);

		// setting last hit values
		hitting[0] = x;
		hitting[1] = y;

		// determined position and result hit
		result = determineComputerHit();
		return result;
	}

	/**
	 * algorithm for Ai hitting player ships
	 * 
	 * @param result
	 *            result value of position of last hit
	 * @return result value of position of this hit
	 */
	public double computerHit(double result) {

		// if last hit is a miss and there are no "to be hunted" cells
		if (result == -1) {

			// use search method to determine hit coordinate
			hitting = search();

			// record and show procedure
			logRoom.add(new JLabel("The Computer hits " + letters[hitting[0] + 1] + (hitting[1] + 1) + "!"));

			// determine and record results of hitting position
			result = determineComputerHit();
			playerShip[hitting[0]][hitting[1]] = result;

		} else {

			// if there is "to be hunted" cells or if last hit landed, use hunt
			// method
			hitting = kill(result);

			// if last hit was set as nothing, use search instead, used to
			// transition from hunt() to search()
			if (hitting == null) {
				hitting = search();
			}

			// record game process
			logRoom.add(new JLabel("The Computer hits " + letters[hitting[0] + 1] + (hitting[1] + 1) + "!"));

			// determine and record hit result
			result = determineComputerHit();
			playerShip[hitting[0]][hitting[1]] = result;

			// if there are more "to be hunted" cells, change result so that
			// hunt will keep getting activated
			if (hitting != null && shipTypes.contains(new Double(lastHitShip.get(lastHitShip.size() - 1)))) {
				result = -2;
			}

		}

		// if game did not end, show player's turn
		if (IntStream.of(playersShip).sum() != 0 && IntStream.of(computersShip).sum() != 0) {
			JOptionPane.showMessageDialog(null, "Your Turn!");
		}

		return result;

	}

	/**
	 * This method activates when searching for ship
	 * 
	 * @return returns the number on the position hit
	 */
	public int[] search() {
		
		System.out.println("search mode engaged");

		determineProbability();
		
		// coordinates of largest chance
		int largestX = 0;
		int largestY = 0;

		// determine largest hit chance as calculated above
		for (int i = 1; i < probGrid.length; i++) {
			for (int j = 1; j < probGrid[i].length; j++) {
				if (probGrid[i][j] > probGrid[largestX][largestY]) {
					largestX = i;
					largestY = j;
				}
			}
		}

		// returns coordinate to be hit
		int[] result = { largestX, largestY };
		return result;

	}

	/**
	 * Hunt method to hunt down nearby ships when a hit a achieved
	 * 
	 * @param cor
	 *            coordinate of value last hit
	 * @param result
	 *            resultant value of last hit
	 * @return parameter/position of next hit
	 */
	public int[] hunt(int[] cor, int result) {

		// initialize coordinate to be returned
		int[] returnVar = cor;

		// only execute the following if a ship is hit
		if (result > 0) {

			// if x coordinate has space to the right
			if ((cor[0] + 1) <= 9) {
				
				// set new hunt cell to the right of last hit position
				int[] coorR = { cor[0] + 1, cor[1] };

				// if it was not hit before and is not in "to be hunted" list,
				// add to "to be hunted" list
				if (playerShip[coorR[0]][coorR[1]] == 0  && !huntCoor.contains(coorR)) {
					huntCoor.add(coorR);
				}

			}

			// same as above but to the right of last hit cell
			if ((cor[0] - 1) >= 0) {
				int[] coorL = { cor[0] - 1, cor[1] };
				if (playerShip[coorL[0]][coorL[1]] == 0 && !huntCoor.contains(coorL)) {
					huntCoor.add(coorL);
				}
			}

			// same as above but bellow the last hit cell
			if ((cor[1] + 1) <= 9) {
				int[] coorD = { cor[0], cor[1] + 1 };
				if (playerShip[coorD[0]][coorD[1]] == 0 && !huntCoor.contains(coorD)) {
					huntCoor.add(coorD);
				}
			}

			// same as above but above the last hit cell
			if ((cor[1] - 1) >= 0) {
				int[] coorU = { cor[0], cor[1] - 1 };
				if (playerShip[coorU[0]][coorU[1]] == 0 && !huntCoor.contains(coorU)) {
					huntCoor.add(coorU);
				}
			}

			if (result == 2) {
				if (playersShip[4] == 0) {
					huntCoor.clear();
				}
			} else if (result == 3 && playerHitsOf3[0] == 0) {
				if (playersShip[2] == 0) {
					huntCoor.clear();
				}
			} else if (result == 3 && playerHitsOf3[0] == 1) {
				if (playersShip[3] == 0) {
					huntCoor.clear();
				}
			} else if (result == 4) {
				if (playersShip[1] == 0) {
					huntCoor.clear();
				}
			} else if (result == 5) {
				if (playersShip[0] == 0) {
					huntCoor.clear();
				}
			}

		}

		// remove last hit coordinate to eliminate repetition
		huntCoor.remove(cor);

		huntCoor = new ArrayList<>(new HashSet<>(huntCoor));

		// if there are still cells to be hunted, return coordinates of cell
		// from list,
		// otherwise return null to transition to search mode
		if (!huntCoor.isEmpty()) {
			returnVar = huntCoor.get(0);
		} else {
			returnVar = null;
		}

		return returnVar;

	}
	
	public int[] kill (double lastResult) {
		
		System.out.println("Kill mode engaged");
		
		int[] result = new int[2];
		
		killProb(lastResult);
		
		int largestX = 0;
		int largestY = 0;
		
		for (int i = 0; i < probKillGrid.length; i++) {
			for (int j = 0; j < probKillGrid[i].length; j++) {
				if (probKillGrid[i][j] > probKillGrid[largestX][largestY]) {
					largestX = i;
					largestY = j;
				}
			}
		}
		
		if (probKillGrid[largestX][largestY] == 0.0) {
			result = null;
		} else {
			result[0] = largestX;
			result[1] = largestY;
		}
		
		return result;
		
	}
	
	public void killProb (double result) {
		
		for (int i = 0; i < probKillGrid.length; i++) {
			for (int j = 0; j < probKillGrid[i].length; j++) {
				probKillGrid[i][j] = 0;
			}
		}
		
		for (int cX = 0; cX < ROW; cX++){
			for (int cY = 0; cY < COL; cY++){
				
				
				for (int p = 0; p < playersShip.length; p++){
					if (playersShip[p] != 0){// if the ship has not been sank
						
						//horizontal placement
						int lX = cX + playersShip[p] - 1, 
								lY = cY;
						
						//check out of bound
						if (lX >= ROW || lY >= COL) continue;
						
						//declare boolean conditions to determine if the grid probability is viable
						boolean hitIncluded = false;
						boolean missIncluded = false;
						
						int nHitIncluded = 0;
						
						//check if the selected area has successful hit on the ship
						
						for (int i = cX; i <= lX; i++){
							
							Double tempCheck = playerShip[i][cY];
							
							// && shipTypes.contains(lastHitShip.get(lastHitShip.size()-1))
							
							if (playerShip[i][cY] == lastHitShip.get(lastHitShip.size()-1)){
								hitIncluded = true;
								nHitIncluded += 1;
							}
							
							
						//check if the selected area has previous miss or sank ship
							
							if (playerShip[i][cY] == -1 || (tempCheck == lastHitShip.get(lastHitShip.size()-1) && !shipTypes.contains(lastHitShip.get(lastHitShip.size()-1)))){
								missIncluded = true;
								break;
							}
						}
						
						
						if (!hitIncluded || missIncluded) continue;
						
						// if the selected area contains successful hit on the ship but not previous hit or sank, then update
						for (int i = cX; i <= lX; i++)
							probKillGrid[i][cY] += nHitIncluded;
					}
				}
				
				for (int p = 0; p < playersShip.length; p++){
					if (playersShip[p] != 0){
						
						//vertical placement
						int lX = cX, 
								lY = cY + playersShip[p] - 1;
						
						if (lX >= ROW || lY >= COL) continue;
						
						//declare boolean conditions to determine if the grid probability is viable
						boolean hitIncluded = false;
						boolean missIncluded = false;
						
						int nHitIncluded = 0;
						
						//check if the selected area has successful hit on the ship
						for (int i = cY; i <= lY; i++){
							
							Double tempCheck = playerShip[cX][i];
							
							// && shipTypes.contains(lastHitShip.get(lastHitShip.size()-1))
							
							if (playerShip[cX][i] == lastHitShip.get(lastHitShip.size()-1)){
								hitIncluded = true;
								nHitIncluded += 1;
							}
							
							//check if the selected area has previous miss or sank ship
							if (playerShip[cX][i] == -1 || (tempCheck == lastHitShip.get(lastHitShip.size()-1) && !shipTypes.contains(lastHitShip.get(lastHitShip.size()-1)))){
								missIncluded = true;
								break;
							}
						}
						
						if ((!hitIncluded) || missIncluded) continue;
						// if the selected area contains successful hit on the ship but not previous hit or sank, then update
						for (int i = cY; i <= lY; i++)
							probKillGrid[cX][i] += nHitIncluded;
					}
				}
				
			}
			
		}
		
		for (int i = 0; i < probKillGrid.length; i++) {
			for (int j = 0; j < probKillGrid[i].length; j++) {
				if (playerShip[i][j] != 0) {
					probKillGrid[i][j] = 0;
				}
			}
		}
		
		double sum5 = 0, sum4 = 0, sum31 = 0, sum33 = 0, sum2 = 0;
		boolean killedAll = true;
		
		for (int i = 0; i < lastHitShip.size(); i++) {
			if (lastHitShip.get(i) == 5.0) {
				sum5 += lastHitShip.get(i);
			}
			if (lastHitShip.get(i) == 4.0) {
				sum4 += lastHitShip.get(i);
			}
			if (lastHitShip.get(i) == 3.3) {
				sum33 += lastHitShip.get(i);
			}
			if (lastHitShip.get(i) == 3.1) {
				sum31 += lastHitShip.get(i);
			}
			if (lastHitShip.get(i) == 2.0) {
				sum2 += lastHitShip.get(i);
			}
		}
		
		if (sum5 != 0 && sum5/5.0 != 5.0) {
			killedAll = false;
		}
		if (sum4 != 0 && sum4/4.0 != 4.0) {
			killedAll = false;
		}
		if (sum31 != 0 && sum31/3.0 != 3.1) {
			killedAll = false;
		}
		if (sum33 != 0 && sum33/3.0 != 3.3) {
			killedAll = false;
		}
		if (sum2 != 0 && sum2/2.0 != 2.0) {
			killedAll = false;
		}
		
		for (int i = 0; i < probKillGrid.length; i++) {
			for (int j = 0; j < probKillGrid[i].length; j++) {
				System.out.print(probKillGrid[i][j] + " ");
			}
			System.out.println("");
		}
		
		System.out.println(sum5 + " " + sum4 + " " + sum33 + " " + sum31 + " " + sum2 + " " + killedAll);
		
		if (killedAll) {
			for (int i= 0; i < probKillGrid.length; i++) {
				for (int j = 0; j < probKillGrid.length; j++) {
					probKillGrid[i][j] = 0;
				}
			}
		}
		
		for (int i = 0; i < probKillGrid.length; i++) {
			for (int j = 0; j < probKillGrid[i].length; j++) {
				System.out.print(probKillGrid[i][j] + " ");
			}
			System.out.println("");
		}

	}

	/**
	 * determining Computer's hit on player
	 * 
	 * @return result of computer's hit (value on hit position)
	 */
	public double determineComputerHit() {

		// initialize resultant value as no hit
		double result = -1.0;

		// initiate interaction variables
		int outcome = 0;
		String ship = "";

		// if game is not over, ask player of hit/miss situation
		if (IntStream.of(playersShip).sum() != 0 && IntStream.of(computersShip).sum() != 0) {
			outcome = JOptionPane.showOptionDialog(null,
					"The Computer hits " + letters[hitting[0] + 1] + (hitting[1] + 1) + "! HIT or MISS?", "Outcome",
					JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
		}

		// if a ship is hit, determine whether it is sunk, otherwise record as
		// no hit and return
		if (outcome == 0) {

			// asking for ship sunk/not sunk information
			if (IntStream.of(playersShip).sum() != 0 && IntStream.of(computersShip).sum() != 0) {
				ship = (String) JOptionPane.showInputDialog(null, "Which ship is hit?", "", JOptionPane.YES_OPTION,
						null, shipNames.toArray(), "");
			}

			// if no ship is sunk
			if (ship == null) {
				ship = "None";
			}

			// if any ship is sunk, set result as ship value and remove ship
			// from all lists
			if (ship.equals("Carrier")) {

				result = 5.0;
				playersShip[0]--;
				lastHitShip.add(5.0);
				if (playersShip[0] == 0) {
					shipNames.remove("Carrier");
					shipTypes.remove(new Double(5.0));
					logRoom.add(new JLabel("Computer sunk Carrier!"));
					JOptionPane.showMessageDialog(null, "Computer sunk Carrier!");
				} else {
					logRoom.add(new JLabel("Computer hit Carrier!"));
					JOptionPane.showMessageDialog(null, "Computer hit Carrier!");
				}

			} else if (ship.equals("Battleship")) {

				result = 4.0;
				playersShip[1]--;
				lastHitShip.add(4.0);
				if (playersShip[1] == 0) {
					shipNames.remove("Battleship");
					shipTypes.remove(new Double(4.0));
					logRoom.add(new JLabel("Computer sunk battleShip!"));
					JOptionPane.showMessageDialog(null, "Computer sunk battleship!");
				} else {
					logRoom.add(new JLabel("Computer hit Battleship!"));
					JOptionPane.showMessageDialog(null, "Computer hit Battleship!");
				}

			} else if (ship.equals("Cruiser")) {

				result = 3.1;
				playersShip[2]--;
				lastHitShip.add(3.1);
				if (playersShip[2] == 0) {
					playerHitsOf3[0] = 1;
					shipNames.remove("Cruiser");
					shipTypes.remove(new Double(3.1));
					logRoom.add(new JLabel("Computer sunk Cruiser!"));
					JOptionPane.showMessageDialog(null, "Computer sunk Cruiser!");
				} else {
					logRoom.add(new JLabel("Computer hit Cruiser!"));
					JOptionPane.showMessageDialog(null, "Computer hit Cruiser!");
				}

			} else if (ship.equals("Submarine")) {

				result = 3.3;
				playersShip[3]--;
				lastHitShip.add(3.3);
				if (playersShip[3] == 0) {
					shipNames.remove("Submarine");
					shipTypes.remove(new Double(3.3));
					logRoom.add(new JLabel("Computer sunk Submarine!"));
					JOptionPane.showMessageDialog(null, "Computer sunk Submarine!");
				} else {
					logRoom.add(new JLabel("Computer hit Submarine!"));
					JOptionPane.showMessageDialog(null, "Computer hit Submarine!");
				}

			} else if (ship.equals("Destroyer")) {

				result = 2.0;
				playersShip[4]--;
				lastHitShip.add(2.0);
				if (playersShip[4] == 0) {
					shipNames.remove("Destroyer");
					shipTypes.remove(new Double(2.0));
					logRoom.add(new JLabel("Computer sunk Destroyer!"));
					JOptionPane.showMessageDialog(null, "Computer sunk Destroyer!");
				} else {
					logRoom.add(new JLabel("Computer hit Destroyer!"));
					JOptionPane.showMessageDialog(null, "Computer hit Destroyer!");
				}

			}

		} else {

			result = -1.0;
			logRoom.add(new JLabel("Computer Missed!"));
			JOptionPane.showMessageDialog(null, "Computer Missed!");

		}

		// if all of player's ship sinks, computer wins

		if (IntStream.of(playersShip).sum() == 0) {

			JOptionPane.showMessageDialog(null, "Computer won!!!");

			// calculate and write game scores
			tally();
			this.dispose();

		}

		return result;
	}

	/**
	 * determining if player's hit has missed
	 * 
	 * @param hitNum
	 *            the number value that was hit
	 */
	public void determinePlayerHit(int hitNum) {

		// use ship value to determine type of ship hit
		// subtract from ship counter (computesShip[])
		// if ship is not sunk then display player hit
		// if ship is sunk then display type of ship sunk
		if (hitNum == 5) {

			computersShip[0]--;

			if (computersShip[0] == 0) {
				logRoom.add(new JLabel("Player sunk the Carrier!"));
				JOptionPane.showMessageDialog(null, "Player sunk the Carrier!");
			} else {
				logRoom.add(new JLabel("Player hit the Carrier!"));
				JOptionPane.showMessageDialog(null, "Player hit the Carrier!");
			}

		} else if (hitNum == 4) {

			computersShip[1]--;

			if (computersShip[1] == 0) {
				logRoom.add(new JLabel("Player sunk the Battleship!"));
				JOptionPane.showMessageDialog(null, "Player sunk the Battleship!");
			} else {
				logRoom.add(new JLabel("Player hit the Battleship!"));
				JOptionPane.showMessageDialog(null, "Player hit the Battleship!");
			}

		} else if (hitNum == 2) {

			computersShip[4]--;

			if (computersShip[4] == 0) {
				logRoom.add(new JLabel("Player sunk the Destroyer!"));
				JOptionPane.showMessageDialog(null, "Player sunk the destroyer!");
			} else {
				logRoom.add(new JLabel("Player hit the Destroyer!"));
				JOptionPane.showMessageDialog(null, "Player hit the destroyer!");
			}

		} else if (hitNum == 3) {

			hitsOf3++;

			if (hitsOf3 <= 3) {
				computersShip[2]--;
				if (computersShip[2] == 0) {
					logRoom.add(new JLabel("Player sunk the Cruiser!"));
					JOptionPane.showMessageDialog(null, "Player sunk the Cruiser!");
				} else {
					logRoom.add(new JLabel("Player hit the Cruiser!"));
					JOptionPane.showMessageDialog(null, "Player hit the Cruiser!");
				}

			} else {

				computersShip[3]--;

				if (computersShip[3] == 0) {
					logRoom.add(new JLabel("Player sunk the Submarine!"));
					JOptionPane.showMessageDialog(null, "Player sunk the Submarine!");
				} else {
					logRoom.add(new JLabel("Player hit the Submarine!"));
					JOptionPane.showMessageDialog(null, "Player hit the Submarine!");
				}

			}

		}

		// if game has ended (all computer ship sunk), tally score and end game
		if (IntStream.of(computersShip).sum() == 0) {
			JOptionPane.showMessageDialog(null, "Player won!!!");
			tally();
			this.dispose();
		}

	}

	/**
	 * Writes to file log for current start of game
	 */
	public void logShips() {

		// try catch needed for file IO
		try {

			// declare new file and filewriter to write to file
			File saveFile = new File("PregameLog.txt");
			PrintWriter write = new PrintWriter(saveFile);

			// writing game name and placements to file
			write.println("BattleShip Pregame: ");
			write.println("Computer ship placements: ");

			// writing ship placement information in grid formation
			for (int i = 0; i < placedShip.length; i++) {
				for (int j = 0; j < placedShip[i].length; j++) {
					write.print(placedShip[i][j] + " ");
				}
				write.println("");
			}

			// writes map for reading ship placement
			write.println("5s = Carrier");
			write.println("4s = Battleship");
			write.println("3s = Cruiser");
			write.println("3s = Submarine");
			write.println("2s = Destroyer");
			write.close();

			// inform player of successful save
			JOptionPane.showMessageDialog(null, "Game saved to " + saveFile);

		} catch (Exception e) {

			// inform player of unsuccessful save
			JOptionPane.showMessageDialog(null, "Save Failed");

		}

	}

	/**
	 * writes to file the end game result
	 */
	public void tally() {

		// try catch needed for file IO
		try {

			// declare new file and filewriter to write to file
			File saveFile = new File("Results.txt");
			PrintWriter write = new PrintWriter(saveFile);

			// writing end game information
			write.println("BattleShip Pregame: ");
			write.println("");
			write.println("Computer ship placements: ");

			// writing computer ship placement in grid formation
			for (int i = 0; i < placedShip.length; i++) {
				for (int j = 0; j < placedShip[i].length; j++) {
					write.print(placedShip[i][j] + " ");
				}
				write.println("");
			}

			// writing map for reading ship placement
			write.println("5s = Carrier");
			write.println("4s = Battleship");
			write.println("3s = Cruiser");
			write.println("3s = Submarine");
			write.println("2s = Destroyer");
			write.println("");

			// writing game score information of player including player hits
			// and ship sunk
			write.println("Player:");
			write.println("Hits on target: " + (17 - (IntStream.of(computersShip).sum())));
			write.print("Sunk: ");
			if (computersShip[0] == 0) {
				write.print("Carrier ");
			}
			if (computersShip[1] == 0) {
				write.print("Battleship ");
			}
			if (computersShip[2] == 0) {
				write.print("Cruiser ");
			}
			if (computersShip[3] == 0) {
				write.print("Submarine ");
			}
			if (computersShip[4] == 0) {
				write.print("Destroyer");
			}
			if ((17 - (IntStream.of(computersShip).sum())) < 2) {
				write.print("none");
			}
			write.println("");
			write.println("");

			// writing game score information of computer including player hits
			// and ship sunk
			write.println("Computer:");
			write.println("Hits on taget: " + (17 - (IntStream.of(playersShip).sum())));
			write.print("Sunk: ");
			if (playersShip[0] == 0) {
				write.print("Carrier ");
			} 
			if (playersShip[1] == 0) {
				write.print("Battleship ");
			} 
			if (playersShip[2] == 0) {
				write.print("Cruiser ");
			} 
			if (playersShip[3] == 0) {
				write.print("Submarine ");
			}
			if (playersShip[4] == 0) {
				write.print("Destroyer ");
			}
			if ((17 - (IntStream.of(playersShip).sum())) < 2) {
				write.print("none");
			}
			write.println("");
			write.println("");

			// writes game time to file
			write.print("Total time: ");
			if (timeSec < 10) {
				write.println(String.valueOf(timeMin) + ":0" + String.valueOf(timeSec));
			} else {
				write.println(String.valueOf(timeMin) + ":" + String.valueOf(timeSec));
			}

			write.close();

			// inform player of successful game save
			JOptionPane.showMessageDialog(null, "Game saved to " + saveFile);

		} catch (Exception e) {

			// inform player of unsuccessful game save
			JOptionPane.showMessageDialog(null, "Save Failed");

		}

	}

}
