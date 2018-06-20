package view;
import mdlaf.*;
import mdlaf.animation.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;

import cookxml.cookswing.CookSwing;
public class ListFrame {
	
	public static void main (String[] args) {
		try {
			UIManager.setLookAndFeel (new MaterialLookAndFeel ());
		}
		catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace ();
		}
		CookSwing cookSwing = new CookSwing ();
		cookSwing.render("src"+File.separator+"view"+File.separator+"layout"+File.separator+"listframe.xml").setVisible(true);
		JButton button =  (JButton) cookSwing.getId("the_button").object;

		// on hover, button will change to a light blue
		MaterialUIMovement.add (button, new Color (255, 255,255));
	}
}
