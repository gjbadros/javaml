import java.awt.*;
import java.util.Hashtable;
import java.io.*;
import java.net.*;

public class escherpaint extends java.applet.Applet {
  generic_paint paint;
  int size=2;
  int linex[],liney[];
  public boolean cut[];
  int l=0;
  int sym;
  boolean keydown=false;
boolean clear=false;
 int last=0;

  public void init() {

    resize(300,300);

    linex = new int[5000];
    liney = new int[5000];
    cut =new boolean[5000];
    paint = new generic_paint(100,100);
    sym=0;

    requestFocus();

  }
  public void end(){
  }
  public void stop(){
  }
  public void update(Graphics g) {
	escher(g);
  }
  public void paint(Graphics g) {
	escher(g);
  } 
  public void escher(Graphics g){

	if(clear){
      g.setColor(java.awt.Color.lightGray);
      g.fillRect(0,0,size().width,size().height);
	clear=false;
	}
      g.setColor(java.awt.Color.black);
      g.draw3DRect(0, 0, size().width-1, size().height-1,true);
      
	int x0=0,x1=0,y0=0,y1=0;
	int sodd=0,todd=0;
	int X0,X1,Y0,Y1;

      for (int i=last;i<l-1;i++){

	if(!cut[i]){


	  for (int s=1;s<8;s++){
	  for (int t=1;t<8;t++){
		sodd = s%2;
		todd = t%2;
		if(sodd==0 && todd==0){
		x0=linex[i];
		y0=liney[i];
		x1=linex[i+1];
		y1=liney[i+1];
		} else if(sodd==0 && todd==1){
	  if(sym==0){
		x0=linex[i];
		y0=liney[i];
		x1=linex[i+1];
		y1=liney[i+1];
	  } else if(sym==1){
		x0=100-linex[i];
		y0=liney[i];
		x1=100-linex[i+1];
		y1=liney[i+1];
	  } else if(sym==2){
		x0=100-liney[i];
		y0=linex[i];
		x1=100-liney[i+1];
		y1=linex[i+1];
	  } else if(sym==3){
		x0=100-linex[i];
		y0=liney[i];
		x1=100-linex[i+1];
		y1=liney[i+1];
	  }
		} else if(sodd==1 && todd==0){
	  if(sym==0){
		x0=linex[i];
		y0=liney[i];
		x1=linex[i+1];
		y1=liney[i+1];
	  } else if(sym==1){
		x0=linex[i];
		y0=liney[i];
		x1=linex[i+1];
		y1=liney[i+1];
	  } else if(sym==2){
		x0=liney[i];
		y0=100-linex[i];
		x1=liney[i+1];
		y1=100-linex[i+1];
	  } else if(sym==3){
		x0=linex[i];
		y0=100-liney[i];
		x1=linex[i+1];
		y1=100-liney[i+1];
	  }
		} else if(sodd==1 && todd==1){
	  if(sym==0){
		x0=linex[i];
		y0=liney[i];
		x1=linex[i+1];
		y1=liney[i+1];
	  } else if(sym==1){
		x0=100-linex[i];
		y0=liney[i];
		x1=100-linex[i+1];
		y1=liney[i+1];
	  } else if(sym==2){
		x0=100-linex[i];
		y0=100-liney[i];
		x1=100-linex[i+1];
		y1=100-liney[i+1];
	  } else if(sym==3){
		x0=100-linex[i];
		y0=100-liney[i];
		x1=100-linex[i+1];
		y1=100-liney[i+1];
	  }
		}
		X0=x0+(s-4)*100;
		X1=x1+(s-4)*100;
		Y0=y0+(t-4)*100;
		Y1=y1+(t-4)*100;
		g.drawLine(X0,Y0,X1,Y1);
		g.drawLine(X0+1,Y0,X1+1,Y1);
		g.drawLine(X0,Y0+1,X1,Y1+1);
		g.drawLine(X0+1,Y0+1,X1+1,Y1+1);
		paint.drawLine(X0,Y0,X1,Y1);
		paint.drawLine(X0+1,Y0,X1+1,Y1);
		paint.drawLine(X0,Y0+1,X1,Y1+1);
		paint.drawLine(X0+1,Y0+1,X1+1,Y1+1);
	  }}
	}
      }
  }

  public void sendImage(){
      try {
 
        URL url = new URL("http://www.neuro.sfc.keio.ac.jp/aly/hotjava/upload/whiteup.cgi?sym="+sym+"&size=100&image="+paint.string());
        getAppletContext().showDocument(url);
	clear();
      } catch (IOException e) {
	  e.printStackTrace();
      }
  }

  public boolean mouseDown(java.awt.Event evt, int x, int y) {

	linex[l]=x;
	liney[l]=y;
	cut[l]=false;
	int dx=0,dy=0;
	boolean change=false;
	l++;

	if(l>1){
	if((liney[l-2]-liney[l-1])>80){
		dy=100;	change=true;
	} else if((liney[l-1]-liney[l-2])>80){
		dy=-100; change=true;	
	}
	if((linex[l-2]-linex[l-1])>80){
		dx=100;	change=true;
	} else if((linex[l-1]-linex[l-2])>80){
		dx=-100; change=true;
	}
	}
	if(change){
	linex[l-1]=x+dx;
	liney[l-1]=y+dy;
	cut[l-1]=true;

	linex[l]=x;
	liney[l]=y;
	cut[l]=false;
	l++;
	}
	repaint();
	return true;
  }
  public boolean mouseDrag(java.awt.Event evt, int x, int y) {
	return mouseDown(evt, x,y);
  }
  public boolean mouseExit(java.awt.Event evt){
	paint.cut();
	last=l;
	return true;
  }
  public boolean mouseUp(java.awt.Event evt, int x,int y){
	cut[l-1]=true;
	paint.cut();
	last=l;
	return true;
  }
  public boolean keyDown(java.awt.Event evt, int key){
        char c=(char)key;
	if(c=='4'){
		sym=0;
		flush();
	} else if(c=='1'){
		sym=1;
		flush();
	} else if(c=='2'){
		sym=2;
		flush();
	} else if(c=='3'){
		sym=3;
		flush();
        } else if(c=='s'){
                sendImage();
        } else if(c=='r'){
		flush();
        } else if(c=='c'){
		clear();
	}
	keydown=true;
	repaint();
	return true;
  }
  public void flush (){
		paint.clear();
		clear=true;
		last=0;		
  }
  public void clear (){
	flush();
	l=0;
  }
}
