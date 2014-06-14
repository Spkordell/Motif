package games.pong;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Random;

class Ball extends Bounceable {
    Point pos, startPos;
    Pong game;
    int dx, dy;
    int sz;
    int xrangemin, xrangemax, yrangemin, yrangemax;
    public boolean inPlay;
    Random random;
    public Ball(Point ps, int s, Pong g) {
	pos = ps;
	startPos = new Point(pos.x, pos.y);
	game = g;
	sz = s;
	dx = 4;
	dy = 6;
	inPlay = false;
	random = new Random();
	setColorBase(1, 1, 0);
    }
    public void startPlay() {
	if (inPlay)
	    return;
	inPlay = true;
	dx = 4;
	dy = 6;
	//better way to copy?
	pos = new Point(startPos.x, startPos.y);
    }
    int randBounce(int d) {
	int dd = (d < 0) ? -1 : 1;
	int n = random.nextInt();
	if (n <= 0)
	    n = 1-n;
	return ((n % 6)+2) * -dd;
    }
    public boolean bounce(Paddle pd) {
	//int fp = pd.getFixedPos();
	int vp = pd.getVarPos();
	int w = pd.getWidth();
	if (pos.x < vp || pos.x >= vp+w)
	    return false;
	boolean bounced = false;
	Rectangle xrg = new Rectangle(pos.x-sz/2, pos.y-sz/2, sz, sz);
	Rectangle prg = pd.getRect();
	xrg.translate(dx, 0);
	if (prg.intersects(xrg)) {
	    dx = randBounce(dx);
	    bounced = true;
	}
	Rectangle yrg = new Rectangle(pos.x-sz/2, pos.y-sz/2, sz, sz);
	yrg.translate(dx, dy);
	if (prg.intersects(yrg)) {
	    dy = randBounce(dy);
	    bounceIt();
	    bounced = true;
	}
	return bounced;
    }
    public void move() {
	if (!inPlay)
	    return;
	pos.x += dx;
	pos.y += dy;
	if (pos.x < xrangemin) {
	    pos.x = xrangemin;
	    dx = -dx;
	}
	if (pos.x > xrangemax) {
	    pos.x = xrangemax;
	    dx = -dx;
	}
	if (pos.y < yrangemin) {
	    inPlay = false;
	    game.updateScore(0);
	}
	if (pos.y > yrangemax) {
	    inPlay = false;
	    game.updateScore(1);
	}
    }
    public int getPaddlePos() {
	return pos.x;
    }
    public void setRange(int mnx, int mxx, int mny, int mxy) {
	xrangemin = mnx+sz/2;
	xrangemax = mxx-sz/2;
	yrangemin = mny+sz/2;
	yrangemax = mxy-sz/2;
    }
    public void draw(Graphics g) {
	if (!inPlay)
	    return;
        setColor(g);
	g.fillOval(pos.x-sz/2, pos.y-sz/2, sz, sz);
    }
}
