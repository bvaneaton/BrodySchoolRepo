package gui;

import javax.swing.*;

/*
 * The application window's main menubar.
 */
public class AppMenuBar extends JMenuBar {

	private static final long serialVersionUID = 1167677002995374689L;

	public AppMenuBar() {
        add(makeFileMenu());
        add(makeHelpMenu());
    }

    private JMenu makeFileMenu() {
        JMenu menu = new JMenu("File");

        JMenuItem menuItemNew = new JMenuItem("New");
        JMenuItem menuItemOpen = new JMenuItem("Open");
        menu.add(menuItemNew);
        menu.add(menuItemOpen);
        return menu;
    }

    private JMenu makeHelpMenu() {
        JMenu menu = new JMenu("Help");
        JMenuItem menuItem = new JMenuItem("About");
        menu.add(menuItem);
        return menu;
    }
}
