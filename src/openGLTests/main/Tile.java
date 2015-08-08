package openGLTests.main;
import static org.lwjgl.opengl.GL11.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import openGLTests.main.pathing.Node;
public class Tile extends Node
{
	private int width,height;
	private String URL;
	
	public Tile(int _x, int _y, String _URL, int backID)
	{
		super(_x,_y,Integer.parseInt(_URL.replace("tile","").replace(".png","")));
		walkable=false;
    backgroundTileID=backID;
		URL = _URL;
		texture = loadTexture("resources/Textures/Tiles/"+URL);
		ID = Integer.parseInt(_URL.replace("tile","").replace(".png",""));
		if(ID==9 || ID == 10){overlayable=true;}
		if(ID==11 || ID==99 || ID==12 || ID==0 || ID==7){walkable=true;}
		if(ID==11){fan=true;}
		x=_x;
		y=-_y;
		width = 32;
		height = 32;
		if(maxX>1280){maxX=1280;}
		if(maxY>720){maxY=720;}
	}
	public void update(int x, int y)
	{
		this.x-=x;
		this.y-=y;
	}
	public void render()
	{
		glEnable (GL_BLEND);
		glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		texture.bind();
		glBegin(GL_QUADS);
		{
			//lower left
			glTexCoord2f(0,0);
			glVertex2f((float)x,(float)y);

			//top left
			glTexCoord2f(0,1);
			glVertex2f((float)x,(float)y+height);

			//top right
			glTexCoord2f(1,1);
			glVertex2f((float)x+width,(float)y+height);
			
			//lower right
			glTexCoord2f(1,0);
			glVertex2f((float)x+width,(float)y);
		}
		glEnd();
	}
	
	private Texture loadTexture(String s)
	{
		try
		{
			return TextureLoader.getTexture("png", new FileInputStream(s+".png"));
		}
		catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}

		return null;
	}
	public String getURL() {return URL;}
	public void setURL(String uRL) {URL = uRL;}
}
