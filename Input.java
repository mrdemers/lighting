package lighting;


public class Input {
	private static InputHandler input = new InputHandler();
	public static boolean[] oldKeys = new boolean[65526];
	public static boolean[] oldMouse = new boolean[10];
	
	public static void update() {
		for (int i = 0; i < InputHandler.keys.length; i++) {
			oldKeys[i] = InputHandler.keys[i];
		}
		for (int i = 0; i < oldMouse.length; i++) {
			oldMouse[i] = InputHandler.mouse[i];
		}
	}
	

	public static boolean isKeyDown(int code) {
		return InputHandler.keys[code];
	}
	
	public static boolean isKeyClicked(int code) {
		return InputHandler.keys[code] && !oldKeys[code];
	}
	
	public static boolean isKeyReleased(int code) {
		return !InputHandler.keys[code] && oldKeys[code];
	}
	
	public static int getMouseX() {
		return InputHandler.mousex/Game.SCALE;
	}
	
	public static int getMouseY() {
		return InputHandler.mousey/Game.SCALE;
	}
	
	public static boolean isMouseDown(int button) {
		return InputHandler.mouse[button];
	}
	
	public static boolean isMouseClicked(int button) {
		return InputHandler.mouse[button] && !oldMouse[button];		
	}
	
	public static boolean isMouseReleased(int button) {
		return !InputHandler.mouse[button] && oldMouse[button];
	}
	
	public static InputHandler getInput() { return input; }
}
