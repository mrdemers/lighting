package lighting;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, FocusListener {
	public static boolean[] keys = new boolean[65526];
	public static boolean[] mouse = new boolean[10];
	public static int mousex, mousey;
	@Override
	public void focusGained(FocusEvent e) {
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		for (int i = 0; i < keys.length; i++) {
			keys[i] = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouse[e.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouse[e.getButton()] = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
}
