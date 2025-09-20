package DNAsequence;

import javax.swing.*;

public class DNAsequence {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InputPage(); // open first page
        });
    }
}

