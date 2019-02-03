package game;

import java.util.*;

public class PlaceShip {

	int[][] placedShip = new int[10][10];
	ArrayList<Integer> ships = new ArrayList<Integer>();

	public Boolean possible = true;

	public PlaceShip(int diff) {

		for (int i = 0; i < placedShip.length; i++) {
			for (int j = 0; j < placedShip[i].length; j++) {
				placedShip[i][j] = 0;
			}
		}

		ships.add(5);
		ships.add(4);
		ships.add(3);
		ships.add(3);
		ships.add(2);

		if (diff == 0) {

			int totalShip = 0;

			do {

				int x, y, shipType, orientationX, orientationY, xOrY;
				orientationX = 0;
				orientationY = 0;
				possible = true;

				do {
					xOrY = (int) (Math.random() * 2);
					System.out.println("xOrY: " + xOrY);
					x = (int) (Math.random() * 10 + 1);
					System.out.println("x: " + x);
					y = (int) (Math.random() * 10 + 1);
					System.out.println("y: " + y);
					shipType = (int) (Math.random() * 4 + 2);
					if (!ships.contains(shipType)) {
						possible = false;
						break;
					}
					System.out.println("shipType: " + shipType);
					if (xOrY == 0) {
						do {
							orientationX = (int) (Math.random() * 3 - 1);
						} while (orientationX == 0);
						if ((x + orientationX * shipType < 0) || (x + orientationX * shipType) > 9) {
							possible = false;
							break;
						}
						for (int i = 0; i < shipType; i++) {
							if (placedShip[x + orientationX * i][y] != 0) {
								possible = false;
								break;
							}
						}
						System.out.println("orientationX: " + orientationX);
					} else {
						do {
							orientationY = (int) (Math.random() * 3 - 1);
						} while (orientationY == 0);
						if ((y + orientationY * shipType) < 0 || (y + orientationY * shipType) > 9) {
							possible = false;
							break;
						}
						for (int i = 0; i < shipType; i++) {
							if (placedShip[x][y + orientationY * i] != 0) {
								possible = false;
								break;
							}
						}
						System.out.println("orientationY: " + orientationY);
					}
				} while (possible = false);
				
				Integer shipTypeTemp = shipType;
				ships.remove(shipTypeTemp);
				
				if (xOrY == 0) {
					for (int i = 0; i < shipType; i++) {
						placedShip[x + i * orientationX][y] = shipType;
						totalShip += shipType;
					}
				} else {
					for (int i = 0; i < shipType; i++) {
						placedShip[x][y + i * orientationY] = shipType;
						totalShip += shipType;
					}
				}

			} while (totalShip != 63);

		} else {

		}

	}
	
	public int[][] getPlacedShip () {
		return placedShip;
	}

}
