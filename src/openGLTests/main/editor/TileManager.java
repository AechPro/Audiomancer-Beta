package openGLTests.main.editor;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class TileManager
{
	private boolean saving;
	private boolean loading;
	private boolean loadSpaces;
	private boolean ctrl;
	private int availTiles;
	private int mapWidth;
	private int mapHeight;
	private int curTile;
	private boolean dragging;
	private int prevX;
	private int prevY;
	private ArrayList<Tile> tiles;
	private boolean updateScrollbars;
	private int maxTiles;
	private int maxX;
	private int maxY;
	private boolean tileSelected;
	private int botMap,rightMap;
	private int initX, initY;
	public TileManager(int width, int height)
	{
		mapWidth=width;
		mapHeight=height;
		availTiles=12;


		initX=192;
		initY=32;

		maxX = (mapWidth*32)+initX;
		maxY = (mapHeight*32)+initY;

		tiles = new ArrayList<Tile>();
		maxTiles = (mapWidth*mapHeight)+1;
		botMap = mapHeight*32;
		rightMap = mapWidth*32;
	}
	public void checkRequests(String fileName)
	{
		if(saving && tiles.size()>0)
		{
			createFile(fileName); 
			saveData(fileName,tiles);
		}
		else if(loading){loadData(fileName); updateScrollbars = true;}
	}

	public void update()
	{
		if(curTile>=tiles.size()){curTile=0;}
		//----------check for removed tiles----------\\
		for(int i=0,stop=tiles.size();i<stop;i++)
		{
			if(tiles.size()>i)
			{
				if(tiles.get(i).isRemoved())
				{
					if(tiles.size()>1)
					{
						tiles.remove(i);
						if(curTile==i || curTile>=tiles.size())
						{
							curTile=0;
						}
					}
				}
			}
		}

		//----------update tiles----------\\
		boolean searching=true;
		for(int i=0,stop=tiles.size();i<stop;i++)
		{
			tiles.get(i).update();
			if(tiles.get(i).getY()+32<720-initY+1 && tiles.get(i).getX()+32<maxX+32 && tiles.get(i).getY()>initY-1 && tiles.get(i).getX()>initX-1)
			{

				tiles.get(i).setHighlightable(true);
				if(tiles.get(i).isClicked() && tiles.get(i).isFan() && ctrl)
				{
					tiles.get(i).openMenu(); 
					if(tiles.get(i).receivedVals())
					{
						ctrl=false;
					}
				}
				if(tiles.get(i).isClicked() && searching)
				{
					curTile=i;
					tileSelected=true;
					searching=false;
				}
			}
		}

		//----------check for clicked tile----------\\
		for(int i=0,stop = tiles.size();i<stop;i++)
		{
			if(tiles.get(i).getY()+32<720-initY+1 && tiles.get(i).getX()+32<maxX+32 && tiles.get(i).getY()>initY-1 && tiles.get(i).getX()>initX-1)
			{
				if(i!=curTile && tiles.get(curTile).isClicked())
				{
					tiles.get(i).setSelectable(false);
					if(tiles.get(i).isClicked())
					{
						tiles.get(i).setClicked(false);
					}
				}
			}
		}

		//----------dragging handler----------\\
		if(tiles.get(curTile).isClicked() && dragging)
		{
			int tileX = (int)tiles.get(curTile).getX();
			int tileY = (int)tiles.get(curTile).getY();
			String tileURL = tiles.get(curTile).getURL();
			int backgroundTileID=-1;
			if(tileX != prevX || tileY != prevY)
			{
				boolean canPlace=true;
				for(int i=0,stop=tiles.size();i<stop;i++)
				{
					if(tiles.get(i).getY()+32<720-initY+1 && tiles.get(i).getX()+32<maxX+32 && tiles.get(i).getY()>initY-1 && tiles.get(i).getX()>initX-1)
					{
						if(i != curTile)
						{
							if(tiles.get(curTile).isOverlayable() && tiles.get(i).isOverlayable() && tileX==tiles.get(i).getX() && tileY==tiles.get(i).getY()){canPlace = false;}
							else if(!tiles.get(curTile).isOverlayable() && tileX==tiles.get(i).getX() && tileY==tiles.get(i).getY()){canPlace = false;}
							else if(tiles.get(curTile).isOverlayable()&& tileX==tiles.get(i).getX() && tileY==tiles.get(i).getY())
							{
								backgroundTileID=tiles.get(i).getID();
							}
						}
					}
				}
				if(canPlace && tiles.size()+1<maxTiles)
				{
					tiles.add(new Tile(tileX,tileY,tileURL,backgroundTileID, maxX, maxY));
					tiles.get(tiles.size()-1).setSelectable(false);
					tiles.get(tiles.size()-1).setClicked(false);
					prevX=tileX;
					prevY=tileY;
				}
			}
		}

		//----------check to make sure placed tiles are in proper locations----------\\
		if(tiles.get(curTile).isPlaced())
		{
			boolean collisionCheck=true;
			for(int i=0,stop=tiles.size();i<stop;i++)
			{
				if(i!=curTile)
				{
					if(tiles.get(curTile).isColliding(tiles.get(i)) && !tiles.get(curTile).isOverlayable())
					{
						//System.out.println("failed check 1");
						collisionCheck=false;
						tiles.get(curTile).setPlaced(false);
						tiles.get(curTile).setClicked(true);
						tiles.get(curTile).setColliding(true);
					}
					else if(tiles.get(curTile).isColliding(tiles.get(i)) && tiles.get(curTile).isOverlayable())
					{
						if(tiles.get(i).isOverlayable())
						{
							collisionCheck=false;
							tiles.get(curTile).setPlaced(false);
							tiles.get(curTile).setClicked(true);
							tiles.get(curTile).setColliding(true);
						}
						else
						{
							tiles.get(curTile).setBackgroundTileID(tiles.get(i).getID());
							tileSelected=false;
						}
					}
					else
					{
						//System.out.println("failed check 2");
					}
				}

				if(tiles.get(curTile).getX()<initX-1)
				{
					collisionCheck=false;
					tiles.get(curTile).setPlaced(false);
					tiles.get(curTile).setClicked(true);
					tiles.get(curTile).setColliding(true);
				}
			}
			if(collisionCheck)
			{
				tileSelected=false;
			}
		}
		for(int i=0,stop=tiles.size();i<stop;i++)
		{
			if(tiles.get(i).getY()+32<720-initY+1 && tiles.get(i).getX()+32<maxX+32 && tiles.get(i).getY()>initY-1 && tiles.get(i).getX()>initX-1 && !tileSelected)
			{
				tiles.get(i).setSelectable(true);
				tiles.get(i).setColliding(false);
			}
			else if(!tileSelected){tiles.get(i).setSelectable(false);}
		}

		//----------end----------\\
	}
	public void render(Graphics2D g)
	{
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		for(int i=0,stop=tiles.size();i<stop;i++)
		{
			if(tiles.get(i).getY()+32<720+32 && tiles.get(i).getX()+32<1280+32 && tiles.get(i).getY()>0 && tiles.get(i).getX()>initX-1)
			{
				if(i!=curTile){tiles.get(i).render(g);}
				if(tiles.get(i).isOverlayable())
				{
					indexes.add(i);
				}
			}
		}
		for(int i=0;i<indexes.size();i++)
		{
			tiles.get(indexes.get(i)).render(g);
		}
		if(tiles.get(curTile).getY()+32<720+32 && tiles.get(curTile).getX()+32<1280+32 && tiles.get(curTile).getY()>0 && tiles.get(curTile).getX()>-10)
		{
			tiles.get(curTile).render(g);
		}
	}
	public void createFile(String fileName)
	{
		try 
		{
			System.out.println("creating file");
			PrintWriter writer = new PrintWriter("resources/Editor/levels/maps/"+fileName, "UTF-8");
			//PrintWriter writer = new PrintWriter("bin/openGLTests/main/editor/resources/Editor/levels/maps/"+fileName, "UTF-8");
			writer.close();
			System.out.println("file created successfully");
		}
		catch (FileNotFoundException | UnsupportedEncodingException e1) {System.out.println("failed to create file");}
	}
	public void saveData(String fileName, ArrayList<Tile> tiles)
	{
		System.out.println("saving data");
		int x=0;
		int y=0;
		int lowestY=(int)tiles.get(0).getY();
		int lowestX=(int)tiles.get(0).getX();
		for(int i=0,stop=tiles.size();i<stop;i++)
		{
			if((int)tiles.get(i).getX()<lowestX){lowestX=(int)tiles.get(i).getX();}
			if((int)tiles.get(i).getY()<lowestY){lowestY=(int)tiles.get(i).getY();}
		}
		x=lowestX;
		y=lowestY;
		try 
		{
			boolean found=false;
			BufferedWriter s = new BufferedWriter(new FileWriter("resources/Editor/levels/maps/"+fileName));
			//BufferedWriter s = new BufferedWriter(new FileWriter("bin/openGLTests/main/editor/resources/Editor/levels/maps/"+fileName));
			s.write(""+mapWidth);
			s.newLine();
			s.write(""+mapHeight);
			s.newLine();
			s.write(""+availTiles);
			s.newLine();
			for(int i=0;i<mapHeight;i++)
			{
				for(int j=0;j<mapWidth;j++)
				{
					found=false;
					int tempIndex=0;
					boolean overlayed=false;
					for(int k=0;k<tiles.size();k++)
					{
						if(x==(int)tiles.get(k).getX() && y==(int)tiles.get(k).getY())
						{
							found=true;
							tempIndex=k;
						}
					}
					for(int k=0;k<tiles.size();k++)
					{
						if(k!=tempIndex && found && x==(int)tiles.get(k).getX() && y==(int)tiles.get(k).getY())
						{
							if(tiles.get(tempIndex).isOverlayable() && tiles.get(tempIndex).getBackgroundTileID()!=-1)
							{
								s.write(""+tiles.get(tempIndex).getID()+","+tiles.get(tempIndex).getBackgroundTileID());
								s.write(" ");
								overlayed=true;
							}
							else if (tiles.get(k).isOverlayable() && tiles.get(k).getBackgroundTileID()!=-1)
							{
								s.write(""+tiles.get(k).getID()+","+tiles.get(k).getBackgroundTileID());
								s.write(" ");
								overlayed=true;
							}
						}
					}
					if(!found)
					{
						s.write(""+0);
						s.write(" ");
					}
					if(found && tiles.get(tempIndex).getID()==11 && !overlayed)
					{
						s.write(""+tiles.get(tempIndex).getID()+"{"+tiles.get(tempIndex).getVelocity()+"-"+tiles.get(tempIndex).getFanHeight()+"}");
						s.write(" ");
					}
					else if(found && !overlayed)
					{
						s.write(""+tiles.get(tempIndex).getID());
						s.write(" ");
					}

					x+=32;
				}
				x=lowestX;
				s.newLine();
				y+=32;
			}
			s.close();
			System.out.println("data saved successfully");
		}
		catch (IOException e) 
		{
			System.out.println("unable to write data to file");
		}
		saving=false;
	}
	public void loadData(String fileName)
	{
		int x=initX;
		int y=initY;
		tiles = new ArrayList<Tile>();
		try 
		{

			BufferedReader s = new BufferedReader(new FileReader("resources/Editor/levels/maps/"+fileName));
			System.out.println(fileName);
			//BufferedReader s = new BufferedReader(new FileReader("bin/openGLTests/main/editor/resources/Editor/levels/maps/"+fileName));
			mapWidth = Integer.parseInt(s.readLine());
			mapHeight = Integer.parseInt(s.readLine());
			availTiles = Integer.parseInt(s.readLine());
			maxTiles = (mapWidth*mapHeight)+1;
			maxX = (mapWidth*32)+initX;
			maxY = (mapHeight*32)+initY;
			botMap = mapHeight*32;
			rightMap=mapWidth*32;
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

							if(parsed[k].contains(","))
							{
								String[] newTileString = parsed[k].split(",");
								tileID=Integer.parseInt(newTileString[0]);
								backgroundTileID=Integer.parseInt(newTileString[1]);
								if(loadSpaces)
								{
									tiles.add(new Tile(x,y,"tile_"+tileID+".png",backgroundTileID, maxX, maxY));

									tiles.add(new Tile(x,y,"tile_"+backgroundTileID+".png",-1, maxX, maxY));
								}
								else
								{
									if(tileID!=0)
									{
										tiles.add(new Tile(x,y,"tile_"+tileID+".png",backgroundTileID, maxX, maxY));
										if(backgroundTileID!=0)
										{
											tiles.add(new Tile(x,y,"tile_"+backgroundTileID+".png",-1, maxX, maxY));
										}
									}
								}
							}
							else
							{
								char[] chars = parsed[k].toCharArray();
								if(chars.length>2 && Character.getNumericValue(chars[0])==1 && Character.getNumericValue(chars[1])==1)
								{
									Tile fan = new Tile(x,y,"tile_"+11+".png",backgroundTileID, maxX, maxY);
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

								else if(loadSpaces)
								{
									tileID=Integer.parseInt(parsed[k]);
									tiles.add(new Tile(x,y,"tile_"+tileID+".png",backgroundTileID, maxX, maxY));
								}
								else
								{
									tileID=Integer.parseInt(parsed[k]);
									if(tileID!=0)
									{
										tiles.add(new Tile(x,y,"tile_"+tileID+".png",backgroundTileID, maxX, maxY));
									}
								}
							}
							x+=32;
						}
					}
					x=initX;
					y+=32;
				}
			}
			s.close();
		} 
		catch (Exception e){e.printStackTrace();}
		loading=false;
	}
	public void updateHoriz(int x)
	{
		for(int i=0,stop=tiles.size();i<stop;i++)
		{
			tiles.get(i).setHighlightable(false);
			tiles.get(i).setX((int)tiles.get(i).getOrigX()-x);
		}
	}
	public void updateVert(int y)
	{
		for(int i=0,stop=tiles.size();i<stop;i++)
		{
			tiles.get(i).setHighlightable(false);
			tiles.get(i).setY((int)tiles.get(i).getOrigY()-y);
		}
	}

	public void addTile(int ID, int _x, int _y)
	{
		boolean found=false;
		int x=0;
		int y=0;
		//	System.out.println(tiles.size());
		if(!tileSelected && tiles.size()<maxTiles)
		{
			for(int i=0;i<tiles.size();i++)
			{
				if(tiles.get(i).getY()>y && !found)
				{
					y=(int)tiles.get(i).getY();
					x=(int)tiles.get(i).getX();
					found=true;
				}
			}
			Tile tile = new Tile(x,y,"tile_"+(ID)+".png",-1, maxX, maxY);
			tile.setClicked(true);
			tileSelected=true;
			tiles.add(tile);
			curTile = tiles.size()-1;
		}
	}

	public ArrayList<Tile> getTiles(){return tiles;}
	public void setLoading(boolean i, boolean spaces){loading=i; loadSpaces=spaces;}
	public void setSaving(boolean i){saving=i;}
	public void setDragging(boolean i){dragging=i;}
	public boolean getDragging(){return dragging;}
	public boolean isTileSelected(){return tileSelected;}
	public boolean getScrollbarUpdate(){return updateScrollbars;}
	public void setScrollbarUpdate(boolean i){updateScrollbars = i;}
	public int getMapHeight(){return mapHeight;}
	public int getMapWidth(){return mapWidth;}
	public void setCtrl(boolean i){ctrl=i;}
}