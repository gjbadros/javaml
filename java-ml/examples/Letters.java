/////////////////////////////////////////////////////////////////////
//  Letters.java   -- LED Sign V1.0f
//  
//  This class parses the font file and stores
//  each letter in an array of boolean (on/off).
//  It takes care of all the storage and
//  retrieval of letters data structure.
//
//  Revisions:
//     V1.0f: Written July 13 - 14, 1995
//
//  By Darrick Brown
//     dbrown@cs.hope.edu
//     http://www.cs.hope.edu/~dbrown/
//
//  © Copyright 1995
/////////////////////////////////////////////////////////////////////

package LED;

import java.awt.*;
import java.io.*;
import java.net.*;

// my struct :)
class Index
{
   byte ch;
   int width;
   boolean letter[][];

   Index(byte b, int w, int h)
   {
      letter = new boolean[w][h];
      width = w;
      ch = b;
   }
}


//////////////////////////////////////////////////////////////////
// The Letters Class
//////////////////////////////////////////////////////////////////
class Letters
{
   int HEIGHT,TOTAL;
   String let;
   URL baseURL;
   InputStream file;
   DataInputStream dis;
   int w,h,num,place,len,space,swidth;
   Index index[];
   
   
   //////////////////////////////////////////////////////////////////
   // The class constructor
   public Letters(URL baseURL, String URLfile, int width) throws IOException
   {
      // Set some initial variables
      file = (new URL(baseURL,URLfile)).openStream();
      dis = new DataInputStream(file);
      swidth = width;
      initLetters();  
   }

   //////////////////////////////////////////////////////////////////
   public int height()
   {
      return HEIGHT;
   }

   //////////////////////////////////////////////////////////////////
   // Read in the letters
   void initLetters() throws IOException
   {
      int a,b,c;
      byte ch;     // the character of the letter
      int i,j,k;
      String s;    // A line in the font file
      boolean done;
      int width;

      // Just to make the compiler shut up about
      // these "may not be initialized".
      w = 5;
      h = 5;
      num = 100;

      // find the size().height
      done = false;
      while(!done)
      {
         s = dis.readLine();
         if(!s.startsWith("!!")) // If is not a comment line
         {
            h = (new Integer(s)).intValue();
            HEIGHT = h*5;
            done = true;
         }
      }

      // find the width
      done = false;
      while(!done)
      {
         s = dis.readLine();
         if(!s.startsWith("!!")) // If is not a comment line
         {
            w = (new Integer(s)).intValue();
            done = true;
         }
      }

      // Find the number of characters
      done = false;
      while(!done)
      {
         s = dis.readLine();
         if(!s.startsWith("!!")) // If is not a comment line
         {
            num = (new Integer(s)).intValue();
            done = true;
         }
      }

      // The "num+1" allocates the extra array position for " " (space)
      index = new Index[num+1];

      // Ok we gots the data, lets read in the characters!
      for(i=0;i<num;i++)
      {
         // to make the compiler shut up about how
         // these "may not have been initialized"
         ch = 2;
         width = 10;

         //read the header for the letter
         done = false;
         while(!done)
         {
            s = dis.readLine();
            if(!s.startsWith("!!")) // If is not a comment line
            {
               ch = (byte)s.charAt(0);
               done = true;
            }
         }
         done = false;
         while(!done)
         {
            s = dis.readLine();
            if(!s.startsWith("!!")) // If is not a comment line
            {
               width = (new Integer(s)).intValue();
               done = true;
            }
         }

         // initialize the struct
         index[i] = new Index(ch,width,h);

         // read in the character
         for(j=0;j<h;j++)
         {
            done = false;
            s = "";
            while(!done)
            {
               s = dis.readLine();

               if(s.length() > 0)
               {
                  if(!s.startsWith("!!")) // If is not a comment line
                  {
                     done = true;
                  }
               }
               else
               {
                  s = " ";
                  done = true;
               }
            }

            for(k=0;k<index[i].width;k++)
            {
               if(k>=s.length())
               {
                  index[i].letter[k][j] = false;
               }
               else
               {
                  if(s.charAt(k) == '#')
                     index[i].letter[k][j] = true;
                  else
                     index[i].letter[k][j] = false;
               }
            }
         }
      } // end reading in the letters

      index[num] = new Index((byte)32,swidth,h);

      // close the datastreams
      file.close();
      dis.close();

   } // end of InitLetters()

   //////////////////////////////////////////////////////////////////
   // find the LED letter and return it
   public Index getLetter(char c)
   {
      int j;

      if(c == (char)(32))
      {
         j = num; // I know where this one is!
      }
      else
      {
         // look for it
         j = 0;
         while(c != index[j].ch && j < num)
            j++;
      }

      return index[j];
   } // End getLetter()
} // End Letters Class
