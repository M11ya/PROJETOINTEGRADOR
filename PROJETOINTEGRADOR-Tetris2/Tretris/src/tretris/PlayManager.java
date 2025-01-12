package tretris;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;
import mino.Block;
import mino.Mino;
import mino.Mino_Bar;
import mino.Mino_L1;
import mino.Mino_L2;
import mino.Mino_Quadrado;
import mino.Mino_T;
import mino.Mino_Z1;
import mino.Mino_Z2;

public class PlayManager {

    // MAIN PLAY AREA
    final int LARGURA = 360;
    final int ALTURA = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    // MINO
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    // OUTROS
    public static int dropInterval = 60; // MINO DROPA EM 60 FRAMES
    boolean gameOver;
    private String playerName; // Nome do jogador
    private DatabaseManager dbManager;

    // Efeitos
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    // SCORE
    int Level = 1;
    int Linhas;
    int Pontos;

    public PlayManager() {

        // MAIN PLAY AREA FRAME
        left_x = (GamePanel.LARGURA / 2) - (LARGURA / 2); // 1280/2 - 360/2 - 460
        right_x = left_x + LARGURA;
        top_y = 50;
        bottom_y = top_y + ALTURA;

        MINO_START_X = left_x + (LARGURA / 2) - Block.SIZE; // Centro da área de jogo
        MINO_START_Y = top_y + Block.SIZE;                // Próximo ao topo
        NEXTMINO_X = right_x + 175;                       // Fora da área de jogo
        NEXTMINO_Y = top_y + 500;

        // SOLICITAR NOME DO JOGADOR
        playerName = JOptionPane.showInputDialog(null, "Insira o nome do jogador:", "Nome do Jogador", JOptionPane.QUESTION_MESSAGE);
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Jogador Desconhecido";
        }

        // INICIAR CONEXÃO COM BANCO DE DADOS
        dbManager = new DatabaseManager();

        // SET STARTING MINO
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

    }

    private Mino pickMino() {

        // RANDOM MINO
        Mino mino = null;
        int i = new Random().nextInt(7);

        switch (i) {
            case 0:
                mino = new Mino_L1();
                break;
            case 1:
                mino = new Mino_L2();
                break;
            case 2:
                mino = new Mino_Quadrado();
                break;
            case 3:
                mino = new Mino_Bar();
                break;
            case 4:
                mino = new Mino_T();
                break;
            case 5:
                mino = new Mino_Z1();
                break;
            case 6:
                mino = new Mino_Z2();
                break;
        }
        return mino;

    }

    public void update() {

        // Checar se o currentMino esta Ativado
        if (currentMino.active == false) {
            // Se o bloco não está ativado, colocar o staticBlok
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            //Checa se perdeu o jogo
            if (currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y) {
                gameOver = true;
                saveScore();
            }

            currentMino.deactivating = false;

            // Substituir o currentMino com o próximo bloco
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

            // substituir um mino fica inativo, verifica se as linhas podem ser deletadas
            checkDelete();
        } else {
            currentMino.update();

        }

    }

    private void checkDelete() {

        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        while (x < right_x && y < bottom_y) {

            for (int i = 0; i < staticBlocks.size(); i++) {
                if (staticBlocks.get(i).x == x && staticBlocks.get(i).y == y) {
                    // Aumenta o Contador se houver um bloco estatico
                    blockCount++;
                }
            }
            x += Block.SIZE;

            if (x == right_x) {

                // se o blockCount bater 12, isso significa que as linhas y atuais estão todas preenchidas com blocos
                // Para que possa ser deletado
                if (blockCount == 12) {

                    effectCounterOn = true;
                    effectY.add(y);

                    for (int i = staticBlocks.size() - 1; i > -1; i--) {
                        // Remove todos os blocos da linha Y
                        if (staticBlocks.get(i).y == y) {
                            staticBlocks.remove(i);
                        }
                    }

                    lineCount++;
                    Linhas++;
                    // Drop Rapido
                    if (Linhas % 10 == 0 && dropInterval > 1) {

                        Level++;
                        if (dropInterval > 10) {
                            dropInterval -= 10;
                        } else {
                            dropInterval -= 1;
                        }
                    }

                    // uma linha foi deletada então precisa deslizar para baixo os blocos que estão acima
                    for (int i = 0; i < staticBlocks.size(); i++) {
                        // Se um bloco está acima da linha y, move para baixo o tamanho do bloco
                        if (staticBlocks.get(i).y < y) {
                            staticBlocks.get(i).y += Block.SIZE;
                        }
                    }

                }

                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }

        // ADICIONA O SCORE
        if (lineCount > 0) {
            int singleLineScore = 10 + Level;
            Pontos += singleLineScore + lineCount;
        }

    }

    private void saveScore() {
        if (dbManager != null) {
            dbManager.savePlayerScore(playerName, Pontos);
        }
    }

    public void draw(Graphics2D g2) {

        //PINTAR PLAY AREA FRAME
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x - 4, top_y - 4, LARGURA + 8, ALTURA + 8);

        // DESENHE O PROXIMO MINO
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("PRÓXIMO", x + 32, y + 32);

        // Pintar o SCORE
        g2.drawRect(x, top_y, 200, 300);
        x += 40;
        y = top_y + 90;
        g2.drawString("LEVEL: " + Level, x, y);
        y += 70;
        g2.drawString("LINHAS: " + Linhas, x, y);
        y += 70;
        g2.drawString("Pontos: " + Pontos, x, y);

        // PINTAR O CURRENTMINO
        if (currentMino != null) {
            currentMino.draw(g2);
        }

        // PINTAR O Proximo Bloco
        nextMino.draw(g2);

        // PINTAR O BLOCK STATIC
        for (int i = 0; i < staticBlocks.size(); i++) {
            staticBlocks.get(i).draw(g2);
        }

        // Pintar Efeito
        if (effectCounterOn) {
            effectCounter++;

            g2.setColor(Color.red);
            for (int i = 0; i < effectY.size(); i++) {
                g2.fillRect(left_x, effectY.get(i), LARGURA, Block.SIZE);
            }

            if (effectCounter == 10) {
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }

        }

        // PINTAR O PAUSE
        g2.setColor(Color.yellow);
        g2.setFont(g2.getFont().deriveFont(50f));
        if (gameOver) {
            x = left_x + 25;
            y = top_y + 320;
            g2.drawString("GAME OVER", x, y);
        } else if (KeyHandler.pausePressed) {
            x = left_x + 70;
            y = top_y + 320;
            g2.drawString("PAUSADO", x, y);
        }

        x = 25;
        y = top_y + 320;
        g2.setColor(Color.white);
        g2.setFont(new Font("Time New Roman", Font.ITALIC, 60));
        g2.drawString("TETRIS", x + 20, y);
    }

    void setPlayerName(String playerName) {
    }
}
