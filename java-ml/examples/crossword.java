//-------------------------------------------------------
//
// Written by Carl Haynes
//
// Submitted to Sun Microsystems for their
// java.applet.Applet Contest 8/31/95
//
//--------------------------------------------------------

import java.awt.*;

public class crossword extends java.applet.Applet {
    final	static	int	kAppWidth 	= 500;
    final	static	int	kAppHeight 	= 550;

    final	static	int	kBlockWidth	= 30;
    final	static	int	kBlockHeight	= 30;

    final	static	int	kBlocksWide	= 15;
    final	static	int	kBlocksHigh	= 15;
    final 	static	int	kButtonWidth	= 100;
    final 	static	int	kButtonHeight	= 25;

    final	static	int	kUp = 0;
    final	static	int	kDown = 1;

    final static 	int kAcross = 0;

    final static int kPadding =	20;
    final static int kQuestionAreaHeight = 40;

    int gDirection = kAcross;
    int gCurX	= 0;
    int	gCurY	= 0;

    int		clipLeft = -1;
    int		clipTop = -1;
    int		clipWidth = -1;
    int 		clipHeight = -1;

    int		gBlockMinY	= 0;
    int		gBlockMaxY 	= 0;
    int		gBlockMinX = 0;
    int		gBlockMaxX	= 0;

    int		gOldBlockMinY	= 0;
    int		gOldBlockMaxY 	= 0;
    int		gOldBlockMinX = 0;
    int		gOldBlockMaxX	= 0;

    String	letters[] = {	"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
				"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    /*---------------------------------------------------------------*/
    /*---------------------------------------------------------------*/

    final static int 	layout[][] = {
    {1, 2, 3, -1, -1, 4, 5, 6, 7, -1, -1, 8, 9, 10, -1},
    {11, 00, 00, 12, -1, 13, 00, 00, 00, -1, 14, 00, 00, 00, 15},
    {16, 00, 00, 00, -1, 17, 00, 00, 00, -1, 18, 00, 00, 00, 00},
    {-1, 19, 00, 00, 20, 00, -1, 21, 00, 22, 00, -1, 23, 00, 00},
    {-1, 24, 00, 00, 00, 00, -1, -1, -1, 25, 00, 26, -1, -1, -1},
    {-1, -1, -1, -1, 27, 00, 28, 29, -1, 30, 00, 00, 31, 32, -1},
    {33, 34, 35, 36, 00, -1, 37, 00, 38, 00, -1, 39, 00, 00, 40},
    {41, 00, 00, 00, -1, -1, 42, 00, 00, -1, -1, 43, 00, 00, 00},
    {44, 00, 00, 00, -1, 45, 00, 00, 00, -1, 46, 00, 00, 00, 00},
    {-1, 47, 00, 00, 48, 00, -1, 49, 00, 50, 00, -1, -1, -1, -1},
    {-1, -1, -1, 51, 00, 00, -1, -1, -1, 52, 00, 53, 54, 55, -1},
    {56, 57, 58, -1, 59, 00, 60, 61, -1, 62, 00, 00, 00, 00, -1},
    {63, 00, 00, 64, 00, -1, 65, 00, 66, 00, -1, 67, 00, 00, 68},
    {69, 00, 00, 00, 00, -1, 70, 00, 00, 00, -1, 71, 00, 00, 00},
    {-1, 72, 00, 00, -1, -1, 73, 00, 00, 00, -1, -1, 74, 00, 00}
    };


    final static String answers[][] = {
    {"G", "A", "D", " ", " ", "C", "R", "A", "B", " ", " ", "B", "A", "D", " "},
    {"A", "B", "E", "D", " ", "A", "U", "R", "A", " ", "B", "A", "B", "E", "L"},
    {"G", "A", "M", "E", " ", "G", "E", "A", "R", " ", "A", "R", "E", "N", "A"},
    {" ", "C", "O", "V", "E", "N", " ", "B", "E", "E", "N", " ", "L", "E", "G"},
    {" ", "A", "S", "I", "D", "E", " ", " ", " ", "G", "A", "B", " ", " ", " "},
    {" ", " ", " ", " ", "D", "Y", "A", "D", " ", "A", "L", "L", "A", "H", " "},
    {"C", "A", "C", "A", "O", " ", "B", "E", "A", "D", " ", "A", "D", "A", "M"},
    {"A", "M", "O", "K", " ", " ", "B", "I", "G", " ", " ", "D", "I", "K", "E"},
    {"B", "I", "D", "E", " ", "D", "A", "C", "E", " ", "B", "E", "T", "E", "L"},
    {" ", "D", "E", "L", "A", "Y", " ", "E", "S", "A", "U", " ", " ", " ", " "},
    {" ", " ", " ", "A", "R", "K", " ", " ", " ", "B", "R", "E", "A", "D", " "},
    {"J", "A", "G", " ", "D", "E", "A", "F", " ", "A", "P", "P", "L", "E", " "},
    {"A", "B", "O", "V", "O", " ", "B", "L", "O", "C", " ", "E", "L", "L", "S"},
    {"B", "U", "Y", "E", "R", " ", "B", "E", "A", "U", " ", "E", "A", "V", "E"},
    {" ", "T", "A", "T", " ", " ", "E", "A", "R", "S", " ", " ", "Y", "E", "A"}
    };



    final static String gQuestionsAcross[] = {
	"",			//  0
	"Goad for driving cattle", // 1
	"",			   // 2
	"",			   // 3
	"Crustacean",		   // 4
	"",			   // 5
	"",			   // 6
	"",			   // 7
	"Not good",		   // 8
	"",			   // 9
	"",			   // 10
	"In bed",		   // 11
	"",			   // 12
	"Distinctive quality",	   // 13
	"Confused mixture of sounds", // 14
	"",			      // 15
	"Amusement",		      // 16
	"Equipment",		      // 17
	"Area used for sports",	      // 18
	"Assembly of witches",	      // 19
	"",			      // 20
	"Part of verb to be",	      // 21
	"",			      // 22
	"Lower limb",		      // 23
	"To one side",		      // 24
	"Chatter",		      // 25
	"",			      // 26
	"Group of two",		      // 27
	"",			      // 28
	"",			      // 29
	"Supreme Being",	      // 30
	"",			      // 31
	"",			      // 32
	"Source of cocoa",	      // 33
	"",			      // 34
	"",			      // 35
	"",			      // 36
	"Glass ornament",	      // 37
	"",			      // 38
	"First man",		      // 39
	"",			      // 40
	"Diventare pazzo",	      // 41
	"Large",		      // 42
	"Embankment",		      // 43
	"Remain",		      // 44
	"Fresh-water fish",	      // 45
	"East Indian pepper plant",   // 46
	"Postpone",		      // 47
	"",			      // 48
	"Son of Isaac and Rebekah",   // 49
	"",			      // 50
	"Vessel built by Noah",	      // 51
	"Baked dough",		      // 52
	"",			      // 53
	"",			      // 54
	"",			      // 55
	"Sharp projection",	      // 56
	"",			      // 57
	"",			      // 58
	"Deprived of the sense of hearing", // 59
	"",				    // 60
	"",				    // 61
	"Edible fruit",			    // 62
	"From the beginning",		    // 63
	"",				    // 64
	"Cartel",			    // 65
	"",				    // 66
	"Old cloth measures",		    // 67
	"",				    // 68
	"Purchaser",			    // 69
	"Sweetheart",			    // 70
	"Overhanging lower edge of a roof", // 71
	"Make lace",			    // 72
	"Hearing organs",		    // 73
	"Affirmative reply"		    // 74
	};


    final static String gQuestionsDown[] = {
	"",			//  0
	"Bavaglio",		// 1
	"Manila hemp plant",	// 2
	"The common people",	// 3
	"U.S. film actor",	// 4
	"Regret",		// 5
	"Bedouin",		// 6
	"Uncover",		// 7
	"Ingot",		// 8
	"Second son of Adam and Eve", // 9
	"Sandy tract",		      // 10
	"",			      // 11
	"Hindu mother goddess",	      // 12
	"",			      // 13
	"Trite",		      // 14
	"Fall behind",		      // 15
	"",			      // 16
	"",			      // 17
	"",			      // 18
	"",			      // 19
	"Root of the taro",	      // 20
	"",			      // 21
	"Mild oath",		      // 22
	"",			      // 23
	"",			      // 24
	"",			      // 25
	"Part of an ice skate",	      // 26
	"",			      // 27
	"Title of respect for God",   // 28
	"Free of ice",		      // 29
	"",			      // 30
	"Entrance",		      // 31
	"Codlike fish",		      // 32
	"Taxicab",		      // 33
	"Among",		      // 34
	"Message symbols",	      // 35
	"Pack leader",		      // 36
	"",			      // 37
	"Matures",		      // 38
	"",			      // 39
	"Honey",		      // 40
	"",			      // 41
	"",			      // 42
	"",			      // 43
	"",			      // 44
	"Female homosexual",	      // 45
	"Relieve gas",		      // 46
	"",			      // 47
	"Zeal",			      // 48
	"",			      // 49
	"Calculating device",	      // 50
	"",			      // 51
	"",			      // 52
	"Fencing sword",	      // 53
	"Alleviate",		      // 54
	"Research deeply",	      // 55
	"Punch",		      // 56
	"Adjoin",		      // 57
	"Spanish painter",	      // 58
	"",			      // 59
	"French clergyman",	      // 60
	"Bloodsucking insect",	      // 61
	"",			      // 62
	"",			      // 63
	"Veterinarian",		      // 64
	"",			      // 65
	"Lever for rowing",	      // 66
	"",			      // 67
	"Ocean"			      // 68
	};

    /*---------------------------------------------------------------*/

    String gGuesses[][] = new String[kBlocksWide][kBlocksHigh];

    boolean	gUpdateActiveAreaFlag = false;
    boolean	gChangedActiveAreaFlag = false;

    Font buttonFont = null;
    Font tileFont = null;

    public void init() {
	int viewWidth;
	int left, top;
    
	resize((kBlocksWide * kBlockWidth) + (kPadding * 2), (kBlocksHigh * kBlockHeight) + (kPadding * 3) + kQuestionAreaHeight);
    
	NewGame();
    
    
	buttonFont = new java.awt.Font("Courier", Font.PLAIN, 12);
	tileFont = new java.awt.Font("Helvetica", Font.PLAIN, 36);
    
    }

    /*---------------------------------------------------------------*/

    public void NewGame() {
	for (int j = 0 ; j < kBlocksHigh ; j++) {
	    for (int i = 0 ; i < kBlocksWide ; i++) {
		gGuesses[i][j] = "";	
	    }
	}

	gOldBlockMinY	= 0;
	gOldBlockMaxY 	= 0;
	gOldBlockMinX = 0;
	gOldBlockMaxX	= 0;

	gDirection = kAcross;
	gCurX = 0;
	gCurY = 0;
	SetActiveBlock(gCurX, gCurY, gDirection);

    }

    /*----------------------------------------------*/

    public void paint(Graphics g) {
	int left = 0;
	int right = kAppWidth - 1;
	int top = 0;
	int bottom = kAppHeight - 1;
	int tempLeft = 0;
	int tempRight = 0;
	int tempTop = 0;
	int	viewWidth;
	int	viewHeight;
	int buttonWidth = 0;
	int buttonHeight = 0;
	int buttonLeft = 0;
	int buttonTop = 0;
	int tileLeft;
	int tileTop;
    
	Font f = new java.awt.Font("Helvetica", 0, 12);
	g.setFont(f);
    
	Font numFont = new java.awt.Font("Helvetica", 0, 10);
    
	Font answerFont = new java.awt.Font("Helvetica", 0, 18);
	Font questionFont = new java.awt.Font("Courier", 0, 24);
	Font questionFont18 = new java.awt.Font("Courier", 0, 18);

	FontMetrics answerFontMetrics = g.getFontMetrics(answerFont);
	FontMetrics questionFontMetrics = g.getFontMetrics(questionFont);
	FontMetrics questionFont18Metrics = g.getFontMetrics(questionFont18);
    
	g.setColor(Color.lightGray);
	g.draw3DRect(0, 0, size().width - 1, size().height - 1, true);
    
	viewWidth = kBlocksWide * kBlockWidth;
	viewHeight = kBlocksHigh * kBlockHeight;
    
    
	top = kPadding;
	left = (size().width / 2) - (viewWidth / 2);
	g.setColor(Color.white);
	g.fill3DRect(left, top, viewWidth, kQuestionAreaHeight, false);
    
	g.setFont(f);
	String s = new String(	String.valueOf(layout[gBlockMinY][gBlockMinX]));
	s = s.concat(" - ");
	if (gDirection == kAcross)
	    s = s.concat("across");
	else
	    s = s.concat("down");
	g.drawString(s, left + 5, top + 12);	
	g.setFont(questionFont);
	if (gDirection == kAcross) {
	    Font userFont = questionFont;
	
	    if (questionFontMetrics.stringWidth(gQuestionsAcross[layout[gBlockMinY][gBlockMinX]]) > viewWidth - 4)
	    {
		userFont = questionFont18;
		g.setFont(questionFont18);
	    }
	
	    g.drawString(gQuestionsAcross[layout[gBlockMinY][gBlockMinX]], 
			 (size().width / 2) - (g.getFontMetrics().stringWidth(gQuestionsAcross[layout[gBlockMinY][gBlockMinX]]) / 2), 
			 (top + kQuestionAreaHeight) - 8);
	
	}	
	else {
	    Font userFont = questionFont;
	
	    if (questionFontMetrics.stringWidth(gQuestionsDown[layout[gBlockMinY][gBlockMinX]]) > viewWidth - 4)
	    {
		userFont = questionFont18;
		g.setFont(questionFont18);
	    }
	
	    g.drawString(gQuestionsDown[layout[gBlockMinY][gBlockMinX]], 
			 (size().width / 2) - (g.getFontMetrics().stringWidth(gQuestionsDown[layout[gBlockMinY][gBlockMinX]]) / 2), 
			 (top + kQuestionAreaHeight) - 8);
	}

	left = (size().width / 2) - (viewWidth / 2);
	top = (kPadding * 2) + kQuestionAreaHeight; 

	for (int j = 0 ; j < kBlocksHigh ; j++) {
	    for (int i = 0 ; i < kBlocksWide ; i++) {
		tempLeft = left + (i * kBlockWidth);
		tempTop = top + (j * kBlockHeight);
	
		if (InActiveBlock(i, j)) {
		    if (i == gCurX && j == gCurY)
			g.setColor(Color.cyan);
		    else
			g.setColor(Color.yellow);
		    g.fillRect(tempLeft, tempTop, kBlockWidth, kBlockHeight);
		}
		else {
		    g.setColor(Color.white);
		    g.fillRect(tempLeft, tempTop, kBlockWidth, kBlockHeight);
		}
	
		g.setColor(Color.black);
		g.drawRect(tempLeft, tempTop, kBlockWidth , kBlockHeight );
	
		if (layout[j][i] == -1){
		    g.setColor(Color.black);
		    g.fillRect(tempLeft, tempTop, kBlockWidth, kBlockHeight);
		}
		else if (layout[j][i] != 0) {
		    String numStr = String.valueOf(layout[j][i]);
	    
		    g.setFont(numFont);
		    g.drawString(numStr, tempLeft + 4 , tempTop + 10);
	    
		}
	
		// -- put in text if needed
		
		if (layout[j][i] != -1) {
		    if (gGuesses[i][j].length() != 0) {
			int sWidth = 0;
			
			if (gGuesses[i][j].equalsIgnoreCase(answers[j][i]) == false)
			    g.setColor(Color.red);
			else
			    g.setColor(Color.black);
			
			sWidth = answerFontMetrics.stringWidth(gGuesses[i][j]);
			g.setFont(answerFont);
			g.drawString( gGuesses[i][j], tempLeft + ((kBlockWidth / 2) - (sWidth / 2)), (tempTop + kBlockHeight) - 6);
		    }
		    
		}	
		
	    }
	}
    }

    /*----------------------------------------------*/

    void PaintWord(Graphics g, int minX, int maxX, int minY, int maxY) {
	int viewWidth = kBlocksWide * kBlockWidth;
	int viewHeight = kBlocksHigh * kBlockHeight;
	int left = (size().width / 2) - (viewWidth / 2);
	int top = (kPadding * 2) + kQuestionAreaHeight; 
	int tempLeft = 0;
	int tempRight = 0;
	int tempTop = 0;
    
	left += (minX * kBlockWidth);
	top += (minY * kBlockHeight);
	/*
	g.clipRect(	left, top, 
		   (kBlockWidth * (maxX - minX)) + kBlockWidth, 
		   (kBlockHeight * (maxY - minY)) + kBlockHeight);
		   */
	       
	       
	Font f = new java.awt.Font("Helvetica", 0, 12);
	g.setFont(f);
	       
	Font numFont = new java.awt.Font("Helvetica", 0, 10);
	       
	Font answerFont = new java.awt.Font("Helvetica", 0, 18);
	FontMetrics answerFontMetrics = g.getFontMetrics(answerFont);
	       
	viewWidth = kBlocksWide * kBlockWidth;
	viewHeight = kBlocksHigh * kBlockHeight;
	       
	left = (size().width / 2) - (viewWidth / 2);
	top = (kPadding * 2) + kQuestionAreaHeight; 
	       
	for (int j = minY ; j <= maxY ; j++) {
	    for (int i = minX ; i <= maxX ; i++) {
		tempLeft = left + (i * kBlockWidth);
		tempTop = top + (j * kBlockHeight);
		       
		if (InActiveBlock(i, j)) {
		    if (i == gCurX && j == gCurY)
			g.setColor(Color.cyan);
		    else
			g.setColor(Color.yellow);
		    g.fillRect(tempLeft, tempTop, kBlockWidth, kBlockHeight);
		}
		else {
		    g.setColor(Color.white);
		    g.fillRect(tempLeft, tempTop, kBlockWidth, kBlockHeight);
		}
		       
		g.setColor(Color.black);
		g.drawRect(tempLeft, tempTop, kBlockWidth , kBlockHeight );
		       
		if (layout[j][i] == -1) {
		    g.setColor(Color.black);
		    g.fillRect(tempLeft, tempTop, kBlockWidth, kBlockHeight);
		}
		else if (layout[j][i] != 0) {
		    String numStr = String.valueOf(layout[j][i]);
			   
		    g.setFont(numFont);
		    g.drawString(numStr, tempLeft + 4 , tempTop + 10);
			   
		}
		       
		// -- put in text if needed
		
		if (layout[j][i] != -1) {
		    if (gGuesses[i][j].length() != 0) {
			int sWidth = 0;
			
			if (gGuesses[i][j].equalsIgnoreCase(answers[j][i]) == false)
			    g.setColor(Color.red);
			else
			    g.setColor(Color.black);
			
			sWidth = answerFontMetrics.stringWidth(gGuesses[i][j]);
			g.setFont(answerFont);
			g.drawString( gGuesses[i][j], tempLeft + ((kBlockWidth / 2) - (sWidth / 2)), (tempTop + kBlockHeight) - 6);
		    }
		    
		}	
		
	    }
	}	
    }

    /*----------------------------------------------*/

    void PaintQuestionArea(Graphics g) {
	Font f = new java.awt.Font("Helvetica", 0, 12);
	Font questionFont = new java.awt.Font("Courier", 0, 24);
	Font questionFont18 = new java.awt.Font("Courier", 0, 18);
	FontMetrics questionFontMetrics = g.getFontMetrics(questionFont);
    
	int viewWidth = kBlocksWide * kBlockWidth;
	int viewHeight = kBlocksHigh * kBlockHeight;
    
	int top = kPadding;
	int left = (size().width / 2) - (viewWidth / 2);
	//g.clipRect(left, top, viewWidth, kQuestionAreaHeight);
    
	g.setColor(Color.white);
	g.fill3DRect(left, top, viewWidth, kQuestionAreaHeight, false);
    
	g.setFont(f);
	String s = new String(	String.valueOf(layout[gBlockMinY][gBlockMinX]));
	s = s.concat(" - ");
	if (gDirection == kAcross)
	    s = s.concat("across");
	else
	    s = s.concat("down");
	g.drawString(s, left + 5, top + 12);	
	g.setFont(questionFont);
	int fontSize = 24;
	if (gDirection == kAcross) {
	    Font userFont = questionFont;
	
	    if (questionFontMetrics.stringWidth(gQuestionsAcross[layout[gBlockMinY][gBlockMinX]]) > viewWidth - 4) {
		userFont = questionFont18;
		g.setFont(questionFont18);
	    }
	
	    g.drawString(gQuestionsAcross[layout[gBlockMinY][gBlockMinX]], 
			 (size().width / 2) - (g.getFontMetrics().stringWidth(gQuestionsAcross[layout[gBlockMinY][gBlockMinX]]) / 2), 
			 (top + kQuestionAreaHeight) - 8);
	
	}	
	else {
	    Font userFont = questionFont;
	
	    if (questionFontMetrics.stringWidth(gQuestionsDown[layout[gBlockMinY][gBlockMinX]]) > viewWidth - 4) {
		userFont = questionFont18;
		g.setFont(questionFont18);
	    }
	
	    g.drawString(gQuestionsDown[layout[gBlockMinY][gBlockMinX]], 
			 (size().width / 2) - (g.getFontMetrics().stringWidth(gQuestionsDown[layout[gBlockMinY][gBlockMinX]]) / 2), 
			 (top + kQuestionAreaHeight) - 8);
	}

    }

    /*----------------------------------------------*/

    private boolean InActiveBlock(int x, int y) {
	if (x < gBlockMinX)
	    return(false);
	if (x > gBlockMaxX)
	    return(false);
	if (y < gBlockMinY)
	    return(false);
	if (y > gBlockMaxY)
	    return(false);
    
	return(true);
    }

    /*----------------------------------------------*/

    private void SetActiveBlock(int x, int y, int direction) {
	int tempx;
	int tempy;

	gOldBlockMinY	= gBlockMinY;
	gOldBlockMaxY 	= gBlockMaxY;
	gOldBlockMinX = gBlockMinX;
	gOldBlockMaxX	= gBlockMaxX;


	if (direction == kAcross) {
	    gBlockMinY = y;
	    gBlockMaxY = y;
	    tempx = x;
	    while (tempx > 0 && layout[y][tempx] != -1) {
		tempx--;
	    }
	    if (tempx > 0)
		gBlockMinX = tempx + 1;
	    else {
		if (layout[y][0] == -1)
		    gBlockMinX = 1;
		else
		    gBlockMinX = 0;
	    }

	    tempx = x;
	    while (tempx < kBlocksWide && layout[y][tempx] != -1)
	    {
		tempx++;
	    }
	    gBlockMaxX = tempx -1;
	}
	else {
	    gBlockMinX = x;
	    gBlockMaxX = x;
	    tempy = y;
	    while (tempy > 0 && layout[tempy][x] != -1) {
		tempy--;
	    }
	    if (tempy > 0)
		gBlockMinY = tempy + 1;
	    else {
		if (layout[0][x] == -1)
		    gBlockMinY = 1;
		else
		    gBlockMinY = 0;
	    }

	    tempy = y;
	    while (tempy < kBlocksHigh && layout[tempy][x] != -1) {
		tempy++;
	    }
	    gBlockMaxY = tempy -1;
	}	
    }

    /*----------------------------------------------*/

    public void update(Graphics g)  {
	if (clipLeft != -1 && clipTop != -1) {
	    //g.clipRect(clipLeft, clipTop, clipWidth, clipHeight);
	    clipLeft = clipTop = clipWidth = clipHeight = -1;
	}

	if (gChangedActiveAreaFlag == false && gUpdateActiveAreaFlag == false) {		
	    paint(g);
	    return;
	}

	if (gChangedActiveAreaFlag == true) {
	    PaintQuestionArea(g);
	    PaintWord(g, gOldBlockMinX, gOldBlockMaxX, gOldBlockMinY, gOldBlockMaxY);
	    PaintWord(g, gBlockMinX, gBlockMaxX, gBlockMinY, gBlockMaxY);
	    gChangedActiveAreaFlag = false;
	    return;
	}

	//-----------------------------------------------
	
	if (gUpdateActiveAreaFlag == true) {
	    gUpdateActiveAreaFlag = false;
	    PaintWord(g, gBlockMinX, gBlockMaxX, gBlockMinY, gBlockMaxY);
	    return;
	}
	
    }

    /*----------------------------------------------*/

    private void beep() {
	play(getCodeBase(), "nope.au");
    }

    /*----------------------------------------------*/

    public boolean mouseDown(java.awt.Event evt, int x, int y) {
	int viewWidth = kBlocksWide * kBlockWidth;
	int left = (size().width / 2) - (viewWidth / 2);
	int top = (kPadding * 2) + kQuestionAreaHeight; 

	requestFocus();

	if (x < left)
	    return false;
	if (y < top)
	    return false;
    
	int j = y - top;
	j /= kBlockHeight;
    
	int i = x - left;
	i /= kBlockWidth;
    
    
	if (i >= 0 && i < kBlocksWide && j >= 0 && j < kBlocksHigh) {
	    if (layout[j][i] != -1) {
		gCurX = i;
		gCurY = j;
		if (InActiveBlock(i, j)) {
		    gUpdateActiveAreaFlag = true;
		    repaint();
		}
		else {
		    SetActiveBlock(i, j, gDirection);
		    gChangedActiveAreaFlag = true;
		    repaint();
		}
		return true;
	    }
	}
	return true;
    }

    /*----------------------------------------------*/

    public boolean mouseUp(java.awt.Event evt, int x, int y) {
	requestFocus();
	return true;
    }

    /*----------------------------------------------*/

    public boolean mouseDrag(java.awt.Event evt, int x, int y) {
	requestFocus();
	return true;
    }


    /*----------------------------------------------*/

    public boolean mouseExit(java.awt.Event evt) {
	return true;
    }

    /*----------------------------------------------*/

    public boolean mouseEnter(java.awt.Event evt) {
	requestFocus();
	return true;
    }

    /*----------------------------------------------*/

    public boolean mouseMove(java.awt.Event evt, int x, int y) {
	requestFocus();
	return true;
    }

    /*----------------------------------------------*/

    public boolean keyDown(java.awt.Event evt, int key) {

	if ((key >= 'A' && key <= 'Z') || (key >= 'a' && key <= 'z')) {
	    char charArray[] = new char[1];
    
	    charArray[0] = (char)key;
    
	    gGuesses[gCurX][gCurY] = new String(charArray); 
	    gGuesses[gCurX][gCurY] = gGuesses[gCurX][gCurY].toUpperCase();
    
    
	    if (gDirection == kAcross) {
		if (gCurX < kBlocksWide - 1 && layout[gCurY][gCurX+1] != -1)
		    gCurX++;
	    }
	    else {
		if (gCurY < kBlocksHigh - 1 && layout[gCurY + 1][gCurX] != -1)
		    gCurY++;
	    }
	    gUpdateActiveAreaFlag = true;
	    repaint();
	    return true;
	}

	switch ((char)key) {
	  case ' ':
	    ChangeDirection();
	    gChangedActiveAreaFlag = true;
	    repaint();
	    break;
	  case 0x08:
	    if (gGuesses[gCurX][gCurY] != "") {
		gGuesses[gCurX][gCurY] = "";
		gUpdateActiveAreaFlag = true;
		repaint();
	    }
	    else {
		if (gDirection == kAcross) {
		    if (gCurX != 0 && layout[gCurY][gCurX-1] != -1) {
			gCurX--;
			gGuesses[gCurX][gCurY] = "";
			gUpdateActiveAreaFlag = true;
			repaint();
		    }
		}
		else {
		    if (gCurY != 0 && layout[gCurY - 1][gCurX] != -1) {
			gCurY--;
			gGuesses[gCurX][gCurY] = "";
			gUpdateActiveAreaFlag = true;
			repaint();
		    }
		}
	    }
	    break;
	  default:
	    beep();
	    break;
	}
	return true;
    }


    /*----------------------------------------------*/

    int random(int max) {
	return (int)Math.floor(Math.random() * max);
    }

    /*----------------------------------------------*/

    boolean PtInRect(int x, int y, int left, int top, int right, int bottom) {
	if (x < left)
	    return(false);
    
	if (x > right)
	    return(false);
    
	if (y < top)
	    return(false);
    
	if (y > bottom)
	    return(false);
    
	return(true);
    }

    /*----------------------------------------------*/

    void ChangeDirection() {
	if (gDirection == kDown) {
	    gDirection = kUp;	
	}
	else
	    gDirection = kDown;
	SetActiveBlock(gCurX, gCurY, gDirection);	
    }


}

