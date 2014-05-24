package GUI;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class ImageComponent extends JComponent{
	BufferedImage image;
	public ImageComponent(BufferedImage image){
		this.image = image;
	}
	
	public void paintComponent(Graphics g){
		g.drawImage(image,0,0,null);
	}
}