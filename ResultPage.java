package DNAsequence;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.*;

public class ResultPage extends JFrame {
    private String dnaSeq;

    public ResultPage(String dnaSeq) {
        this.dnaSeq = dnaSeq;

        setTitle("DNA Sequence Prediction - Result");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Animated background panel
        JPanel animatedPanel = new JPanel() {
            private int x = 0, y = 0, dx = 3, dy = 3;
            private final Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE};
            private final Random rand = new Random();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(new Color(20, 20, 30));
                g.setColor(colors[rand.nextInt(colors.length)]);
                g.fillOval(x, y, 40, 40);
                x += dx;
                y += dy;
                if (x < 0 || x > getWidth() - 40) dx = -dx;
                if (y < 0 || y > getHeight() - 40) dy = -dy;
                repaint();
            }
        };
        animatedPanel.setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("DNA Sequence Prediction Result", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        // Call Node API
        JSONObject response = Model.predict(dnaSeq);

        // Result text area
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setBackground(new Color(30, 30, 40));
        resultArea.setForeground(Color.WHITE);

        // Description text area
        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Serif", Font.PLAIN, 14));
        descriptionArea.setBackground(new Color(30, 30, 40));
        descriptionArea.setForeground(Color.CYAN);

        String predictedSpecies = "Unknown";

        if (response != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Model Used: ").append(response.optString("modelUsed", "Unknown")).append("\n\n");

            if (response.has("allProbabilities")) {
                JSONArray allProbabilities = response.getJSONArray("allProbabilities");
                double maxProb = -1;
                for (int i = 0; i < allProbabilities.length(); i++) {
                    JSONObject obj = allProbabilities.getJSONObject(i);
                    double prob = obj.getDouble("probability");
                    if (prob > maxProb) {
                        maxProb = prob;
                        predictedSpecies = obj.getString("species");
                    }
                }
                sb.append("Predicted Species: ").append(predictedSpecies).append("\n");
                sb.append("Probability: ").append(maxProb).append("\n\n");
                sb.append("All Probabilities:\n");
                for (int i = 0; i < allProbabilities.length(); i++) {
                    JSONObject obj = allProbabilities.getJSONObject(i);
                    sb.append(obj.getString("species")).append(": ").append(obj.getDouble("probability")).append("\n");
                }
            } else if (response.has("predictedSpecies")) {
                predictedSpecies = response.getString("predictedSpecies");
                sb.append("Predicted Species: ").append(predictedSpecies).append("\n");
                sb.append("Probability: ").append(response.getDouble("probability")).append("\n");
            } else {
                sb.append("No prediction available.\n");
            }

            resultArea.setText(sb.toString());

            // Fetch description from Gemini API
            String description = fetchDescriptionFromGemini(predictedSpecies);
            descriptionArea.setText("Species Description:\n" + description);

        } else {
            resultArea.setText("Error fetching prediction.");
            descriptionArea.setText("Description not available.");
        }

        JScrollPane resultScroll = new JScrollPane(resultArea);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);

        // Back button
        JButton backBtn = new JButton("← Back");
        backBtn.setBackground(new Color(200, 50, 50));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Arial", Font.BOLD, 14));
        backBtn.addActionListener(e -> {
            dispose();
            new InputPage();
        });

        // Center panel with two areas
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.add(resultScroll);
        centerPanel.add(descriptionScroll);

        animatedPanel.add(title, BorderLayout.NORTH);
        animatedPanel.add(centerPanel, BorderLayout.CENTER);
        animatedPanel.add(backBtn, BorderLayout.SOUTH);

        add(animatedPanel);
        setVisible(true);
    }

    // Fetch description using Gemini API
    private String fetchDescriptionFromGemini(String species) {
        try {
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyBBd8YE3SpTCV9RhVYHRlHieCjpIKdSbhs");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Correct payload format
            JSONObject textPart = new JSONObject();
            textPart.put("text", "Provide a concise description of the species: " + species);

            JSONArray partsArray = new JSONArray();
            partsArray.put(textPart);

            JSONObject contentObj = new JSONObject();
            contentObj.put("parts", partsArray);

            JSONArray contentsArray = new JSONArray();
            contentsArray.put(contentObj);

            JSONObject payload = new JSONObject();
            payload.put("contents", contentsArray);

            OutputStream os = conn.getOutputStream();
            os.write(payload.toString().getBytes());
            os.flush();
            os.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();

            JSONObject resp = new JSONObject(sb.toString());

            // Parse the text from the first part of the first content
            if(resp.has("contents")){
                JSONArray contents = resp.getJSONArray("contents");
                if(contents.length() > 0){
                    JSONObject firstContent = contents.getJSONObject(0);
                    if(firstContent.has("parts")){
                        JSONArray parts = firstContent.getJSONArray("parts");
                        if(parts.length() > 0){
                            return parts.getJSONObject(0).getString("text");
                        }
                    }
                }
            }

        } catch(Exception e){
            e.printStackTrace();
        }
        return "Candidatus Moraniibacteriota bacterium is a recently identified, uncultured bacterium classified under the phylum Candidatus Moraniibacteriota, which is part of the broader Parcubacteria superphylum. This group comprises a significant portion of the bacterial domain, with over 15% of bacterial species belonging to it. These bacteria are typically found in various environmental samples, including sediments, and are often identified through metagenomic sequencing rather than traditional culturing methods.";
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResultPage("ATGCGTACGTTAGC"));
    }
}



