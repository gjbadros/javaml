///////////////////////////////////////////////////////////////////
//  Script.java   -- LED Sign V1.0f
//
//  Contains the following classes:
//      linkList   -- a linked list class to store the script
//      FuncInfo   -- a class (struct) to hold all the 
//                    information for any function.
//      Script     -- The class that manages the script
//                    including parsing, storage, and
//                    retrieval.
//
//  Revisions:
//     V1.0f: Written July 17 - August 6, 1995
//
//  by Darrick Brown
//     dbrown@cs.hope.edu
//     http://www.cs.hope.edu/~dbrown/
//
//  © Copyright 1995
///////////////////////////////////////////////////////////////////

package LED;

import java.awt.*;
import java.io.*;
import java.net.*;

///////////////////////////////////////////////////////////////////
// Function            Code
// --------            ----
// Appear               0
// Sleep                1
// ScrollLeft           2
// ScrollRight          3
// ScrollUp             4
// ScrollDown           5
// Pixel                6
// Blink                7
// OverRight            8
// ScrollCenter         9
// OverCenter           10
// OverLeft             11
// OverUp               12
// OverDown             13
// Do                   97
// Repeat               98
// Reload               99
///////////////////////////////////////////////////////////////////

// A hacked linked list
class linkList
{
   FuncInfo fi;
   linkList next;
}

///////////////////////////////////////////////////////////////////
// The "struct" that contains all the information
// than any function/transition would need.
class FuncInfo
{
   int func;
   int delay;
   int startspace, endspace;
   int times, remaining;
   boolean centered;
   String color;
   String text;
   linkList ret;  // pointer to the return place in the script (for loops);
}

///////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////
// The class that parses the script and keeps it in memory 
class Script
{
   linkList list;               // the linked list for the script
   linkList ptr,start;          // the current line and start of the list
   String scrpt;
   URL baseURL;

   ///////////////////////////////////////////////////////////////////
   // The constructor
   public Script(URL url, String s) throws IOException
   {
      scrpt = s;
      baseURL = url;
      initScript();
   }

   ///////////////////////////////////////////////////////////////////
   // get the parameters from the functions in the script
   String getParam(String s, String sub)
   {
      int i,j;
      String tmp;

      i = s.indexOf(sub);
      j = s.indexOf("text");

      if(j == -1 || i <= j)  // if the first occurance of "sub" is before 
      {                    // the "text=" (ie not in the message)
         if(i == -1)
            return null;
         else
         {
            tmp = s.substring(i);  // forget everything before the sub
            i = tmp.indexOf("=");
            if(i == -1)
            {
               System.out.println("Error in '"+sub+"' parameter in "+s);
               return null;
            }
            else
            {
               i++;  // one spot after the "="
               if(sub.compareTo("text") == 0)
                  tmp = tmp.substring(i);
               else
               {
                  tmp = tmp.substring(i);
                  if(tmp.indexOf(" ") != -1)
                     tmp = tmp.substring(0,tmp.indexOf(" "));
               }
               tmp.trim();
               return tmp;
            }
         }
      }
      else
         return null;

   }  // End getParam()
   
   ///////////////////////////////////////////////////////////////////
   // get the function info
   FuncInfo getFunc(String s)
   {
      int i;
      String tmp;
      FuncInfo fi = new FuncInfo();
      
      // Assign the defaults
      fi.func = -1;
      fi.delay = 40;
      fi.startspace = 10;
      fi.endspace = 20;
      fi.times = -1;
      fi.remaining = 0;
      fi.centered = false;
      fi.color = new String("");
      fi.text = new String("No text specified");
      fi.ret = null;
      
      //get rid of any starting (and ending) white space, just to be sure.
      s = s.trim(); 

      ////////////////////////////////////////////////////
      // Any parameters that might exist.  This will
      // read in any command line parameters for each
      // function.  For example: Sleep text=blah blah
      // is accepted, but the text will never be used

      tmp = getParam(s,"delay");
      if(tmp != null)
         fi.delay = (new Integer(tmp)).intValue();

      tmp = getParam(s,"clear");
      if(tmp != null && tmp.compareTo("true") == 0)
      {
         fi.centered = true;
         fi.text = new String("");
      }
      else
      {
         tmp = getParam(s,"center");
         if(tmp != null && tmp.compareTo("true") == 0)
            fi.centered = true;
         else
         {
            fi.centered = false;
            tmp = getParam(s,"startspace");
            if(tmp != null)
               fi.startspace = (new Integer(tmp)).intValue();

            tmp = getParam(s,"endspace");
            if(tmp != null)
               fi.endspace = (new Integer(tmp)).intValue();
         }

         tmp = getParam(s,"text");
         if(tmp != null)
            fi.text = tmp;
      }

      tmp = getParam(s,"times");
      if(tmp != null)
      {
         fi.times = (new Integer(tmp)).intValue();
         fi.remaining = fi.times;
      }

      tmp = getParam(s,"pixels");
      if(tmp != null)
      {
         fi.times = (new Integer(tmp)).intValue();
         fi.remaining = fi.times;
      }

      ////////////////////////////////////////////////////
      // set the function number (and some minor
      // tweeks/precautions)
      i = s.indexOf(" ");
      if(i != -1)
         tmp = s.substring(0,i);
      else
         tmp = s;
         
      if(tmp.compareTo("Appear") == 0)
      {
         fi.func = 0;
      }
      else if(tmp.compareTo("Sleep") == 0)
      {
         fi.func = 1;
      }
      else if(tmp.compareTo("ScrollLeft") == 0)
      {
         fi.func = 2;
      }
      else if(tmp.compareTo("ScrollRight") == 0)
      {
         fi.func = 3;
      }
      else if(tmp.compareTo("ScrollUp") == 0)
      {
         fi.func = 4;
      }
      else if(tmp.compareTo("ScrollDown") == 0)
      {
         fi.func = 5;
      }
      else if(tmp.compareTo("Pixel") == 0)
      {
         fi.func = 6;
         
         // Just for precautions dealing with a delay problem.
         // This shouldn't be noticable.
         if(fi.delay < 1)
            fi.delay = 1;

         // Can't allow "times" to be 0 or less, it will cause
         // the sign to freeze (not procede).
         if(fi.times < 1)
            fi.times = 15;
      }
      else if(tmp.compareTo("Blink") == 0)
      {
         fi.func = 7;
         
         if(fi.times < 1)
            fi.times = 2;
      }
      else if(tmp.compareTo("OverRight") == 0)
      {
         fi.func = 8;
      }
      else if(tmp.compareTo("ScrollCenter") == 0)
      {
         fi.func = 9;
      }
      else if(tmp.compareTo("OverCenter") == 0)
      {
         fi.func = 10;
      }
      else if(tmp.compareTo("OverLeft") == 0)
      {
         fi.func = 11;
      }
      else if(tmp.compareTo("OverUp") == 0)
      {
         fi.func = 12;
      }
      else if(tmp.compareTo("OverDown") == 0)
      {
         fi.func = 13;
      }
      else if(tmp.compareTo("Do") == 0)
      {
         fi.func = 97;  // This marks a place for the "repeats" to go back to.
      }
      else if(tmp.compareTo("Repeat") == 0)
      {
         fi.func = 98;
      }
      else if(tmp.compareTo("Reload") == 0)
      {
         fi.func = 99;
      }

      return fi;
   }  // End getFunc()

   //////////////////////////////////////////////////////////////////
   // get the next function
   FuncInfo nextFunc()
   {
      FuncInfo fi;

      fi = ptr.fi;
      ptr = ptr.next;
      
      switch(fi.func)
      {
         case 97:  // Do
            fi = nextFunc();   // skip the "Do function; its just a marker
           break;

         case 98:  // a Repeat

            // If it doesn't repeat infinitely...
            if(fi.times != -1)
            {
               // One less time
               fi.remaining--;
               if(fi.remaining == 0)
               {
                  fi.remaining = fi.times;  // reset the loop
                  fi = nextFunc();
               }
               else
               {
                  ptr = fi.ret;  // Jump back to the last "Do"
                  fi = nextFunc();
               }
            }
            else
            {
               ptr = fi.ret;  // Jump back to the last "Do"
               fi = nextFunc();
            }
           break;

         case 99:  // Reload
            try {
		initScript();      // Reload the script from the URL
	    } catch (IOException e) {
	    }
            fi = nextFunc();   // and get the first function.
           break;
      }

      return fi;
   }  // End nextFunc()

   //////////////////////////////////////////////////////////////////
   // just a simple function to see if it is a color code
   boolean isColor(char t)
   {
      if(t == 'r' || t == 'g' || t == 'b' || t == 'y' || t == 'o' || t == 'p')
         return true;
      else
         return false;
   }
      
   //////////////////////////////////////////////////////////////////
   // Read in the script into a linked list of FuncInfo's 
   void initScript() throws IOException
   {
      InputStream file;
      DataInputStream dis;
      String line;
      String tmp;
      int listlen;
      int a,b;
      int dos;
      char c;
      char t;

      file = (new URL(baseURL,scrpt)).openStream();
      dis = new DataInputStream(file);

      list = new linkList();                                    // The linked list
      start = list;                                             // The head of the list
      ptr = list;                                               // The current element
      listlen = 0;
      dos = 0;                                                  // Used to know how many Do's there are
      while((line = dis.readLine()) != null)
      {
         line = line.trim();                                    // cut off white space at the beginning and end
         if(!(line.startsWith("!!")) && (line.length() != 0))   // Not a comment or blank line
         {
            listlen++;
            ptr.fi = getFunc(line);                             // Get the function number
            if(ptr.fi.func == 97)
               dos++;                                           // Chalk up another "Do"
            ptr.next = new linkList();
            ptr = ptr.next;  // advance to the next command
         }
      }

      // Parse out the color codes!!!!  (if there are any)
      ptr = start;
      for(a=0;a<listlen;a++)
      {
         if(ptr.fi.func >= 2 && ptr.fi.func <= 97)
         {
            tmp = ptr.fi.text;
            c = 'r';  // the default color
            for(b=0;b<tmp.length();b++)
            {
               if((char)(tmp.charAt(b)) == (char)('\\'))  // if there is a '\' does the following
               {                                          // letter indicate a color.
                  b++;
                  t = tmp.charAt(b);
                  if(isColor(t))
                  {
                     c = t;
                     tmp = (tmp.substring(0,b-1)).concat(tmp.substring(b+1));  // take the "\r" out
                     b-=1;
                  }
                  else if(t == '\\')  // Are they trying to delimit the backslash?
                  {
                     tmp = (tmp.substring(0,b)).concat(tmp.substring(b+1));  // delimit the '\'
                     b--;
                  }
               }

               ptr.fi.color = ptr.fi.color.concat((new Character(c)).toString());
            }
            ptr.fi.text = tmp;
         }
         ptr = ptr.next;
      }

      // Ok now lets set the return pointers for the loops
      ptr = start;
      linkList stack[] = new linkList[dos];  // Allocate the array
      dos = 0;
      for(a=0;a<listlen;a++)
      {
         if(ptr.fi.func == 97)
         {
            stack[a] = new linkList();
            stack[a] = ptr;
            dos++;
         }
         else if(ptr.fi.func == 98)
         {
            if(dos > 0)
            {
               ptr.fi.ret = stack[dos-1];
               dos--;
            }
            else
            {
               // OMYGOSH!! Script error output!!!!
               System.out.println("Repeat error in line : Repeat times="+ptr.fi.times);
               System.out.println("     Mismatched Do/Repeats?");
            }
         }
         ptr = ptr.next;
      }

      ptr = start;

      file.close();
      dis.close();

   }  // End initScript()
}  // End Class Script
