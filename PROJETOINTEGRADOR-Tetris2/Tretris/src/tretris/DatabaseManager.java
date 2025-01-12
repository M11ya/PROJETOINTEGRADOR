package tretris;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/tetris_db";
    private static final String USER = "root"; 
    private static final String PASSWORD = "root"; 

    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexão com o banco de dados estabelecida!");
            createTableIfNotExists(); // Verifica e cria a tabela se ela não existir
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao conectar ao banco de dados!");
        }
    }

    // Cria a tabela Scores se ela não existir
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

    // Salva a pontuação do jogador no banco de dados
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

    // Recupera as pontuações em ordem decrescente
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

    // Fecha a conexão com o banco de dados
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

    void savePlayerScore(String playerName, int Pontos) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}

