/* 
java jode 
this is an applet that demonstraits the dining philosophers problem
*/

import java.awt.Graphics; 
import java.awt.*;
import java.net.*;
import java.io.InputStream;

public class DiningPhilosophers extends java.applet.Applet implements Runnable 
  {
    String audio;
    Image bullet;
    Image phil[] = new Image[5];
    Image oldphil[] = new Image[5];
    Image thinkingphil[][] = new Image[5][4];
    Image hungryphil[][] = new Image[5][4];
    Image eatingphil[][] = new Image[5][4];
    Image background;
    int slide1=185,slide2=185,slide3=185,oldslide1,oldslide2,oldslide3;
    boolean slide1click=false, slide2click=false, slide3click=false;
    sticks Sticks[] = new sticks[5];
    int thishand[] = new int[5];
    int thathand[] = new int[5];
    Thread au=null;
    Thread bu=null;
    Thread cu=null;
    Thread du=null;
    Thread eu=null;
    Thread fu=null;
    int ticketnumber=0;

    float EatChance=.25f;
    float ThinkChance=.75f;
    int speed=80;

    final int xoffset =-80 ,yoffset=-100;
    final int buttonx=xoffset+80+73, buttony=yoffset+138+398, buttonspace=90;
    final int slide1y=yoffset+138+433, slide2y=yoffset+138+463, slide3y=yoffset+138+493;
    final int slidexl=xoffset+80+60,slidexr=xoffset+80+430;

    public void init() 
      {
	audio="eat";

	oldslide1=slide1;
	oldslide2=slide2;
	oldslide3=slide3;

	Sticks[0]=new sticks();
	Sticks[1]=new sticks();
	Sticks[2]=new sticks();
	Sticks[3]=new sticks();
	Sticks[4]=new sticks();

	Sticks[0].returnStick(7);
	Sticks[1].returnStick(3);
	Sticks[2].returnStick(3);
	Sticks[3].returnStick(3);
	Sticks[4].returnStick(3);


	au=new Thread(this);
	bu=new Thread(this);
	cu=new Thread(this);
	du=new Thread(this);
	eu=new Thread(this);
	fu=new Thread(this);
	System.out.println("we think, therefore we are");

	au.start();
	bu.start();
	cu.start();
	du.start();
	eu.start();
	fu.start();



	stop();
	initializeImages();
      }

    public void start()
      {
	System.out.println("we live");

	au.resume();
	bu.resume();
	cu.resume();
	du.resume();
	eu.resume();
	fu.resume();
      }

    public void stop()
      {
	System.out.println("help, they're comming to stop us");

	au.suspend();
	bu.suspend();
	cu.suspend();
	du.suspend();
	eu.suspend();
	fu.suspend();
      }
    public void destroy()
      {
	System.out.println("help, they're comming to killing us!");

	au.stop();
	bu.stop();
	cu.stop();
	du.stop();
	eu.stop();
	fu.stop();

/* wake up threads so they can die */

	au.resume();
	bu.resume();
	cu.resume();
	du.resume();
	eu.resume();
	fu.resume();

	Sticks[0].returnStick(3);
	Sticks[1].returnStick(3);
	Sticks[2].returnStick(3);
	Sticks[3].returnStick(3);
	Sticks[4].returnStick(3);

      }

    Graphics offg;
    Image offimg;

    public void paint(Graphics g)
      {
	  if (background == null) {
	      return;
	  }

	  Dimension d = size();
/*only draw things that might have changed */
	  if (offimg == null) {
	      offimg = createImage(d.width, d.height);
	      offg = offimg.getGraphics();
	  }
	  
	  offg.setColor(getBackground());
	  offg.fillRect(0, 0, d.width, d.height);
	  offg.setColor(Color.black);

	    offg.drawImage(background,xoffset+80,yoffset+138, this);
	    offg.drawImage(bullet, buttonx, buttony, this);
	    offg.drawImage(bullet, buttonx+buttonspace, buttony, this);
	    offg.drawImage(bullet, buttonx+buttonspace*2, buttony, this);
	    offg.drawImage(bullet, buttonx+buttonspace*3, buttony, this);
	    offg.drawString("Speed",xoffset+80,slide1y+12);
	    offg.drawString("Think",xoffset+80,slide2y+12);
	    offg.drawString("Talk ",xoffset+80,slide3y+12);
	    offg.drawLine(slidexl,slide1y+8,slidexr,slide1y+8);
	    offg.drawLine(slidexl,slide2y+8,slidexr,slide2y+8);
	    offg.drawLine(slidexl,slide3y+8,slidexr,slide3y+8);

	offg.drawImage(phil[0],xoffset+216,yoffset+181, this);
	offg.drawImage(phil[1],xoffset+335,yoffset+187, this);
	offg.drawImage(phil[2],xoffset+379,yoffset+261, this);
	offg.drawImage(phil[3],xoffset+245,yoffset+318, this);
	offg.drawImage(phil[4],xoffset+138,yoffset+247, this);

	  //offg.clearRect(slidexl+oldslide1, slide1y,16,16);
	    offg.drawLine(slidexl+oldslide1,slide1y+8,slidexl+oldslide1+16,slide1y+8);
	    offg.drawImage(bullet, slidexl+slide1, slide1y, this);
	    oldslide1=slide1;

	  //offg.clearRect(slidexl+oldslide2, slide2y,16,16);
	    offg.drawLine(slidexl+oldslide2,slide2y+8,slidexl+oldslide2+16,slide2y+8);
	    offg.drawImage(bullet, slidexl+slide2, slide2y, this);
	    oldslide2=slide2;

	  //offg.clearRect(slidexl+oldslide3, slide3y,16,16);
	    offg.drawLine(slidexl+oldslide3,slide3y+8,slidexl+oldslide3+16,slide3y+8);
	    offg.drawImage(bullet, slidexl+slide3, slide3y, this);
	    oldslide3=slide3;

	  g.drawImage(offimg, 0, 0, null);
      }

    public void update(Graphics g)
      {
	/* pretty cheesey, but gets rid of flash */
	paint(g);
      }

    public void run()
      {
/* starts threads, one for each phil, + one do keep track of whats up */
	int number;


	Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
	number=getTicket();
	
	if(number>4) whatsup();
	else philosopher(number);
      }

    public boolean mouseDown(java.awt.Event evt, int x, int y)
      {
/* grab bullet */
	int by2;

	if(y<20)
	  {
	    System.out.println("eh?");
	    System.out.println("number of threads:"+Thread.activeCount());
	  }

	if((y>buttony)&&(y<buttony+16))
	   {
	     if((x>buttonx)&&(x<buttonx+16)) audio="eat";
	     if((x>buttonx+buttonspace)&&(x<buttonx+buttonspace+16)) audio="talk";
	     if((x>buttonx+buttonspace*2)&&(x<buttonx+buttonspace*2+16)) audio="laugh";
	     if((x>buttonx+buttonspace*3)&&(x<buttonx+buttonspace*3+16)) audio=null;
	   }

	if((y>60)&&(y<76)&&(x>(slide3-8))&&(x<(slide3+8))) slide3click=true;

	y=y-slide1y;
	x=x-slidexl-8;


	if((y>0 )&&(y<16)&&(x>(slide1-8))&&(x<(slide1+8))) slide1click=true;
	if((y>30)&&(y<46)&&(x>(slide2-8))&&(x<(slide2+8))) slide2click=true;
	if((y>60)&&(y<76)&&(x>(slide3-8))&&(x<(slide3+8))) slide3click=true;

	
	return true;
      }

    public boolean mouseExit(java.awt.Event evt)
      {
/* let go bullet */
	slide1click=false;
	slide2click=false;
	slide3click=false;
	return true;
      }

    public boolean mouseUp(java.awt.Event evt, int x, int y)
      {
/* let go bullet */
	slide1click=false;
	slide2click=false;
	slide3click=false;
	return true;
      }

    public boolean mouseDrag(java.awt.Event evt, int x, int y)
      {
/* handles sliders, and sets parameters */
	y=y-slide1y;
	x=x-slidexl-8;
	int len=slidexr-slidexl-16;

	    if(slide1click)
	      {
		if(x<=0) x=0;
		if(x>=len) x=len;
		speed=len-x;
		speed=(speed*speed)/len;
		slide1=x;
	      }


	    if(slide2click)
	      {
		if(x<=0) x=0;
		if(x>=len) x=len;
		ThinkChance=(float)x/len;
		ThinkChance*=ThinkChance;
		ThinkChance=1-ThinkChance;
		slide2=x;
	      }

	    if(slide3click)
	      {	
		if(x<=0) x=0;
		if(x>=len) x=len;
		EatChance=1-(float)x/len;
		EatChance=EatChance*EatChance;
		slide3=x;
	      }
	return true;
      }

    synchronized int getTicket()
      {
/* so each philnumber will be unique */	
	return ticketnumber++;
      }


    void initializeImages()
      {
/* load in lots of pretty pictures */
	int i,j;

	bullet=this.getImage(getCodeBase(), "images/b2.gif");
	if(bullet==null) System.out.println("no bullet");

	background=this.getImage(getCodeBase(), "images/background.gif");
	if(background==null) System.out.println("no picture ");

	for(i=0;i<5;i++)
	  {

	    for(j=0;j<4;j++)
	      {
		thinkingphil[i][j]=this.getImage(getCodeBase(), "images/thinking"+j+"."+i+".gif");
		if(thinkingphil[i][j]==null)
		  System.out.println("no picture "+i);
	      }

	    for(j=0;j<4;j++)
	      {
		hungryphil[i][j]=this.getImage(getCodeBase(), "images/hungry"+j+"."+i+".gif");
		if(hungryphil[i][j]==null) System.out.println("no picture "+i);
	      }

	    for(j=0;j<4;j++)
	      {
		eatingphil[i][j]=this.getImage(getCodeBase(), "images/eating"+j+"."+i+".gif");
		if(eatingphil[i][j]==null)
		  System.out.println("no picture "+i);
	      }

	  }
	phil[0]=thinkingphil[0][0];
	phil[1]=thinkingphil[1][0];
	phil[2]=thinkingphil[2][0];
	phil[3]=thinkingphil[3][0];
	phil[4]=thinkingphil[4][0];

      }

    void philosopher(int mynumber)
      {
/* the phil process */	
	System.out.println(mynumber);
	for(;;)
	  {
	    think(mynumber);
	    PickUpSticks(mynumber);
	    eat(mynumber);
       	    DropSticks(mynumber);
	  }
      }
    
    void think(int mynumber)
      {
	int i=0;
	
	while(i<4)
	  {
	    phil[mynumber]=thinkingphil[mynumber][i];
	    i+= (Math.random()<ThinkChance) ? 1 : -1;
	try {Thread.sleep(speed*8);} catch (InterruptedException e){}
	    if(i<0) i=0;
	  }
	phil[mynumber]=thinkingphil[mynumber][0];
       
      }

    void eat(int mynumber)
      {
	do
	  {
	    phil[mynumber]=eatingphil[mynumber][0];
	try {Thread.sleep(speed);} catch (InterruptedException e){}
	    phil[mynumber]=eatingphil[mynumber][1];
	try {Thread.sleep(speed);} catch (InterruptedException e){}
	    phil[mynumber]=eatingphil[mynumber][2];

	    if(audio!=null) play(getCodeBase(), "audio/"+audio+mynumber+".au");

	try {Thread.sleep(speed);} catch (InterruptedException e){}
	    phil[mynumber]=eatingphil[mynumber][3];
	try {Thread.sleep(speed);} catch (InterruptedException e){}
	    phil[mynumber]=eatingphil[mynumber][2];
	try {Thread.sleep(speed);} catch (InterruptedException e){}
	    phil[mynumber]=eatingphil[mynumber][1];
	try {Thread.sleep(speed);} catch (InterruptedException e){}
	    phil[mynumber]=eatingphil[mynumber][0];
	try {Thread.sleep(speed);} catch (InterruptedException e){}
	    phil[mynumber]=thinkingphil[mynumber][0];
	try {Thread.sleep(speed);} catch (InterruptedException e){}
	  } while(Math.random()>EatChance);
      }

    void PickUpSticks(int n)
      {
/*  picks up stickes
a phil must have both sticks before it can eat*/

	int thisside,thatside;
	if(.5f>Math.random())
	  {
	    thisside= (n<4) ? n+1 : 0;
	    thatside= n;
	  }
	else
	  {
	    thatside= (n<4) ? n+1 : 0;
	    thisside= n;
	  }
/* pick up a stick */
	phil[n]=hungryphil[n][1];
	try {Thread.sleep(speed);} catch (InterruptedException e){}
	thishand[n]=Sticks[thisside].waitForStick();
	if(thishand[n]==7)
	  {
/* if its the magic stick, drop, and pick up other sticks */
	    phil[n]=hungryphil[n][2];
	try {Thread.sleep(speed);} catch (InterruptedException e){}
	    Sticks[thisside].returnStick(thishand[n]);
/* a deadlock is possible if the magic stick goes all the way around the 
   table to "thathand",  dont think it can happen though */


	    thathand[n]=Sticks[thatside].waitForStick();

	    phil[n]=hungryphil[n][3];
	try {Thread.sleep(speed);} catch (InterruptedException e){}
	    thishand[n]=Sticks[thisside].waitForStick();

	    phil[n]=hungryphil[n][0];
	try {Thread.sleep(speed);} catch (InterruptedException e){}
	    
	  }
	else
	  { 
/* get other stick */
	    phil[n]=hungryphil[n][0];
	try {Thread.sleep(speed);} catch (InterruptedException e){}
	    thathand[n]=Sticks[thatside].waitForStick();
	  }
      }

    void DropSticks(int n)
      {
/* drop sticks */
	int thisside,thatside;
	if(.5f>Math.random())
	  {
	    thisside= (n<4) ? n+1 : 0;
	    thatside= n;
	  }
	else
	  {
	    thatside= (n<4) ? n+1 : 0;
	    thisside= n;
	  }

       	Sticks[thisside].returnStick(thathand[n]);
	Sticks[thatside].returnStick(thishand[n]);
      }

    

    void whatsup()
      {
/* repaints the scene */
	for(;;)
	  {
	try {Thread.sleep(speed);} catch (InterruptedException e){}
	    repaint();
	  }
      }

  }

/* an instance of the class is created for each stick */
class sticks
{
  int Stick=0;

  int waitForStick()
    {
      return stickstuff(0);
    }
  void returnStick(int s)
    {
      stickstuff(s);
    }

  synchronized int stickstuff( int s)
/* make sure phils dont read and write at the same time */
    {
      if(s!=0)
	{
	  Stick=s;
	  notify();
	  return 0;
	}
      else
	{
	  if(Stick<1)
	    {
	      Stick=-4;
	      try {wait();} catch (InterruptedException e) {}
	    }
	  s=Stick;
	  Stick=0;
	  return s;
	}
    }
}
