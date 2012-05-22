package sully.vc.util;

import static core.Script.*;

public class Camscroll {
	
	static // ================================================================================================
	// Geas : camscroll.lib.vc
	// ------------------------------------------------------------------------------------------------
	//	Four functions to cause the camera to smoothly scroll to a new place. Underneath, only it's
	//	all really only one function: camScrollTo(). The others are simply wrappers to facilitate
	//	certain other uses of it.
	//
	//		camScrollTo(x, y, time);
	//			Scrolls the camera to [x],[y] in [time] ticks.
	//			This function places x,y at the topleft corner.
	//			your 'starting position' is xwin, ywin.
	//
	//		camScrollToS(x, y, speed);
	//			Scrolls the camera to [x],[y] at [speed] pixels/second.
	//			This function places x,y at the topleft corner.
	//			your 'starting position' is xwin, ywin.
	//
	//		camCtrScrollTo(x, y, time);
	//			Scrolls the camera centered upon [x],[y] in [time] ticks.
	//			your 'starting position' is xwin, ywin.
	//
	//		camCtrScrollToS(x, y, speed);
	//			Scrolls the camera centered upon [x],[y] at [speed] pixels/second.
	//			your 'starting position' is xwin, ywin.
	//
	//		camReturnToPlayer(entity, time);
	//			Returns the camera to [entity]'s position in [time] ticks.
	//
	//		camReturnToPlayerS(entity, speed);
	//			Returns the camera to [entity]'s position at [speed] pixels/second.
	//
	//		camZoomToEntity(int ent, int time)
	//		camZoomToEntityS(int ent, int speed)
	//			Like camReturnToPlayer/S, but for any entity.  Does not set CameraTracking to 1.
	//
	// Coding by Shamus "Kildorf" Peveril
	// Light mods done by Ben "McGrue" McGraw
	// ================================================================================================
	
	int camIsScrolling = 0;			// a flag that shows whether the camera is currently scrolling or not
									// technically read/writable, but writing will only make it wrong, not
									// change anything ^_^
	
	// ------------------------------------------------------------------------------------------------
	
	public static void camScrollTo(int targetx, int targety, int time) {
		cameratracking = 0;			// set camera tracking off	
		camIsScrolling = 1;			// and set our query flag (see above)
	
		int camx, camy;				// camx, camy are the current position of the screen * 10000
									// if we work directly with xwin, ywin, the camera jitters as it
									// travels; adding to a huge number allows smaller additions and
									// smooths the scrolling of the camera
		int dx, dy;					// the change at each iteration of the loop
		int tpass, ot;				// time passed since last iteration, and last value of timer
		int txwin, tywin;			// target xwin and ywin
		int done = 0;				// a finishing flag
		
		txwin = targetx;			// calculate txwin, tywin
		tywin = targety;
		
		camx = xwin*10000;									// get camx and camy
		camy = ywin*10000;
		
				// check for border cases; if the target x and y are near the edge of the screen, we want to
				// scroll directly to where the /screen/ will be; without these checks we get the camera
				// hitting the edge of the screen and sliding along it until it reaches the right place
		if(txwin < 0) txwin = 0;
		if(txwin > (((current_map.getWidth())*16) - screen.getWidth())) txwin = (((current_map.getWidth())*16) - (screen.getWidth()) - 1);
		if(tywin < 0) tywin = 0;
		if(tywin > ((current_map.getHeight()*16) - screen.getHeight())) tywin = (((current_map.getHeight())*16) - (screen.getHeight()) - 1);
	
		dx = ((txwin*10000) - (xwin*10000)) / time;			// calculate the pixel "step" for each timer tick
		dy = ((tywin*10000) - (ywin*10000)) / time;			// we use the *10000 value, to smooth the travelling
	
		ot = timer;					// set ot to timer to get ready for loop
		while(done==0 && time > 0) {	// we have a double check; done will get set when the camera reaches the right place,
									// and time counts down to 0
			tpass = timer - ot;						// get tpass for this iteration
			ot = timer;								// set ot for the next iteration
			if((time - tpass) <= 0) tpass = time;	// if tpass is more than what time we have left then just use what time's left
			time -= tpass;							// subtract tpass from time
					
		
				// camx work:
				//  if the next step will take us past our target location, then just jump onto that location
			if(dx > 0 && (camx + (dx*tpass)) > (txwin*100000)) {
				camx = txwin*10000;
			} else if(dx < 0 && (camx + (dx*tpass)) < (txwin*10000)) {
				camx = txwin*10000;
			} else {
				//  otherwise, just make the step
				camx += (dx*tpass);
			}
			
				// repeat for camy		
			if(dy > 0 && (camy + (dy*tpass)) > (tywin*10000)) {
				camy = tywin*10000;
			} else if(dy < 0 && (camy + (dy*tpass)) < (tywin*10000)) {
				camy = tywin*10000;
			} else {
				camy += (dy*tpass);
			}
			
				// set the game's camera coordinates
			xwin = camx/10000;
			ywin = camy/10000;
			
				// and check if we've gotten there yet
			if(xwin == txwin && ywin == tywin) done = 1;
			
				// lastly, repaint the screen after the camera has been moved
			render();
			showpage();
		}
		
			// set our query flag back to 0
		camIsScrolling = 0;
		
			// and, to be certain, set our camera coordinates to where we were aiming
		xwin = txwin;
		ywin = tywin;
	}
	
	
	public static void camCtrScrollTo(int targetx, int targety, int time) {
		cameratracking = 0;			// set camera tracking off	
		camIsScrolling = 1;			// and set our query flag (see above)
	
		int camx, camy;				// camx, camy are the current position of the screen * 10000
									// if we work directly with xwin, ywin, the camera jitters as it
									// travels; adding to a huge number allows smaller additions and
									// smooths the scrolling of the camera
		int dx, dy;					// the change at each iteration of the loop
		int tpass, ot;				// time passed since last iteration, and last value of timer
		int txwin, tywin;			// target xwin and ywin
		int done = 0;				// a finishing flag
		
		txwin = (targetx - (screen.getWidth()/2));			// calculate txwin, tywin
		tywin = (targety - (screen.getHeight()/2));
		
		camx = xwin*10000;									// get camx and camy
		camy = ywin*10000;
		
				// check for border cases; if the target x and y are near the edge of the screen, we want to
				// scroll directly to where the /screen/ will be; without these checks we get the camera
				// hitting the edge of the screen and sliding along it until it reaches the right place
		if(txwin < 0) txwin = 0;
		if(txwin > (((current_map.getWidth())*16) - screen.getWidth())) txwin = (((current_map.getWidth())*16) - (screen.getWidth()) - 1);
		if(tywin < 0) tywin = 0;
		if(tywin > ((current_map.getHeight()*16) - screen.getHeight())) tywin = (((current_map.getHeight())*16) - (screen.getHeight()) - 1);
	
		dx = ((txwin*10000) - (xwin*10000)) / time;			// calculate the pixel "step" for each timer tick
		dy = ((tywin*10000) - (ywin*10000)) / time;			// we use the *10000 value, to smooth the travelling
	
		ot = timer;					// set ot to timer to get ready for loop
		while(done==0 && time > 0) {	// we have a double check; done will get set when the camera reaches the right place,
									// and time counts down to 0
			tpass = timer - ot;						// get tpass for this iteration
			ot = timer;								// set ot for the next iteration
			if((time - tpass) <= 0) tpass = time;	// if tpass is more than what time we have left then just use what time's left
			time -= tpass;							// subtract tpass from time
					
		
				// camx work:
				//  if the next step will take us past our target location, then just jump onto that location
			if(dx > 0 && (camx + (dx*tpass)) > (txwin*100000)) {
				camx = txwin*10000;
			} else if(dx < 0 && (camx + (dx*tpass)) < (txwin*10000)) {
				camx = txwin*10000;
			} else {
				//  otherwise, just make the step
				camx += (dx*tpass);
			}
			
				// repeat for camy		
			if(dy > 0 && (camy + (dy*tpass)) > (tywin*10000)) {
				camy = tywin*10000;
			} else if(dy < 0 && (camy + (dy*tpass)) < (tywin*10000)) {
				camy = tywin*10000;
			} else {
				camy += (dy*tpass);
			}
			
				// set the game's camera coordinates
			xwin = camx/10000;
			ywin = camy/10000;
			
				// and check if we've gotten there yet
			if(xwin == txwin && ywin == tywin) done = 1;
			
				// lastly, repaint the screen after the camera has been moved
			render();
			showpage();
		}
		
			// set our query flag back to 0
		camIsScrolling = 0;
		
			// and, to be certain, set our camera coordinates to where we were aiming
		xwin = txwin;
		ywin = tywin;
	}
	
	// ------------------------------------------------------------------------------------------------
	
	public static void camReturnToPlayer(int playerentity, int time) {
			// simple: scroll to the middle of the entity's hotspot, in time ticks
		camCtrScrollTo(
			entity.get(playerentity).getx()+(entity.get(playerentity).getHotW()/2),
			entity.get(playerentity).gety()+(entity.get(playerentity).getHotH()/2),
			time
		);
		
			// and turn camera tracking on
		cameratracking = 1;
		
			// THIS WOULD BE EVEN COOLER IF THERE WAS A GETPLAYER()
	}
	
	// ------------------------------------------------------------------------------------------------
	
	public static void camCtrScrollToS(int targetx, int targety, int speed) {
			// wrapper for camScrollTo()...
		int txwin, tywin;
		txwin = (targetx - (screen.getWidth()/2));
		tywin = (targety - (screen.getHeight()/2));
	
			// adjust txwin/tywin to account for being next to the edge of the map
			// without these adjustments, the time calculated will be somewhat less accurate
		if(txwin < 0) txwin = 0;
		if(txwin > ((current_map.getWidth()*16) - screen.getWidth())) txwin = ((current_map.getWidth()*16) - (screen.getWidth()));
		if(tywin < 0) tywin = 0;
		if(tywin > ((current_map.getHeight()*16) - screen.getHeight())) tywin = ((current_map.getHeight()*16) - (screen.getHeight()));
	
			// calculate the distance to your desired cam location...
		int dx = xwin - txwin, dy = ywin - tywin;
		
		int d = sqrt((dx*dx) + (dy*dy));
		
			// and then just scroll in that distance / your speed --> this gives us the time it takes!
			// MATH IS FUN!
		camCtrScrollTo(targetx, targety, (d*100)/(speed));	
	}
	
	public static void camScrollToS(int targetx, int targety, int speed) {
			// wrapper for camScrollTo()...
		int txwin, tywin;
		txwin = targetx;
		tywin = targety;
	
			// adjust txwin/tywin to account for being next to the edge of the map
			// without these adjustments, the time calculated will be somewhat less accurate
		if(txwin < 0) txwin = 0;
		if(txwin > ((current_map.getWidth()*16) - screen.getWidth())) txwin = ((current_map.getWidth()*16) - (screen.getWidth()));
		if(tywin < 0) tywin = 0;
		if(tywin > ((current_map.getHeight()*16) - screen.getHeight())) tywin = ((current_map.getHeight()*16) - (screen.getHeight()));
	
			// calculate the distance to your desired cam location...
		int dx = xwin - txwin, dy = ywin - tywin;
		
		int d = sqrt((dx*dx) + (dy*dy));
		
			// and then just scroll in that distance / your speed --> this gives us the time it takes!
			// MATH IS FUN!
		camScrollTo(targetx, targety, (d*100)/(speed));	
	}
	
	// ------------------------------------------------------------------------------------------------
	
	public static void camReturnToPlayerS(int playerentity, int speed) {
			// basically the same as camScrollToS, except that we use the entity's middle-of-hotspot,
			// rather than passed in parameters
		int txwin, tywin;
		txwin = (entity.get(playerentity).getx()+(entity.get(playerentity).getHotW()/2) - (screen.getWidth()/2));
		tywin = (entity.get(playerentity).gety()+(entity.get(playerentity).getHotH()/2) - (screen.getHeight()/2));
		
		if(txwin < 0) txwin = 0;
		if(txwin > ((current_map.getWidth()*16) - screen.getWidth())) txwin = ((current_map.getWidth()*16) - (screen.getWidth()));
		if(tywin < 0) tywin = 0;
		if(tywin > ((current_map.getHeight()*16) - screen.getHeight())) tywin = ((current_map.getHeight()*16) - (screen.getHeight()));
	
		int dx = xwin - txwin, dy = ywin - tywin;
		
		int d = sqrt((dx*dx) + (dy*dy));
		camCtrScrollTo(
			entity.get(playerentity).getx()+(entity.get(playerentity).getHotW()/2),
			entity.get(playerentity).gety()+(entity.get(playerentity).getHotH()/2),
			(d*100)/(speed)
		);
		
		cameratracking = 1;
	}
	
	
	// ------------
	
	public static void camZoomToEntity(int ent, int time) {
			// simple: scroll to the middle of the entity's hotspot, in time ticks
		camCtrScrollTo(
			entity.get(ent).getx()+(entity.get(ent).getHotW()/2),
			entity.get(ent).gety()+(entity.get(ent).getHotH()/2),
			time
		);
	}
	
	public static void camZoomToEntityS(int ent, int speed) {
			// basically the same as camScrollToS, except that we use the entity's middle-of-hotspot,
			// rather than passed in parameters
		int txwin, tywin;
		txwin = (entity.get(ent).getx()+(entity.get(ent).getHotW()/2) - (screen.getWidth()/2));
		tywin = (entity.get(ent).gety()+(entity.get(ent).getHotH()/2) - (screen.getHeight()/2));
		
		if(txwin < 0) txwin = 0;
		if(txwin > ((current_map.getWidth()*16) - screen.getWidth())) txwin = ((current_map.getWidth()*16) - (screen.getWidth()));
		if(tywin < 0) tywin = 0;
		if(tywin > ((current_map.getHeight()*16) - screen.getHeight())) tywin = ((current_map.getHeight()*16) - (screen.getHeight()));
	
		int dx = xwin - txwin, dy = ywin - tywin;
		
		int d = sqrt((dx*dx) + (dy*dy));
		camCtrScrollTo(
			entity.get(ent).getx()+(entity.get(ent).getHotW()/2),
			entity.get(ent).gety()+(entity.get(ent).getHotH()/2),
			(d*100)/(speed)
		);
	}

}