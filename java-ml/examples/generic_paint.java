import java.lang.*;

class generic_paint {
boolean paint[];
boolean painting=true;
int width,height;
public int lastx,lasty;

	generic_paint(int iwidth, int iheight){

		width=iwidth-1;
		height=iheight-1;
		paint = new boolean[(width+1)*(height+1)];  	
		lastx=-1;
		lasty=-1;

		for(int i=0;i<(height+1);i++){
		for(int j=0;j<(width+1);j++){
			paint[(width+1)*i+j]=false;
		}}
	}
	public boolean painted(int x,int y){
		if(paint[y*(width+1)+x]){
			return true;
		} else {
			return false;
		}
	}
	private void paint(int x,int y){
		if(x>=0 && y>=0 && x<=width && y <= height ){
		paint[y*(width+1)+x]=painting;
		}
	}
	public String string(){
		String str ="";		
		
		int a=32,b=0; 
		for(int j=0;j<=height;j++){
		for(int i=0;i<=width;i++){
			a=a/2; 
			if(paint[(width+1)*j+i]){
				b=b+a;	
			}
			if(a==1){
				str=str+Integer.toString(b,32);
				a=32;
				b=0;
			}
		}}
		return str;
	}
	public boolean cutted(){
		if(lastx!=-1 && lasty!=-1){
			return false;
		} 
		return true;
	}
	public void last(int x,int y){
		lastx=x;
		lasty=y;
	}
	public void cut(){
		lastx=-1;
		lasty=-1;
	}
	public void drawLine(int xFrom,int yFrom,int xTo,int yTo){
  	int dx, dy, inerE, inerNE, d,x,y;

  		dx = xTo - xFrom;
  		dy = yTo - yFrom;

  		if (((dx < 0 ) && (-dx > dy)) || ((dy < 0) && (-dy > dx))){
			x = xFrom;
			y = yFrom;
			xFrom = xTo;
			yFrom = yTo;
			xTo = x;
			yTo = y;
			dy = - dy;
			dx = - dx;
		}
		x = xFrom;
		y = yFrom;

		paint(x,y);
		//board[x][y] = POINT1;

		if (dx > dy){
      		if (dy < 0){
			d = -2*dy- dx;
			inerE  = -2*dy;
			inerNE = 2*(-dy-dx);

			for(x = xFrom+1;x < xTo; x++){
				if (d <= 0) {
					d += inerE;
				} else {
					d += inerNE;
					y--;
				}
				paint(x,y);
				//board[x][y] = LINE;
			}
		} else {
			d = 2*dy- dx;
			inerE  = 2*dy;
			inerNE = 2*(dy-dx);

			for(x = xFrom+1;x < xTo; x++){
				if (d <= 0){
					d += inerE;
				} else {
					d += inerNE;
					y++;
				}
				paint(x,y);
				//board[x][y] = LINE;
			}
		}
    		} else {
		if (dx < 0) {
			d = -2*dx- dy;
			inerE  = -2*dx;
			inerNE = 2*(-dx-dy);

			for(y = yFrom+1;y < yTo; y++){
				if (d <= 0) {
					d += inerE;
				} else {
					d += inerNE;
					x--;
				}
				paint(x,y);
				//board[x][y] = LINE;
			}
		} else {
			d = 2*dx- dy;
			inerE  = 2*dx;
			inerNE = 2*(dx-dy);
			for(y = yFrom+1;y < yTo; y++) {
				if (d <= 0) {
					d += inerE;
				} else {
					d += inerNE;
					x++;
				}
				paint(x,y);
				//board[x][y] = LINE;
            		}
        	}
    		}

	} 
	public void clear (){
		for(int i=0;i<(height+1);i++){
		for(int j=0;j<(width+1);j++){
			paint[(width+1)*i+j]=false;
		}}
	}
}


