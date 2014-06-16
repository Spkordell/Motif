package games.pong;

import java.awt.Graphics;
import java.awt.Rectangle;

// this should be "player", probably
class Paddle extends Bounceable {
    int varpos;
    int fixedpos;
    int targetpos;
    public int scorePos;
    int width, dir, rangemin, rangemax, thick;
    public int score;
    public Paddle(int vp, int fp, int sp, int w) {
    	varpos = vp;
	fixedpos = fp;
	scorePos = sp;
	width = w;
	dir = 5;
	thick = 8;
	targetpos = vp;
    }
    public Rectangle getRect() {
    	return new Rectangle(varpos, fixedpos-thick/2, width, thick);
    }
    public void setTarget(int x) {
    	targetpos = x-width/2;
    }
    public int getTarget() {
    	return targetpos+width/2;
    }
    public int getVarPos() {
    	return varpos;
    }
    public int getFixedPos() {
    	return fixedpos;
    }
    public int getWidth() {
    	return width;
    }
    void move(int x) {
    	varpos = x;
	if (varpos < rangemin)
	    varpos = rangemin;
	if (varpos > rangemax)
	    varpos = rangemax;
    }
    public void move() {
	int d = targetpos - varpos;
	if (d < -dir)
	    d = -dir;
	if (d > dir)
	    d = dir;
	move(varpos+d);
    }
    public void setRange(int mn, int mx) {
	rangemin = mn;
	rangemax = mx-width+1;
    }
    public void draw(Graphics g) {
        setColor(g);
    	g.fillRect(varpos, fixedpos-thick/2, width, thick);
    }
}
