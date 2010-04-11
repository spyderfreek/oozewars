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

import javax.swing.ImageIcon;

public class Backdrop implements Sprite 
{
	private BufferedImage image;
	private int width, height;
	
	public Backdrop( String path )
	{
		try
		{
			Image tempImg = new ImageIcon(path).getImage();
			
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
		//graphics.drawRenderedImage(image, null);
	}

}
