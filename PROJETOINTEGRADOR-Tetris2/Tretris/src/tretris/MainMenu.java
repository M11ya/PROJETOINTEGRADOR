package tretris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends JFrame {
    private DatabaseManager dbManager;

    public MainMenu() {
        dbManager = new DatabaseManager();

        // Configuração da janela
        setTitle("Tetris - Menu Inicial");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Painel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Botão para cadastrar jogador
        JButton registerPlayerButton = new JButton("Cadastrar Jogador");
        registerPlayerButton.addActionListener(new RegisterPlayerAction());
        panel.add(registerPlayerButton);
        panel.add(Box.createVerticalStrut(10));

        // Botão para ver tabela de pontuações
        JButton leaderboardButton = new JButton("Ver Pontuações");
        leaderboardButton.addActionListener(new ShowLeaderboardAction());
        panel.add(leaderboardButton);

        add(panel);
    }

    // Ação para cadastrar jogador
    private class RegisterPlayerAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String playerName = JOptionPane.showInputDialog(
                    MainMenu.this,
                    "Digite o nome do jogador:",
                    "Cadastrar Jogador",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (playerName != null && !playerName.trim().isEmpty()) {
                // Exemplo de pontuação inicial (pode ser ajustado)
                dbManager.saveScore(playerName, 0);
                JOptionPane.showMessageDialog(
                        MainMenu.this,
                        "Jogador " + playerName + " cadastrado com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        MainMenu.this,
                        "O nome do jogador não pode ser vazio.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // Ação para exibir a tabela de pontuações
    private class ShowLeaderboardAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            List<String> scores = dbManager.getScores();
            if (scores.isEmpty()) {
                JOptionPane.showMessageDialog(
                        MainMenu.this,
                        "Nenhuma pontuação disponível ainda!",
                        "Tabela de Pontuações",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                StringBuilder leaderboard = new StringBuilder("Tabela de Pontuações:\n");
                for (String score : scores) {
                    leaderboard.append(score).append("\n");
                }
                JOptionPane.showMessageDialog(
                        MainMenu.this,
                        leaderboard.toString(),
                        "Tabela de Pontuações",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }
}

class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/tetris_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexão com o banco de dados estabelecida!");
            createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao conectar ao banco de dados!");
        }
    }

    private void createTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Scores ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "player_name VARCHAR(50) NOT NULL,"
                + "score INT NOT NULL"
                + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Tabela Scores verificada/criada com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao criar a tabela Scores!");
        }
    }

    public void saveScore(String playerName, int score) {
        if (connection == null) {
            System.out.println("Conexão com o banco de dados não estabelecida. Pontuação não foi salva.");
            return;
        }

        String sql = "INSERT INTO Scores (player_name, score) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            stmt.setInt(2, score);
            stmt.executeUpdate();
            System.out.println("Pontuação salva com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao salvar pontuação!");
        }
    }

    public List<String> getScores() {
        List<String> scores = new ArrayList<>();
        if (connection == null) {
            System.out.println("Conexão com o banco de dados não estabelecida. Não foi possível buscar pontuações.");
            return scores;
        }

        String sql = "SELECT player_name, score FROM Scores ORDER BY score DESC";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String playerName = rs.getString("player_name");
                int score = rs.getInt("score");
                scores.add(playerName + " - " + score + " pontos");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao buscar as pontuações do banco de dados!");
        }
        return scores;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexão com o banco de dados encerrada.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Erro ao fechar a conexão com o banco de dados!");
            }
        }
    }
}
