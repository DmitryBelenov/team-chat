package chat.database;

import chat.database.entity.GroupEntity;
import chat.database.entity.MessageEntity;
import chat.database.entity.UserEntity;
import chat.database.mappers.GlobalMapper;
import chat.json.chat.Message;
import chat.objects.GroupCommonData;
import chat.utils.CryptoUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class DBConnection {

    private static synchronized SqlSessionFactory getFactory() {
        SqlSessionFactory sqlSessionFactory = null;
        String resource = "mybatis.xml";
        try {
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return sqlSessionFactory;
    }

    public static synchronized <E> boolean persist(E entity) {
        boolean res = false;
        SqlSessionFactory sqlSessionFactory = getFactory();
        if (sqlSessionFactory != null) {
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                GlobalMapper globalMapper = session.getMapper(GlobalMapper.class);
                if (entity instanceof UserEntity) {
                    res = globalMapper.saveUser((UserEntity) entity);
                } else if (entity instanceof GroupEntity) {
                    res = globalMapper.appendUserToGroup((GroupEntity) entity);
                } else if (entity instanceof MessageEntity) {
                    res = globalMapper.sendMessage((MessageEntity) entity);
                }
            }
        }
        return res;
    }

    public static List<MessageEntity> getMessages(String id) {
        List<MessageEntity> messages = new LinkedList<>();
        SqlSessionFactory sqlSessionFactory = getFactory();
        if (sqlSessionFactory != null) {
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                GlobalMapper globalMapper = session.getMapper(GlobalMapper.class);
                String nickname = getNickNameById(id);
                messages = globalMapper.getIncomingPrivateMessages(nickname);
                if (messages.size() > 0) {
                    for (MessageEntity msg : messages) {
                        String fromNick = getNickNameById(msg.getFromId());
                        msg.setFromId(fromNick);
                        setReceivedMessage(msg.getId());
                    }
                }
            }
        }
        return messages;
    }

    public static List<MessageEntity> getGroupMessages(String id) {
        List<MessageEntity> messages = new LinkedList<>();
        SqlSessionFactory sqlSessionFactory = getFactory();
        if (sqlSessionFactory != null) {
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                GlobalMapper globalMapper = session.getMapper(GlobalMapper.class);
                List<String> userGroups = globalMapper.getUserGroupNames(id);
                for (String group : userGroups) {
                    List<MessageEntity> messageEntity = globalMapper.getIncomingGroupMessage(group);
                    if (messageEntity != null)
                        messages.addAll(messageEntity);
                }
            }
        }
        return messages;
    }

    private static String getNickNameById(String id) {
        String nickname = null;
        SqlSessionFactory sqlSessionFactory = getFactory();
        if (sqlSessionFactory != null) {
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                GlobalMapper globalMapper = session.getMapper(GlobalMapper.class);
                nickname = globalMapper.getNickNameById(id);
            }
        }
        return nickname;
    }

    private static void setReceivedMessage(String id) {
        SqlSessionFactory sqlSessionFactory = getFactory();
        if (sqlSessionFactory != null) {
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                GlobalMapper globalMapper = session.getMapper(GlobalMapper.class);
                globalMapper.setReceivedMessage(id);
            }
        }
    }

    public static synchronized List<UserEntity> getUsers(String alias) {
        List<UserEntity> users = new ArrayList<>();
        SqlSessionFactory sqlSessionFactory = getFactory();
        if (sqlSessionFactory != null) {
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                GlobalMapper globalMapper = session.getMapper(GlobalMapper.class);
                users = getUsersByAlias(alias, globalMapper);
            }
        }
        return users;
    }

    private static synchronized List<UserEntity> getUsersByAlias(String alias, GlobalMapper globalMapper) {
        List<UserEntity> list = new ArrayList<>();
        if ("all".equals(alias)) {
            list = globalMapper.getAllUsers();
        } else if (alias.startsWith("group_")) {
            List<String> ids = globalMapper.getGroupUserIds(alias.substring(6));
            for (String id : ids){
                list.add(globalMapper.getGroupUser(id));
            }
        }
        return list;
    }

    public static synchronized UserEntity authorization(String[] userAuthData) {
        UserEntity user = null;
        SqlSessionFactory sqlSessionFactory = getFactory();
        if (sqlSessionFactory != null) {
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                GlobalMapper globalMapper = session.getMapper(GlobalMapper.class);

                String password = "";
                try {
                    password = CryptoUtils.getHexBase64(userAuthData[1]);
                } catch (NoSuchAlgorithmException nsa) {
                    nsa.printStackTrace();
                }
                String login = userAuthData[0];
                user = globalMapper.getUser(login, password);
            }
        }
        return user;
    }

    public static String createGroup(GroupCommonData groupCommonData) {
        String name = groupCommonData.getGroupName();
        String result = "Группа '" + name + "' создана\nУчастников: " + (groupCommonData.getGroupUsers().size() + 1);
        SqlSessionFactory sqlSessionFactory = getFactory();
        if (sqlSessionFactory != null) {
            List<String> groupUserIds = new ArrayList<>();
            String groupName = groupCommonData.getGroupName();
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                GlobalMapper globalMapper = session.getMapper(GlobalMapper.class);

                List<GroupEntity> groupList = globalMapper.getGroupDataByName(groupName);
                if (groupList == null || groupList.size() == 0) {
                    for (String nick : groupCommonData.getGroupUsers()) {
                        groupUserIds.add(globalMapper.getIdByNickName(nick));
                    }
                    groupUserIds.add(groupCommonData.getCreatorId());
                } else {
                    result = "Группа с именем '" + groupName + "' уже зарегистрирована в системе";
                }
            }

            if (groupUserIds.size() > 0) {
                for (String userId : groupUserIds) {
                    GroupEntity groupEntity = new GroupEntity();
                    groupEntity.setId(UUID.randomUUID().toString());
                    groupEntity.setGroupName(groupName);
                    groupEntity.setUserId(userId);
                    groupEntity.setBlocked(false);
                    groupEntity.setCreateDate(new Date());

                    persist(groupEntity);
                }

                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setId(UUID.randomUUID().toString());
                messageEntity.setFromId(name);
                messageEntity.setToId(name);
                messageEntity.setReceived(false);
                messageEntity.setGroupMsg(true);
                messageEntity.setMsgText("* Вы были добавлены в группу '"+name+"', добро пожаловать!");
                messageEntity.setSendDate(new Date());

                persist(messageEntity);
            }
        }
        return result;
    }

    public static List<GroupEntity> getUserGroups(String userId){
        List<GroupEntity> groups = new ArrayList<>();
        SqlSessionFactory sqlSessionFactory = getFactory();
        if (sqlSessionFactory != null) {
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                GlobalMapper globalMapper = session.getMapper(GlobalMapper.class);
                groups = globalMapper.getUserGroups(userId);
            }
        }
        return groups;
    }
}


