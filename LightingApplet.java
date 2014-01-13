package lighting;

import java.applet.Applet;
import java.awt.BorderLayout;

public class LightingApplet extends Applet{
	private static final long serialVersionUID = 1L;
	public LightingTest lt = new LightingTest();
	
	public void init() {
		setLayout(new BorderLayout());
		add(lt, BorderLayout.CENTER);
	}
	
	public void start() {
		lt.start();
	}
	
	public void stop() {
		lt.stop();
	}
}
