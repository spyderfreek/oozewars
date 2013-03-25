package oozeWars;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.Iterator;

import com.jhlabs.image.AlphaThresholdFilter;
import com.jhlabs.image.FadeFilter;
import com.jhlabs.image.LiquidFilter;

/**
 * <center><b>OOZEVIEW.JAVA</b></center>
 * This extends <code>View.java</code> and is used to customize how things are drawn to the
 * screen.  OozeView also holds a reference to the menu screens.
 * 
 * <p>
 * @author Nick Kitten<br />Sean Fedak
 */
@SuppressWarnings("serial")
public class OozeView extends View 
{
	public final double SCALE;
	private MenuScreen menu;
	private BufferedImage smallFront, smallBack, smallAccum;
	private AffineTransform transform;
	private BufferedImageOp fader;
	private BufferedImageOp threshold;
        private LiquidFilter liquid;
	
	public OozeView(Game game, int layers, int preferredWidth,
			int preferredHeight, double scale) 
	{
		super(game, layers, preferredWidth, preferredHeight);
		SCALE = scale;
		int realWidth = (int)(preferredWidth * SCALE);
		int realHeight = (int)(preferredHeight * SCALE);
		smallFront = new BufferedImage( realWidth + 20, realHeight + 20, BufferedImage.TYPE_INT_ARGB);
		smallBack = new BufferedImage( realWidth + 20, realHeight + 20, BufferedImage.TYPE_INT_ARGB);
                smallAccum = new BufferedImage( realWidth + 20, realHeight + 20, BufferedImage.TYPE_INT_ARGB);
		fader = new FadeFilter( 0.7f );
		threshold = new AlphaThresholdFilter( (int)(255*0.3) );
		transform = AffineTransform.getScaleInstance((double)preferredWidth / realWidth, (double)preferredHeight / realHeight);
                liquid = new LiquidFilter((int)(255*0.4),(int)(255*0.4),2);
	}

	/** Paints the View by drawing all the sprites in order.  Painting is done antialiased. */
	public void paintComponent(Graphics g)
	{
		if( ! game.repaintFlag.testAndTurnOff() )
			return;
		
		Graphics2D graphics = (Graphics2D) g;
		
		fader.filter(smallAccum, smallBack);
                
		
		Graphics2D small = smallBack.createGraphics();
		small.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		
		Iterator<Sprite> iterator = sprites[0].iterator();
		while(iterator.hasNext())
		{
			Sprite sprite = iterator.next();
			sprite.draw(graphics, game);
		}
		
		iterator = sprites[1].iterator();
		while(iterator.hasNext())
		{
			Sprite sprite = (Sprite)(iterator.next());
			sprite.draw(small, game);
		}
		small.dispose();
                
                threshold.filter(smallBack, smallAccum);
		
                liquid.filter(smallBack, smallFront);
		//threshold.filter(smallBack, smallFront);
		graphics.drawRenderedImage(smallFront, transform);
		
		//BufferedImage temp = smallBack;
		//smallBack = smallFront;
		//smallFront = temp;
		
		
		for(int i = 2; i < sprites.length; i++)
		{
			iterator = sprites[i].iterator();
			while(iterator.hasNext())
			{
				Sprite sprite = (Sprite)(iterator.next());
				sprite.draw(graphics, game);
			}
		}
		
		graphics.dispose();
		
	}
	
	public void setMenu(MenuScreen menu)
	{
		this.menu = menu;
	}
	
	public void swapToMainMenu()
	{
		Container container = getParent();
		container.remove(this);
		menu.switchToMain();
		container.add(menu, BorderLayout.CENTER);
		menu.requestFocus();
		menu.setVisible(true);
		container.validate();
		menu.updateUI();
		menu.repaint();
	}
	
	public void swapToPauseMenu()
	{
		Container container = getParent();
		container.remove(this);
		menu.switchToPause();
		container.add(menu, BorderLayout.CENTER);
		menu.requestFocus();
		menu.setVisible(true);
		container.validate();
		menu.updateUI();
		menu.repaint();
	}
}
