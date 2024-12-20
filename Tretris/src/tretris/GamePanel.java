package tretris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    public static final int LARGURA = 1000;
    public static final int ALTURA = 700;
    final int FPS = 60;
    Thread gameThread;
    PlayManager pm;

    public GamePanel() {

        //PAINEL DE CONFIGURAÇÕES
        this.setPreferredSize(new Dimension(LARGURA, ALTURA));
        this.setBackground(Color.black);
        this.setLayout(null);

        //IMPLEMENTAÇÃO DE KEYLISTENER
        this.addKeyListener(new KeyHandler());
        this.setFocusable(true);
        
        pm = new PlayManager();

    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        //GAME LOOP
        double drawIntervalo = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawIntervalo;
            lastTime = currentTime;

            if (delta > - 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        if(KeyHandler.pausePressed == false) {
        pm.update();

        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        pm.draw(g2);
    }

}
