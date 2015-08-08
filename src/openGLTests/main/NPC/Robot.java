package openGLTests.main.NPC;

import java.util.concurrent.Semaphore;

import openGLTests.main.ChatHandler;
import openGLTests.main.Player;
import openGLTests.main.TileMap;

public class Robot extends NPC 
{
	private boolean tmpFlag;
	int vx = 5;
	boolean right=false;
	int _dx;
	int _dy;
	public Robot(String animationPath, int x, int y, String name, Player p,TileMap tm, ChatHandler ch, Semaphore[] sems) 
	{
		super(animationPath, x, y, name, p, tm, ch,sems);
		tmpFlag=false;
	}

	@Override
	public void fear() 
	{
		xSpd=-vx;
	}

	@Override
	public void interact()
	{ 
		chat.singleMessage("Hello, minion.");
	}

	@Override
	public void idle() 
	{
		if(!tmpFlag)
		{
			jumpTo(5,-32);
			tmpFlag=true;
		}
		//xSpd=0;
	}

	@Override
	public void attack() 
	{

	}

	@Override
	public void walk() 
	{
		if(dx<x)
		{
			xSpd=-vx;
			direction = Direction.LEFT;
		}
		else
		{
			xSpd=vx;
			direction = Direction.RIGHT;
		}
		if(x<dx-xSpd || x>dx+xSpd){}
		else
		{
			changeState(State.IDLE,1);
		}

	}
	public void pace()
	{
		if(tmpFlag){moveDist=200; tmpFlag=false;}
		else{moveDist=-100; tmpFlag=true;}
		dest=x+moveDist;
		changeState(State.WALKING,1);
	}
	public boolean simulateJump(int dx, int dy)
	{
		System.out.println("simulating jump to: "+"("+dx+","+dy+")");
		right=false;
		if(dx>x){right=true;}
		double tmpX, tmpY;
		double t = Math.abs(x-dx)/(2.0*vx);
		System.out.println(t);
		tmpY = (int)(jumpSpeed + gravity*t);
		t = (tmpY - jumpSpeed)/(double)gravity;
		
		tmpX = vx*t;
		tmpX+=x;
		tmpY+=y;
		System.out.println("("+x+","+y+")   "+"   ("+tmpX+","+tmpY+")");
		if(right){return tmpY<=dy && tmpX>=dx;}
		return tmpY<=dy && tmpX<=dx;
	}
	@Override
	public void run() 
	{

	}

	@Override
	public void jump() 
	{
		if(!tmpFlag)
		{
			ySpd=jumpSpeed;
			jumping=true;
			tmpFlag=true;
		}
		if(!jumping)
		{
			System.out.println("changing state");
			tmpFlag=false;
			changeState(prevState,1);
			prevState = State.JUMPING;
		}
	}
	public void jumpTo(int _x, int _y)
	{
		right=false;
		if(_dx>x){right=true;}
		_dx=x+_x;
		_dy=y+_y;
		if(simulateJump(_dx,_dy))
		{
			System.out.println("can make jump");
			Thread t = new Thread()
			{
				public void run()
				{
					boolean running=true;
					ySpd=jumpSpeed;
					
					jumping=true;
					while(running)
					{
						if(right){xSpd=vx;}
						else{xSpd=-vx;}
						if(right)
						{
							System.out.println("("+x+","+y+")   "+"   ("+_dx+","+_dy+")");
							if(x>=_dx && y<=_dy){running=false;}
						}
						else
						{
							if(x<=_dx && y<=_dy){running=false;}
						}
						try {sleep(50);} 
						catch (Exception e) {e.printStackTrace();}
					}
					xSpd=0;
					System.out.println("jump completed");
				}
			};
			t.start();
		}
	}

}
