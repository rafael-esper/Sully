package sully.vc.Special_effects;

public class Bouncy {
	
	public int x_100;
	public int y_100;
	public int frame;
	
	public static Bouncy _iCry[] = new Bouncy[160];
	public static Bouncy _iDar[] = new Bouncy[160];
	
	// Because I suck and couldn't figure out pretty formulae for Crystal's jumping, 
	//   I cheated and made a lookup table. Bad me.  -Grue
	public static void initIntro()
	{
		int i;
		
		// [Rafael, the Esper] Initialize variables
		for(i=0; i<160; i++) {
			_iCry[i] = new Bouncy();
			_iDar[i] = new Bouncy();
		}
		
		
		for(i=0; i<25; i++)
		{
			_iCry[i].x_100 = i*128;
			
			if( i < 5 ) 	_iCry[i].frame = 15;
			else if( i<10 )	_iCry[i].frame = 16;
			else if( i<14 )	_iCry[i].frame = 17;
			else if( i<17 )	_iCry[i].frame = 16;
			else if( i<20 )	_iCry[i].frame = 15;
			else if( i<23 )	_iCry[i].frame = 18;
			else _iCry[i].frame = 19;
		}
		
		for(i=25; i<38; i++)
		{
			_iCry[i].x_100 = 3200+((i-25)*231);
			_iCry[i].y_100 = 0+((i-25)*16);
			_iCry[i].frame = 30;
		}
		
		
		for(i=38; i<50; i++)
		{
			_iCry[i].x_100 = 6200+((i-38)*217);
			_iCry[i].y_100 = (0-200)-((i-38)*142);
			_iCry[i].frame = 30;
		}
		
		
		for(i=50; i<61; i++)
		{
			_iCry[i].x_100 = 8800+((i-50)*227);
			_iCry[i].y_100 = (0-1900)-((i-50)*273);
			_iCry[i].frame = 30;
		}
		
		
		for(i=61; i<70; i++)
		{
			_iCry[i].x_100 = 11300+((i-61)*244);
			_iCry[i].y_100 = (0-4900)-((i-61)*222);
			_iCry[i].frame = 30;
		}
		
		
		for(i=70; i<75; i++)
		{
			_iCry[i].x_100 = 13500+((i-70)*440);
			_iCry[i].y_100 = (0-6000);
			_iCry[i].frame = 30;
		}
		
		for(i=75; i<90; i++)
		{
			_iCry[i].x_100 = 15700+((i-75)*93);
			_iCry[i].y_100 = (0-6000)+((i-75)*220);
			_iCry[i].frame = 29;
		}
		
		for(i=90; i<100; i++)
		{
			_iCry[i].x_100 = 17100+((i-90)*50);
			_iCry[i].y_100 = (0-2700)+((i-90)*190);
			_iCry[i].frame = 29;
		}
		
		
		for(i=100; i<110; i++)
		{
			_iCry[i].x_100 = 17500+((i-100)*180);
			_iCry[i].y_100 = (0-800)-((i-100)*1350);
			_iCry[i].frame = 30;
		}
	
		
		for(i=110; i<120; i++)
		{
			_iCry[i].x_100 = 19300+((i-110)*210);
			_iCry[i].y_100 = (0-12700)-((i-110)*460);
			_iCry[i].frame = 30;
		}
		
		
		for(i=120; i<130; i++)
		{
			_iCry[i].x_100 = 21400+((i-120)*180);
			_iCry[i].y_100 = (0-17300)-((i-120)*460);
			_iCry[i].frame = 30;
		}
		
		for(i=130; i<160; i++)
		{
			_iCry[i].x_100 = 23200+((i-130)*177);
			_iCry[i].y_100 = (0-21900)+((i-130)*730);
			_iCry[i].frame = 29;
		}
	}
	
}
