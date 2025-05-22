import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class FAQViewer {
    static class FAQ {
        String question;
        String answer;

        FAQ(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }

    private static int fontSize = 14;

    public static void showFAQWindow() {
        JFrame frame = new JFrame("FAQs - Help & Support");
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        JPanel faqPanel = new JPanel();
        faqPanel.setLayout(new BoxLayout(faqPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(faqPanel);
        scrollPane.setBorder(null);

        // Search + Zoom controls
        JTextField searchField = new JTextField(30);
        JButton searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset");
        JButton zoomIn = new JButton("➕");
        JButton zoomOut = new JButton("➖");

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(resetButton);
        topPanel.add(zoomOut);
        topPanel.add(zoomIn);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        ArrayList<FAQ> faqs = getAllFAQs();
        renderFAQList(faqs, faqPanel);

        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().toLowerCase();
            ArrayList<FAQ> filtered = new ArrayList<>();
            for (FAQ faq : faqs) {
                if (faq.question.toLowerCase().contains(keyword) || faq.answer.toLowerCase().contains(keyword)) {
                    filtered.add(faq);
                }
            }
            renderFAQList(filtered, faqPanel);
        });

        resetButton.addActionListener(e -> {
            searchField.setText("");
            renderFAQList(faqs, faqPanel);
        });

        zoomIn.addActionListener(e -> {
            fontSize += 2;
            renderFAQList(faqs, faqPanel);
        });

        zoomOut.addActionListener(e -> {
            if (fontSize > 10) {
                fontSize -= 2;
                renderFAQList(faqs, faqPanel);
            }
        });

        frame.setVisible(true);
    }

    private static void renderFAQList(ArrayList<FAQ> list, JPanel container) {
        container.removeAll();
        container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        for (FAQ faq : list) {
            container.add(createFAQCard(faq));
            container.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        container.revalidate();
        container.repaint();
    }

    private static JPanel createFAQCard(FAQ faq) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // 🟡 Use default background based on current theme
        Color defaultBg = UIManager.getColor("Panel.background");
        card.setBackground(defaultBg);

        JLabel qLabel = new JLabel("<html><b>" + faq.question + "</b></html>");
        qLabel.setFont(new Font("SansSerif", Font.PLAIN, fontSize + 2));

        JLabel aLabel = new JLabel("<html>" + faq.answer + "</html>");
        aLabel.setFont(new Font("SansSerif", Font.PLAIN, fontSize));

        // Optional: set colors dynamically too
        Color textColor = UIManager.getColor("Label.foreground");
        qLabel.setForeground(textColor);
        aLabel.setForeground(textColor);

        card.add(qLabel, BorderLayout.NORTH);
        card.add(aLabel, BorderLayout.CENTER);

        return card;
    }


    private static ArrayList<FAQ> getAllFAQs() {
        ArrayList<FAQ> faqList = new ArrayList<>();

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT question, answer FROM faq")) {

            while (rs.next()) {
                faqList.add(new FAQ(rs.getString("question"), rs.getString("answer")));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error loading FAQs: " + e.getMessage());
        }

        return faqList;
    }
}
