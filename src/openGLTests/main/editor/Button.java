package openGLTests.main.editor;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class Button implements MouseListener, MouseMotionListener
{
	private int x,y,width,height;
	private Image[] images;
	private ImageIcon[] imageIcons;
	private boolean clicked;
	private long t1;
	private String ID;
	private boolean clickable;
	public Button(int _x,int _y, int _w, int _h, String _ID)
	{
		ID=_ID;
		clicked=false;
		clickable=true;
		x=_x;
		y=_y;
		width=_w;
		height=_h;
		images = new Image[2];
		imageIcons = new ImageIcon[2];
	}
	public void update()
	{
		
	}
	public void render(Graphics2D g)
	{
		if(clicked)
		{
			if(images[1]!=null)
			{
				g.drawImage(images[1],x,y,null);
			}
			else
			{
				g.setColor(Color.RED);
				g.fillRect(x, y, width, height);
			}
		}
		else
		{
			if(images[0]!=null)
			{
				g.drawImage(images[0],x,y,null);
			}
		}
	}

	public void mouseDragged(MouseEvent e) 
	{
		if(clickable)
		{
			if(e.getX()>x && e.getX()<x+width)
			{
				if(e.getY()>y && e.getY()<y+height)
				{
					t1 = System.nanoTime();
					clicked=true;
				}
			}
		}
	}
	public void mouseClicked(MouseEvent e) 
	{
	}
	public void setClickedImage(String i)
	{
		imageIcons[1] = new ImageIcon(this.getClass().getResource(i));
		images[1] = imageIcons[1].getImage();
		width = images[1].getWidth(null);
		height = images[1].getHeight(null);
	}
	public void setImage(String i)
	{
		imageIcons[0] = new ImageIcon(this.getClass().getResource(i));

		images[0] = imageIcons[0].getImage();
		width = images[0].getWidth(null);
		height = images[0].getHeight(null);
	}
	public void setClickable(boolean i){clickable=i;}
	public void setClicked(boolean i){clicked=i;}

	public boolean isClicked(){return clicked;}
	public String getID(){return ID;}


	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) 
	{
		if(clickable)
		{
			if(e.getX()>x && e.getX()<x+width)
			{
				if(e.getY()>y && e.getY()<y+height)
				{
					t1 = System.nanoTime();
					clicked=true;
				}
			}
		}
	}
	public void mouseReleased(MouseEvent e) 
	{
		if(clicked){clicked=false;}
	}
	public void mouseMoved(MouseEvent e) {}

}
