package gui;

import javax.swing.JLabel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

//TODO resize and center images, right now images are on the left hand side
//TODO create Resource folder in subversion to store images

public class VerbLabel extends JLabel implements ActionListener{

	//TODO create private variables for the path to the images
	private static final long serialVersionUID = 1L;
	private ImageIcon defaultIcon = createImageIcon("./default.png", "default");
	private ImageIcon moveIcon = createImageIcon("./default.png", "default");
	private ImageIcon lookIcon = createImageIcon("./default.png", "default");
	
	
	public VerbLabel()
	{
		super();
		setIcon(defaultIcon);//TODO instead of setIcon use default constructor
		createContextMenu();
	}
	
	//if no default icon is available display text for the icon.
	public VerbLabel(String text)
	{
		super(text);
		createContextMenu();
	}
	
	public VerbLabel(ImageIcon icon)//has to be ImageIcon because casting to Icon doesn't work
	{
		super(icon);//TODO need to test if this constructor works
		createContextMenu();
	}
	
	
	private void createContextMenu() {
		//TODO grab nouns from another class instead of hard coding them
		
		//setPreferredSize(new Dimension(10, 10));
		setBorder(BorderFactory.createLineBorder(Color.black));
        JMenuItem menuItem;

        //Create the context Menu for the noun
        //register this class
        JPopupMenu popup = new JPopupMenu();
        menuItem = new JMenuItem("move");
        menuItem.addActionListener(this); 
        popup.add(menuItem);
        menuItem = new JMenuItem("look");
        menuItem.addActionListener(this);
        popup.add(menuItem);
 
        //register this object to listen for right clicks
        //remember to override actionPerformed
        RightClickListener rightClickListener = new RightClickListener(popup);
        addMouseListener(rightClickListener);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem)(e.getSource());
		String s = source.getText();
		if(s.equals("move"))
			setIcon(moveIcon);
		else if(s.equals("look"))
			setIcon(lookIcon);
		System.out.println(s);
	}
	
	//copied from http://download.oracle.com/javase/tutorial/uiswing/components/icon.html
	/** Returns an ImageIcon, or null if the path was invalid. */
	public  ImageIcon createImageIcon(String path,
            		String description) {
		URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
	
	//inner class implements Observer pattern
	private class RightClickListener extends MouseAdapter {
        private JPopupMenu popup;
 
        RightClickListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }
 
        public void mousePressed(MouseEvent e) {
        	showContextMenu(e);
        }
 
        public void mouseReleased(MouseEvent e) {
        	showContextMenu(e);
        }
 
        private void showContextMenu(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                           e.getX(), e.getY());//show  popup at that location
            }
        }
	}
}
