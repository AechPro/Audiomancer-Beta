package openGLTests.main.NPC;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import openGLTests.main.*;
import openGLTests.main.pathing.*;

public abstract class NPC
{
	protected Thread thread;
	protected State state;
	protected State prevState;
	protected Direction direction;
	protected String aPath,name;
	protected int x,y,width,height,prevX;
	protected Animation walkAnim;
	protected Animation jumpAnim;
	protected Animation idleAnim;
	protected int curAnim;
	protected Player player;
	protected Texture speechBubble;
	protected TrueTypeFont font;
	protected Font awtFont;
	protected static final int fontSize=14;

	protected int xSpd,ySpd;
	protected int runMod;
	protected int jumpSpeed;
	protected int gravity;
	protected boolean jumping;
	protected String text;
	protected ChatHandler chat;
	protected boolean colV,colH,foundCorner;
	protected int dest;
	protected int moveDist;
	protected Astar pathfinder;
	private boolean flag1;
	protected int dx, dy;
	protected Semaphore[] sems;

	private ArrayList<Node> collidableTiles;
	private ArrayList<Node> collisions;
	private ArrayList<Node> path;

	protected TileMap map;

	protected boolean pathing;

	public enum State
	{
		IDLE, INTERACTING, FEAR, ATTACKING, WALKING, RUNNING, JUMPING;
		private boolean val;
		public void setPerform(boolean i){val=i;}
		public boolean canPerform(){return val;}
	}
	public enum Direction{LEFT,RIGHT}

	public NPC(String animationPath, int x, int y,String name, Player p, TileMap tm, ChatHandler ch, Semaphore[] s)
	{
		sems = s;
		pathfinder = null;
		chat = ch;
		gravity = 1;
		runMod=5;
		ySpd=xSpd=0;
		jumpSpeed=-12;
		width=height=32;
		this.x=x;
		this.y=y;
		this.name=name;
		aPath=animationPath;
		map=tm;
		jumpAnim = new Animation(width,height,aPath+"/Fear/"+0+"/Fear_",4,3,250,0);
		walkAnim = new Animation(width,height,aPath+"/Walking/"+0+"/Walk_",4,3,250,0);
		idleAnim = new Animation(width,height,aPath+"/Idle/"+0+"/Idle_",4,3,250,0);
		System.out.println("about to change state");
		changeState(State.JUMPING,1);
		System.out.println("changed state");
		state.setPerform(true);
		direction=Direction.RIGHT;
		collisions = new ArrayList<Node>();
		path = new ArrayList<Node>();
		player = p;

		awtFont= new Font("Times New Roman", Font.BOLD, fontSize);
		font = new TrueTypeFont(awtFont, false);

		initPath();
	}
	public void update()
	{
		System.out.println("\n");
		System.out.println("update top: ("+x+","+y+")");
		
		x-=map.getXDiffs();
		y-=map.getYDiffs();
		if(!colV){y+=ySpd;}
		if(!colH){x+=xSpd; System.out.println("moving with "+xSpd);}
		checkCollisions();
		ySpd+=gravity;
		updateStates();
		System.out.println("before collisions: "+xSpd);
		
		System.out.println("after collisions: "+xSpd);
		System.out.println("update bottom: ("+x+","+y+")");
		System.out.println("\n");

	}
	@SuppressWarnings("incomplete-switch")
	public void render()
	{
		try
		{
			sems[0].acquire();
			switch(direction)
			{
			case LEFT:
				switch(state)
				{
				case WALKING:
					walkAnim.render(x,y,true,true,true);
					break;
				case JUMPING:
					jumpAnim.render(x,y,true,true,true);
					break;
				case IDLE:
					idleAnim.render(x,y,true,true,true);
					break;
				}

				break;
			case RIGHT:
				switch(state)
				{
				case WALKING:
					walkAnim.render(x,y,false,true,true);
					break;
				case JUMPING:
					jumpAnim.render(x,y,false,true,true);
					break;
				case IDLE:
					idleAnim.render(x,y,false,true,true);
					break;
				}
				break;
			}
			font.drawString(x,y-fontSize,name);
			sems[0].release();
		}catch(Exception e){}
	}

	protected void checkCollisions()
	{
		double prevYSpd = ySpd,prevXSpd=xSpd;
		colV=false;
		colH=false;
		collidableTiles = map.getCollisionTiles();

		int px = (int)(x+xSpd);
		int py = (int)(y+ySpd);

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
		xSpd=(int) prevXSpd;
		ySpd=(int) prevYSpd;
		px = (int)(x+xSpd);
		py = (int)(y+ySpd);
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
				System.out.println("vert collision");
				ySpd=0;
				if(!colV){y-=yOffset;}
				colV=true;
			}
			else if((angle<44 || angle>316))
			{
				System.out.println("horiz collision");
				xSpd=0;
				if(!colH){x+=xOffset;}
				colH=true;
			}
			else if(angle>136 && angle<224)
			{
				System.out.println("horiz collision");
				xSpd=0;
				if(!colH){x-=xOffset;}
				colH=true;
			}
			else if((angle>226 && angle<314))
			{
				System.out.println("vert collision");
				ySpd=0;
				if(!colV){y+=yOffset;}
				colV=true;
			}
		}
	}

	public boolean isColliding(int x1, int y1, int w, int h, int x2, int y2, int w2, int h2)
	{
		Rectangle r1 = new Rectangle(x1,y1,w,h);
		Rectangle r2 = new Rectangle(x2,y2,w2,h2);
		return r1.intersects(r2);
	}


	public abstract void fear();
	public abstract void interact();
	public abstract void idle();
	public abstract void attack();
	public abstract void walk();
	public abstract void run();
	public abstract void jump();
	public void path()
	{
		Thread thread = new Thread()
		{
			public void run()
			{
				dx=1280/2;
				dy=720/2;
				findPath(dx,dy);
				System.out.println("path found");
				followPath();
				System.out.println("path followed");
			}
		};
		thread.start();
	}
	public void initPath()
	{
		for(int i=0;i<map.getTiles().size();i++)
		{
			map.getTiles().get(i).setPath(false);
		}
		pathfinder = new Astar(map.getTiles(),10,32,jumpSpeed,2,gravity,sems[1]);
	}
	public void findPath(int destX, int destY)
	{
		if(pathfinder != null)
		{
			pathing=true;
			map.setDest(destX,destY);
			map.setStart(x,y);
			
			pathing=false;
		}
	}
	public void followPath()
	{
		path = pathfinder.calculatePath();
		if(path != null && !pathing)
		{
			collidableTiles = map.getCollisionTiles();
			width = 16;
			for(int i=0;i<path.size() && path!=null;i++)
			{
				try
				{
					//while(true)
					//{
						int val = 10;
						if(x>dx-val && x<dx+val)
						{
							if(y>dy && y<dy)
							{
								break;
							}
						}
						dx = path.get(i).getX();
						dy = path.get(i).getY();
						sems[1].acquire();
						if(path.get(i).getX()>x){direction = Direction.RIGHT;}
						else{direction = Direction.LEFT;}
						if(state!=NPC.State.WALKING){changeState(NPC.State.WALKING,1);}
						flag1=false;
						boolean topCollision = false;
						int xSign=0;
						double sy=0,sx=0;
						double jS=0,grav=0,xS=0;
						int t = 0;
						int jT = 0;
						int xOffset=0, yOffset=0;
						if(direction == Direction.LEFT){sx = x;}
						else{sx = x+width;}
						xSign = (int) ((sx - path.get(i).getX())/Math.abs(sx-path.get(i).getX()));
						for(int k=(int)sx;k<path.get(i).getX();k++,t++)
						{
							if(xS!=0){sx = (xS*xSign*t);}
							if(jS!=0 || grav!=0)
							{
								sy = (jS*jT) + (grav/2.0)*t*t;
							}
							xOffset=0;
							yOffset=0;

							for(int j=0;j<collidableTiles.size();j++)
							{
								if(isColliding((int)sx,(int)sy,width,height,collidableTiles.get(j).getX(),collidableTiles.get(j).getY(),32,32))
								{
									double angle=0;
									angle = Math.atan2(Math.toDegrees((double)(collidableTiles.get(j).getY()+16)-(sy+height/2.0)),Math.toDegrees((double)(collidableTiles.get(j).getX()+16)-(sx+width/2.0)));
									angle*=180.0;
									angle/=-Math.PI;
									if(angle<0){angle=360-Math.abs(angle);}
									yOffset=(int)(Math.abs((sy + height/2) - (collidableTiles.get(j).getY()+16)) - (32/2 + height/2));
									xOffset = (int) (Math.abs((sx + width/2) - (collidableTiles.get(j).getX()+16)) - (32/2 + width/2));
									if((angle<44 || angle>316) || (angle>136 && angle<224))
									{
										xS=0;
										sx+=xOffset*xSign;
									}
									else if((angle>46 && angle<134))
									{
										topCollision=true;
										sy-=yOffset;

									}
									else if((angle>226 && angle<314))
									{
										grav=jS=xS=0;
										sy+=yOffset;
									}

								}
							}

							if(!topCollision){jT++;}
 							if(isColliding((int)sx,(int)sy,width,height,path.get(i).getX(),path.get(i).getY(),32,32) && !flag1)
							{
								double d = Math.sqrt(Math.pow((sx+width/2) - (path.get(i).getX()+16),2)+Math.pow((sy+height/2) - (path.get(i).getY()+16),2));
								if(d<=32.0/1.5)
								{
									flag1=true;
									break;
								}

							}
							sems[1].release();
						}
						if(flag1)
						{
							changeState(NPC.State.JUMPING,1);
						}
						
						System.out.println("completing path follow");
					//}
					pathing=false;
				}
				catch(Exception e){e.printStackTrace();}
			}
			width = 32;
		}

	}

	public void updateStates()
	{
		if(state.canPerform())
		{
			switch(state)
			{
			case IDLE:
				idle();
				break;
			case INTERACTING:
				interact();
				break;
			case FEAR:
				fear();
				break;
			case ATTACKING:
				attack();
				break;
			case WALKING:
				walk();
				break;
			case RUNNING:
				run();
				break;
			case JUMPING:
				jump();
				break;
			}
		}
	}

	public void changeState(State newState, int num)
	{
		try 
		{
			sems[0].acquire();
			prevState=state;
			state=newState;
			state.setPerform(true);
			sems[0].release();
		} 
		catch (InterruptedException e) {System.out.println("I'm not tired enough to sleep mommy");}
	}

	protected Texture loadTexture(String s)
	{
		try
		{
			return TextureLoader.getTexture("png", new FileInputStream(s+".png"));
		}
		catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}

		return null;
	}

}
