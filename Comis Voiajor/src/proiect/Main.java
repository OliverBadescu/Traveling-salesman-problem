package proiect;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ComisVoiajorRomania frame = new ComisVoiajorRomania();
            frame.setVisible(true);
        });
    }
}