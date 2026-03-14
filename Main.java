import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Jraffic");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setResizable(false);
            GamePanel panel = new GamePanel(800, 700);
            f.add(panel);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
            panel.start();
        });
    }
}