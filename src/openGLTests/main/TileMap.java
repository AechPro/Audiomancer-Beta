package openGLTests.main;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import openGLTests.main.pathing.*;

public class TileMap
{
	private int x,y;

	private ArrayList<Node> tiles;

	private int tileSize;

	private int mapWidth;
	private int mapHeight;
	private int tileCount;
	private int playerSpawnX;
	private int playerSpawnY;
	private int xDiffs,yDiffs;
	private ArrayList<Node> collisionTiles, fanTiles;
	private Semaphore[] sems;
	public TileMap (String s, int tileSize, Semaphore[] sem)
	{
		sems = sem;
		tiles = new ArrayList<Node>();
		collisionTiles = new ArrayList<Node>();
		fanTiles = new ArrayList<Node>();
		loadData("resources/testMap.txt");
		this.tileSize = tileSize;
		for(int i=0;i<tiles.size();i++)
		{
			if(!tiles.get(i).isWalkable()){collisionTiles.add(tiles.get(i));}
			//if(tiles.get(i).isFan()){fanTiles.add(tiles.get(i));}
			if(tiles.get(i).getID()==99)
			{
				playerSpawnX= (int)tiles.get(i).getX();
				playerSpawnY= (int)tiles.get(i).getY();
				System.out.println("("+playerSpawnX+","+playerSpawnY+")");
			}
		}
		tiles.get(0).setHome(true);
		tiles.get(tiles.size()-1).setDest(true);
	}

	public void loadData(String fileName)
	{
		
		System.out.println("loading");
		int x=0;
		int y=0;
		tiles = new ArrayList<Node>();
		try 
		{
			sems[1].acquire();
			BufferedReader s = new BufferedReader(new FileReader(fileName));
			mapWidth = Integer.parseInt(s.readLine());
			mapHeight = Integer.parseInt(s.readLine());
			y=mapHeight*32;
			tileCount = Integer.parseInt(s.readLine());
			String delimiters = " ";
			for(int i=0;i<mapHeight;i++)
			{
				for(int j=0;j<mapWidth;j++)
				{
					String raw = s.readLine();
					if(raw!=null)
					{
						String[] parsed = raw.split(delimiters);
						for(int k=0;k<parsed.length;k++)
						{
							int tileID=0;
							int backgroundTileID=-1;
							char[] chars = parsed[k].toCharArray();
							if(parsed[k].contains(","))
							{
								String[] newTileString = parsed[k].split(",");
								tileID=Integer.parseInt(newTileString[0]);
								backgroundTileID=Integer.parseInt(newTileString[1]);

								tiles.add(new Tile(x,y,"tile"+tileID,backgroundTileID));
								tiles.add(new Tile(x,y,"tile"+backgroundTileID,-1));
							}
							else if(chars.length>2 && Character.getNumericValue(chars[0])==1 && Character.getNumericValue(chars[1])==1)
							{
								Tile fan = new Tile(x,y,"tile"+11,backgroundTileID);
								String val = "";
								int index=3;
								for(int l=3,stop=chars.length;l<stop;l++,index++)
								{
									if(chars[l]=='-'){break;}
									else{val+=chars[l];}
								}
								fan.setVelocity(Integer.parseInt(val));
								val="";
								for(int l=index+1,stop=chars.length;l<stop;l++)
								{
									if(chars[l]=='}'){break;}
									else{val+=chars[l];}
								}
								fan.setFanHeight(Integer.parseInt(val));
								tiles.add(fan);
							}
							else
							{
								tileID=Integer.parseInt(parsed[k]);
								tiles.add(new Tile(x,y,"tile"+tileID,backgroundTileID));
							}
							x+=32;
						}
					}
					x=0;
					y-=32;
				}
			}
			s.close();
			sems[1].release();
		} 
		catch (Exception e){e.printStackTrace();}
		System.out.println("loading complete");
	}

	public int getXDiffs(){return xDiffs;}
	public int getYDiffs(){return yDiffs;}
	public int getTileSize()
	{
		return tileSize;
	}
	public int getX(){return x;}
	public int getY(){return y;}
	public void setY(int i){y=i;}
	public void setX(int i){x=i;}
	public int getHeight()
	{
		return(mapHeight*32)-tileSize*2;
	}
	public int getWidth(){return mapWidth;}
	public int getPlayerSpawnX(){return playerSpawnX;}
	public int getPlayerSpawnY(){return playerSpawnY;}
	public ArrayList<Node> getCollisionTiles(){return collisionTiles;}
	public ArrayList<Node> getFanTiles(){return fanTiles;}
	public ArrayList<Node> getTiles(){return tiles;}
	public Node getNode(int x, int y)
	{
		int val=32;
		for(int i=0,stop=tiles.size();i<stop;i++)
		{
			if(tiles.get(i).isPlayerWalkable())
			{
				if(tiles.get(i).getX()<x+val && tiles.get(i).getX()>x-val)
				{
					if(tiles.get(i).getY()==y)
					{
						return tiles.get(i);
					}
				}
			}
		}
		return null;
	}
	public void setStart(int x, int y)
	{
		try
		{
			sems[1].acquire();
			for(int i=0,stop=tiles.size();i<stop;i++)
			{
				tiles.get(i).setHome(false);
			}
			int val=32;
			for(int i=0,stop=tiles.size();i<stop;i++)
			{
				if(tiles.get(i).isPlayerWalkable())
				{
					//System.out.println("checking tile");
					if(tiles.get(i).getX()<x+val && tiles.get(i).getX()>x-val)
					{
						//System.out.println(tiles.get(i).getY()+"  "+y);
						if(tiles.get(i).getY()==y)
						{
							//System.out.println("setting tile at "+tiles.get(i).getX()+" "+tiles.get(i).getY()+" to home");
							tiles.get(i).setHome(true);
							break;
						}
					}
				}
			}
			sems[1].release();
		}
		catch(Exception e){}
	}
	public void setDest(int x, int y)
	{
		try
		{
			sems[1].acquire();
			for(int i=0,stop=tiles.size();i<stop;i++)
			{
				tiles.get(i).setDest(false);
			}
			int val=32;
			for(int i=0,stop=tiles.size();i<stop;i++)
			{
				if(tiles.get(i).isPlayerWalkable())
				{
					if(tiles.get(i).getX()<x+val && tiles.get(i).getX()>x-val)
					{
						if(tiles.get(i).getY()==y)
						{
							//System.out.println("setting tile at "+tiles.get(i).getX()+" "+tiles.get(i).getY()+" to dest");

							tiles.get(i).setDest(true);
							break;
						}
					}
				}
			}
			sems[1].release();
		}
		catch(Exception e){}
	}
	public void update(int x, int y)
	{
		try
		{
			sems[1].acquire();
			xDiffs=this.x-x;
			this.x=x;

			yDiffs=this.y-y;
			this.y=y;

			for(int i=0,stop=tiles.size();i<stop;i++)
			{
				tiles.get(i).update(xDiffs,yDiffs);
			}
			sems[1].release();
		} 
		catch (Exception e) {System.out.println("tile map isn't tired enough to sleep");}
	}
	public void render()
	{
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		for(int i=0,stop=tiles.size();i<stop;i++)
		{
			if(tiles.get(i).getY()+32<720+32 && tiles.get(i).getX()+32<1280+32 && tiles.get(i).getY()>-32 && tiles.get(i).getX()>-32)
			{
				if(!tiles.get(i).isPath())
				{
					tiles.get(i).render();
					if(tiles.get(i).isOverlayable())
					{
						indexes.add(i);
					}
				}
			}
		}
		for(int i=0;i<indexes.size();i++)
		{
			tiles.get(indexes.get(i)).render();
		}
	}


}