package lighting;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class LightingTest extends Canvas implements Runnable{
	private static final long serialVersionUID = 1L;
	public static final String title = "A thing";
	private static boolean running;
	private Thread thread;
	private Game game;
	private VolatileImage image;
	
	public LightingTest() {
		setSize(new Dimension(Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE));
		game = new Game();
		addKeyListener(Input.getInput());
		addMouseListener(Input.getInput());
		addMouseMotionListener(Input.getInput());
		addFocusListener(Input.getInput());
	}

	private void update() {
		game.update();
		Input.update();
	}
	
	private void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(2);
			return;
		}
		if (image == null) {
			image = createVolatileImage(Game.WIDTH, Game.HEIGHT);
		}
		Graphics2D g2D = image.createGraphics();
		g2D.setColor(Color.black);
		g2D.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		game.render(g2D);
		
		Graphics g = bs.getDrawGraphics();
		g.fillRect(0, 0, getWidth(), getHeight());	
		g.drawImage(image, 0, 0, Game.WIDTH, Game.HEIGHT, null);
		g.dispose();
		bs.show();
	}
	
	public synchronized void start() {
		if (running) return;
		thread = new Thread(this);
		running = true;
		thread.start();
	}
	
	public synchronized void stop() {
		if (!running) return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		long last = System.nanoTime();
		double unprocessedSeconds = 0;
		double secondsPerTick = 1 / 60.0;
		int updates = 0, frames = 0;
		while(running) {
			long now = System.nanoTime();
			long passed = now - last;
			last = now;
			if (passed < 0) passed = 0;
			if (passed > 100000000) passed = 100000000;
			unprocessedSeconds += passed / 1000000000.0;
			boolean updated = false;
			while (unprocessedSeconds > secondsPerTick) {
				update();
				unprocessedSeconds -= secondsPerTick;
				updated = true;
				updates++;
			}
			
			if (updated) {
				if (updates >= 60) {
					System.out.println("FPS: " + frames);
					updates -= 60;
					frames = 0;
				}
			}
			
			if (updated) {
				render();
				frames++;
			}
		}
	}
	
	public static void main(String[] args) {
		JPanel panel = new JPanel(new BorderLayout());
		LightingTest game = new LightingTest();
		panel.add(game);
		JFrame frame = new JFrame(title);
		frame.add(panel);
		int border = 28;
		frame.setSize(Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE+border);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.requestFocus();
		game.start();
	}
}
