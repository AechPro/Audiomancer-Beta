package openGLTests.main;

import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.opengl.Texture;
import java.io.*;


public class Animation 
{
	private Texture[] frames;
	private int delay;
	private int waitTime;
	private int waitFrame;
	private int current;
	private long startTime;
	private boolean wait;
	private int frameCount;
	private float width,height;
	private float imWidth,imHeight;
	private boolean reverse;
	private boolean repeat;
	private int numPlays;

	public Animation(int _width, int _height, String pathToTexture, int frames, int waitFrame, int millis, int waitMillis)
	{
		//System.out.println("loading animation from: "+pathToTexture);
		this.frames = new Texture[frames];
		for(int i=0;i<frames;i++)
		{
			this.frames[i] = loadTexture(pathToTexture+(i+1));
		}
		delay=millis;
		waitTime = waitMillis;
		this.waitFrame = waitFrame;
		imWidth=_width;
		imHeight=_height;
		frameCount=frames;
		startTime=System.nanoTime();
		numPlays=0;
		//System.out.println("animation loaded");
	}

	public void render(float x, float y, boolean mirror, boolean flip, boolean loop)
	{
		glEnable (GL_BLEND);
		glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		long passedTime;


		passedTime = System.nanoTime() - startTime;

		if(passedTime/1000000 >= delay)
		{
			if(current==waitFrame){wait=true;}

			if(wait && passedTime/1000000 < waitTime){}

			else
			{
				if(repeat){current--;}
				else{current++;}
				wait=false;
				passedTime=0;
				startTime=System.nanoTime();
			}
		}
		if(current<=0 && repeat){current=0; repeat=false;} 
		if(current>=frameCount && loop)
		{
			if(reverse){current=frameCount-1; repeat=true;}
			else{current=0;}
			numPlays++;
		}
		else if(current>=frameCount && !reverse)
		{
			current=frameCount-1;
		}
		renderFunc(x,y,flip,mirror);
		//System.out.println("rendering animation from: ("+x+","+y+") to: ("+width+","+height+")");

	}
	public void renderFunc(float x, float y, boolean flip, boolean mirror)
	{
		width=x+imWidth;
		height=y+imHeight;
		frames[current].bind();
		if(flip)
		{
			if(mirror)
			{
				glBegin(GL_QUADS);
				{
					//lower left
					glTexCoord2f(0,1);
					glVertex2f(x,y);

					//top left
					glTexCoord2f(0,0);
					glVertex2f(x,height);

					//top right
					glTexCoord2f(1,0);
					glVertex2f(width,height);

					//lower right
					glTexCoord2f(1,1);
					glVertex2f(width,y);
				}
				glEnd();
			}

			else
			{
				glBegin(GL_QUADS);
				{
					//lower left
					glTexCoord2f(1,1);
					glVertex2f(x,y);

					//top left
					glTexCoord2f(1,0);
					glVertex2f(x,height);

					//top right
					glTexCoord2f(0,0);
					glVertex2f(width,height);

					//lower right
					glTexCoord2f(0,1);
					glVertex2f(width,y);
				}
				glEnd();
			}
		}
		else
		{
			if(mirror)
			{
				glBegin(GL_QUADS);
				{
					//lower left
					glTexCoord2f(0,0);
					glVertex2f(x,y);
		
					//top left
					glTexCoord2f(0,1);
					glVertex2f(x,height);
		
					//top right
					glTexCoord2f(1,1);
					glVertex2f(width,height);
					
					//lower right
					glTexCoord2f(1,0);
					glVertex2f(width,y);
				}
				glEnd();
			}
			
			else
			{
				glBegin(GL_QUADS);
				{
					//lower left
					glTexCoord2f(1,0);
					glVertex2f(x,y);
		
					//top left
					glTexCoord2f(1,1);
					glVertex2f(x,height);
		
					//top right
					glTexCoord2f(0,1);
					glVertex2f(width,height);
					
					//lower right
					glTexCoord2f(0,0);
					glVertex2f(width,y);
				}
				glEnd();
			}
		}
	}
	public void setCurrent(int i)
	{
		current=i;
	}
	public int getNumPlays(){return numPlays;}
	public void setNumPlays(int i){numPlays=i;}
	public void setReverse(boolean i){reverse=i;}
	private Texture loadTexture(String s)
	{
		try
		{
			return TextureLoader.getTexture("png", new FileInputStream(s+".png"));
		}
		catch (Exception e) {e.printStackTrace();}
		return null;
	}
}
