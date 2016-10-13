import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JFrame;

public class MyMouseAdapter extends MouseAdapter {
	private Random generator = new Random();
	private Mines myMines;
	
	public MyMouseAdapter(Mines mines) {//This is the constructor... this code runs first to initialize
		myMines = mines; //Use the Mines object to obtain the position of the mines.
	}
	
	
	private MyPanel getGridComponents(MouseEvent e) {	
		Component c = e.getComponent();
		while (!(c instanceof JFrame)) {
			c = c.getParent();
			if (c == null) {
				return null;
			}
		}
		JFrame myFrame = (JFrame) c;
		MyPanel myPanel = (MyPanel) myFrame.getContentPane().getComponent(0);
		Insets myInsets = myFrame.getInsets();
		int x1 = myInsets.left;
		int y1 = myInsets.top;
		e.translatePoint(-x1, -y1);
		int x = e.getX();
		int y = e.getY();
		myPanel.x = x;
		myPanel.y = y;
		
		return myPanel;
	}
	
	public void mousePressedPos(MyPanel myPanel, MouseEvent e) {
		//Returns the grid's x and y indices of the position of the mouse
		//The return type is an integer array.
		myPanel.mouseDownGridX = myPanel.getGridX(e.getX(), e.getY());
		myPanel.mouseDownGridY = myPanel.getGridY(e.getX(), e.getY());

	}
	
	public void mousePressed(MouseEvent e) {
		
		MyPanel myPanel = getGridComponents(e); //Obtain all the frame and grid components
		if (myPanel == null) {return;}
		switch (e.getButton()) {
		case 1:		//Left mouse button
			
			mousePressedPos(myPanel, e);
			myPanel.repaint();
			break;
			
		case 3:		//Right mouse button
			mousePressedPos(myPanel, e);
			myPanel.repaint();
			break;
			
		default:    //Some other button (2 = Middle mouse button, etc.)
			//Do nothing
			break;
		}
	}
	
	public boolean isInsideGrid(MyPanel myPanel, int gridX, int gridY) {
		
		if ((myPanel.mouseDownGridX != -1) && (myPanel.mouseDownGridY != -1)) {
			//Had not pressed outside
			if ((gridX != -1) && (gridY != -1)) {
				//Is not releasing outside
				if ((myPanel.mouseDownGridX == gridX) && (myPanel.mouseDownGridY == gridY)) {
					//Released the mouse button on the same cell where it was pressed
					return true;
				} 
			}
		} 
		return false;
	}
	
	public void mouseReleased(MouseEvent e) {
		
		MyPanel myPanel = getGridComponents(e); //Obtain all the frame and grid components
		if (myPanel == null) {return;}
		int gridX = myPanel.getGridX(e.getX(), e.getY());
		int gridY = myPanel.getGridY(e.getX(), e.getY());
		OrderedPair mouseReleasePos = new OrderedPair(gridX, gridY);
		
		switch (e.getButton()) {
		case 1:		//Left mouse button	
			
			if (isInsideGrid(myPanel, gridX, gridY)) { //If released on the same cell as it was pressed...
				
				Color oldColor = myPanel.colorArray[myPanel.mouseDownGridX][myPanel.mouseDownGridY];
				
				if (oldColor != Color.RED) { //The cell does not have a flag
					
					if (!myMines.isMine(mouseReleasePos)) {
						myPanel.colorArray[myPanel.mouseDownGridX][myPanel.mouseDownGridY] = Color.GRAY;
						myPanel.numberArray[myPanel.mouseDownGridX][myPanel.mouseDownGridY] = myMines.findMinesAround(mouseReleasePos);
						myPanel.repaint();
					} else { //The cell is a mine
						myPanel.colorArray[myPanel.mouseDownGridX][myPanel.mouseDownGridY] = Color.BLACK;
						for (OrderedPair p : myMines.getMinePositions()) {
							myPanel.colorArray[p.x][p.y] = Color.BLACK;
						}

						myPanel.repaint();
					}
					
				}
			}
			break;
			
		case 3:		//Right mouse button

			if (isInsideGrid(myPanel, gridX, gridY)) { //If released on the same cell as it was pressed...
				//Set or remove the RED FLAG
				Color oldColor = myPanel.colorArray[myPanel.mouseDownGridX][myPanel.mouseDownGridY];
				if (oldColor == Color.WHITE) {
					myPanel.colorArray[myPanel.mouseDownGridX][myPanel.mouseDownGridY] = Color.RED;
				} else if (oldColor == Color.RED) {
					myPanel.colorArray[myPanel.mouseDownGridX][myPanel.mouseDownGridY] = Color.WHITE;
				}	
				myPanel.repaint();
			}
			break;
			
		default:    //Some other button (2 = Middle mouse button, etc.)
			//Do nothing
			break;
		}
	}
}