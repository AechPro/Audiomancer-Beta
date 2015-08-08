package openGLTests.main.editor;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;
public class Tile implements MouseListener, MouseMotionListener
{
	private double x,y;
	private int mouseX, mouseY;
	private int width,height;
	private String URL;
	private int ID;
	private boolean clicked;
	private boolean placed;
	private boolean selectable;
	private boolean colliding;
	private boolean saved;
	private boolean removed;
	private Image image;
	private ImageIcon imageIcon;
	private boolean highlightable;
	private boolean overlayable;
	private int backgroundTileID;
	private int maxX;
	private int maxY;
	private int velocity;
	private int fanHeight;
	private boolean fan,vals;
	private final int origX, origY;
	public Tile(int _x, int _y, String _URL, int backID, int _maxX, int _maxY)
	{
		backgroundTileID=backID;
		URL = _URL;
		imageIcon = new ImageIcon(this.getClass().getResource("resources/Editor/textures/environment/tiles/"+URL));
		//imageIcon = new ImageIcon(this.getClass().getResource("resources/textures/environment/tiles/"+URL));
		image = imageIcon.getImage();
		width=imageIcon.getIconWidth();
		height=imageIcon.getIconHeight();
		String temp = _URL.replace("tile_","");
		String temp2 = temp.replace(".png","");
		ID = Integer.parseInt(temp2);
		if(ID==9 || ID == 10){overlayable=true;}
		if(ID == 11){fan=true;}
		x=origX=_x;
		y=origY=_y;
		maxX = _maxX;
		maxY = _maxY;
		
		if(maxX>1280-32){maxX=1280-32;}
		if(maxY>720-32){maxY=720-32;}
		selectable=true;
	}
	public void update()
	{
		if(clicked || colliding)
		{
			//System.out.println("("+x+","+y+")");
			if(mouseX>x+width){x+=(width);}
			if(mouseX<x){x-=(width);}
			if(mouseY>y+height){y+=(height);}
			if(mouseY<y){y-=(height);}
			if(x+width>maxX+10){x-=(width);}
			if(x<190){x+=(width);}
			if(y<22){y+=(height);}
			if(y+height>maxY+10){y-=(height);}
		}
	}
	public void render(Graphics2D g)
	{
		if(clicked || colliding)
		{
			if(highlightable)
			{
				g.setColor(Color.GREEN);
				g.drawRect((int)x-1, (int)y-1, width+1, height+1);
			}
		}
		g.drawImage(image,(int)x,(int)y,null);
	}
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e){}
	public void mouseDragged(MouseEvent e) 
	{
		if(SwingUtilities.isRightMouseButton(e))
		{
			if(e.getX()>x && e.getX()<x+width)
			{
				if(e.getY()>y && e.getY()<y+height)
				{
					if(!clicked && !colliding){removed=true;}
				}
			}
		}
		else
		{
			removed=false;
			if(selectable)
			{
				placed=false;
				mouseX = e.getX();
				mouseY = e.getY();
				if(e.getX()>x && e.getX()<x+width)
				{
					if(e.getY()>y && e.getY()<y+height)
					{
						clicked=true;
					}
				}
			}
			
		}
	}
	public void openMenu()
	{
		vals=false;
		InputManager getter = new InputManager("Input fan velocity:");
		velocity = getter.getInput();
		getter.dispose();
		getter = new InputManager("Input fan height: ");
		fanHeight = getter.getInput();
		getter.dispose();
		vals=true;
	}
	public void mouseMoved(MouseEvent e) 
	{
		if(colliding)
		{
			mouseX = e.getX();
			mouseY = e.getY();
		}
		if(clicked)
		{
			placed=true;
		}
		clicked=false;
	}
	public void mouseReleased(MouseEvent e)
	{
		if(SwingUtilities.isRightMouseButton(e))
		{
			removed=false;
		}
	}

	public boolean isColliding(Tile o1)
	{
		Rectangle r1 = new Rectangle((int)x,(int)y,width,height);
		Rectangle r2 = new Rectangle((int)o1.getX(),(int) o1.getY(), o1.getWidth(), o1.getHeight());
		return r1.intersects(r2);
	}
	public double getX() {return x;}
	public double getY() {return y;}	
	public int getWidth() {return width;}	
	public int getHeight() {return height;}
	public String getURL() {return URL;}
	public int getID() {return ID;}
	public boolean isClicked() {return clicked;}
	public boolean isPlaced(){return placed;}
	public boolean isSelectable(){return selectable;}
	public boolean isSaved(){return saved;}
	public boolean isRemoved(){return removed;}
	public int getBackgroundTileID(){return backgroundTileID;}
	public boolean isOverlayable(){return overlayable;}
	public boolean isFan(){return fan;}
	public boolean receivedVals(){return vals;}
	public int getFanHeight(){return fanHeight;}
	public int getVelocity(){return velocity;}
	public int getOrigX(){return origX;}
	public int getOrigY(){return origY;}
	
	
	public void setClicked(boolean clicked) {this.clicked = clicked;}
	public void setPlaced(boolean placed) {this.placed = placed;}
	public void setX(double x) {this.x = x;}
	public void setY(double y) {this.y = y;}
	public void setWidth(int width) {this.width = width;}
	public void setHeight(int height) {this.height = height;}
	public void setURL(String uRL) {URL = uRL;}
	public void setID(int iD) {ID = iD;}
	public void setSelectable(boolean i){selectable=i;}
	public void setColliding(boolean i){colliding=i;}
	public void setSaved(boolean i){saved=i;}
	public void setHighlightable(boolean i){highlightable=i;}
	public void setBackgroundTileID(int i){backgroundTileID=i;}
	public void setOverlayable(boolean i){overlayable=i;}
	public void setFanHeight(int i){fanHeight=i;}
	public void setVelocity(int i){velocity=i;}
}