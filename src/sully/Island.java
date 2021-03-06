package sully;

import static core.Script.*;
import static sully.Flags.*;
import static sully.Sully.*;
import domain.VSound;

import static sully.vc.simpletype_rpg.Party.*;
import static sully.vc.v1_rpg.V1_RPG.*;
import static sully.vc.v1_rpg.V1_Maineffects.*;
import static sully.vc.v1_rpg.V1_Weather.*;
import static sully.vc.v1_rpg.V1_Textbox.*;
import static sully.vc.v1_rpg.V1_Music.*;
import static sully.vc.v1_menu.Menu_System.*;

// island.vc
public class Island {

	public static void start()
	{
		//hookretrace(""); //[Rafael, the Esper]
		Sully.SaveDisable();
		
		//if Crystal's already joined, we don't want to see the version of her that's
		// pre-built into the map...
		if ( flags[F_CRYS_JOIN]!=0 ) 
		{
			entity.get(0).setx(30000);
		}
		
		//entity.script[0] = "U2";
		
		//if Sully's shown the way
		if( flags[F_SULLY_OPEN_PEARL]!=0 )
		{
			AlterBTile(24,17,23,2);
		}
	
		// This is the code for the flashback sequence triggered in bumsville.
		if ( flags[F_BUM_NIGHT] !=0  )
		{
			do_flashback();
		}

		InitMap();
	
		V1_StartMusic( "res/music/MEDIOEVA.MOD" );
		Banner("Paradise Isle",300);
		
	}
	
	//
	// This plays a looping 'waves on the beach' sound for the flashback.
	public static void island_flashback_timer()
	{
	 	if (systemtime>wavetimer)
	 	{
	 		playsound(sfx_waves, 100);
	  		wavetimer=systemtime+680;
	 	}
	}
	
	// This is the first cutscene in the game, where crystal joins the party.
	// I'll do very explicit comments on this one to explain exactly what's 
	// going on here. (-Grue)
	public static void crystal_event()
	{
		//Whenever we start a sequence of events that we don't want the Menu 
		// accessible during, call MenuOff().  Otherwise, we could open a menu
		// while people are talking, and that's bad! :D
		EntStart();  // [Rafael, the Esper] MenuOff();
		
	    Wait(75);
		FadeOut(100);
		
		//fill the vclayer with black to serve as a black matte for the art cells
		FillVCLayer( RGB(0,0,0) );
		
		//put the art cel	 on the black matte.
		VCPutIMG( "res/images/cells/cryssand.gif", 158,20 );
		
		// At this point we haven't changed anything about the screen.
		// Now we want to fade in the vclayer we created (which is now an 
		// image of crystal on a black background that fills the screen).
		//
		// This function, FYI, is not a system function, but rather defined in
		// ./vc_lib/util/v1_maineffects.vc
		FadeInImg( 100, v1_vclayer );
		
	
		// Now we do the talking!
		TextBoxM(T_CRYSTAL,	"Darin.... Hello. How are you today? I",
		           			"needed to talk to you about something..." ,"");
		TextBox(T_CRYSTAL,	"I... I've been thinking and I've decided",
		            		"we need to leave Paradise Island.", "");
		TextBox(T_DARIN, 	"But Crystal! What's wrong? I kinda like",
		           			"this blue lagoon thing we have going on.","");
				
		TextBox(T_CRYSTAL,	"No... it has to be this way. We'll go",
		           			"insane if we don't have some adventure", 
		           			"soon!");
		TextBox(T_DARIN,	"I suppose you're right. I'd like to go",
		           			"exploring some caves and stuff.", "");
		
		
	
		//Fade out so we can switch from the vclayer to regular map-rendering
		// without anyone seeing the old switcheroo!
		FadeOut(50);
		
		//We're done with the vclayer for now, so let's wipe it clean
		ClearVCLayer();
		
		//now that the vclayer is clear, trigger the map-rendering.
		screen.render();
		FadeIn(30);
		TextBox(T_CRYSTAL,	"I'm glad you understand. I suppose we",
		           			"can use the undersea passage. Let's go!", "");
	
		
		EntFinish(); // [Rafael, the Esper]
		
		// let's move the on-map crystal 'onto' Darin.
		entitymove(0, "U1");
	
		// let's wait until the on-map crystal has stopped moving
		while (entity.get(0).movecode != 0)
			Wait(5);
			
		//now we add the in-party Crystal, which will appear right on top 
		//of darin...
		JoinParty("Crystal",1);
		
		//and we move the on-map crystal way off-screen.  We've successfully added
		//her to the party!
		entity.get(0).setx(30000);
		
		//let's save a flag that Crystal's joined.
		flags[F_CRYS_JOIN] = 1;
		
		//now turn on the ability to summon the menu, and we're done.
		// [Rafael, the Esper] MenuOff();
	}
	
	
	public static void sully() 
	{	
		EntStart();
		
		//if Galfrey hasn't joined yet, we default to the intro text.
		if( flags[F_HECK_GALFREY_JOIN]==0 )
		{
			sully_intro();
		}
		else
		{
			if(flags[F_SULLY_OPEN_PEARL]!=0)
			{
				TextBox( T_SULLY,	"Please leave me be.",
									"I'm just a silly little clam.",
									"I have no objects of value for you!" );
			}
			else
			{
	
				// If we got this far, we're doing the sully-scene!
				//
	
				TextBox( T_SULLY,	"Hi! My name is Sully Clam.",
									"I welcome you back to Paradise Isle.", "" );
				TextBox( T_GALFREY,	"Enough small talk, clam.",
									"Tell us where to find the [Pearl of Truth]",
									"before I rough you up!");
				TextBox( T_SARA,	"I thought Pearls came from oysters, not",
									"clams.","");
				TextBox( T_CRYSTAL,	"Sully, is there something you haven't told",
									"us?","");
				TextBox( T_SULLY,	"Alright, I give up! ",
									"The ancient race of cyclops once lived on",
									"this very island.");
				TextBox( T_SULLY,	"There's a sacred hall beneath here that",
									"holds the [Pearl of Truth].", "");
				TextBox( T_GALFREY,	"Grand! Show us the entrance or I'll teach",
									"you a whole new meaning of shellfish", 
									"abuse!");
				TextBox( T_SULLY,	"Okee dokee. Let me just try to remember",
									"the location...","");
	
				TextBox( T_GALFREY,	"Stop stalling, clam!",
									"I need to get back inside the Castle Heck",
									"right away!");
	
				AlterBTile(24,17,23,2);
	
				TextBox( T_SULLY,	"Oh, I remember! It's near the lagoon",
									"between the trees.",
									"I have revealed the entrance!");
				TextBox( T_DARIN,	"Thanks, Sully. We owe ya one!","","");
	
				flags[F_SULLY_OPEN_PEARL]=1;
			}
		}
		
		EntFinish();
	}
	
	//the introductory babble of Sully's before he has a use in the plot!
	public static void sully_intro()
	{	
		if( flags[F_SULLY_INTRO]==0 )
		{
			TextBox(T_SULLY,	"Hi.","","" );
			TextBox(T_DARIN,	"Yo.","","" );
			TextBox(T_SULLY,	"...","","" );
			TextBox(T_DARIN,	"...","","" );
			TextBox(T_SULLY,	"......","","" );
			TextBox(T_DARIN,	"......anything new?","","" );
			TextBox(T_SULLY,	"....","","" );
			TextBox(T_SULLY,	"......","...I have a speech portrait now.","" );
			TextBox(T_DARIN,	"'coo.","","" );
			AutoText(T_SULLY,	"Also, I guess there's a little matter of the entire world finally being reconstructed within verge 3, including fully functional menus, shops, load/save, working items, and various other goodnesses like well-documented prepackaged vc functions and Rysen's tutorial right in the docs subdirectory for anyone new to the system to read and enjoy!" );
			TextBox(T_DARIN,	"!!!","","" );
			TextBox(T_SULLY,	"But wait, there's more!","","" );
			AutoText(T_SULLY,	"Tatsumi has increased maped 3's capabilities, and vecna has added internet capabilities into verge!&  For some demonstrations of the new network abilities, go talk to Sancho over there.  He'd be the sad octopus." );
			TextBox(T_DARIN,	"...with all of these additions,","surely there must be...","" );
			TextBox(T_SULLY,	"No.","","" );
			TextBox(T_DARIN,	"No?","","" );
			TextBox(T_SULLY,	"Not yet.","","" );
			TextBox(T_DARIN,	"Aw man, I'm *never* gonna get to use","my sword.","" );
			
			//set the flag to 1 so this conversation doesn't get repeated...
			flags[F_SULLY_INTRO]++; 
		} 
		else if( flags[F_SULLY_INTRO] == 1 ) //the second time you talk to Sully...
		{
			
			TextBox(T_SULLY,	"We are NOT repeating that monolouge.","","");
			TextBox(T_DARIN,	"Agreed.","","");
			
			//set the flag to 1 so this conversation doesn't get repeated...
			flags[F_SULLY_INTRO]++; 
		} 
		else if( flags[F_SULLY_INTRO] == 2 ) //the third time you talk to Sully... 
		{		
			TextBox(T_DARIN,	"Isn't vecna the GREATEST?!! ## =)","","");
			TextBox(T_DARIN,	"{{ Hail to the vec {{","","");
			TextBox(T_DARIN,	"vec is a \\. ","He likes ^s","They make him go }, not"+chr(127) );
			
			flags[F_SULLY_INTRO]++; 
		}
		else //the fourth and every subsequent time you talk to Sully...
		{
			TextBox(T_SULLY,	"Seriously, check out Rysen's tutorial.","It's in the docs directory, and it's really","useful!");
		}
	}
	
	// Exit to the Undersea Passage.
	public static void Undersea()
	{
		V1_MapSwitch("undersea.map", 13, 2, TCIRCLE);
	}
	
	
	//the part of the Bumsville-inn flashback that takes place on Paradise Isle!
	public static void do_flashback()
	{
		stopmusic();
		MenuOff();
		SetWeather(WEATHER_NIGHT);
		sfx_waves=new VSound(load("res/sfx/water03.wav"));
		wavetimer = systemtime+680;
		hooktimer("island_flashback_timer");
	
		playsound(sfx_waves, 100);
		entity.get(0).setx(12*16);
		entity.get(0).sety(16);
		entity.get(0).face=1;
	
		InitMap();
	
		entity.get(1).face=1;
		FadeIn(200);
		Wait(300);
		TextBox(T_CRYSTAL,"It's so beautiful how the moonlight",
				  "reflects off the ocean, don't you think?","");
		TextBox(T_DARIN,"Yes, it is. But its beauty pales in",
				  "comparison to your sparkling eyes.", "");
		entity.get(0).face=3;
		TextBox(0,"Oh, Darin...","","");
		entity.get(1).face=4;
		TextBox(0,"Oh, Crystal...","","");
		Wait(200);
		entity.get(0).face=1;
		Wait(50);
		entity.get(1).face=1;
		Wait(100);
		TextBox(T_CRYSTAL,"I just wish this could go on forever,",
				  "don't you? I hope absolutely nothing",
				  "changes.");
		entity.get(1).face=4;
		TextBox(T_DARIN,"Don't worry, Crystal. I'll never let anyone",
				  "or anything disturb our happiness!","");
		entity.get(1).face=1;
		Wait(500);
		FadeOut(200);
		hooktimer("");	
		//FreeSound(sfx_waves);
	
		flags[F_BUM_NIGHT] = 2; //we set it to 2 so it doesn't trigger
								// this flashback anymore, yet the flag 
								// still counts as true!
	
		// we'll pass the love the the 
		// bumsville map autoexec function to continue the 
		// cutscene...  see you there!
		V1_MapSwitch("bumville.map", 23, 12, TNONE);	
	}
	
	public static void Pearl_Cave()
	{
		if( flags[F_SULLY_OPEN_PEARL]!=0 )
		{
			flags[F_SEA_IN_CYCLOPS_HALL] = 1;
			V1_MapSwitch("undersea.map",47,21,TWHITE);
		}	
	}
	
	// Sancho, the Sad Octopus, first born about 5 posts into this thread:
	// http://www.verge-rpg.com/boards/display_thread.php?id=18329
	//
	// He is now the guardian of all netcode demonstrations.  Good for him!
	public static void Sancho()
	{	
		EntStart();
		
		int answer, responses = 0;
		String n1,n2,n3;
		
		TextBoxM( T_SANCHO, "I'm a sad, sad octopus.","","" );
		
		if( flags[F_SANCHO_MENU] !=0 )	
		{
			answer = Prompt( T_SANCHO, "Those are all my tricks.","Want to see one again?","", "Bug report&Tagboard&inet conversation&Could you sigh?&nothing now, thanks&" );
			
			if( answer < 3 )
			{
				flags[F_SANCHO] = answer;
			}
			else
			{
				flags[F_SANCHO] = 3;
				
				if( answer == 3 )
				{
					TextBoxM( T_SANCHO, "*sigh*","","");
				}
			}
		}
		
		if( flags[F_SANCHO]==0 )
		{	
			TextBoxM( T_SANCHO, "When McGrue gets around to it, ","I'll let you post bugreports","right to the website.");
			
			answer = Prompt( T_SANCHO, "Would you like that?","","", "Yes&No&Maybe?&" );
	
			if( answer == 0 ) 
			{
				TextBox( T_SANCHO, "Well, I'm glad you're interested.  I guess.","","" ); 
			} 
			else if( answer == 1 ) 
			{
				TextBox( T_SANCHO, "My life is a constant downward spiral of","pain.","" );
			} 
			else 
			{
				TextBox( T_SANCHO, "I see.","","" );
			}
	
			flags[F_SANCHO] = 1;
		}
		else if( flags[F_SANCHO]!=0 )
		{
			TextBox( T_SANCHO, "Instead of anything useful,","I have the power to communicate with the","world of the gods.");
			TextBoxM( T_DARIN, "!!!", "","");
			TextBox(T_DARIN, "After all these years, vecna has finally","gifted us with magic!","" );
			TextBoxM( T_SANCHO, "...not quite.","He has given you internet connectivity,","though...");
			answer = Prompt( T_SANCHO, "Would you like to hear the words of the","gods?","(requires internet connection)", "Yes&No&Maybe?&" );
			
			if( answer== 0 )
			{
				TextBoxM( T_SANCHO, "...Are you really sure?","They aren't that interesting.","" );
				TextBoxM( T_SANCHO, "Honestly, they probably have contests","to see how many fart jokes they can","fit into 255 characters." );
				answer = Prompt( T_SANCHO, "Do you really want to hear their words?","","", "Yes&No&Maybe?&" );
				
				if( answer== 0 )
				{
					TextBox( T_SANCHO, "*sigh*","...don't say I didn't warn you. Here's three,","randomly selected trite quotes:");
					
					n1 = ""; //[Rafael, the Esper] Ignored now GetTag( "" );
					n2 = ""; //[Rafael, the Esper] Ignored now GetTag( n1 );
					n3 = ""; //[Rafael, the Esper] Ignored now GetTag( n1+","+n2 );
					
					if( check3(n1,n2,n3, "vecna" ) )
					{
						TextBox( T_DARIN, "Holy wow!","That was *the* vecna!","I'll never wash these ears again!");
						TextBox( T_SANCHO, "I'm happy for you, really.","","");
						responses++;
					}
					
					if( check3(n1,n2,n3, "hahn" ) )
					{
						TextBox( T_DARIN, "I'm faint! That was *Hahn*!","He created the earth and sky around us","with his bare hands!" );
						TextBox( T_SANCHO, "I wouldn't be so excited, really.","I mean, have you seen the trees?","");
						responses++;
					}
					
					if( check3(n1,n2,n3, "mcgrue" ) )
					{
						TextBox( T_DARIN, "McGrue?  I don't remember him from","sunday school...","" );
						TextBox( T_SANCHO, "Your church doesn't seem to have very","high standards.","");
						TextBox( T_DARIN, "Yeah.","","" );
						responses++;
					}
					
					if( check3(n1,n2,n3, "zip" ) )
					{
						TextBox( T_DARIN, "Wowwie Kabowie!","That was Zip, god of Menus and Aggressive","Helpfulness!" );
						TextBox( T_SANCHO, "Had he not made me so very, very sad,","I would be touched to have seen my","father.");
						TextBox( T_DARIN, "How sad!","","");
						TextBox( T_SANCHO, "Yes.","","");
						responses++;
					}
					
					if( check3(n1,n2,n3, "Kildorf" ) )
					{
						TextBox( T_DARIN, "Ooh, Kildorf.  He was a lesser god","with dominion over the 'camera'.","" );
						TextBox( T_SANCHO, "Do you know what a camera is?","","");
						TextBox( T_DARIN, "Some sort of dangerous beast.","Kildorf was ever-vigilant in tracking it!","" );
						
						if( CharInParty("Sara") )
						{
							TextBox( T_SARA, "Do you ever get tired of playing the","straight man here, Darin?","" );
							TextBox( T_DARIN, "Nope!","","" );
						}
						
						responses++;
					}
					
					if( check3(n1,n2,n3, "Gayo") )
					{
						TextBox( T_DARIN, "That was Gayo, god of war and art!","...he studied backward under Sun Tzu.","" );
						TextBox( T_SANCHO, "'Gayo', eh?  Nice name.","","");
						TextBox( T_DARIN, "It's pronounced 'Guy-o'.","","");
						TextBox( T_SANCHO, "...sure.  Sure it is.","","");
						
						responses++;
					}
					
					if( responses != 0 )
					{
						Wait( 150 );
						TextBox( T_SANCHO, "You know, usually you wait until the end of ","a game for the credits..","");	
						TextBox( T_DARIN, "Huh?","","");
						TextBox( T_SANCHO, "Nevermind.","","");
					}
					else
					{
						TextBox( T_DARIN, "Wow!","Never before have I felt so alive!","Thank you mister magical octopus!");
						TextBox( T_SANCHO, "...sure.","","");
					}
					
					if( flags[F_SANCHO_EXPLAIN] == 0 ) 
					{					
						TextBox( T_SANCHO, "Anyway, if you want to leave a","message for everyone to read yourself,","You can." );
						TextBox( T_DARIN, "Really?!  How?!","","");
						AutoText( T_SANCHO, "Somewhere in the world is a place you can do it. However, you still require an internet Connection, and a valid VERGE-RPG.COM username and password.  On top of that, you're limited to only 255 characters, and can only do it once ever.  And on top of *that*, the gods have to approve your message, which isn't likely since they're all lazy jerks.");
						TextBox( T_DARIN, "Oh boy, a sidequest!","","");
						TextBox( T_SANCHO, "You don't listen very well, do you?","","");
						
						flags[F_SANCHO_EXPLAIN] = 1;
					}
					else
					{
						AutoText( T_SANCHO, "Remember, you can leave your own message on the tagboard somewhere within the game.  Have fun." );
					}
				}
				else if( answer == 1 )
				{
					TextBox( T_SANCHO, "A wise choice.","My respect for you is raised by a","nearly unmeasurable amount.");
				}
				else
				{
					TextBox( T_SANCHO, "I see..","","");
				}
			}
			else if( answer == 1 )
			{
				TextBox( T_DARIN, "I dare not look upon their divinity!","I mean... what if it overwhelmed me and","melted off my face?!" );
			}
			else
			{
				TextBox( T_SANCHO, "I see..","","");
			}
				
			flags[F_SANCHO]++;
		}
		else if( flags[F_SANCHO] == 2 )
		{
			
			TextBoxM( T_SANCHO, "As another demonstration of godly","prowess, McGrue made a stupid little","library that pulls conversations from");
			TextBoxM( T_SANCHO, "plaintext files on the internet.","Would you like to see?", "(requires internet connection)");
			
			answer = Prompt( T_SANCHO, "There are two demo files to play:","http://verge-rpg.com/sully.txt","http://verge-rpg.com/song.txt", "Sully.txt&Song.txt&Neither, thanks.&" );
			
			if( answer== 0 )
			{
				//[Rafael, the Esper] Ignored now DoInternetConversation( "http://verge-rpg.com/sully.txt" );
			}
			else if( answer == 1 )
			{
				//[Rafael, the Esper] Ignored now DoInternetConversation( "http://verge-rpg.com/song.txt" );
			}
			else
			{
				TextBox( T_SANCHO, "You chose wisely.","","");
			}
			
			Wait(50);
			
			flags[F_SANCHO_MENU ] = 1;
		}
		
		EntFinish();
	}
	
	static boolean check3( String s1, String s2, String s3, String comp )
	{
		if( s1.equals(comp) || s2.equals(comp) || s3.equals(comp) )
			return true;
		return false;
	}
	
}	
