package lighting;

import java.util.Arrays;

public class Bitmap {
	public int[] pixels;
	public int w, h;
	
	public Bitmap(int w, int h) {
		this.w = w;
		this.h = h;
		pixels = new int[w*h];
	}
	
	public void draw(Bitmap b, int x, int y) {
		int x0 = x;
		int y0 = y;
		int x1 = x + b.w;
		int y1 = y + b.h;
		if (x0 < 0) x0 = 0;
		if (x1 > w) x1 = w;
		if (y0 < 0) y0 = 0;
		if (y1 > h) y1 = h;
		for (int yy = y0; yy < y1; yy++) {
			for (int xx = x0; xx < x1; xx++) {
				int col = b.pixels[(xx-x0)+(yy-y0)*b.w];
				pixels[xx+yy*w] = col;
			}
		}
	}
	
	public void blendDraw(Bitmap b, int xo, int yo) {
		int x0 = xo;
		int y0 = yo;
		int x1 = xo + b.w;
		int y1 = yo + b.h;
		if (x0 < 0) x0 = 0;
		if (y0 < 0) y0 = 0;
		if (x1 > w) x1 = w;
		if (y1 > h) y1 = h;
		for (int y = y0; y < y1; y++) {
			int yp = y - yo;
			if (yp < 0 || yp >= b.h) continue; 
			for (int x = x0; x < x1; x++) {
				int xp = x - xo;
				if (xp < 0 || xp >= b.w) continue;
				int col1 = b.pixels[xp + yp * b.w];
				double a1 = ((col1 >> 24)&0xff)/255.0;
				if (a1 == 0) continue;
				double r1 = ((col1 >> 16)&0xff)/255.0;
				double g1 = ((col1 >> 8)&0xff)/255.0;
				double b1 = (col1&0xff)/255.0;
				int col2 = pixels[x + y * w];
				double r2 = ((col2 >> 16)&0xff)/255.0;
				double g2 = ((col2 >> 8)&0xff)/255.0;
				double b2 = (col2&0xff)/255.0;
				
				double r3 = r1 * a1 + r2 * (1-a1);
				double g3 = g1 * a1 + g2 * (1-a1);
				double b3 = b1 * a1 + b2 * (1-a1);
				int col = (255<<24|(int)(r3*255)<<16|(int)(g3*255)<<8|(int)(b3*255));
				pixels[x + y * w] = col;
			}
		}
	}
	
	public void createGradient(int col1, int col2) {
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int radius = h / 2;
				int yd = y-radius;
				int xd = x-radius;
				double distance = Math.sqrt(xd*xd+yd*yd);
				double dist = distance/radius;
				int a1 = col1>>24; a1 &= 0xff;
				int r1 = col1>>16; r1 &= 0xff;
				int g1 = col1>>8; g1 &= 0xff;
				int b1 = col1; b1 &= 0xff;
				
				int a2 = col2>>24; a2 &= 0xff;
				int r2 = col2>>16; r2 &= 0xff;
				int g2 = col2>>8; g2 &= 0xff;
				int b2 = col2; b2 &= 0xff;
				
				int a = (int)(a1 * (1-dist) + a2 * dist);
				int r = (int)(r1 * (1-dist) + r2 * dist);
				int g = (int)(g1 * (1-dist) + g2 * dist);
				int b = (int)(b1 * (1-dist) + b2 * dist);
				if (a > 255) a = 255;
				if (a < 0) a = 0;
				if (r > 255) r = 255;
				if (r < 0) r = 0;
				if (g > 255) g = 255;
				if (g < 0) g = 0;
				if (b > 255) b = 255;
				if (b < 0) b = 0;
				
				pixels[x + y * w] = a<<24|r<<16|g<<8|b;
			}
		}
	}
	
	public void fill(int col) {
		Arrays.fill(pixels, col);
	}
}
