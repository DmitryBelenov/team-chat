package chat.database;

import chat.database.entity.GroupEntity;
import chat.database.entity.MessageEntity;
import chat.database.entity.UserEntity;
import chat.database.mappers.GlobalMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
                } else if (entity instanceof GroupEntity){
                    res = globalMapper.appendUserToGroup((GroupEntity) entity);
                } else if (entity instanceof MessageEntity){
                    res = globalMapper.sendMessage((MessageEntity) entity);
                }
            }
        }
        return res;
    }

    public static List<MessageEntity> getMessages(String id){
        List<MessageEntity> messages = new LinkedList<>();
        SqlSessionFactory sqlSessionFactory = getFactory();
        if (sqlSessionFactory != null) {
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                GlobalMapper globalMapper = session.getMapper(GlobalMapper.class);
                String nickname = getNickNameById(id);
                messages = globalMapper.getIncomingPrivateMessages(nickname);
                if (messages.size() > 0){
                    for (MessageEntity msg : messages){
                        String fromNick = getNickNameById(msg.getFromId());
                        msg.setFromId(fromNick);
                    }
                    setReceivedMessages(messages);
                }
            }
        }
        return messages;
    }

    private static String getNickNameById(String id){
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

    private static void setReceivedMessages(List<MessageEntity> messages){
        SqlSessionFactory sqlSessionFactory = getFactory();
        if (sqlSessionFactory != null) {
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                GlobalMapper globalMapper = session.getMapper(GlobalMapper.class);
                int i=0;
                StringBuilder ids = new StringBuilder();
                for (MessageEntity me : messages){
                    ids.append("'").append(me.getId()).append("'").append(i == messages.size()-1 ? "" : ",");
                    i++;
                }
                globalMapper.setReceivedMessages(ids.toString());
            }
        }
    }

    public static synchronized List<UserEntity> getUsers(String alias){
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

    private static synchronized List<UserEntity> getUsersByAlias(String alias, GlobalMapper globalMapper){
        List<UserEntity> list = new ArrayList<>();
        if ("all".equals(alias)){
            list = globalMapper.getAllUsers();
        } else if (alias.startsWith("group_")){
            List<String> ids = globalMapper.getGroupUserIds(alias.substring(7));
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (String id : ids){
                sb.append("'").append(id).append("'").append(i == ids.size()-1 ? "" : ",");
                i++;
            }
            list = globalMapper.getGroupUsers(sb.toString());
        }
        return list;
    }
}


