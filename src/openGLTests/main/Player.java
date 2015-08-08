package openGLTests.main;

import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;

import openGLTests.main.pathing.Node;

import org.lwjgl.input.*;
import org.newdawn.slick.TrueTypeFont;

import static org.lwjgl.opengl.GL11.*;

public class Player 
{
	private Animation walk;
	private Animation stand;
	private Animation jump;
	private Animation fall;
	private Animation shoot;

	private TileMap tileMap;

	private ArrayList<Node> collidableTiles;
	private ArrayList<Node> collisions;
	private int x,y,width,height;
	private double xSpd,ySpd,xVel;
	private int jumpSpeed;
	private double gravity;
	private int size=32;

	private int spawnX, spawnY;
	
	private boolean left,right;
	private boolean walking,jumping;
	private boolean falling, shooting, attacking, inAir;
	private boolean colV, colH;
	private int hitBox=9;
	
	private ChatHandler chat;
	
	Font awtFont = new Font("Times New Roman", Font.BOLD, 24); //name, style (PLAIN, BOLD, or ITALIC), size
	TrueTypeFont font= new TrueTypeFont(awtFont, false);

	public Player(TileMap tm, int x, int y, ChatHandler _ch)
	{
		spawnX=x;
		spawnY=y;
		
		chat = _ch;
		this.x=x;
		this.y=y;
		tileMap = tm;

		width=size-hitBox*2;
		height=size;

		ySpd=gravity;
		xVel=2;
		jumpSpeed=-8;
		gravity = 0.5;
		right=true;
		
		collisions = new ArrayList<Node>();
		
		
		
		initAnims();
	}

	public void update()
	{
		//check collisions and move
		//System.out.println("before collision check "+ySpd+" "+colV);
	//	System.out.println(colH+"  "+colV);
		if(ySpd>0){jumping=false; falling=true;}
		if(ySpd<=0){falling=false;}
		if(!colV){y+=ySpd;}
		if(!colH){x+=xSpd;}
		ySpd+=gravity;
		checkCollisions();
		
	}
	public void render(int tmx, int tmy)
	{
		//render the correct animation for the player state in the correct orientation
		//true = mirrored images 
		//false = unchanged images
		if(left && !jumping && walking && !inAir){walk.render(x+tmx,y+tmy,true,false,true);}
		else if(right && !jumping && walking && !inAir){walk.render(x+tmx,y+tmy,false,false,true);}

		else if(jumping && right){jump.render(x+tmx, y+tmy, true,false,true);}
		else if(jumping && left){jump.render(x+tmx, y+tmy, false,false,true);}

		/*else if(falling && right){fall.render(x+tmx, y+tmy, true,false,false);}
		else if(falling && left){fall.render(x+tmx, y+tmy, false,false,false);}*/
		
		else if(inAir && right){jump.render(x+tmx, y+tmy, true,false,true);}
		else if(inAir && left){jump.render(x+tmx, y+tmy, false,false,true);}
		
		else
		{
			if(left){stand.render(x+tmx,y+tmy,false,false,true);}
			else if(right){stand.render(x+tmx,y+tmy,true,false,true);}
		}
		
	}

	public boolean isColliding(int x1, int y1, int w, int h, int x2, int y2, int w2, int h2)
	{
		Rectangle r1 = new Rectangle(x1,y1,w,h);
		Rectangle r2 = new Rectangle(x2,y2,w2,h2);
		return r1.intersects(r2);
	}
	public void checkCollisions()
	{
		double prevYSpd = ySpd,prevXSpd=xSpd;
		colV=false;
		colH=false;
		collidableTiles = tileMap.getCollisionTiles();
		
		int px = (int)((1280/2)+xSpd)+hitBox;
		int py = (int)((720/2)+ySpd);
		
		collisions.clear();
		
		for(int i=0;i<collidableTiles.size();i++)
		{
			if(isColliding(px,py,width,height,collidableTiles.get(i).getX(),collidableTiles.get(i).getY(),32,32)){collisions.add(collidableTiles.get(i));}
		}
		for(int i=0;i<collisions.size();i++)
		{
			double angle=0;
			angle = Math.atan2((double)(collisions.get(i).getY()+16)-(py-ySpd+height/2.0),(double)(collisions.get(i).getX()+16)-(px-xSpd+width/2.0));
			angle*=180.0/-Math.PI;
			if(angle<0){angle=360-Math.abs(angle);}
			int yOffset = (int) (Math.abs((py + height/2) - (collisions.get(i).getY()+16)) - (height/2 + 16));
			int xOffset = (int) (Math.abs((px + width/2) - (collisions.get(i).getX()+16)) - (width/2 + 16));
			if((angle>46 && angle<134))
			{
				ySpd=0;
				if(!colV){py-=yOffset;}
				colV=true;
			}
			else if((angle<44 || angle>316))
			{
				xSpd=0;
				if(!colH){px+=xOffset;}
				colH=true;
			}
			else if(angle>136 && angle<224)
			{
				xSpd=0;
				if(!colH){px-=xOffset;}
				colH=true;
			}
			else if((angle>226 && angle<314))
			{
				ySpd=0;
				if(!colV){py+=yOffset;}
				colV=true;
			}
			for(int j=0;j<collisions.size();j++)
			{
				if(i!=j)
				{
					if(!isColliding(px,py,width,height,collisions.get(j).getX(),collisions.get(j).getY(),32,32))
					{
						collisions.remove(collisions.get(j));
					}
				}
			}
		}
		colV=false;
		colH=false;
		xSpd=prevXSpd;
		ySpd=prevYSpd;
		px = (int)((1280/2)+xSpd)+hitBox;
		py = (int)((720/2)+ySpd);
		for(int i=0;i<collisions.size();i++)
		{
			double angle=0;
			angle = Math.atan2((double)(collisions.get(i).getY()+16)-(py-ySpd+height/2.0),(double)(collisions.get(i).getX()+16)-(px-xSpd+width/2.0));
			angle*=180.0/-Math.PI;
			if(angle<0){angle=360-Math.abs(angle);}
			int yOffset = (int) (Math.abs((py + height/2) - (collisions.get(i).getY()+16)) - (height/2 + 16));
			int xOffset = (int) (Math.abs((px + width/2) - (collisions.get(i).getX()+16)) - (width/2 + 16));
			if((angle>46 && angle<134))
			{
				ySpd=0;
				if(!colV){y-=yOffset;}
				colV=true;
			}
			else if((angle<44 || angle>316))
			{
				xSpd=0;
				if(!colH){x+=xOffset;}
				colH=true;
			}
			else if(angle>136 && angle<224)
			{
				xSpd=0;
				if(!colH){x-=xOffset;}
				colH=true;
			}
			else if((angle>226 && angle<314))
			{
				ySpd=0;
				if(!colV){y+=yOffset;}
				colV=true;
			}
		}
	}
	public void checkFanCollisions()
	{
	}
	public void getInput()
	{
		
		if(Keyboard.isKeyDown(Keyboard.KEY_D) && !colH)
		{
			right=true;
			left=false;
			walking=true;
			xSpd=xVel;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_A) && !colH)
		{
			left=true;
			right=false;
			walking=true;
			xSpd=-xVel;
		}
		else{walking=false; xSpd=0;}
		if(Keyboard.isKeyDown(Keyboard.KEY_D) && Keyboard.isKeyDown(Keyboard.KEY_A)){xSpd=0; walking = false;}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !jumping && !falling)
		{
			ySpd=jumpSpeed;
			jumping=true;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_E))
		{
			//tileMap.setStart(1280/2,720/2);
			//tileMap.setDest(1280/2+192,720/2);
			x=tileMap.getPlayerSpawnX();
			y=tileMap.getPlayerSpawnY();
		}
	}
	private void initAnims()
	{
		
		//stand= new Animation(32,32,"Resources/Textures/NPCs/Enemies/Test/Idle/0/Idle_", 2,1,1000,3000);
		stand= new Animation(32,32,"Resources/Textures/Players/Audiomancer/Stand/audiomancer_stand_right_", 2,1,1000,3000);
		walk= new Animation(32,32,"Resources/Textures/Players/Audiomancer/Walk/am_walk_", 5,-1,83,0);
		jump= new Animation(32,32,"Resources/Textures/Players/Audiomancer/Jump/audiomancer_jump_right_", 1,-1,83,0);
		fall= new Animation(32,32,"Resources/Textures/Players/Audiomancer/Jump_Fall/audiomancer_jumpfall_right_", 7,1,125,2000/3);
		shoot= new Animation(32,32,"Resources/Textures/Players/Audiomancer/Spell/audiomancer_spell_right_", 12,-1,320,0);
	}

	public float getX(){return x;}
	public float getY(){return y;}
	public float getWidth(){return width;}
	public float getHeight(){return height;}
	public boolean isRight(){return right;}
	public boolean isLeft(){return left;}
}