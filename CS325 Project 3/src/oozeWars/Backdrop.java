package oozeWars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;

/**
 * The Class that contains a background image for OozeView.
 * 
 * <p>To set this up, one merely has to input the name of the image including its extenstion.  
 * For instance, if there is an image named thisImage and it is a jpeg image, then the correct
 * format to input for the constructor is as follows:<br />
 * <code>Backdrop thisBackdrop = new Backdrop("thisImage.jpg");</code>
 * 
 * <p><center><b><u>Important Note</b></u></center>
 * Absolute path names are not supported as the Constructor uses getResource(path) as the
 * method for retrieving the image.  This also means that the image must be in the same
 * folder that contains the .class file of Backdrop.java.
 * 
 * @author Nick Kitten <br /> Sean Fedak
 *
 */

public class Backdrop implements Sprite 
{
	//The image that will be used for this Backdrop.
	private BufferedImage image;
	private int width, height;
	
	public Backdrop( String path )
	{
		try
		{
			try 
			{
				System.out.println(new File("").getCanonicalPath());
			} 
			catch (IOException e) {e.printStackTrace();}
			
			Image tempImg = new ImageIcon(getClass().getResource(path)).getImage();
			
			ImageObserver observer = new ImageObserver()
			{
				@Override
				public boolean imageUpdate(Image img, int info, int x,
						int y, int w, int h) 
				{
					if( ( info & ImageObserver.ERROR ) != 0 )
						System.out.println("Image Loading Error");
					return ( info & ImageObserver.ALLBITS ) != 0;
				}
			};
			
			// need to wait for image to load
			while( tempImg.getWidth(observer) == -1);
			
			GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice device = env.getDefaultScreenDevice();
			GraphicsConfiguration config = device.getDefaultConfiguration();
			image = config.createCompatibleImage(tempImg.getWidth(observer), tempImg.getHeight(observer));
			
			Graphics2D g = image.createGraphics();
			g.drawImage(tempImg, 0, 0, observer);
			g.dispose();
			
			System.out.println("Background Loaded");
		}
		catch(SecurityException e)
		{
			System.out.println("Image load failed");
		}
	}
	
	@Override
	public void draw(Graphics2D graphics, Game game) 
	{
		OozeWars g = (OozeWars) game;
		graphics.drawImage(image, 0, 0, g.getWidth(), g.getHeight(), Color.MAGENTA, null);
		//graphics.drawRenderedImage(image, AffineTransform.getTranslateInstance(0, 0));
	}

}
