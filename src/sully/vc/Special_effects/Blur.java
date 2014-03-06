package sully.vc.Special_effects;

import static core.Script.*;
import domain.VImage;

/****************************************************
*****Blur Library v1.5 by Tristan Michael(ragecage)**********
*************completed: sept 1, 2004********************
****************************************************/
public class Blur {
	
	public static final int BLUR_ITERATIONS = 6;
	public static final int BLUR_DLY = 5;
	
	VImage blur_buffer[] = new VImage[BLUR_ITERATIONS];
	int blur_stamp;
	
	void loadBlurBuffer(){
		int i;
		for(i=0;i<BLUR_ITERATIONS;i++){
			blur_buffer[i]=new VImage(screen.getWidth(),screen.getHeight());
		}
	}
	
	/*void freeBlurBuffer(){
		int i;
		for(i=0;i<BLUR_ITERATIONS;i++){
			//freeImage(blur_buffer[i]);
		}
	}*/
	
	void renderMotionBlur(){
		int i;
	//screen buffering
		if(timer>blur_stamp+BLUR_DLY){
			blur_stamp=timer;     
			for(i=1;i<BLUR_ITERATIONS;i++){
				blur_buffer[i-1].blit(0,0,blur_buffer[i]);
			}
			blur_buffer[i-1].blit(0,0,screen);
		}
	//blur blitting
		for(i=0;i<BLUR_ITERATIONS-1;i++){
			setlucent(100/(BLUR_ITERATIONS-i));
			screen.blit(0,0,blur_buffer[i]);
		}
		setlucent(0);
	}
	
	void focalBlur(int iterations, int distance, VImage src){
		int i;
		VImage tempImage=new VImage(src.getWidth(),src.getHeight());
		tempImage.blit(0,0,src);
		
		if(iterations >= distance) iterations=distance-1;
		
		for(i=1;i<=iterations;i++){  
			setlucent(100/iterations *i);
			src.tscaleblit(0-(distance/iterations *i /2), 
				0-(distance/iterations*i /2), 
				tempImage.getWidth()+(distance/iterations*i), 
				tempImage.getHeight()+(distance/iterations*i), 
				tempImage);
		}
		setlucent(0);
		//freeImage(tempImage);
	}
	
	void xyBlur(int x, int y, int iterations, int distance, VImage src){
		int i;
	
	//check for offscreen extremes
		if(x > screen.getWidth()) x=screen.getHeight();
		if(y > screen.getHeight()) y=screen.getHeight();
		if(x < 0) x=0;
		if(y < 0) y=0;
	
	//grab focus region
		VImage tempImage=new VImage(screen.getWidth()*2, screen.getHeight()*2);
		tempImage.grabRegion(x-(tempImage.getWidth()/2), y-(tempImage.getHeight()/2), x+(tempImage.getWidth()/2), y+(tempImage.getHeight()/2),0,0,src);
		
	//Make sure that the iterations does not exceed the distance
		if(iterations >= distance) iterations=distance-1;
	
	//streach focus region to create blur
		for(i=1;i<=iterations;i++){  
			setlucent(100/iterations *i);
			src.scaleblit(		x - (tempImage.getWidth()/2)-(distance/iterations *i /2), 
						y - (tempImage.getHeight()/2)-(distance/iterations *i /2), 
						tempImage.getWidth()+(distance/iterations*i), 
						tempImage.getHeight()+(distance/iterations*i), 
						tempImage);
		}
		
	//end of function
		setlucent(0);
		////freeImage(tempImage);
	}
	
	static void radialBlur(int iterations, int distance, VImage src){
		int i;
		VImage tempImage=new VImage(src.getWidth(),src.getHeight());
		tempImage.blit(0,0,src);
		
		for(i=0;i<iterations;i++){
			setlucent(100/iterations *i);
			src.rotscale(src.getWidth()/2, src.getHeight()/2, distance/(iterations-i), 1000, tempImage);
		}
		setlucent(0);
		////freeImage(tempImage);
	}
	
	void crossBlur(int iterations, int distance, VImage src){
		int i;
		VImage tempImage=new VImage(src.getWidth(),src.getHeight());
		tempImage.blit(0,0,src);
		
		if(iterations >= distance) iterations=distance-1;
		
		for(i=1;i<=iterations;i++){  
			setlucent(100/iterations *i);
			src.blit(0-(distance/iterations *i), 0-(distance/iterations *i), tempImage);
		}
		setlucent(0);
		//freeImage(tempImage);
	}
	
	void imageBlur(int iterations, int distance, VImage src){
		int i;
		VImage tempImage=new VImage(src.getWidth(),src.getHeight());
		tempImage.blit(0,0,src);
		
		if(iterations >= distance) iterations=distance-1;
		
		for(i=1;i<=iterations;i++){  
			setlucent(100/iterations *i);
			src.blit(0-(distance/iterations *i), 0, tempImage);
		}
		setlucent(0);
		tempImage.blit(0,0,src);
		for(i=1;i<=iterations;i++){  
			setlucent(100/iterations *i);
			src.blit(0, 0-(distance/iterations *i), tempImage);
		}
		setlucent(0);
		//freeImage(tempImage);
	}
}