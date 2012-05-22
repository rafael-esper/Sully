package sully.vc.v1_rpg;

import static core.Script.*;
import domain.VImage;

import java.awt.Color;


/***************************************************************************
*                                                                          *
*                    V1RPG VergeC Library for Verge3                       *
*                        Copyright (C)2004 vecna                           *
*                                                                          *
***************************************************************************/
public class V1_Maineffects {

	/********************************** data **********************************/
	
	static VImage crossfade_img = screen.duplicateimage();
	static VImage alpha_img = screen.duplicateimage();
	static VImage wipe_img = new VImage(load("res/system/wipe2.gif"));
	
	/********************************** code **********************************/
	
	/*******************************************
	 *            Transition Effects           *
	 *******************************************/
	
	// Fades the screen from black to normal over the specified duration.
	// This renders over everything.
	public static void FadeIn(int _dur)
	{
		timer = 0;
		while (timer<_dur)
		{
			render();
			setlucent(timer*100/_dur);
			screen.rectfill(0, 0, screen.getWidth(), screen.getHeight(), Color.BLACK);
			setlucent(0);
			showpage();
		}
	}
	
	
	// Fades the screen from black to normal over the course of one second.
	// during this time the v1rpg global sound volume is changed from 0 to 100,
	// finally resting at 100.
	//
	// This renders over everything.
	public static void FadeInWSound()
	{
		timer = 0;
		while (timer<100)
		{
			render();
			setlucent(timer);
			V1_Music.V1_SetCurVolume(timer);
			screen.rectfill(0, 0, screen.getWidth(), screen.getHeight(), Color.BLACK);
			setlucent(0);
			showpage();
		}
		
		V1_Music.V1_SetCurVolume(100);
	}
	
	// Fades the screen from normal to black over the specified time.
	//
	// This renders over everything.
	public static void FadeOut(int _dur)
	{
		timer = 0;	
		while (timer<_dur)
		{
			render();
			setlucent(100 - (timer*100/_dur));
			screen.rectfill(0, 0, screen.getWidth(), screen.getHeight(), Color.BLACK);
			setlucent(0);	
			showpage();
		}
	}
	
	
	// Fades the screen from normal to black over the specified time.
	// during this time the v1rpg global sound volume is changed from 100 to 0,
	// finally resting at 0.
	//
	// This renders over everything.
	public static void FadeOutWSound(int _dur)
	{
		timer = 0;	
		while (timer<_dur)
		{
			render();
			setlucent(100 - (timer*100/_dur));
			V1_Music.V1_SetCurVolume(100 - (timer*100/_dur));
			
			screen.rectfill(0, 0, screen.getWidth(), screen.getHeight(), Color.BLACK);
			setlucent(0);	
			showpage();
		}
		
		V1_Music.V1_SetCurVolume(0);
	}
	
	// Fades the screen from white to normal over the specified duration.
	// This renders over everything.
	public static void WhiteIn(int _dur)
	{
		timer = 0;
		while (timer<_dur)
		{
			render();
			setlucent(timer*100/_dur);
			screen.rectfill(0, 0, screen.getWidth(), screen.getHeight(), RGB(255,255,255));
			setlucent(0);
			showpage();
		}
	}
	
	// Fades the screen from normal to white over the specified duration.
	// This renders over everything.
	public static void WhiteOut(int _dur)
	{
		timer = 0;	
		while (timer<_dur)
		{
			render();
			setlucent(100 - (timer*100/_dur));
			screen.rectfill(0, 0, screen.getWidth(), screen.getHeight(), RGB(255,255,255));
			setlucent(0);	
			showpage();
		}
	} 
	
	// fades from the image referenced by the global handle crossfade_img to
	// the present screen over the specified duration
	//
	// This renders over everything.
	static void CrossFade(int _dur)
	{
		timer = 0;
		while (timer<_dur)
		{
			render();
			setlucent(timer*100/_dur);
			screen.blit(0, 0, crossfade_img);
			setlucent(0);	
			showpage();
		}
	}
	
	// Does a starwars-style wipe-transition from the image referenced by the 
	// global handle crossfade_img to the current screen over the specified period
	//
	// This renders over everything.
	static void TransWipe(int _dur)
	{
		alpha_img.rectfill(0, 0, 320, 240, RGB(255,255,255));
	
		timer = 0;
		int x;
		while (timer<_dur)
		{
			x = (timer*370/_dur)-25;
			alpha_img.blit(x, 0, wipe_img);
			alpha_img.rectfill(0-30, 0, x-1, 240, Color.BLACK);
			render();
			screen.alphablit(0, 0, crossfade_img, alpha_img);
			showpage();
		}
	}
	
	// Goes from the current screen to a blackout by way of a shrinking black box
	// transition over the specified time.
	//
	// This renders over everything.
	static void BoxOut(int _dur)
	{
		timer = 0;
		int hd, vd;
		while (timer<_dur)
		{
			render();
			hd=timer*160/_dur;
			vd=timer*120/_dur;
			screen.rectfill(0,0,hd,240,Color.BLACK);
			screen.rectfill(320-hd,0,320,240,Color.BLACK);
			screen.rectfill(0,0,320,vd,Color.BLACK);
			screen.rectfill(0,240-vd,320,240,Color.BLACK);
			showpage();
		}
	}
	
	// Goes from a blackout to the current screen by way of a growing black box
	// transition over the specified time.
	//
	// This renders over everything.
	static void BoxIn(int _dur)
	{
		timer = 0;
		int hd, vd;
		while (timer<_dur)
		{
			render();
			hd=timer*160/_dur;
			vd=timer*120/_dur;
			hd = 160-hd;
			vd = 120-vd;
			screen.rectfill(0,0,hd,240,Color.BLACK);
			screen.rectfill(320-hd,0,320,240,Color.BLACK);
			screen.rectfill(0,0,320,vd,Color.BLACK);
			screen.rectfill(0,240-vd,320,240,Color.BLACK);
			showpage();
		}
	}
	
	// Goes from the current screen to a blackout by way of a shrinking black circle
	// transition over the specified time.
	//
	// This renders over everything.
	public static void CircleOut(int dur)
	{
		timer = 0;
		while (timer<dur)
		{
			render();
			crossfade_img.rectfill(0,0,320,240,Color.BLACK);
			crossfade_img.circlefill(160, 120, 200-(timer*200/dur), 200-(timer*200/dur), transcolor);
			screen.tblit(0, 0, crossfade_img);
			showpage();
		}
	}
	
	// Goes from a blackout to the current screen by way of a growing black circle
	// transition over the specified time.
	//
	// This renders over everything.
	public static void CircleIn(int dur)
	{
		timer = 0;
		while (timer<dur)
		{
			render();
			crossfade_img.rectfill(0,0,320,240,Color.BLACK);
			crossfade_img.circlefill(160, 120, timer*200/dur, timer*200/dur, transcolor);
			screen.tblit(0, 0, crossfade_img);
			showpage();
		}
	}
	
	// Fades the specified image lucently in over the screen over a specified time.
	//
	// This renders over everything.
	public static void FadeInImg(int _dur, VImage _img)
	{
		timer = 0;
		while (timer<_dur)
		{
			screen.blit(0, 0, _img);
			setlucent(timer*100/_dur);
			screen.rectfill(0, 0, screen.getWidth(), screen.getHeight(), Color.BLACK);
			setlucent(0);
			showpage();
		}
	}
	
	// Fades the specified image lucently out from over the screen over a specified 
	// time.
	//
	// This renders over everything.
	public static void FadeOutImg(int _dur)
	{
		timer = 0;
		VImage _img = screen.duplicateimage();
		while (timer<_dur)
		{
			screen.blit(0, 0, _img);
			setlucent(100 - (timer*100/_dur));
			screen.rectfill(0, 0, screen.getWidth(), screen.getHeight(), Color.BLACK);
			setlucent(0);	
			showpage();
		}
	}
	
	public static void WhiteInImg(int _dur, VImage _img)
	{
		timer = 0;
		while (timer<_dur)
		{
			screen.blit(0, 0, _img);
			setlucent(timer*100/_dur);
			screen.rectfill(0, 0, screen.getWidth(), screen.getHeight(), RGB(255,255,255));
			setlucent(0);
			showpage();
		}
	}
	
	public static void WhiteOutImg(int _dur)
	{
		timer = 0;
		VImage _img = screen.duplicateimage();
		while (timer<_dur)
		{
			screen.blit(0, 0, _img);
			setlucent(100 - (timer*100/_dur));
			screen.rectfill(0, 0, screen.getWidth(), screen.getHeight(), RGB(255,255,255));
			setlucent(0);	
			showpage();
		}
	} 
	
	public static void CrossFadeImg(int _dur, VImage _img)
	{
		timer = 0;
		VImage _scr = screen.duplicateimage();
		while (timer<_dur)
		{
			screen.blit(0, 0, _img);
			setlucent(timer*100/_dur);
			screen.blit(0, 0, _scr);
			setlucent(0);	
			showpage();
		}
	}
	
	// waits for the specified time, while continuing to update the screen 
	// and process entities.
	//
	public static void Wait(int _dur)
	{
		timer = 0;
		while (timer<_dur)
		{
			render();
			showpage();
		}
	}
	
	// Exactly like Wait() except that the screen is superimposed with the 
	// specified color.
	//
	// This renders over everything.
	public static void WaitOut(int _dur, Color color)
	{
		timer = 0;
		while (timer<_dur)
		{
			render();
			screen.rectfill(0, 0, screen.getWidth(), screen.getHeight(), color);
			showpage();
		}
	}
	
	
	// This fades from a screen full of the specifed color to normal over the 
	// specified time.
	//
	// This renders over everything.
	public static void FadeFromColor(Color _color, int _dur)
	{
		timer = 0;
		while (timer<_dur)
		{
			render();
			setlucent(timer*100/_dur);
			screen.rectfill(0, 0, screen.getWidth(), screen.getHeight(), _color);
			setlucent(0);
			showpage();
		}
	}
	
	// This fades from a normal screen to a screen full of the specifed color over 
	// the specified time.
	//
	// This renders over everything.
	public static void FadeToColor(Color _color, int _dur)
	{
		timer = 0;	
		while (timer<_dur)
		{
			render();
			setlucent(100 - (timer*100/_dur));
			screen.rectfill(0, 0, screen.getWidth(), screen.getHeight(), _color);
			setlucent(0);	
			showpage();
		}
	} 
}