package sully.vc;

import static core.Script.*;

import java.net.URL;

import domain.VSound;

public class Sfx {
	
	// Three global variables for the global music, interface, 
	// and sound effect volumes.
	// Note: The music volume is only enforced by using the V1_sound.vc
	// functions, and is not directly tied to any builtin sound functions.
	public static int interface_volume;
	public static int sfx_volume;
	public static int global_music_volume;
	
	/**************************** public resources ****************************/
	
	static VSound sfx_open 	= new VSound(load("res/sfx/open03.WAV"));
	static VSound sfx_warp 	= new VSound(load("res/sfx/shadow.WAV"));
	static VSound sfx_switch	= new VSound(load("res/sfx/boxopen.WAV"));
	static VSound sfx_save 	= new VSound(load("res/sfx/ding.WAV"));
	static VSound sfx_magic1	= new VSound(load("res/sfx/magic05.mp3"));
	static VSound sfx_hit		= new VSound(load("res/sfx/HIT01.WAV"));
	static VSound sfx_well	= new VSound(load("res/sfx/MAGIC.WAV"));
	static VSound sfx_magic2	= new VSound(load("res/sfx/MAGIC02.WAV"));
	static VSound sfx_magic4	= new VSound(load("res/sfx/MAGIC04.WAV"));
	static VSound sfx_bling	= new VSound(load("res/sfx/BUYSELL.WAV"));
	static VSound sfx_bomb	= new VSound(load("res/sfx/BOMB.WAV"));
	static VSound sfx_crikts	= new VSound(load("res/sfx/CRICKETS.WAV"));
	static VSound sfx_shing	= new VSound(load("res/sfx/SHING.WAV"));
	static VSound sfx_quake	= new VSound(load("res/sfx/QUAKE.WAV"));
	static VSound sfx_xplode	= new VSound(load("res/sfx/EXPLOSION.WAV"));
	static VSound sfx_fanfare	= new VSound(load("res/sfx/SFANFARE.mp3"));
	static VSound sfx_splash	= new VSound(load("res/sfx/WATER02.WAV"));
	static VSound sfx_dblclck	= new VSound(load("res/sfx/CLICK01.WAV"));
	static VSound sfx_crash	= new VSound(load("res/sfx/crash05.WAV"));
	static VSound sfx_twinkle	= new VSound(load("res/sfx/twinkle.WAV"));
	static VSound sfx_grandfn	= new VSound(load("res/sfx/BIGFNFR.mp3"));
	static VSound sfx_fall	= new VSound(load("res/sfx/fall02.WAV"));
	
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