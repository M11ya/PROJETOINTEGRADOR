package mino;

import java.awt.Color;

public class Mino_Quadrado extends Mino {

    public Mino_Quadrado() {
        create(Color.yellow);
    }

    public void setXY(int x, int y) {

        b[0].x = x;
        b[0].y = y;                         // O O
        b[1].x = b[0].x;                    // O O   
        b[1].y = b[0].y + Block.SIZE;       
        b[2].x = b[0].x + Block.SIZE;
        b[2].y = b[0].y;
        b[3].x = b[0].x + Block.SIZE;
        b[3].y = b[0].y + Block.SIZE;

    }

    public void getDirection1() {

        tempB[0].x = b[0].x;
        tempB[0].y = b[0].y;
        tempB[1].x = b[0].x;
        tempB[1].y = b[0].y + Block.SIZE;
        tempB[2].x = b[0].x + Block.SIZE;
        tempB[2].y = b[0].y;
        tempB[3].x = b[0].x + Block.SIZE;
        tempB[3].y = b[0].y + Block.SIZE;

        updateXY(1);

    }

    public void getDirection2() {
    }

    public void getDirection3() {
    }

    public void getDirection4() {
    }

}
