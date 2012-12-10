package gui;

import java.awt.Dimension;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;

/*
 * The main GUI class.
 */
public class AppGui extends JFrame {

	private static final long serialVersionUID = 5224850506811752504L;
	final int MAX_WIDTH_PX = 1024;
    final int MAX_HEIGHT_PX = 768;
	final int MAIN_TOOLBAR_HEIGHT_PX = 20;

	private AppMenuBar appMenuBar;

	public AppGui() {
		makeAndAddMenuBar();
		setLayout(new MigLayout());
		addGuiComponents();
		setPreferredSize(new Dimension(MAX_WIDTH_PX, MAX_HEIGHT_PX));
		pack();
	}

	private void makeAndAddMenuBar() {
		appMenuBar = new AppMenuBar();
		setJMenuBar(appMenuBar);
	}

	private void addGuiComponents() {
		add(new MainToolbar(MAX_WIDTH_PX, MAIN_TOOLBAR_HEIGHT_PX), "span");
		add(new BoardUI());
		add(new CardUI(), "wrap");
		add(new BoardControlUI());
	}

	/*
	 * Called from event-dispatch thread.
	 */
	private static void init() {
		AppGui gui = new AppGui();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setVisible(true);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				init();
			}
		});
	}
}
