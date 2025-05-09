
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.plaf.FontUIResource;





public class Main {
    static Map<String, String> memory = new HashMap<>();
    static java.util.List<Map<String, String>> chatHistory = new ArrayList<>();
    static JPanel chatPanel; // Panel holding messages
    static JScrollPane chatScrollPane; // Scrollable container
    static Font currentFont = new Font("SansSerif", Font.PLAIN, 14); // <== Add this




    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        JFrame frame = new JFrame("Ameen - Your Guide");
        frame.setMinimumSize(new Dimension(800, 800));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        ImageIcon logo = new ImageIcon("src/assets/ameen_logo.png");
        JLabel logoLabel = new JLabel(logo);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(logoLabel);

        loadMemory();

        JTextField searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(500, 30));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(searchField);

        JButton askButton = new JButton("Ask");
        JButton speakButton = new JButton("Speak");
        speakButton.addActionListener(e -> {
            String voiceInput = recognizeSpeechFromMic();
            searchField.setText(voiceInput);
            askButton.doClick();
        });
        JButton guidesButton = new JButton("View Guides");
        guidesButton.setFocusPainted(false);
        guidesButton.addActionListener(e -> GuideViewer.showMainMenu());

        JPanel askSpeakPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        askSpeakPanel.setOpaque(false);
        askSpeakPanel.add(askButton);
        askSpeakPanel.add(speakButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(askSpeakPanel);

        // Chat Panel Setup (new)
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(UIManager.getColor("Panel.background"));  // ✅ Adapts to light/dark mode


        JScrollPane chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setPreferredSize(new Dimension(600, 250));
        chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.setBorder(null);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(chatScrollPane);







        JButton resetMemoryButton = new JButton("Reset Memory");
        resetMemoryButton.setFocusPainted(false);
        resetMemoryButton.addActionListener(e -> {
            try {
                FileWriter writer = new FileWriter("memory.json");
                writer.write("{}");
                writer.close();
                memory.clear();
                JOptionPane.showMessageDialog(frame, "Ameen's memory has been reset.");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to reset memory.");
            }
        });

        int[] fontSize = {14};
        JButton zoomIn = new JButton("➕");
        JButton zoomOut = new JButton("➖");

        zoomIn.addActionListener(e -> applyFontSize(++fontSize[0], searchField, askButton, speakButton, resetMemoryButton));
        zoomOut.addActionListener(e -> {
            if (fontSize[0] > 10) applyFontSize(--fontSize[0], searchField, askButton, speakButton, resetMemoryButton);
        });

        JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        zoomPanel.setOpaque(false);
        zoomPanel.add(zoomOut);
        zoomPanel.add(zoomIn);
        JButton faqButton = new JButton("View FAQs");
        faqButton.setFocusPainted(false);
        faqButton.addActionListener(e -> FAQViewer.showFAQWindow());

        JPanel faqPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        faqPanel.setOpaque(false);
        faqPanel.add(faqButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(faqPanel);

        faqPanel.add(Box.createRigidArea(new Dimension(0, 10))); // spacing
        faqPanel.add(guidesButton);




        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(zoomPanel);

        JPanel resetPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        resetPanel.setOpaque(false);
        resetPanel.add(resetMemoryButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(resetPanel);

        ImageIcon darkIcon = new ImageIcon("src/assets/dark_icon.png");
        ImageIcon lightIcon = new ImageIcon("src/assets/light_icon.png");
        int iconSize = 35;
        darkIcon = new ImageIcon(darkIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
        lightIcon = new ImageIcon(lightIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));

        JButton themeToggle = new JButton(darkIcon);
        themeToggle.setFocusPainted(false);
        themeToggle.setBorderPainted(false);
        themeToggle.setContentAreaFilled(false);
        themeToggle.setOpaque(false);

        final boolean[] isDark = {true};
        ImageIcon finalLightIcon = lightIcon;
        ImageIcon finalDarkIcon = darkIcon;
        themeToggle.addActionListener((ActionEvent e) -> {
            try {
                UIManager.setLookAndFeel(isDark[0] ? new FlatLightLaf() : new FlatDarkLaf());
                themeToggle.setIcon(isDark[0] ? finalLightIcon : finalDarkIcon);
                isDark[0] = !isDark[0];
                SwingUtilities.updateComponentTreeUI(frame);
                // Fix chat text color and font after switching theme
                for (Component comp : chatPanel.getComponents()) {
                    if (comp instanceof JLabel label) {
                        label.setForeground(getTextColor());
                        label.setFont(currentFont); // <- also keep size
                    }
                }
                chatPanel.repaint();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.setOpaque(false);
        topRightPanel.add(themeToggle);

        frame.setLayout(new BorderLayout());
        frame.add(topRightPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);

        askButton.addActionListener(e -> {
            String userInput = searchField.getText();
            if (!userInput.isEmpty()) {
                checkAndStoreMemory(userInput);
                String response = getOpenAIResponse(userInput);
                speak(response);

                searchField.setText("");

                chatHistory.add(Map.of("role", "user", "content", userInput));
                chatHistory.add(Map.of("role", "assistant", "content", response));

                int maxMessages = 10;
                if (chatHistory.size() > maxMessages * 2) {
                    chatHistory = chatHistory.subList(chatHistory.size() - maxMessages * 2, chatHistory.size());
                }

                addMessage("You: " + userInput);
                addMessage("Ameen: " + response);
                chatScrollPane.getVerticalScrollBar().setValue(chatScrollPane.getVerticalScrollBar().getMaximum());
            }
        });

        frame.setVisible(true);
        DatabaseManager.initializeDatabase();
        DatabaseManager.clearFAQs(); // ← just for testing
        // Prevent re-inserting if table already has data
        if (DatabaseManager.faqTableIsEmpty()) {
            DatabaseManager.insertSampleFAQs();
        }



    }


    public static String getOpenAIResponse(String userInput) {
        try {
            String apiKey = System.getenv("OPENAI_API_KEY Removed so i can push the code in github Profssor Khalid ");

            URL url = new URL("https://api.openai.com/v1/chat/completions");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);

            // Start JSON body
            StringBuilder messagesJson = new StringBuilder("[");

            // Add persistent memory as system message
            messagesJson.append("{\"role\": \"system\", \"content\": \"");
            messagesJson.append("User told you the following info:\\n");
            for (Map.Entry<String, String> entry : memory.entrySet()) {
                messagesJson.append(entry.getKey()).append(": ").append(entry.getValue()).append("\\n");
            }
            messagesJson.append("\"}");

            // Add chat history
            for (Map<String, String> msg : chatHistory) {
                messagesJson.append(",{\"role\": \"").append(msg.get("role")).append("\", ");
                messagesJson.append("\"content\": \"").append(msg.get("content").replace("\"", "\\\"")).append("\"}");
            }

            messagesJson.append("]");

            JSONArray messages = new JSONArray();

// Add system memory summary
            StringBuilder memoryIntro = new StringBuilder("The user previously told you the following:\n");
            for (Map.Entry<String, String> entry : memory.entrySet()) {
                memoryIntro.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            messages.put(new JSONObject()
                    .put("role", "system")
                    .put("content",
                            "You are Ameen, a smart assistant built to help users with tasks related to Saudi services. " +
                                    "Always be helpful, positive.\n\n" +
                                    memoryIntro.toString()));


// Add short-term memory (chat history)
            for (Map<String, String> message : chatHistory) {
                messages.put(new JSONObject(message));
            }

// Add latest user message
            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", userInput));

// Final body
            JSONObject jsonBody = new JSONObject()
                    .put("model", "gpt-3.5-turbo")
                    .put("messages", messages)
                    .put("max_tokens", 200);

            String body = jsonBody.toString();


            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line.trim());
            }

            JSONObject json = new JSONObject(response.toString());
            JSONArray choices = json.getJSONArray("choices");
            return choices.getJSONObject(0).getJSONObject("message").getString("content").trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Couldn't get a response from OpenAI.";
        }
    }
    public static void addMessage(String text) {
        JLabel messageLabel = new JLabel("<html><div style='width:500px'>" + text + "</div></html>");
        messageLabel.setForeground(getTextColor());
        messageLabel.setFont(currentFont); // apply current font size
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        chatPanel.add(messageLabel);
        chatPanel.revalidate();
    }



    public static void loadMemory() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("memory.json")));
            if (!content.isEmpty()) {
                JSONObject json = new JSONObject(content);
                for (String key : json.keySet()) {
                    memory.put(key, json.getString(key));
                }
            }
        } catch (Exception e) {
            System.out.println("No memory found, starting fresh.");
        }
    }

    public static void saveMemory() {
        try {
            JSONObject json = new JSONObject(memory);
            FileWriter writer = new FileWriter("memory.json");
            writer.write(json.toString(2));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void checkAndStoreMemory(String userInput) {
        userInput = userInput.toLowerCase();
        if (userInput.startsWith("my name is")) {
            memory.put("name", userInput.replace("my name is", "").trim());
            saveMemory();
        } else if (userInput.startsWith("my id is")) {
            memory.put("id", userInput.replace("my id is", "").trim());
            saveMemory();
        } else if (userInput.startsWith("my age is")) {
            memory.put("age", userInput.replace("my age is", "").trim());
            saveMemory();
        }
    }
    public static void applyFontSize(int size, JTextField searchField, JButton... buttons) {
        currentFont = new Font("SansSerif", Font.PLAIN, size); // update the shared font
        searchField.setFont(currentFont);
        for (JButton button : buttons) {
            button.setFont(currentFont);
        }

        for (Component comp : chatPanel.getComponents()) {
            if (comp instanceof JLabel label) {
                label.setFont(currentFont); // update chat message fonts too
            }
        }
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    public static Color getTextColor() {
        return UIManager.getLookAndFeel().getName().toLowerCase().contains("dark") ? Color.WHITE : Color.BLACK;
    }
    public static void speak(String text) {
        try {
            // Use Windows built-in TTS via PowerShell
            String command = "PowerShell -Command \"Add-Type -AssemblyName System.Speech;" +
                    "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer;" +
                    "$speak.Speak('" + text.replace("'", "''") + "');\"";
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            System.out.println("❌ Error with TTS: " + e.getMessage());
        }
    }
    public static String recognizeSpeechFromMic() {
        String resultText = "";
        try {
            ProcessBuilder builder = new ProcessBuilder("powershell.exe", "-ExecutionPolicy", "Bypass", "-File", "src/assets/speech.ps1");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                resultText += line + " ";
            }
            process.waitFor();
        } catch (Exception e) {
            System.out.println("❌ Speech recognition failed: " + e.getMessage());
        }
        return resultText.trim();
    }


}

