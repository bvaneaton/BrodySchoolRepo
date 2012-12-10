package gui;

import javax.swing.*;
import java.awt.*;

public class MainToolbar extends JToolBar {

	private static final long serialVersionUID = 1258052911638558877L;

	public MainToolbar(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.RED);

        addButtons();
    }

    private void addButtons() {
        JLabel mapName = new JLabel("Map 1");
        JButton b1 = new JButton("New");
        JButton b2 = new JButton("Open");
        b2.setEnabled(false);
        JButton b3 = new JButton("Save");
        b3.setEnabled(false);
        b1.setSize(40, 40);
        b2.setSize(40, 40);
        b3.setSize(40, 40);
        add(mapName);
    }

}
