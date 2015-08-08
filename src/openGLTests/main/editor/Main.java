package openGLTests.main.editor;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.*;
public class Main extends JPanel implements Runnable, KeyListener
{
	private static final long serialVersionUID = -2193874754023463406L;
	private static final int width=1280;
	private static final int height=720;
	private int mapWidth;
	private int mapHeight;
	private static boolean running;
	private Thread thread;
	private BufferedImage image;
	private Graphics2D g;
	private TAdapter inputListener;
	private MasterController mc;
	private boolean loadSpaces=false;
	private boolean ctrlPressed=false;
	private boolean requestLoad=false;
	private boolean requestSave=false;
	private boolean updating = true;
	private long startTime=0;
	private int frameCount=0;
	private long endTime=0;
	private int fps=0;
	public Main()
	{
		super();
		this.setFocusable(true);
		addKeyListener(this);
		requestFocus();
	}
	public void addNotify()
	{
		super.addNotify();
		
		if(thread==null)
		{
			thread = new Thread(this);
			thread.start();
		}
	}
	public void init()
	{
		image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		g = (Graphics2D) image.getGraphics();
		mc = new MasterController(width, height, mapWidth, mapHeight);
		mc.init();
		inputListener = new TAdapter();
		addMouseMotionListener(inputListener);
		addMouseListener(inputListener);
		running=true;
	}
	public void run() 
	{
		getInitParams();
		init();
		startTime=System.nanoTime();
		while(running)
		{
			update();
			render();
			draw();
			frameCount++;
			endTime=System.nanoTime();
			if((endTime-startTime)/1000000000>=1)
			{
				fps = frameCount;
				frameCount=0;
				startTime=System.nanoTime();
			}
		}
	}
	public void update()
	{
		if(requestLoad)
		{
			mc.setLoading(true,loadSpaces);
			if(!mc.isLoading()){requestLoad=false;}
		}
		else if(requestSave)
		{
			mc.setSaving(true);
			if(!mc.isSaving()){requestSave=false;}
		}
		else
		{
			updating=true;
			mc.update();
			updating=false;
		}
	}
	public void render()
	{
		clearCanvas();
		mc.render(g);
		g.setColor(Color.GREEN);
		g.setFont(new Font("Italic", Font.PLAIN, 40));
		g.drawString("frame rate: "+fps, 570,63);
	}
	public void draw()
	{
		Graphics g2 = getGraphics();
		g2.drawImage(image,0,0,null);
		g2.dispose();
	}
	
	public void getInitParams()
	{
		InputManager x = new InputManager("Input width:");
		mapWidth = x.getInput();
		x.dispose();
		x = new InputManager("Input height:");
		mapHeight = x.getInput();
		x.dispose();
	}
	public void keyPressed(KeyEvent e) 
	{
		if(mc!=null){mc.keyPressed(e);}
		if(e.getKeyCode()==KeyEvent.VK_CONTROL)
		{
			ctrlPressed=true;
		}
		int counter=0;
		if(e.getKeyCode()==KeyEvent.VK_S && counter==0)
		{
			requestSave=true;
			counter++;
		}
		counter=0;
		if(e.getKeyCode()==KeyEvent.VK_L && counter==0 && !ctrlPressed && !updating)
		{
			loadSpaces=false;
			requestLoad=true;
			counter++;
		}
		else if(e.getKeyCode()==KeyEvent.VK_L && counter==0 && ctrlPressed && !updating)
		{
			loadSpaces=true;
			requestLoad=true;
			counter++;
		}
		
		if(e.getKeyCode()==KeyEvent.VK_SHIFT && mc!=null){mc.setDragging(true);}
	}
	public void keyReleased(KeyEvent e) 
	{
		if(mc!=null){mc.keyReleased(e);}
		if(e.getKeyCode()==KeyEvent.VK_CONTROL)
		{
			ctrlPressed=false;
		}
		if(e.getKeyCode()==KeyEvent.VK_SHIFT){mc.setDragging(false);}
	}
	public void keyTyped(KeyEvent e) {mc.keyTyped(e);}
	public void clearCanvas()
	{
		g.clearRect(0, 0, width, height);
		g.setColor(Color.WHITE);
		g.fillRect(0,0,width,height);
	}
	public void delay(int millis)
	{
		try{Thread.sleep(millis);}
		catch(Exception e){e.printStackTrace();}
	}
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Main");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new Main());
		frame.setSize(width+16,height+38);
		frame.setVisible(true);
	}
	class TAdapter extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e){if(mc!=null){mc.mouseClicked(e);}}
		public void mousePressed(MouseEvent e){if(mc!=null){mc.mousePressed(e);}}
		public void mouseReleased(MouseEvent e){if(mc!=null){mc.mouseReleased(e);}}
		public void mouseDragged(MouseEvent e)
		{
			if(mc!=null){mc.mouseDragged(e);}
		}
		public void mouseExited(MouseEvent e){}
		public void mouseMoved(MouseEvent e)
		{
			if(mc!=null){mc.mouseMoved(e);}
		}	
	}
}