package openGLTests.main.editor;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

public class MasterController extends JPanel implements KeyListener
{
	private static final long serialVersionUID = -6281093899078909779L;
	private Scrollbar verticalScrollbar;
	private Scrollbar horizontalScrollbar;

	private Overlay overlay;
	private TileManager tm;
	private boolean loading, saving;
	private int mapWidth;
	private int mapHeight;
	private int width;
	private int height;
	private boolean updating;
	private boolean placed=false;
	public MasterController(int screenWidth, int screenHeight, int _width, int _height)
	{
		width = screenWidth;
		height = screenHeight;
		mapWidth = _width;
		mapHeight = _height;
	}
	public void init()
	{
		initScrollBars();
		tm = new TileManager(mapWidth, mapHeight);
		overlay = new Overlay();
	}
	public void update()
	{
		updating = true;
		tm.checkRequests("testMap.txt");
		if(tm.getScrollbarUpdate())
		{
			mapWidth = tm.getMapWidth();
			mapHeight = tm.getMapHeight();
			initScrollBars();
			tm.setScrollbarUpdate(false);
		}
		updateScrollBars();
		if(!loading && !saving && !verticalScrollbar.isSelected() && !horizontalScrollbar.isSelected())
		{
			overlay.update();
			if(overlay.getSelectedID()!=-1 && !placed)
			{
				placed=true;
				tm.addTile(overlay.getSelectedID(),overlay.getTileX(), overlay.getTileY());
				overlay.resetSelection();
			}
		}

		if(!loading && !saving && !verticalScrollbar.isSelected() && !horizontalScrollbar.isSelected() && tm.getTiles().size()>0)
		{
			tm.update();
		}
		updating = false;
	}
	public void render(Graphics2D g)
	{
		if(tm.getTiles().size()>0){tm.render(g);}
		verticalScrollbar.render(g);
		horizontalScrollbar.render(g);
		overlay.render(g);
	}
	public void updateScrollBars()
	{
		if(tm.getTiles().size()>0)
		{
			verticalScrollbar.update();
			if(verticalScrollbar.isSelected() && !tm.isTileSelected() || verticalScrollbar.isKeyboardInput())
			{
				//System.out.println("vert bar selected: "+verticalScrollbar.isSelected()+", "+verticalScrollbar.isKeyVert());
				tm.updateVert((int)verticalScrollbar.getDisplacement());

			}
			horizontalScrollbar.update();
			if(horizontalScrollbar.isSelected() && !tm.isTileSelected() || horizontalScrollbar.isKeyboardInput())
			{
				//System.out.println("horiz bar selected: "+horizontalScrollbar.isSelected()+ ", "+horizontalScrollbar.isKeyHoriz());
				tm.updateHoriz((int)horizontalScrollbar.getDisplacement());

			}
		}
	}
	public void initScrollBars()
	{
		verticalScrollbar = new Scrollbar(mapWidth*32, mapHeight*32, width, height, 64, height-64, true);
		horizontalScrollbar = new Scrollbar(mapWidth*32, mapHeight*32, width, height, 32, width-64, false);
	}
	public ArrayList<Tile> getTiles(){return tm.getTiles();}
	public void setLoading(boolean i, boolean spaces){tm.setLoading(i,spaces);}
	public void setSaving(boolean i){tm.setSaving(i);}
	public void setDragging(boolean i){tm.setDragging(i);}
	public boolean isLoading(){return loading;}
	public boolean isSaving(){return saving;}


	//----------mouse listeners----------\\
	public void mouseDragged(MouseEvent e)
	{
		if(!updating)
		{
			if(!loading && !updating && !saving && !verticalScrollbar.isSelected() && !horizontalScrollbar.isSelected())
			{
				overlay.mouseDragged(e);
			}
			if(!tm.isTileSelected())
			{
				verticalScrollbar.mouseDragged(e);
				horizontalScrollbar.mouseDragged(e);
			}
			if(!updating && tm.getTiles().size()>0 && !loading && !saving && !verticalScrollbar.isSelected() && !horizontalScrollbar.isSelected())
			{
				for(int i=0;i<tm.getTiles().size();i++)
				{
					if(tm.getTiles().get(i)!=null){tm.getTiles().get(i).mouseDragged(e);}
				}
			}
		}
	}
	public void mouseClicked(MouseEvent e)
	{	
		if(!loading && !updating && !saving && !verticalScrollbar.isSelected() && !horizontalScrollbar.isSelected())
		{
			overlay.mouseClicked(e);
		}
	}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e)
	{
		if(!loading && !updating && !saving && !verticalScrollbar.isSelected() && !horizontalScrollbar.isSelected())
		{
			overlay.mousePressed(e);
		}
		if(!updating && tm.getTiles().size()>0)
		{
			for(int i=0;i<tm.getTiles().size();i++)
			{
				tm.getTiles().get(i).mousePressed(e);
			}
		}
	}
	public void mouseReleased(MouseEvent e)
	{
		if(!loading && !updating && !saving && !verticalScrollbar.isSelected() && !horizontalScrollbar.isSelected())
		{
			overlay.mouseReleased(e);
		}
		if(!updating && !loading && !saving && !verticalScrollbar.isSelected() && !horizontalScrollbar.isSelected() && tm.getTiles().size()>0)
		{
			for(int i=0;i<tm.getTiles().size();i++)
			{
				if(tm.getTiles().get(i)!=null){tm.getTiles().get(i).mouseReleased(e);}
			}
		}
	}
	public void mouseMoved(MouseEvent e)
	{
		placed=false;
		if(!tm.isTileSelected())
		{
			if(verticalScrollbar !=null){verticalScrollbar.mouseMoved(e);}
			if(horizontalScrollbar !=null){horizontalScrollbar.mouseMoved(e);}
		}
		if(!loading && !updating && !saving && !verticalScrollbar.isSelected() && !horizontalScrollbar.isSelected() && tm.getTiles().size()>0)
		{
			for(int i=0;i<tm.getTiles().size();i++)
			{
				if(tm.getTiles().get(i)!=null){tm.getTiles().get(i).mouseMoved(e);}
			}

		}
	}
	public void keyPressed(KeyEvent e) 
	{
		if(e.getKeyCode()==KeyEvent.VK_CONTROL){tm.setCtrl(true);}
		if(verticalScrollbar !=null){verticalScrollbar.keyPressed(e);}
		if(horizontalScrollbar !=null){horizontalScrollbar.keyPressed(e);}
	}
	public void keyReleased(KeyEvent e) 
	{
		if(e.getKeyCode()==KeyEvent.VK_CONTROL){tm.setCtrl(false);}
		if(verticalScrollbar !=null){verticalScrollbar.keyReleased(e);}
		if(horizontalScrollbar !=null){horizontalScrollbar.keyReleased(e);}
	}
	public void keyTyped(KeyEvent e) 
	{
		if(verticalScrollbar !=null){verticalScrollbar.keyTyped(e);}
		if(horizontalScrollbar !=null){horizontalScrollbar.keyTyped(e);}
	}
}