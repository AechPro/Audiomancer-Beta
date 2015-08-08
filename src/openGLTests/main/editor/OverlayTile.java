package openGLTests.main.editor;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;

public class OverlayTile implements MouseMotionListener
{
	private int x,y;
	private int width,height;
	private String URL;
	private int ID;
	private boolean selected;
	private boolean spawned;
	private Image image;
	private ImageIcon imageIcon;
	public OverlayTile(int _x, int _y, String _URL)
	{
		URL = _URL;
		imageIcon = new ImageIcon(this.getClass().getResource("resources/Editor/textures/environment/tiles/"+URL));
		//imageIcon = new ImageIcon(this.getClass().getResource("resources/textures/environment/tiles/"+URL));
		image = imageIcon.getImage();
		width=imageIcon.getIconWidth();
		height=imageIcon.getIconHeight();
		String temp = _URL.replace("tile_","");
		String temp2 = temp.replace(".png","");
		ID = Integer.parseInt(temp2);
		x=_x;
		y=_y;
	}
	public void render(Graphics2D g)
	{
		g.drawImage(image,x,y,null);
	}
	public int getX() {return x;}
	public int getY() {return y;}
	
	public int getID() {return ID;}
	public int getWidth() {return width;}
	public int getHeight() {return height;}
	public boolean isSelected() {return selected;}
	public boolean isSpawned() {return spawned;}
	public String getURL() {return URL;}
	
	public void setWidth(int i) {width = i;}
	public void setY(int i) {y = i;}
	public void setHeight(int i) {height = i;}
	public void setURL(String i) {URL = i;}
	public void setX(int i) {x = i;}
	public void setSelected(boolean i) {selected = i;}
	public void setID(int i) {ID = i;}
	public void setSpawned(boolean i) {spawned = i;}
	
	
	public void mouseDragged(MouseEvent e)
	{
		if(e.getX()>x && e.getX()<x+width)
		{
			if(e.getY()>y && e.getY()<y+height)
			{
				selected=true;
			}
		}
	}
	public void mouseMoved(MouseEvent e)
	{
		
	}
}
