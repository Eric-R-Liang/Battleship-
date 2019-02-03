package game;

import java.util.Random;

class Placeships {
	int difficulty;
	final int ROW = 10;
	final int COL = 10;
	protected int[][] placedShip = new int[ROW][COL];
	int side; // used only for advancaed difficulty to determine which side of
				// the grid does the AI place the ships on
	int direction;
	int x;
	int y;

	public Placeships(int diff) {
		for (int i = 0; i < placedShip.length; i++) {
			for (int j = 0; j < placedShip[i].length; j++) {
				placedShip[i][j] = 0;
			}
		}
		difficulty = diff;
		ShipPlacing(difficulty);
	}

	public void ShipPlacing(int newDifficulty) {
		int size = 2;// initialize the size of the ship to be 2
		if (newDifficulty == 0) {
			while (size != 6) { // while loop used to place ships from size 2 to
								// size 5
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
			int newx = 0;
			int newy = 0;
			randomize();
			if (side == 0) {
				newx = 0;
				newy = 2;
				for (int q = 0; q < 5; q++) {
					if (q == 4) {
						size = 3;
					}
					place(newx, newy, 2, size);
					newy++;
					size++;
				}
			} else if (side == 1) {
				newx = 2;
				newy = 9;
				for (int q = 0; q < 5; q++) {
					if (q == 4) {
						size = 3;
					}
					place(newx, newy, 3, size);
					newx++;
					size++;
				}
			} else if (side == 2) {
				newx = 9;
				newy = 2;
				for (int q = 0; q < 5; q++) {
					if (q == 4) {
						size = 3;
					}
					place(newx, newy, 0, size);
					newy++;
					size++;
				}
			} else if (side == 3) {
				newx = 2;
				newy = 0;
				for (int q = 0; q < 5; q++) {
					if (q == 4) {
						size = 3;
					}
					place(newx, newy, 1, size);
					newx++;
					size++;
				}
			}
		}

	}

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

	public void randomize() {
		Random rand = new Random();
		side = (int) (rand.nextDouble() * 4); // side: 0---Top ; 1----Right ;
												// 2----Bottom ; 3----Left
		direction = (int) (rand.nextDouble() * 4); // direction: 0---Up ;
													// 1----Right ; 2----Down ;
													// 3----Left
		x = (int) (rand.nextDouble() * ROW); // randomize the coordinates of the
												// ship
		y = (int) (rand.nextDouble() * COL);
	}

	protected int[][] getPlacedShip(int difficult) {
		Placeships myShip = new Placeships(difficult);
		return placedShip;
	}
	
}
