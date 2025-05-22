
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GuideViewer {

    private static final Map<String, String[]> guideImages = new HashMap<>();

    static {
        guideImages.put("SNB: How to Transfer Money", new String[]{
                "src/main/resources/assets/guides/SNB_page1.png",
                "src/main/resources/assets/guides/SNB_page2.png",
                "src/main/resources/assets/guides/SNB_page3.png",
                "src/main/resources/assets/guides/SNB_page4.png"
        });
        guideImages.put("Absher: How to Check for Car Violations", new String[]{
                "src/main/resources/assets/guides/Absher_page1.png",
                "src/main/resources/assets/guides/Absher_page2.png",
                "src/main/resources/assets/guides/Absher_page3.png"
        });
        guideImages.put("Tawakkalna: How to See National ID", new String[]{
                "src/main/resources/assets/guides/Tawakkalna_page1.png",
                "src/main/resources/assets/guides/Tawakkalna_page2.png"
        });
    }

    public static void showMainMenu() {
        JFrame frame = new JFrame("Guides");
        frame.setSize(500, 700);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        Map<String, String> guideTitles = new LinkedHashMap<>();
        guideTitles.put("SNB: How to Transfer Money", "src/main/resources/assets/guides/SNB_logo.png");
        guideTitles.put("Absher: How to Check for Car Violations", "src/main/resources/assets/guides/Absher_logo.png");
        guideTitles.put("Tawakkalna: How to See National ID", "src/main/resources/assets/guides/Tawakkalna_logo.png");

        for (Map.Entry<String, String> entry : guideTitles.entrySet()) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(new Color(240, 240, 240));
            card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            ImageIcon icon = new ImageIcon(entry.getValue());
            Image scaledImg = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));
            imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel title = new JLabel(entry.getKey());
            title.setFont(new Font("SansSerif", Font.BOLD, 16));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            card.add(imgLabel);
            card.add(Box.createRigidArea(new Dimension(0, 8)));
            card.add(title);

            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    showGuideViewer(entry.getKey());
                }
            });

            frame.add(card);
            frame.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        // Back Button
        JButton back = new JButton("Back");
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.setBackground(Color.RED);
        back.setForeground(Color.WHITE);
        back.addActionListener(e -> frame.dispose());
        frame.add(back);

        frame.setVisible(true);
    }

    private static void showGuideViewer(String title) {
        JFrame viewer = new JFrame(title);
        viewer.setSize(400, 700);
        viewer.setLayout(new BorderLayout());

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        viewer.add(imageLabel, BorderLayout.CENTER);

        JPanel navPanel = new JPanel();
        JButton back = new JButton("Back");
        JButton next = new JButton("Next");
        navPanel.add(back);
        navPanel.add(next);
        viewer.add(navPanel, BorderLayout.SOUTH);

        String[] images = guideImages.get(title);
        final int[] index = {0};
        updateImage(imageLabel, images[index[0]]);

        back.addActionListener(e -> {
            if (index[0] > 0) {
                index[0]--;
                updateImage(imageLabel, images[index[0]]);
            }
        });

        next.addActionListener(e -> {
            if (index[0] < images.length - 1) {
                index[0]++;
                updateImage(imageLabel, images[index[0]]);
            }
        });

        viewer.setVisible(true);
    }

    private static void updateImage(JLabel label, String path) {
        ImageIcon icon = new ImageIcon(path);
        Image scaled = icon.getImage().getScaledInstance(350, 600, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaled));
    }
}