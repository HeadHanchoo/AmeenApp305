Ameen App 🧕🇸🇦
This is my final project for CPIT 305. It’s a simple Java desktop app I built to help elderly users in Saudi Arabia do basic stuff like check their ID on Tawakkalna, see car violations on Absher, or transfer money on SNB. I wanted it to be as easy as possible, so it has visual guides and even voice support.


## 🎯 Key Features:
- Simple and clean UI using Java Swing
- 🔊 Voice-to-text  and Text-to-Voice support using PowerShell
- 💬 Real-time AI responses via OpenAI's API
- 📖 Visual step-by-step guides for key services
- ❓ FAQ search with built-in database
- 🌓 Light/Dark mode toggle with FlatLaf
- 🔠 Adjustable text size for accessibility
- 💾 Memory: Remembers user's name, ID, and age locally
- 🧠 Persistent chat memory and system instructions
- 🗃️ SQLite database integration for FAQs

## 🧪 What I Used:
- Java Swing (GUI)
- FlatLaf (Modern Look & Feel)
- OpenAI API (Language Model)
- PowerShell (Voice input/output)
- SQLite (FAQ database)
- Maven (to manage everything)
- JUnit (I added test cases for the database)


## 📁 File Structure:
- `Main.java` – Main app logic and UI window
- `GuideViewer.java` – Shows visual step-by-step guides (SNB, Absher, Tawakkalna)
- `FAQViewer.java` – Shows FAQs and allows keyword search
- `DatabaseManager.java` – Manages SQLite DB (insert, connect, clear, etc.)
- `DatabaseManagerTest.java` – JUnit test class for database methods
- `assets/` – Logos, guide images, and UI icons
- `speech.ps1` – PowerShell script used for voice input (speech-to-text)
- `memory.json` – Stores basic memory of the assistant (Q&A pairs)
- `ameen.db` – SQLite database file used by the app (stored under `resources/data/`)
- `pom.xml` – Maven build file with all dependencies and config
## 🧠 How to Run It:
1. Make sure you have Java 17 and Maven installed.
2. Clone the project:
   ```bash
   git clone https://github.com/HeadHanchoo/AmeenApp305.git
   cd AmeenApp305
3. Then run it with Maven:
   mvn compile
   mvn exec:java -Dexec.mainClass="Main"

##



## 🖥️ How It Works (Walkthrough):
After launching the app, you're greeted with a simple and clean interface designed for elderly users.
Everything is built to be clear, readable, and easy to follow—even for someone not used to tech.

<img src="https://github.com/user-attachments/assets/149652bf-7060-4f60-bf92-2183d91876be" width="500"/>

## 🧠 AI Chat Assistant
The main screen features the assistant (Ameen), who can answer questions about government services like Absher and Tawakkalna.
- It supports both typed and voice input (via PowerShell).

- Responses come from OpenAI and are tailored to be helpful and simple.
- The assistant remembers your name, age, and ID using a persistent memory system stored locally.
- There are two memory types:
- - Big memory: Stays saved between sessions and stores your name, age, and assistant chat.
- - Small memory: Gets reset when you close the app and stores temporary chat info.

## 🌓 Light & Dark Mode
Users can toggle between light and dark mode using the FlatLaf library.
Font size also adjusts based on user preferences.

<p align="center">
  <img src="https://github.com/user-attachments/assets/e084f935-323a-45a9-8f9e-5a0701908a79" width="400"/>
  <img src="https://github.com/user-attachments/assets/cb69e7c5-510d-4468-b771-26dfda833f3b" width="400"/>
</p>

## 🔍 FAQ Section
-There’s a built-in FAQ viewer where users can search or scroll for common questions

<img src="https://github.com/user-attachments/assets/fe9a3cfe-0bbe-4d0c-b3d1-65f504ea93c0" width="500"/>


## 📸 Visual Step-by-Step Guides
Visual guides walk users through actual screens for:
- How to transfer money in SNB
- How to check car violations in Absher
- How to view your ID in Tawakkalna
- - Each one is clickable and displayed in a carousel-style window with “Back” and “Next” buttons.
<div align="center"> <img src="https://github.com/user-attachments/assets/b5c3b529-1540-4610-8265-d250de6af194" width="300"/> <img src="https://github.com/user-attachments/assets/1a137ad5-aaff-42f4-a9f8-2a7ab47b1a05" width="250"/>
<br><br>

<img src="https://github.com/user-attachments/assets/3b3a427e-e7ee-4936-90cb-935e1d905482" width="250"/> <img src="https://github.com/user-attachments/assets/d87187ca-f983-48ac-901b-52afa278f24b" width="250"/>
<br><br>

<img src="https://github.com/user-attachments/assets/1b35bc78-b0f6-4b71-b5d2-14b7e1dc28f3" width="250"/> <img src="https://github.com/user-attachments/assets/9678697c-2a0b-4d2b-8d69-1180de68a56d" width="300"/> </div>


## ✅ Final Updates Based on Feedback
After receiving feedback from Professor Khalid on the initial submission, I made the following changes:
1. ✅Multithreading Thread Management
 - Feedback: "No thread creation or concurrent operations"
  - i added a multi-threaded response system using SwingWorker to keep the UI responsive while interacting with the OpenAI API.
### 

i used `SwingWorker` to make sure our OpenAI API call runs in the background without freezing the UI. Here's the code:

```java
// your code here
SwingWorker<String, Void> worker = new SwingWorker<>() {
    @Override
    protected String doInBackground() {
        return getOpenAIResponse(userInput);
    }

    @Override
    protected void done() {
        try {
            String response = get();
            chatHistory.add("Ameen: " + response);
            updateChatDisplay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
};
worker.execute();
```
2. ✅Multithreading Synchronization:
   - Feedback: "No synchronization or thread safety"
   - added a synchronized methods for shared memory access to avoid race conditions.
```java
 public static synchronized void loadMemory() {
        try (InputStream is = Main.class.getResourceAsStream("/data/memory.json")) {
            if (is != null) {
                String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                if (!content.isEmpty()) {
                    JSONObject json = new JSONObject(content);
                    for (String key : json.keySet()) {
                        memory.put(key, json.getString(key));
                    }
                }
            } else {
                System.out.println("memory.json not found in resources.");
            }
        } catch (Exception e) {
            System.out.println("Failed to load memory: " + e.getMessage());
        }
    }


    public static synchronized void saveMemory() {
        JSONObject json = new JSONObject(memory);
        try {
            // Write to the resources folder under target/classes/data
            File file = new File("src/main/resources/data/memory.json");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json.toString(2)); // Pretty print with indentation
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Failed to save memory.");
        }
    }
```
3. ✅Code Quality / Version Control
   - Feedback: "Poor structure. This is not a Maven project. Hard to build and test."
   - i fully converted the project to Maven with a proper pom.xml, updated folder structure, and removed local JAR dependencies.
   - 
4. ✅Testing & Reliability
   - Feedback: "No unit tests or test coverage was found."
   - added a DatabaseManagerTest.java file with unit tests using JUnit
   - - only tested this class because it handles data logic (DB connection, insert, clear, etc.) which is easy to test in isolation. The other classes are mostly UI-related and harder to test with JUnit.
     

   ![image](https://github.com/user-attachments/assets/1c198778-672e-4bd0-88e2-df7aaedafb76)

5. ✅README / Documentation
   - i rewrote the README in a cleaner, better formatted , and more professional tone,

##  👤 Made By
- Bassam Barak
- Student ID: [2236668]
- CPIT 305 – Final Project
- King Abdulaziz University

Thanks for checking out my project!

and Special thanks to Professor Khalid Alharbi for the guidance and feedback throughout this project and Semester.





