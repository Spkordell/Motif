package games.pong;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import motif.Network;
import motif.Prediction;

public class Pong extends Applet implements Runnable {
    
    Thread engine = null;

    Paddle paddles[];
    Ball ball;
    Dimension winSize;
    Font scoreFont, smallBannerFont, largeBannerFont;
    Image dbimage;
    
    int lastPaddlePostion;
    int lastBallPosition;
    
    public static final int defaultPause = 50;
    int pause;
    
    public String getAppletInfo() {
	return "Pong by Paul Falstad";
    }

    public void init() {
	setBackground(Color.white);
        Dimension d = winSize = size();
	paddles = new Paddle[2];
    	paddles[0] = new Paddle(10, 40, 120, 50);
    	paddles[1] = new Paddle(d.width/2, d.height-40, d.height-120, 40);
	paddles[0].setRange(0, d.width-1);
	paddles[1].setRange(0, d.width-1);
	paddles[0].setColorBase(1, 0, 0);
	paddles[1].setColorBase(0, 0, 1);
	ball = new Ball(new Point(d.width/2, d.height/2), 9, this);
	ball.setRange(0, d.width-1, 0, d.height-1);
	pause = defaultPause;
	scoreFont = new Font("TimesRoman", Font.BOLD, 36);
	largeBannerFont = new Font("TimesRoman", Font.BOLD, 48);
	smallBannerFont = new Font("TimesRoman", Font.BOLD, 16);
	dbimage = createImage(d.width, d.height);
	try {
	    String param = getParameter("PAUSE");
	    if (param != null)
		pause = Integer.parseInt(param);
	} catch (Exception e) { }
    }

    public void updateScore(int which) {
	paddles[1-which].score++;
    }

    public void run() {
	while (true) {
	    try {
		for (int i = 0; i != 3; i++)
		    step();
		repaint();
    		Thread.currentThread().sleep(pause);
	    } catch (Exception e) {}
	}
    }

    public void step() {
		paddles[1].setTarget(ball.getPaddlePos());
		if (ball.inPlay) {
			if (paddles[0].score >=1) {
				
				
				
				//TODO (for now, just moving to the first item in the prediction list, in the future, will want to perform all maneuvers)
				
				
				
				
				Prediction prediction = Network.getInstance().getInput(0).getBestPrediction();
				System.out.println("prediction: "+prediction);
				if (prediction != null) {
					paddles[0].setTarget(Integer.parseInt(prediction.getPrediction().split(" ")[0]));
					paddles[0].move();
				}
			} else {
				paddles[0].setTarget(0); //TODO: temporarily allowing the game to play itself.
				paddles[0].move();
			}
		}
		
		if (ball.inPlay)
		    paddles[1].move();
		if (ball.bounce(paddles[0]))
		    paddles[0].bounceIt();
		if (ball.bounce(paddles[1]))
		    paddles[1].bounceIt();
		ball.move();
		
		//add unique values only and only as fast as they can be processed
		//if ((paddles[0].getTarget() != lastPaddlePostion || ball.getPaddlePos() != lastBallPosition) && Network.getInstance().getInput(0).isEmpty()) {

		//add unique values only and only when game is in play
		/* watch this player
		if ((paddles[0].getTarget() != lastPaddlePostion && ball.getPaddlePos() != lastBallPosition) && ball.inPlay) {
			Network.getInstance().getInput(0).addData(paddles[0].getTarget());
			Network.getInstance().getInput(1).addData(ball.getPaddlePos());
			lastPaddlePostion = paddles[0].getTarget();
			lastBallPosition = ball.getPaddlePos();
		}*/
		
		//watch the enemy
		if ((paddles[1].getTarget() != lastPaddlePostion && ball.getPaddlePos() != lastBallPosition) && ball.inPlay) {
			Network.getInstance().getInput(0).addData(paddles[1].getTarget());
			Network.getInstance().getInput(1).addData(ball.getPaddlePos());
			lastPaddlePostion = paddles[1].getTarget();
			lastBallPosition = ball.getPaddlePos();
		}
		
		//TODO: Game will auto restart when network catches up and ball is not in play
		if (!ball.inPlay && Network.getInstance().getInput(0).isEmpty())
			ball.startPlay();

    }

    public void centerString(Graphics g, FontMetrics fm, String str, int ypos) {
	g.drawString(str, (winSize.width-fm.stringWidth(str))/2, ypos);
    }

    public void drawBanner(Graphics g) {
	g.setFont(largeBannerFont);
	FontMetrics fm = g.getFontMetrics();
	g.setColor(Color.red);
	centerString(g, fm, "PONG", 100);
	g.setColor(Color.blue);
	g.setFont(scoreFont);
	fm = g.getFontMetrics();
	centerString(g, fm, "by Paul Falstad", 160);
	g.setFont(smallBannerFont);
	fm = g.getFontMetrics();
	centerString(g, fm, "www.falstad.com", 190);
	g.setColor(Color.black);
	centerString(g, fm, "Press mouse button to start", 300);
    }

    public void update(Graphics realg) {
	Graphics g = dbimage.getGraphics();
	g.setColor(getBackground());
	g.fillRect(0, 0, winSize.width, winSize.height);
	g.setColor(getForeground());
	if (!ball.inPlay) {
	    g.setFont(scoreFont);
	    FontMetrics fm = g.getFontMetrics();
	    if (paddles[0].score == 0 && paddles[1].score == 0)
		drawBanner(g);
	    else
		for (int i = 0; i != 2; i++) {
        	    String score = Integer.toString(paddles[i].score);
		    g.setColor(paddles[i].scoreColor);
		    centerString(g, fm, score, paddles[i].scorePos);
		}
	}
	for (int i = 0; i != 2; i++)
    	    paddles[i].draw(g);
	ball.draw(g);
	realg.drawImage(dbimage, 0, 0, this);
    }

    public void start() {
	if (engine == null) {
	    engine = new Thread(this);
	    engine.start();
	}
    }

    public void stop() {
	if (engine != null && engine.isAlive()) {
	    engine.stop();
	}
	engine = null;
    }

    public boolean handleEvent(Event evt) {
	if (evt.id == Event.MOUSE_MOVE) {
	    paddles[0].setTarget(evt.x);
	    return true;
	} else if (evt.id == Event.MOUSE_DOWN) {
	    ball.startPlay();
	    return true;
	} else {	    
	    return super.handleEvent(evt);
	}
    }
    
}


