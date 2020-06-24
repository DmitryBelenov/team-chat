package chat.json;

import chat.json.chat.Chat;
import chat.json.chat.Message;
import chat.windows.chat.ChatWindow;
import javafx.scene.control.TextArea;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JsonMessageStorage {

    private static final String teamChatsStoragePath = System.getProperty("user.home") + "/TeamChat";

    public static void init(String userId){
        String userStoragePath = teamChatsStoragePath + "/" + userId;
        File file = new File(userStoragePath);

        if (!file.exists()){
            boolean created = file.mkdirs();
            if (!created)
                System.out.println("Не могу создать папку хранилища сообщений для пользователя "+userId);
        }
    }

    public static synchronized Chat get(String chatName){
        String userStoragePath = teamChatsStoragePath + "/" + ChatWindow.authorizedUserId + "/" + chatName + ".json";
        File file = new File(userStoragePath);

        if (!file.exists())
            return null;

        String chatJson = null;
        try {
            chatJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (chatJson != null){
            ObjectMapper mapper = new ObjectMapper();
            Chat chat = null;

            try {
                chat = mapper.readValue(chatJson, Chat.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return chat;
        } else {
            return null;
        }
    }

    public static synchronized void addChatMessage(String chatName, Message message){
        Chat chat = get(chatName);
        if (chat == null){
            chat = new Chat();
            chat.setName(chatName);
            chat.setMessages(Collections.singletonList(message));
        } else {
            chat.getMessages().add(message);
        }

        ObjectMapper mapper = new ObjectMapper();
        String chatJson = null;
        try {
            chatJson = mapper.writeValueAsString(chat);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (chatJson != null){
            try {
                String workFile = teamChatsStoragePath + "/" + ChatWindow.authorizedUserId + "/" +chatName+ ".json";
                File file = new File(workFile);

                if (file.exists())
                    FileUtils.forceDelete(new File(workFile));

                FileUtils.writeStringToFile(new File(workFile), chatJson, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateFromLocalStorage(Map<String, TextArea> chatsMap, boolean group){
        String userStoragePath = teamChatsStoragePath + "/" + ChatWindow.authorizedUserId + "/";
        File file = new File(userStoragePath);
        File[] chats = file.listFiles();

        if (chats != null && chats.length > 0) {
            for (File f : chats) {
                String chatName = f.getName().substring(0, f.getName().length() - 5);
                TextArea chatTextArea = chatsMap.get(chatName);

                if (chatTextArea != null) {
                    Chat chatStory = get(chatName);
                    StringBuilder story = new StringBuilder("");
                    if (chatStory != null) {
                        int i = 0;
                        for (Message msg : chatStory.getMessages()) {
                            if (group && i == chatStory.getMessages().size() - 1) {
                                ChatWindow.groupLastMsgDatesMap.put(msg.getTo(), msg.getMessageDate());
                            }
                            story.append(msg.getText()).append("\n");
                            i++;
                        }
                    }

                    chatTextArea.clear();
                    chatTextArea.setText(story.toString());
                }
            }
        }
    }

    public static void initializeGroupChatMapping(String userId){
        String userStoragePath = teamChatsStoragePath + "/" + userId + "/";
        File file = new File(userStoragePath);
        File[] chats = file.listFiles();

        if (chats != null && chats.length > 0) {
            for (File f : chats) {
                String chatName = f.getName().substring(0, f.getName().length() - 5);

                Chat chatStory = get(chatName);
                if (chatStory != null){
                    TreeSet<Date> dates = new TreeSet<>();
                    for (Message message : chatStory.getMessages()){
                        dates.add(message.getMessageDate());
                    }

                    ChatWindow.groupLastMsgDatesMap.put(chatStory.getName(), dates.last());
                }
            }
        }
    }
}
