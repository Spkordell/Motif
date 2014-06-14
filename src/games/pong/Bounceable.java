package games.pong;

import java.awt.Color;
import java.awt.Graphics;

class Bounceable {
    static final int numcols = 10;
    static final int topCounter = 3;
    Color cols[];
    int bounceno, counter;
    int rbase, gbase, bbase;
    public Color scoreColor;
    Bounceable() {
		bounceno = 0;
		counter = topCounter;
    }
    public void setColor(Graphics g) {
	g.setColor(cols[bounceno]);
	if (bounceno > 0 && counter-- <= 0) {
	    bounceno--;
	    counter = topCounter;
	}
    }
    public void bounceIt() {
    	bounceno = numcols-1;
    }
    public void setColorBase(int rx, int gx, int bx) {
    	rbase = rx;
    	gbase = gx;
    	bbase = bx;
		int i;
		cols = new Color[numcols];
		for (i = 0; i != numcols; i++) {
			int val = 255*i/(numcols-1);
			cols[i] = new Color(rbase*val, gbase*val, bbase*val);
		}
		scoreColor = cols[numcols-1];
    }
}
