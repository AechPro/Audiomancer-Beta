package openGLTests.main.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
public class Overlay implements MouseMotionListener, MouseListener
{
	private Image image;
	private ImageIcon icon;
	private int x,y;
	private int width,height;
	private boolean inMenu;
	private ArrayList<OverlayTile> tiles;
	private int availTiles;
	private int curID;
	private int tileX, tileY;
	private ArrayList<Button> buttons;
	public Overlay()
	{
		System.out.println(this.getClass().getResource(""));
		//icon = new ImageIcon(this.getClass().getResource("resources/textures/editor/frame/frame.png"));

		icon = new ImageIcon(this.getClass().getResource("resources/Editor/textures/UI/frame/frame.png"));
		image = icon.getImage();
		x=0;
		y=0;
		curID=-1;
		width=image.getWidth(null);
		height=image.getHeight(null);
		tiles = new ArrayList<OverlayTile>();
		availTiles=12;
		int index=0;
		for(int i=0;i<availTiles;i++)
		{
			tiles.add(new OverlayTile(14,(44*i)+78,"tile_"+(i)+".png"));
			index++;
		}
		tiles.add(new OverlayTile(14,(44*index)+78,"tile_99.png") );
		initButtons();
		inMenu=false;
	}
	public void update()
	{
		inMenu=false;
		for(int i=0;i<buttons.size();i++)
		{
			buttons.get(i).update();
			if(buttons.get(i).isClicked() && !inMenu)
			{
				inMenu=true;
				switch(buttons.get(i).getID())
				{
				case "file":
					System.out.println("file clicked");
					break;
				case "view":
					System.out.println("view clicked");
					break;
				case "settings":
					System.out.println("settings clicked");
					break;
				case "edit":
					System.out.println("edit clicked");
					break;
				case "help":
					System.out.println("help clicked");
					break;
				case "check":
					System.out.println("check clicked");
					break;
				case "window size":
					System.out.println("window size clicked");
					break;
				case "exit":
					System.out.println("exit clicked");
					break;
				}
			}
			else if(buttons.get(i).isClicked() && inMenu)
			{
				buttons.get(i).setClickable(false);
				buttons.get(i).setClicked(false);
			}
		}
		if(!inMenu)
		{
			for(int i=0;i<buttons.size();i++)
			{
				buttons.get(i).setClickable(true);
			}
			for(int i=0,stop=tiles.size();i<stop;i++)
			{
				if(tiles.get(i).isSelected())
				{
					curID=tiles.get(i).getID();
					tileX = tiles.get(i).getX();
					tileY = tiles.get(i).getY();
					break;
				}
			}
		}
	}
	public void render(Graphics2D g)
	{
		g.drawImage(image,x,y,null);
		for(int i=0,stop=tiles.size();i<stop;i++)
		{
			tiles.get(i).render(g);
		}
		for(int i=0;i<buttons.size();i++)
		{
			buttons.get(i).render(g);
		}

	}

	public void resetSelection()
	{
		for(int i=0,stop=tiles.size();i<stop;i++)
		{
			tiles.get(i).setSelected(false);
		}
		curID=-1;
	}

	public int getTileX(){return tileX;}
	public int getTileY(){return tileY;}
	public int getSelectedID(){return curID;}
	public ArrayList<OverlayTile> getTiles(){return tiles;}


	public void mouseMoved(MouseEvent e)
	{
		System.out.println("("+e.getX()+","+e.getY()+")");
	}
	public void mouseDragged(MouseEvent e)
	{
		for(int i=0;i<buttons.size();i++)
		{
			buttons.get(i).mouseDragged(e);
		}
		for(int i=0;i<tiles.size();i++)
		{
			tiles.get(i).mouseDragged(e);
		}
	}

	public void mouseClicked(MouseEvent e) 
	{
		for(int i=0;i<buttons.size();i++)
		{
			buttons.get(i).mouseClicked(e);
		}
	}


	public void initButtons()
	{
		buttons = new ArrayList<Button>();
		buttons.add(new Button(4,4,88,32-8,"file"));
		buttons.add(new Button(4+8+88,4,88,32-8,"edit"));
		buttons.add(new Button(4+8+88+8+88,4,88,32-8,"view"));
		buttons.add(new Button(4+8+88+8+88+8+88,4,88,32-8,"settings"));
		buttons.add(new Button(4+8+88+8+88+8+88+8+88,4,88,32-8,"help"));
		buttons.add(new Button(4+8+88+8+88+8+88+8+88+8+88,4,32-8,32-8,"check"));
		buttons.add(new Button(4+8+88+8+88+8+88+8+88+8+88+32,4,32-8,32-8,"window size"));
		buttons.add(new Button(4+8+88+8+88+8+88+8+88+8+88+64,4,32-8,32-8,"exit"));
	}
	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) 
	{
		for(int i=0;i<buttons.size();i++)
		{
			buttons.get(i).mousePressed(e);
		}
	}

	public void mouseReleased(MouseEvent e) 
	{
		for(int i=0;i<buttons.size();i++)
		{
			buttons.get(i).mouseReleased(e);
		}
	}
}
