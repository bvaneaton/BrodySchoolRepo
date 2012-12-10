package gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class BoardControlUI extends JPanel {
	
	private static final long serialVersionUID = 8901327982392637413L;

	public BoardControlUI() {
        // Set layout manager
        super(new MigLayout());

        JButton startButton = new JButton("Start");
        JButton resetButton = new JButton("Reset");
        JButton stepButton = new JButton("Step");
        JSlider stepSpeedSlider = new JSlider();
        JLabel stepSliderLabel = new JLabel("Speed");

        add(startButton);
        add(stepButton);
        add(stepSpeedSlider);
        add(resetButton);

        setPreferredSize(new Dimension(400, 50));
        setDoubleBuffered(true);
        setBackground(Color.white);
    }

}
