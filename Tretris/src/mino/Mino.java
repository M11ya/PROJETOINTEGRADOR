package mino;

import java.awt.Color;
import java.awt.Graphics2D;
import tretris.KeyHandler;
import tretris.PlayManager;
import mino.Mino_L1;

public class Mino {

    public Block b[] = new Block[4];
    public Block tempB[] = new Block[4];
    int autoDropContador = 0;
    public int direction = 1; //AS 4 DIREÇÕES 
    boolean leftCollision, rightCollision, bottomCollision;
    public boolean active = true;

    public void create(Color c) {
        b[0] = new Block(c);
        b[1] = new Block(c);
        b[2] = new Block(c);
        b[3] = new Block(c);
        tempB[0] = new Block(c);
        tempB[1] = new Block(c);
        tempB[2] = new Block(c);
        tempB[3] = new Block(c);
    }

    public void setXY(int x, int y) {
    }

    public void updateXY(int direction) {

        checkRotationCollision();

        if (leftCollision == false && rightCollision == false && bottomCollision == false) {

            this.direction = direction;
            b[0].x = tempB[0].x;
            b[0].y = tempB[0].y;
            b[1].x = tempB[1].x;
            b[1].y = tempB[1].y;
            b[2].x = tempB[2].x;
            b[2].y = tempB[2].y;
            b[3].x = tempB[3].x;
            b[3].y = tempB[3].y;
        }

    }

    public void getDirection1() {
    }

    public void getDirection2() {
    }

    public void getDirection3() {
    }

    public void getDirection4() {
    }

    public void checkMovementCollision() {

        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        // Checar Frame Colisão
        // PAREDE ESQUEDA
        for (int i = 0; i < b.length; i++) {
            if (b[i].x < PlayManager.left_x) {
                leftCollision = true;
            }
        }
        // PAREDE DIREITA
        for (int i = 0; i < b.length; i++) {
            if (b[i].x + Block.SIZE == PlayManager.right_x) {
                rightCollision = true;
            }
        }

        // CHÃO
        for (int i = 0; i < b.length; i++) {
            if (b[i].y + Block.SIZE == PlayManager.bottom_y) {
                rightCollision = true;
            }
        }

    }

    public void checkRotationCollision() {

        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        // Checar Frame Colisão
        // PAREDE ESQUEDA
        for (int i = 0; i < b.length; i++) {
            if (tempB[i].x == PlayManager.left_x) {
                leftCollision = true;
            }
        }
        // PAREDE DIREITA
        for (int i = 0; i < b.length; i++) {
            if (tempB[i].x + Block.SIZE > PlayManager.right_x) {
                rightCollision = true;
            }
        }

        // CHÃO
        for (int i = 0; i < b.length; i++) {
            if (tempB[i].y + Block.SIZE > PlayManager.bottom_y) {
                rightCollision = true;
            }
        }
    }

    public void update() {

        //MOVER O MINO
        if (KeyHandler.upPressed) {
            switch (direction) {
                case 1:
                    getDirection2();
                    break;
                case 2:
                    getDirection3();
                    break;
                case 3:
                    getDirection4();
                    break;
                case 4:
                    getDirection1();
                    break;

            }

            KeyHandler.upPressed = false;
        }

        checkMovementCollision();

        if (KeyHandler.downPressed) {

            if (bottomCollision == false) {
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;

                //QUANDO MOVIDO PARA BAIXO, RESETA O AUTODROPCONTADOR
                autoDropContador = 0;

            }

            KeyHandler.downPressed = false;

        }
        if (KeyHandler.leftPressed) {
            if (leftCollision == false) {

                b[0].x -= Block.SIZE;
                b[1].x -= Block.SIZE;
                b[2].x -= Block.SIZE;
                b[3].x -= Block.SIZE;

            }

            KeyHandler.leftPressed = false;

        }
        if (KeyHandler.rightPressed) {
            if (rightCollision == false) {

                b[0].x += Block.SIZE;
                b[1].x += Block.SIZE;
                b[2].x += Block.SIZE;
                b[3].x += Block.SIZE;

            }

            KeyHandler.rightPressed = false;
        }

        if (bottomCollision) {
            active = false;
        } else {
            autoDropContador++; // O CONTADOR AUMENTA A CADA FRAME
            if (autoDropContador == PlayManager.dropInterval) {
                // O MINO VAI DESCER
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;
                autoDropContador = 0;
            }
        }

    }

    public void draw(Graphics2D g2) {

        int margin = 2;
        g2.setColor(b[0].c);
        g2.fillRect(b[0].x + margin, b[0].y + margin, Block.SIZE - (margin * 2), Block.SIZE - (margin * 2));
        g2.fillRect(b[1].x + margin, b[1].y + margin, Block.SIZE - (margin * 2), Block.SIZE - (margin * 2));
        g2.fillRect(b[2].x + margin, b[2].y + margin, Block.SIZE - (margin * 2), Block.SIZE - (margin * 2));
        g2.fillRect(b[3].x + margin, b[3].y + margin, Block.SIZE - (margin * 2), Block.SIZE - (margin * 2));

    }

}
