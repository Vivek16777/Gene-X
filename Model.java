package DNAsequence;

import java.io.*;
import java.net.*;
import org.json.JSONObject;

public class Model {
    private static String modelName;

    public static void loadModel(String name) {
        modelName = name;
    }

    public static String getModelName() {
        return modelName;
    }

    // Call Node API to predict DNA sequence
    public static JSONObject predict(String dnaSequence) {
        try {
            URL url = new URL("http://localhost:3000/predict"); // Node API URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject payload = new JSONObject();
            payload.put("dna", dnaSequence);
            payload.put("model", modelName);

            OutputStream os = conn.getOutputStream();
            os.write(payload.toString().getBytes());
            os.flush();
            os.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            return new JSONObject(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}



