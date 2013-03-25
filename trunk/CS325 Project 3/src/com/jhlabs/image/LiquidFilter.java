/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jhlabs.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Administrator
 */
public class LiquidFilter extends AbstractBufferedImageOp {
    private int lowAlphaCutoff;
    private int highAlphaCutoff;
    private int offset;
    
    public LiquidFilter( int lowAlphaCutoff, int highAlphaCutoff, int offset ) {
        this.lowAlphaCutoff = lowAlphaCutoff;
        this.highAlphaCutoff = highAlphaCutoff;
        this.offset = offset;
    }
    
    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int width = src.getWidth();
        int height = src.getHeight();

        if ( dst == null )
            dst = createCompatibleDestImage( src, null );

        int[] inPixels = new int[width*height];
        int[] outPixels = new int[width*height];
        getRGB( src, 0, 0, width, height, inPixels );

        for( int y = 0; y < height; ++y)
            for( int x = 0; x < width; ++x) {
                int midArgb = inPixels[y*width + x];
                int midA = (midArgb >> 24) & 0xff;
                int outArgb;
                if( midA < lowAlphaCutoff ) {
                    outArgb = 0;
                }
                else {
                    midA += midA > highAlphaCutoff ? (255 - midA)*2/3 : 0;
                    int topLeftArgb = inPixels[Math.max(0,y-offset)*width + Math.max(0,x-offset)];
                    int topLeftA = (topLeftArgb >> 24) & 0xff;
                    topLeftA += topLeftA > highAlphaCutoff ? (255 - topLeftA)*2/3 : 0;
                    int botRightArgb = inPixels[Math.min(height-1,y+offset)*width + Math.min(width-1,x+offset)];
                    int botRightA = (botRightArgb >> 24) & 0xff;
                    botRightA += botRightA > highAlphaCutoff ? (255 - botRightA)*2/3 : 0;
                    
                    int alpha = ImageMath.clamp( (botRightA-topLeftA)*1 + midA - 127, 0, 255);
                    float[] hsl = new float[3];
                    Color.RGBtoHSB((midArgb >> 16) & 0xff, (midArgb >> 8) & 0xff, midArgb & 0xff, hsl);
                    hsl[2] = alpha*(1.f/255.f);
                    //hsl[1] = hsl[1] < 0.05f ? hsl[1] : 1.f - hsl[2]*hsl[2]*hsl[2]*hsl[2];
                    
                    outArgb = Color.HSBtoRGB(hsl[0], hsl[1], hsl[2]);
                }
                
                outPixels[y*width + x] = outArgb;
            }
        
        setRGB( dst, 0, 0, width, height, outPixels );
        return dst;
    }
    
    /**
     * Converts the components of a color, as specified by the HSL
     * model, to an equivalent set of values for the default RGB model.
     * <p>
     * The <code>saturation</code> and <code>lightness</code> components
     * should be floating-point values between zero and one
     * (numbers in the range 0.0-1.0).  The <code>hue</code> component
     * can be any floating-point number.  The floor of this number is
     * subtracted from it to create a fraction between 0 and 1.  This
     * fractional number is then multiplied by 360 to produce the hue
     * angle in the HSL color model.
     * <p>
     * The integer that is returned by <code>HSLtoRGB</code> encodes the
     * value of a color in bits 0-23 of an integer value that is the same
     * format used by the method {@link #getRGB() <code>getRGB</code>}.
     * This integer can be supplied as an argument to the
     * <code>Color</code> constructor that takes a single integer argument.
     * @param     hue   the hue component of the color
     * @param     saturation   the saturation of the color
     * @param     lightness   the lightness of the color
     * @return    the RGB value of the color with the indicated hue,
     *                            saturation, and lightness.
     * @see       java.awt.Color#getRGB()
     * @see       java.awt.Color#Color(int)
     * @see       java.awt.image.ColorModel#getRGBdefault()
     * @since     JDK1.0
     */
    public static int HSLtoRGB(float hue, float saturation, float lightness)
    {

        float v;
        float r, g, b;
        r = lightness;   // default to gray
        g = lightness;
        b = lightness;
        v = (lightness <= 0.5f) ? (lightness * (1.0f + saturation)) : (lightness + saturation - lightness * saturation);
        if (v > 0)
        {
            float m;
            float sv;
            int sextant;
            float fract, vsf, mid1, mid2;

            m = lightness + lightness - v;
            sv = (v - m) / v;
            hue = (hue - (float)Math.floor(hue)) * 6.0f;
            sextant = (int)hue;
            fract = hue - sextant;
            vsf = v * sv * fract;
            mid1 = m + vsf;
            mid2 = v - vsf;
            switch (sextant)
            {
                case 0:
                    r = v;
                    g = mid1;
                    b = m;
                    break;
                case 1:
                    r = mid2;
                    g = v;
                    b = m;
                    break;
                case 2:
                    r = m;
                    g = v;
                    b = mid1;
                    break;
                case 3:
                    r = m;
                    g = mid2;
                    b = v;
                    break;
                case 4:
                    r = mid1;
                    g = m;
                    b = v;
                    break;
                case 5:
                    r = v;
                    g = m;
                    b = mid2;
                    break;
            }
        }

        return 0xff000000 | (int)(r * 255.0f + 0.5f) << 16 | (int)(g * 255.0f + 0.5f) << 8 | (int)(b * 255.0f + 0.5f);
    }

    /**
     * Converts the components of a color, as specified by the default RGB
     * model, to an equivalent set of values for hue, saturation, and
     * lightness that are the three components of the HSL model.
     * <p>
     * If the <code>hslvals</code> argument is <code>null</code>, then a
     * new array is allocated to return the result. Otherwise, the method
     * returns the array <code>hslvals</code>, with the values put into
     * that array.
     * @param     r   the red component of the color
     * @param     g   the green component of the color
     * @param     b   the blue component of the color
     * @param     hslvals  the array used to return the
     *                     three HSB values, or <code>null</code>
     * @return    an array of three elements containing the hue, saturation,
     *                     and lightness (in that order), of the color with
     *                     the indicated red, green, and blue components.
     * @see       java.awt.Color#getRGB()
     * @see       java.awt.Color#Color(int)
     * @see       java.awt.image.ColorModel#getRGBdefault()
     * @since     JDK1.0
     */
    public static float[] RGBtoHSL(int r, int g, int b, float[] hslvals) {
        float hue, saturation, lightness;
        if (hslvals == null) {
            hslvals = new float[3];
        }
        int cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;
        int cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;

        lightness = (float) (cmax + cmin) / 510.0f;
        if (cmax != 0)
            saturation = ((float) (cmax - cmin)) / (1.f - Math.abs(2.f*lightness - 1.f));
        else
            saturation = 0;
        if (saturation == 0)
            hue = 0;
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0f + redc - bluec;
            else
                hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0)
                hue = hue + 1.0f;
        }
        hslvals[0] = hue;
        hslvals[1] = saturation;
        hslvals[2] = lightness;
        return hslvals;
    }
    
}
