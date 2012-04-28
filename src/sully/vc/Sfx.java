package sully.vc;

import static core.Script.*;

import java.net.URL;

public class Sfx {
	
	// Three global variables for the global music, interface, 
	// and sound effect volumes.
	// Note: The music volume is only enforced by using the V1_sound.vc
	// functions, and is not directly tied to any builtin sound functions.
	public static int interface_volume;
	public static int sfx_volume;
	public static int global_music_volume;
	
	/**************************** public resources ****************************/
	
	static URL sfx_open 	= load("res/sfx/open03.wav");
	static URL sfx_warp 	= load("res/sfx/shadow.wav");
	static URL sfx_switch	= load("res/sfx/boxopen.wav");
	static URL sfx_save 	= load("res/sfx/ding.wav");
	static URL sfx_magic1	= load("res/sfx/magic05.mp3");
	static URL sfx_hit		= load("res/sfx/HIT01.WAV");
	static URL sfx_well	= load("res/sfx/MAGIC.WAV");
	static URL sfx_magic2	= load("res/sfx/MAGIC02.WAV");
	static URL sfx_magic4	= load("res/sfx/MAGIC04.WAV");
	static URL sfx_bling	= load("res/sfx/BUYSELL.WAV");
	static URL sfx_bomb	= load("res/sfx/BOMB.WAV");
	static URL sfx_crikts	= load("res/sfx/CRICKETS.WAV");
	static URL sfx_shing	= load("res/sfx/SHING.WAV");
	static URL sfx_quake	= load("res/sfx/QUAKE.WAV");
	static URL sfx_xplode	= load("res/sfx/EXPLOSION.WAV");
	static URL sfx_fanfare	= load("res/sfx/SFANFARE.mp3");
	static URL sfx_splash	= load("res/sfx/WATER02.WAV");
	static URL sfx_dblclck	= load("res/sfx/CLICK01.WAV");
	static URL sfx_crash	= load("res/sfx/crash05.WAV");
	static URL sfx_twinkle	= load("res/sfx/twinkle.WAV");
	static URL sfx_grandfn	= load("res/sfx/BIGFNFR.mp3");
	static URL sfx_fall	= load("res/sfx/fall02.wav");
	
	/**************************** functions ****************************/
	
	//
	// In-game sound effects.
	// all play at the global sfx_volume volume.
	
	public static void SoundChaChing() {
		playsound( sfx_bling, sfx_volume );
	}
	
	public static void SoundOpenBox() {
		playsound( sfx_open, sfx_volume );
	}
	
	public static void SoundWarpZone() {
		playsound( sfx_warp, sfx_volume );
	}
	
	public static void SoundSwitch() {
		playsound( sfx_switch, sfx_volume );
	}
	
	public static void SoundSavePoint() {
		playsound( sfx_save, sfx_volume );
	}
	
	public static void SoundMagic1() {
		playsound( sfx_magic1, sfx_volume );
	}
	
	public static void SoundMagic2() {
		playsound( sfx_magic2, sfx_volume );
	}
	
	public static void SoundHit() {
		playsound( sfx_hit, sfx_volume );
	}
	
	public static void SoundHealingWell() {
		playsound( sfx_well, sfx_volume );
	}
	
	public static void SoundBomb() {
		playsound( sfx_bomb, sfx_volume );
	}
	
	public static void SoundChirpChirp() {
		playsound( sfx_crikts, sfx_volume );
	}
	
	public static void SoundIceShing() {
		playsound( sfx_magic4, sfx_volume );
	}
	
	public static void SoundShing() {
		playsound( sfx_shing, sfx_volume );
	}
	
	public static void SoundFanfare() {
		playsound( sfx_fanfare, sfx_volume );
	}
	
	public static void SoundGrandFanfare() {
		playsound( sfx_grandfn, sfx_volume );
	}
	
	public static void SoundQuake() {
		playsound( sfx_quake, sfx_volume );
	}
	
	public static void SoundExplosion() {
		playsound( sfx_xplode, sfx_volume );
	}
	
	public static void SoundSplash() {
		playsound( sfx_splash, sfx_volume );
	}
	
	public static void SoundDoubleClick() {
		playsound( sfx_dblclck, sfx_volume );
	}
	
	public static void SoundCrash() {
		playsound( sfx_crash, sfx_volume );
	}
	
	public static void SoundTwinkle() {
		playsound( sfx_twinkle, sfx_volume );
	}
	
	public static void SoundFall() {
		playsound( sfx_fall, sfx_volume );
	}
}