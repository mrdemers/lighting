package lighting;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import test.Light;

public class Game {
	public static final int WIDTH = 1280, HEIGHT = 720;
	public static final int SCALE = 1;
	public static boolean useSound = true;
	ArrayList<Light> lights = new ArrayList<Light>();
	ArrayList<Polygon> shadows = new ArrayList<Polygon>();
	ArrayList<Polygon> hulls = new ArrayList<Polygon>();
	ArrayList<Integer> shadowBrightness = new ArrayList<Integer>();
	int[][] alphaBuffer = new int[Game.WIDTH][Game.HEIGHT];
	
	public Game() {
		for (int y = 0; y < Game.HEIGHT; y++) {
			for (int x = 0; x < Game.WIDTH; x++) {
				alphaBuffer[x][y] = 0;
			}
		}
		hulls.add(new Polygon(new int[]{100, 80, 80, 100, 120, 120}, new int[]{100, 120, 140, 160, 140, 120}, 6));
		hulls.add(new Polygon(new int[]{300, 400, 400, 300}, new int[]{300, 300, 260, 260}, 4));
		lights.add(new Light(0,0,800,800));
		lights.add(new Light(50,100,500,500));
	}
	
	int xPoint = 0;
	int yPoint = 0;
	public void update() {
		int xp = Input.getMouseX();
		int yp = Input.getMouseY();
		shadows.clear();
		shadowBrightness.clear();
		for (int i = 0; i < alphaBuffer.length; i++) {
			Arrays.fill(alphaBuffer[i], 0);
		}
		xPoint = xp;
		yPoint = yp;
		lights.get(0).setLocation(xPoint, yPoint);
		
		for (Light l : lights) {
			createLightSource(l);
			createShadowGeometry(l);
		}
	}
	
	int radius;
	public void render(Graphics2D g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		g.setColor(Color.red);
		for (Polygon p : hulls) {
			g.fillPolygon(p);
		}
		for (int i = 0; i < shadows.size(); i++) {
			Polygon p = shadows.get(i);
			g.setColor(new Color(0, 0, 0, shadowBrightness.get(i)));
			g.fillPolygon(p);
		}
		g.drawImage(getLightMap(), 0, 0, null);
	}
	
	public void createShadowGeometry(Light l) {
		ArrayList<Point> newPoints = new ArrayList<Point>();
		for (Polygon p : hulls) {
			int xAvg = 0;
			int yAvg = 0;
			for (int i = 0; i < p.npoints; i++) {
				xAvg += p.xpoints[i];
				yAvg += p.ypoints[i];
			}
			xAvg /= p.npoints;
			yAvg /= p.npoints;
			int xDist = l.x - xAvg;
			int yDist = l.y - yAvg;
			double dist = Math.sqrt(xDist * xDist + yDist * yDist);
			if (dist > l.radius/2) continue;
			Point p1 = new Point(p.xpoints[0], p.ypoints[0]);
			boolean[] faces = new boolean[p.npoints];
			for (int i = 0; i < p.npoints; i++) {
				int point = (i+1)%p.npoints;
				Point p2 = new Point(p.xpoints[point], p.ypoints[point]);
				int nx = p2.y - p1.y;
				int ny = p1.x - p2.x;
				int vx = p1.x - l.x;
				int vy = p1.y - l.y;
				int dot = nx * vx + ny * vy;
				if (dot > 0) faces[i] = true;	
				p1 = p2;
			}
			int curr = 0;
			boolean wasFront = faces[0];
			boolean front = faces[0];
			int pointsIntersected = 0;
			int scale = 2000;
			while (newPoints.isEmpty()) {
				int current = (++curr)%p.npoints;
				wasFront = front;
				front = faces[current];
				if (curr > 100) break;
				if (!front && wasFront) {
					newPoints.add(new Point(p.xpoints[current], p.ypoints[current]));
					pointsIntersected++;
					while (!front) {
						int vx = p.xpoints[current] - l.x;
						int vy = p.ypoints[current] - l.y;
						float dir = (float)Math.atan2(vy, vx);
						newPoints.add(new Point(p.xpoints[current] + (int)(Math.cos(dir)*scale), p.ypoints[current] + (int)(Math.sin(dir)*scale)));
						pointsIntersected++;
						current = (++curr)%p.npoints;
						front = faces[current];
					}
					int vx = p.xpoints[current] - l.x;
					int vy = p.ypoints[current] - l.y;
					float dir = (float)Math.atan2(vy, vx);
					newPoints.add((new Point(p.xpoints[current] + (int)(Math.cos(dir)*scale), p.ypoints[current] + (int)(Math.sin(dir)*scale))));
					pointsIntersected++;
					for (int i = pointsIntersected; i > 0; i--) {
						int corner = current;
						if (corner < 0) corner += p.npoints;
						newPoints.add(new Point(p.xpoints[corner], p.ypoints[corner]));
						current--;
					}
				}
			}
			
			int size = newPoints.size();
			int[] xPoints = new int[size];
			int[] yPoints = new int[size];
			for (int i = 0; i < size; i++) {
				xPoints[i] = newPoints.get(i).x;
				yPoints[i] = newPoints.get(i).y;
			}
			shadows.add(new Polygon(xPoints, yPoints, size));
			int brightness = 255 - (int)(dist/(l.radius/2)*255);
			shadowBrightness.add(brightness);
			newPoints.clear();
		}
	}
	
	public void createLightSource(Light l) {
		int range = l.radius;
		int brightness = l.brightness;
		int x0 = l.x;
		int y0 = l.y;
		for (int y = 0; y < range/2; y++) {
			for (int x = 0; x < range/2; x++) {
				int xd = range/2 - x;
				int yd = range/2 - y;
				double dist = Math.sqrt(xd * xd + yd * yd);
				int a = (int)(brightness * ((range/2 - dist)/range/2));
				if (a > 255) a = 255; 
				if (a < 0) a = 0;
				int x1 = 0, y1 = 0;
				for (int i = 0; i < 4; i++) {
					if (i == 0) {
						x1 = x0-range/2 + x;
						y1 = y0-range/2+y;
					}
					if (i == 1) {
						x1 = x0+range/2-x-1;
						y1 = y0-range/2 + y;
					} else if (i == 2) {
						x1 = x0-range/2+x;
						y1 = y0+range/2-y-1;
					} else if (i == 3) {
						x1 = x0+range/2-x-1;
						y1 = y0+range/2-y-1;
					}
					if (!(x1 < 0 || x1 >= Game.WIDTH || y1 < 0 || y1 >= Game.HEIGHT)) {
						int aa = alphaBuffer[x1][y1]>>24&0xff;
						if (aa < 0) aa = 0;
						if (aa > 255) aa = 255;
						int newA = a + aa;
						if (newA > 255) newA = 255;
						if (newA < 0) newA = 0;
						alphaBuffer[x1][y1] = newA<<24|alphaBuffer[x1][y1]&0xffffff;
					}
				}
			}
		}
	}
	
	public BufferedImage getLightMap() {
		BufferedImage img = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < Game.WIDTH; x++) {
			for (int y = 0; y < Game.HEIGHT; y++) {			
				int col = (255-(alphaBuffer[x][y]>>24))<<24 | 0x000000;
				/* Colored Lighting, may implement later
				int rgb = alphaBuffer[x][y];
				int r = (rgb>>16)&0xff;
				int g = (rgb>>8)&0xff;
				int b = rgb&0xff;
				int a = (rgb>>24)&0xff;
				double percent = a/255.0;
				int r1 = (int)(r * percent);
				int g1 = (int)(g * percent);
				int b1 = (int)(b * percent);
				int col = (255-a) << 24 | r1 << 16 | g1 << 8 | b1;
				*/
				img.setRGB(x, y, col);
			}
		}
		return img;
	}
}
