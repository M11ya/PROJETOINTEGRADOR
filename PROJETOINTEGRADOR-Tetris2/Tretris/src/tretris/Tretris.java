package tretris;

import javax.swing.JFrame;

public class Tretris {

    public static void main(String[] args) {

        JFrame window = new JFrame("Tetris");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        
        //ADICIONAR O GAMEPANEL A JANELA
        GamePanel gp = new GamePanel();
        window.add(gp);
        window.pack();
        
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        
        gp.launchGame();
    }
    
}
