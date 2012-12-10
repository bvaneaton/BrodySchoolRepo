package gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class BoardUI extends JPanel {
	
	private static final long serialVersionUID = -8572463628932554021L;
	private final String ICON_DIR = "../img/";
	private final String TREE_ICON_FILE = "tree.png";
	private final int WIDTH_PX = 400;
	private final int HEIGHT_PX = 400;
	final int XSIZE = 10;
	final int YSIZE = 10;
	JLabel[][] grid;

	// Override paint() if using pure AWT like Applet or Canvas
	public void paintComponent(Graphics g) {
		// Paint background first
		super.paintComponent(g);
	}

	public BoardUI() {
		// Set layout manager, with 1 pixel gaps.
		super(new MigLayout("gap 1 1"));

		grid = new JLabel[XSIZE][YSIZE];
		ImageIcon treeIcon = new ImageIcon(getClass().getResource("../img/tree.png"));
		
		for (int i = 0; i < XSIZE; i++) {
			for (int j = 0; j < YSIZE; j++) {
				JLabel treeLabel = new JLabel();
				treeLabel.setBorder(BorderFactory.createLineBorder(Color.black));
				treeLabel.setIcon(treeIcon);
				grid[i][j] = treeLabel;
			}
		}

		setPreferredSize(new Dimension(WIDTH_PX, HEIGHT_PX));
		setDoubleBuffered(true);
		setBackground(Color.white);
		placeTiles();
	}

	public void setTile(ImageIcon icon, int x, int y) {
		assert (x >= 0 && x < XSIZE && y >= 0 && y < YSIZE);
		grid[x][y].setIcon(icon);
	}

	private void placeTiles() {
		for (int col = 0; col < XSIZE; col++) {
			for (int row = 0; row < YSIZE - 1; row++) {
				add(grid[col][row]);
			}
			
			add(grid[col][YSIZE - 1], "wrap");
		}
	}
}
