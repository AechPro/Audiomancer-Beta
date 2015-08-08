package openGLTests.main;

import static org.lwjgl.opengl.GL11.*;

import java.util.concurrent.Semaphore;

import org.lwjgl.input.Keyboard;

import openGLTests.main.NPC.*;

public class Game 
{
	private Player player;
	private TileMap tileMap;
	private NPC robot;
	private ChatHandler chat;
	private Semaphore[] sems;
	private boolean pressed=false;
	public Game()
	{
		sems = new Semaphore[2];
		for(int i=0;i<2;i++)
		{
			sems[i] = new Semaphore(1);
		}
		chat = new ChatHandler(0,720-32,14,6);
		tileMap = new TileMap("testMap.txt",32,sems);
		player = new Player(tileMap, tileMap.getPlayerSpawnX(),tileMap.getPlayerSpawnY(),chat);
		robot = new Robot("Resources/Textures/NPCs/Enemies/Test",tileMap.getPlayerSpawnX(),tileMap.getPlayerSpawnY()-10, "bob",player,tileMap,chat,sems);
	}
	public void getInput()
	{
		if(Keyboard.isKeyDown(Keyboard.KEY_Z))
		{
			if(!pressed){robot.path(); pressed=true;}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_P))
		{
			pressed=false;
		}
		player.getInput();
	}

	public void update()
	{
		chat.update();
		player.update();
		tileMap.update((1280/2)-(int)player.getX(),(720/2)-(int)player.getY());
		robot.update();
	}

	public void render()
	{
		glPushMatrix();
		{
			glClearColor(0,0,0,0);
			glColor4f(255,255,255,255);
			tileMap.render();
			player.render(tileMap.getX(), tileMap.getY());
			chat.render();
			robot.render();
		}
		glPopMatrix();

	}

	public float getX(){return player.getX();}
	public float getY(){return player.getY();}
	public float getWidth(){return player.getWidth();}
	public float getHeight(){return player.getHeight();}
}
