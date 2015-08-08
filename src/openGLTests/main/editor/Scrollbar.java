package openGLTests.main.editor;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.*;
public class Scrollbar implements MouseMotionListener, KeyListener
{
	private double x,y;
	private int mouseX, mouseY;
	private int width,height;
	private boolean selected;
	private Image image;
	private ImageIcon icon;
	private boolean vert;
	private double ratio;
	private int min,max;
	private double displacement;
	private boolean keyboardInput;
	private double runOff;
	public Scrollbar(double _imageWidth, double _imageHeight, int _windowWidth, int _windowHeight, int _min, int _max, boolean vertical) //input imageWidth and imageHeight multiplied by tile size
	{
		vert=vertical;
		min=_min;
		max=_max;
		if(vertical){initVerticalScrollbar(_imageWidth, _imageHeight, _windowWidth, _windowHeight);}
		else{initHoriztonalScrollbar(_imageWidth, _imageHeight, _windowWidth, _windowHeight);}
	}
	public void update() 
	{
		if(vert && selected)
		{
			y=mouseY - (height / 2);
			if(y<min){y=min;}
			else if(y+height>max){y=max-height;}
			displacement = (y-min)*ratio;
		} 
		else if(!vert && selected)
		{
			x=mouseX - (width / 2);
			if(x<min){x=min;}
			else if(x+width>max){x=max-width;}
			displacement = (x-min)*ratio;
		}
	}
	public void render(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.drawImage(image,(int)x,(int)y,null);
	}
	
	public void initVerticalScrollbar(double imgW, double imgH, int winW, int winH)
	{
		icon = new ImageIcon(this.getClass().getResource("resources/Editor/textures/UI/scrollbar/vertBar.png"));
		image = icon.getImage();
		width = image.getWidth(null);
		height = image.getHeight(null);
		
		x=winW-width;
		y=min;
		int offset=64;
		ratio =((double) (imgH - max + offset - min)/(double) (max - min - height));
		
	}
	public void initHoriztonalScrollbar(double imgW, double imgH, int winW, int winH)
	{
		icon = new ImageIcon(this.getClass().getResource("resources/Editor/textures/UI/scrollbar/horizBar.png"));
		image = icon.getImage();
		width = image.getWidth(null);
		height = image.getHeight(null);
		
		y=winH-height;
		x=min;
		
		int offset=192;
		ratio = (double) (imgW - max - min + offset)/(double) (max - min - width);
	}
	
	public void mouseDragged(MouseEvent e) 
	{
		if(e.getX()>x && e.getX()<x+width)
		{
			if(e.getY()>y && e.getY()<y+height)
			{
				selected=true;
			}
		}
		if(selected)
		{
			mouseX=e.getX();
			mouseY=e.getY();
		}
	}
	public void mouseMoved(MouseEvent e) 
	{
		selected=false;
	}
	
	public void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();
		if(vert)
		{
			if(key == KeyEvent.VK_UP)
			{
				keyboardInput=true;
				if(y<min)
				{
					y=min;
				}
				displacement-=32;
			}
			if(key == KeyEvent.VK_DOWN)
			{
				keyboardInput=true;
				if(y+height>max)
				{
					y=max-height;
					displacement = (y-min)*ratio;
				}
				else
				{
					displacement+=32;
					y=(displacement/ratio) + min+64;
				}
			}
		}
		
		if(key == KeyEvent.VK_LEFT)
		{
		}
		if(key == KeyEvent.VK_RIGHT)
		{
		}
	}
	public void keyReleased(KeyEvent e) 
	{
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_LEFT){keyboardInput=true;}
		
	}
	public void keyTyped(KeyEvent e) {}
	
	public int getDisplacement(){return (int)displacement;}
	public boolean isSelected(){return selected;}
	public boolean isKeyboardInput(){return keyboardInput;}
}