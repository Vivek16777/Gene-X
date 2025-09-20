package DNAsequence;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class InputPage extends JFrame {

    public InputPage() {
        setTitle("DNA Sequence Prediction - Input");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Animated background panel
        JPanel animatedPanel = new JPanel() {
            private int x = 0, y = 0, dx = 2, dy = 2;
            private final Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE};
            private final Random rand = new Random();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(new Color(20, 20, 30));
                g.setColor(colors[rand.nextInt(colors.length)]);
                g.fillOval(x, y, 30, 30);
                x += dx;
                y += dy;
                if (x < 0 || x > getWidth() - 30) dx = -dx;
                if (y < 0 || y > getHeight() - 30) dy = -dy;
                repaint();
            }
        };
        animatedPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Labels, text area, combo box, button
        JLabel dnaLabel = new JLabel("Enter DNA Sequence:");
        dnaLabel.setForeground(Color.WHITE);
        dnaLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JTextArea dnaArea = new JTextArea(5, 40);
        dnaArea.setLineWrap(true);
        dnaArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(dnaArea);

        JLabel modelLabel = new JLabel("Select Model:");
        modelLabel.setForeground(Color.WHITE);
        modelLabel.setFont(new Font("Arial", Font.BOLD, 16));

        String[] models = {"MeKaunHu", "FindMyFamily"};
        JComboBox<String> modelBox = new JComboBox<>(models);

        JButton predictBtn = new JButton("Predict");
        predictBtn.setBackground(new Color(50, 150, 50));
        predictBtn.setForeground(Color.WHITE);
        predictBtn.setFont(new Font("Arial", Font.BOLD, 14));

        predictBtn.addActionListener(e -> {
            String dna = dnaArea.getText().trim();
            if (dna.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a DNA sequence!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Model.loadModel((String) modelBox.getSelectedItem());
            new ResultPage(dna);
            dispose();
        });

        // Add components to animatedPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        animatedPanel.add(dnaLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        animatedPanel.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        animatedPanel.add(modelLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        animatedPanel.add(modelBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        animatedPanel.add(predictBtn, gbc);

        add(animatedPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InputPage::new);
    }
}


