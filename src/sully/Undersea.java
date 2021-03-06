package sully;

import static core.Script.*;
import static sully.Flags.*;
import static sully.Sully.*;

import static sully.vc.Sfx.*;
import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Simpletype.*;
import static sully.vc.v1_rpg.V1_Maineffects.*;
import static sully.vc.v1_rpg.V1_Textbox.*;
import static sully.vc.v1_rpg.V1_Music.*;

// undersea.map
public class Undersea {
	
	public static void start()
	{
		Sully.SaveDisable(); //cannot save in dungeons.
		
		//if we're in the Cyclops' Hall, remove the seaweed so the dramatics are visible!
		if( flags[F_SEA_IN_CYCLOPS_HALL] != 0 )
		{
			current_map.setRenderstring("1,E,R");
		}
		
		//let's set the warp zone to the right tile based on it's flag
		switch ( flags[F_SEA_WARP] )
		{
			case 0: current_map.settile(10, 23, 0, 58); break; 
			case 1: current_map.settile(10, 23, 0, 468); current_map.settile(9, 24, 0, 482);break;
			case 2: current_map.settile(10, 23, 0, 474); current_map.settile(9, 24, 0, 54);break;
		}
		
		setdoor(); //set the door-tile to it's proper state
		
		//keep the pearl removed if it has been.
		if(flags[F_PROCURED_PEARL] !=0)	AlterBTile(47,1,424,2);
		
		//make sure the levels are set to their proper state
		if (flags[F_SEA_LEVER_1]!=0) current_map.settile(15, 14, 0, 54);
		if (flags[F_SEA_LEVER_2]!=0) current_map.settile(16, 12, 0, 54);
		if (flags[F_SEA_LEVER_3]!=0) current_map.settile(16, 16, 0, 54);
		
		//make sure any open chests stay open.
		if (flags[CHEST_UNDERSEA_A]!=0) current_map.settile(15, 6, 0, 35);	
		if (flags[CHEST_UNDERSEA_B]!=0) current_map.settile(31, 5, 0, 35);	
		if (flags[CHEST_UNDERSEA_C]!=0) current_map.settile(24, 23, 0, 35);
		
		
	
		//do all the map init stuff (defined in system.vc)
		InitMap();
		
		V1_StartMusic("res/music/MYSTWATR.S3M");
		
		//CHEAT! [Rafael, the Esper]
		/*lever();
		leverA();
		leverb();
		leverc();*/
	}
	
	//exit to Paradise Island
	public static void Entrance()
	{
		V1_MapSwitch("island.map", 33, 38, TBLACK);
	}
	
	//Bert the talking Stingray!
	public static void bert()
	{
		EntStart();
		
		if( flags[F_SEA_BERT] ==0 ) 
		{
			TextBox(T_MANTA,	"Hi there! My name is Bert Stingray",
								"and I'm here to tell you about the",
								"battle system!");
			
			TextBox(T_DARIN, "!!!", "", "");
			TextBox(T_DARIN, "There's a battle system now?!", "", "");
			
			TextBox(T_MANTA, "Nope!  And I'm here to tell you that!","","");
			
			TextBox(T_DARIN, "You know, I can only be teased so much", "before it stops being fun.", "");
			
			flags[F_SEA_BERT] = 1; //trip this conversation flag.
		}
		else
		{
			flags[F_SEA_BERT]++;
			
			TextBox(T_MANTA, 	"Still no fighty.","","");
			TextBox(T_MANTA, 	"Man, you've asked "+str(flags[F_SEA_BERT])+" times.",
								"Asking me more won't change this.", "");
		}
		
		EntFinish();
	}
	
	public static void chestA()
	{	
		if( OpenTreasure(CHEST_UNDERSEA_A, 15, 6, 35) )
		{
			FindMoney( 400 );
		}
	}
	
	public static void chestB()
	{
		if( OpenTreasure(CHEST_UNDERSEA_B, 31, 5, 35) )
		{
			FindItem( "Herb", 3 );
		}
	}
	
	public static void chestC()
	{
		if( OpenTreasure(CHEST_UNDERSEA_C, 24, 23, 35) )
		{
			FindItem( "Herb", 1 );
		}
	}
	
	public static void warpA()
	{
		SoundWarpZone();
		switch (flags[F_SEA_WARP])
		{
			case 0: Warp(26, 11, TBOX);break;
			case 1: Warp(33, 3,  TBOX);break;
			case 2: Warp(18, 28, TBOX);break;
		}
	}
	
	public static void warpB()
	{
		SoundWarpZone();
		Warp(9, 23, TBOX);
	}
	
	public static void lever()
	{
		SoundSwitch();
		switch (flags[F_SEA_WARP])
		{
			case 0: 
				flags[F_SEA_WARP] = 1;
				current_map.settile(10, 23, 0, 468);
				current_map.settile(9, 24, 0, 482);
				break;
			case 1:
			    flags[F_SEA_WARP] = 2;
			    current_map.settile(10, 23, 0, 474);
			    current_map.settile(9, 24, 0, 54);
			    break;
			case 2:
				flags[F_SEA_WARP] = 0;
				current_map.settile(10, 23, 0, 58); 
				current_map.settile(9, 24, 0, 55);
				break;
		}
	}
	
	//
	// Switch Lever A.  This is a once-only switch.
	public static void leverA()
	{
		if (current_map.gettile(15,14,0) == 54) //if lever's switched already, do nothing.
			return;
			
		current_map.settile(15, 14, 0, 54);
		flags[F_SEA_LEVER_1] = 1;	//set lever-1's flag to TRUE
		flags[F_SEA_LEVER_CNT]++;	//increment the lever count flag
		SoundSwitch();				//play a cute sound
		setdoor();					//call the door-checker!
	}
	
	//
	// Switch Lever B.  This is a once-only switch.
	public static void leverb()
	{
		if (current_map.gettile(16,12,0) == 54) //if lever's switched already, do nothing.
			return;
			
		current_map.settile(16, 12, 0, 54);
		flags[F_SEA_LEVER_2] = 1;	//set lever-2's flag to TRUE
		flags[F_SEA_LEVER_CNT]++;	//increment the lever count flag
		SoundSwitch();				//play a cute sound
		setdoor();					//call the door-checker!
	}
	
	//
	// Switch Lever C.  This is a once-only switch.
	public static void leverc()
	{
		if (current_map.gettile(16,16,0) == 54) //if lever's switched already, do nothing.
			return;
			
		current_map.settile(16, 16, 0, 54);
		flags[F_SEA_LEVER_3] = 1;	//set lever-3's flag to TRUE
		flags[F_SEA_LEVER_CNT]++;	//increment the lever count flag
		SoundSwitch();				//play a cute sound
		setdoor();					//call the door-checker!
	}
	
	
	//
	// Changes the sea-door's tile based on the 
	// lever-counting flag
	public static void setdoor()
	{
		switch (flags[F_SEA_LEVER_CNT])
		{
			case 1: current_map.settile(13,14,0,481);break;	// show the "1/3rd open" tile
			case 2: current_map.settile(13,14,0,480);break;	// show the "2/3rds open" tile
			case 3: current_map.settile(13,14,0,57);		// show the "all open" tile
					current_map.setobs(13, 14, 0);break;	// remove the obstruction now that it's open.
		}
	}
	
	//
	// exit to the overworld
	public static void overworld()
	{
		V1_MapSwitch("overworld.map", 4, 8, TBLACK);
	}
	
	// The exit in the secret hall to the lagoon area of Paradise Isle!
	public static void Exit_B()
	{
		flags[F_SEA_IN_CYCLOPS_HALL] = 0;
		V1_MapSwitch("island.map", 24, 16, TWHITE);
	}
	
	
	// Note, add some animation and dramatics here later.  
	//
	//
	public static void Pearl_of_Truth()
	{
		int save_vol = V1_GetCurVolume(); //save the current volume to restore later
		
		EntStart();
		
		if( flags[F_PROCURED_PEARL] == 0 )
		{
			TextBox( T_GALFREY,	"Well, this looks like the place; the ",
								"legendary resting grounds of the Cyclops'.", "");
								
			V1_FadeOutMusic( 100 ); //fade the music out.  There be drama!
	
			TextBox( T_CRYSTAL,	"In the ages past, they defended the land",
								"from evil.","");
	
	
			TextBox( T_SARA,	"Looks like this one has the",
								"[Pearl of Truth] stuck in its eye socket.", "");
	
	
			SoundSavePoint();
	
			AlterBTile(47,1,424,2);
	
			TextBox( T_SARA,	"Wee! It just pops right out.",
								"That was certainly easy.","");
								
			TextBox( T_DARIN,	"Yeah, a little *too* easy.","","");
			
			SoundFanfare();
			FindItem( "Pearl_of_Truth", 1 );
			Wait( 500 );
			
			TextBox( T_GALFREY,	"Aggh! My head! Too much...",
								"information... being crammed...",
								"into brain...");
			TextBox( T_CRYSTAL,	"Galfrey! What's the matter?","","");
			TextBox( T_GALFREY,	"The Pearl is filling my mind with insight...",
								"I can see it all so clearly now!", "");
			TextBox( T_GALFREY,	"The Pearl told me that Lord Stan is my",
								"true enemy!",
								"He's been using me!");
			TextBox( T_GALFREY,	"I thought he truly cared for me, but he's",
								"just been using me for bullying duty!", "");
			
			// Hahn!  We need heroic music for right here!  Sully has no "heroic resolve" theme! :o
			//
			
			TextBox( T_GALFREY,	"Darin, please let me join you to Castle",
								"Heck... We fight the same enemy.",
								"It's personal now!");
			TextBox( T_DARIN,	"Yes, Galfrey. You see the truth!",
								"Lord Stan has a big hunk of justice",
								"coming his way!");
			TextBox( T_GALFREY,	"Grr... and now I see it was he that locked",
								"me out of the castle gates.", "");
			TextBox( T_GALFREY,	"I knew I wouldn't be so dumb as to lock",
								"my keys in!","");
			TextBox( T_SARA,	"Woo hoo! It looks as though we will be",
								"able to save this land from tyranny after", 
								"all!");
			TextBox( T_CRYSTAL,	"Come with us, Galfrey.",
								"Now is the time when we will bring peace",
								"to this wonderful land!");
			TextBox( T_DARIN,	"Let's go, team!",
								"We shall conquer for the forces of",
								"goodness and light!");
								
			V1_FadeInMusic( 400, save_vol );//restore the volume
			
			setMusicVolume( save_vol );
			
			flags[F_PROCURED_PEARL]=1;
		}
		
		V1_SetCurVolume( save_vol );
		
		EntFinish();
	}
	
}