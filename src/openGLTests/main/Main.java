package openGLTests.main;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.*;
import static org.lwjgl.opengl.GL11.*;

public class Main 
{
	
	private static Game game;
	
	public Main()
	{
		initDisplay();
		initGL();
		gameLoop();
		cleanUp();
	}
	public static void main(String[] args)
	{
		Main main = new Main();
	}
	
	public void gameLoop()
	{
		initGame();
		while(!Display.isCloseRequested())
		{
			getInput();
			update();
			render();
		}
	}
	
	private void update()
	{
		game.update();
	}
	
	private void getInput()
	{
		game.getInput();
	}
	
	private void render()
	{
		glClear(GL_COLOR_BUFFER_BIT);
		glLoadIdentity();
		
		game.render();
		
		Display.update();
		Display.sync(60);
	}
	
	private void initGame()
	{
		game = new Game();
	}
	
	public void initDisplay()
	{
		try
		{
			Display.setDisplayMode(new DisplayMode(1280,720));
			Display.create();
			Keyboard.create();
			Display.setVSyncEnabled(true);
		}
		catch(LWJGLException e)
		{
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	
	public void cleanUp()
	{
		Display.destroy();
		Keyboard.destroy();
	}

	public void initGL()
	{
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0,Display.getWidth(),Display.getHeight(),0,-1,1);
		
		glMatrixMode(GL_MODELVIEW);
		
		glDisable(GL_LIGHTING);
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_TEXTURE_2D);
		glClearColor(0,0,0,0);
	}

}
