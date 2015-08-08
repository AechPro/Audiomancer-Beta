package openGLTests.main;

public class FanTile
{
	private Animation anim;
	public FanTile() 
	{
		anim = new Animation(32,32, "Resources/Textures/Tiles/Fan/fan_", 8, -1, 40, 0);
	}
	public void render(int x, int y, int tmx, int tmy)
	{
		anim.render(x, y, false,false,true);
	}
}
