/*----------------------------------------------------------------------*/

/*----------------------------------------------------------------------*/
/* fillTriangle  - simply draws a solid triangle  
                 - the hope was to make it quick with integer
                   calculations (short would do but java need me to do 
                   explicit type casting)...well it works 
                 - I'm using the drawLine routine..since all the lines 
                   are parallel to the axis this is not optimal..possibly
                   using drawRect would be quicker
                 - this routine will most likely be obsolete with newer 
                   versions of java so I will leave it as is--ugly      */
/*----------------------------------------------------------------------*/
/*               Jim Morey  -  morey@math.ubc.ca  - Aug 5               */
/*----------------------------------------------------------------------*/
  
import java.io.InputStream;
import java.awt.*;
import java.net.*;

/*----------------------------------------------------------------------*/

class fillTriangle{
  int vect[][]; /* .. coordinates of the 3 vertices .. */
  private Graphics gr;

  fillTriangle (Graphics g) {
    gr = g;
    vect = new int[3][2];
  }
    
  public void run(){
    int xmax=0,xmin=0,ymax=0,ymin=0,bad,good, v[][], best,i,j;
    int top[],bottom[],t_inc,b_inc,t_rem,b_rem,t_len,b_len,t_hei,b_hei; 
    int t_temp, b_temp,t_one,b_one;

    v = new int[3][2];

    /* .. decide which axis is good to draw with -- it will save 
          on calculations especially with skinny triangles .. */

    for(i=0;i<3;i++){
      if (vect[i][0]>vect[xmax][0]) xmax = i;
      if (vect[i][0]<vect[xmin][0]) xmin = i;
      if (vect[i][1]>vect[ymax][1]) ymax = i;
      if (vect[i][1]<vect[ymin][1]) ymin = i;
    }

    /* .. sort according to the 'good axis' */
    if (vect[xmax][0]-vect[xmin][0] > vect[ymax][1]-vect[ymin][1]) {
      good = 1;
      bad = 0;
      v[0][0] = vect[ymin][0];
      v[0][1] = vect[ymin][1];
      v[1][0] = vect[3-ymax-ymin][0];
      v[1][1] = vect[3-ymax-ymin][1];
      v[2][0] = vect[ymax][0];
      v[2][1] = vect[ymax][1];
    } else{
      good = 0;
      bad = 1;
      v[0][0] = vect[xmin][0];
      v[0][1] = vect[xmin][1];
      v[1][0] = vect[3-xmax-xmin][0];
      v[1][1] = vect[3-xmax-xmin][1];
      v[2][0] = vect[xmax][0];
      v[2][1] = vect[xmax][1];
    } 
 
    /* .. top and bottom may not be the "top" and "bottom" but these are the 
          points that will trace out the triangle with the top point going 
          over the middle vertex.. */

    top = new int[2];
    bottom = new int[2];

    /* .. draw the first part of the triangle up till the middle vertex .. */
    bottom[bad] = v[0][bad];
    b_len = v[2][good]-v[0][good];
    b_hei = v[2][bad]-v[0][bad];
    b_temp = b_len/2;
    if (b_len!=0) {
      b_one = (b_hei >0) ? 1 : -1;
      b_inc = b_hei/b_len;
      b_rem = b_hei - b_inc*b_len;
      if (b_rem < 0 ) b_rem *= -1;

      top[bad] = v[0][bad];
      t_len = v[1][good]-v[0][good];
      t_hei = v[1][bad]-v[0][bad];
      if (t_len!=0) {
        t_one = (t_hei >0) ? 1 : -1;
        t_inc = t_hei/t_len;
        t_rem = t_hei - t_inc*t_len;
        if (t_rem < 0 ) t_rem *= -1;
        t_temp = t_len/2;
    
        for (i=v[0][good];i<=v[1][good];i++){
          top[good] = i;
          bottom[good] = i;
          gr.drawLine(top[0],top[1], bottom[0], bottom[1]);
          top[bad] += t_inc;
          bottom[bad] += b_inc;
          t_temp += t_rem;
          b_temp += b_rem;
          if (t_temp >= t_len) {
            t_temp -= t_len;
            top[bad]+=t_one;
          }
          if (b_temp >= b_len) {
            b_temp -= b_len;
            bottom[bad]+=b_one;
          }
        }
      }

      /* .. draw the second part of the triangle .. */
      top[bad] = v[1][bad];
      t_len = v[2][good]-v[1][good];
      t_hei = v[2][bad]-v[1][bad];
      if (t_len!=0) {
        t_one = (t_hei >0) ? 1 : -1;
        t_inc = t_hei/t_len;
        t_rem = t_hei - t_inc*t_len;
        if (t_rem < 0 ) t_rem *= -1;
        t_temp = t_len/2;
    
        for (i=v[1][good]+1;i<=v[2][good];i++){
          top[good] = i;
          bottom[good] = i;
          gr.drawLine(top[0],top[1], bottom[0], bottom[1]);
          top[bad] += t_inc;
          bottom[bad] += b_inc;
          t_temp += t_rem;
          b_temp += b_rem;
          if (t_temp >= t_len) {
            t_temp -= t_len;
            top[bad]+=t_one;
          }
          if (b_temp >= b_len) {
            b_temp -= b_len;
            bottom[bad]+=b_one;
          }
        }
      }
    }
  }
}
