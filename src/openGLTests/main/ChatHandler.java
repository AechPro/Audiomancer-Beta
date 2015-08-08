package openGLTests.main;

import java.awt.Font;
import java.util.ArrayList;

import org.newdawn.slick.TrueTypeFont;

public class ChatHandler 
{
	private static int x,y;
	private ArrayList<String> chatLog;
	private ArrayList<String> bannedMessages;
	private int fontSize;
	private int msgLimit;
	private TrueTypeFont font;
	private Font awtFont;
	private int origX, origY;
	private long t1;
	
	public ChatHandler(int _x, int _y, int _fontSize, int messageLimit)
	{
		chatLog = new ArrayList<String>();
		bannedMessages=new ArrayList<String>();
		msgLimit=messageLimit;
		x=_x;
		y=_y;
		origX=x;
		origY=y;
		fontSize=_fontSize;
		
		awtFont= new Font("Times New Roman", Font.BOLD, fontSize);
		font = new TrueTypeFont(awtFont, false);
	}
	public void update()
	{
		if(chatLog.size()>=msgLimit || (System.nanoTime()-t1)/1000000000>=5)
		{
			chatLog.clear();
		}
	}
	public void render()
	{
		if(chatLog.size()<msgLimit && chatLog.size()>0)
		{
			y=origY;
			for(int i=chatLog.size()-1;i>0;i--)
			{
				font.drawString(x,y,chatLog.get(i));
				y-=fontSize;
			}
			font.drawString(x,y,chatLog.get(0));
		}
		
	}
	public void putText(String msg)
	{
		if(!bannedMessages.contains(msg))
		{
			chatLog.add(msg);
			t1 = System.nanoTime();
		}
		
	}
	public void singleMessage(String msg)
	{
		if(!chatLog.contains(msg))
		{
			putText(msg);
		}
	}
	public void finalMessage(String msg)
	{
		putText(msg);
		bannedMessages.add(msg);
	}
	public ArrayList<String> getChatLog(){return chatLog;}
}
