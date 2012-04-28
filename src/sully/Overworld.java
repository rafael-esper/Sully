package sully;

import static core.Script.*;
import static sully.Flags.*;
import static sully.Sully.*;

import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Weather.*;
import static sully.vc.v1_rpg.V1_Music.*;


public class Overworld {
	
	public static void start()
	{
		SaveEnable(); //can save on the overworld.
	
		//if galfrey's joined, the bridge is fixed!
		if( flags[F_HECK_GALFREY_JOIN] !=0 )
		{
			settile( 22,26,0,272 );
			setobs( 22,26,0 );
		}
		
		SetWeather(WEATHER_CLOUDS);
		InitMap();
			
		if( flags[F_RODNE_FLASHBACK] !=0 )
		{
			V1_StartMusic("res/music/VANGELIS.MOD");
		}
		else
		{
			V1_StartMusic( "res/music/aurora.mod" );
		}
	}
	
	public static void undersea()
	{
	 	V1_MapSwitch("undersea.map",13,20,TBLACK);
	}
	
	public static void cottage()
	{
		if (entity.get(player).face == 2) // was playerent
		{
			V1_MapSwitch("cottage.map", 41, 2, TBLACK);
		}
		else
		{
			V1_MapSwitch("cottage.map", 23, 49, TBLACK);
		}
	}
	
	public static void mountain()
	{
		V1_MapSwitch("mountain.map", 3, 50, TBLACK);
	}
	
	public static void chasm_west()
	{
		V1_MapSwitch("chasm.map", 4, 1, TBLACK);
	}
	
	public static void chasm_east()
	{
		V1_MapSwitch("chasm.map", 97, 40, TBLACK);
	}
	
	public static void bumsville()
	{
		V1_MapSwitch("bumville.map", 62, 65, TBLACK);
	}
	
	public static void rodne_main()
	{
		if( flags[F_RODNE_FLASHBACK] !=0 )
		{
			V1_MapSwitch("oldville.map",21,1,TBLACK);
			return; // Rafael: Because mapswitch doesn't interrupt the script execution anymore
		}
	
		V1_MapSwitch("village.map",21,1,TBLACK);
	}
	
	public static void rodne_forest()
	{
		V1_MapSwitch("village.map",98,5,TBLACK);
	}
	
	public static void loveshack()
	{
		V1_MapSwitch("shack.map",1,29,TBLACK);
	}
	
	public static void castle_heck()
	{
		V1_MapSwitch("castle.map",20,36,TBLACK);
	}
}