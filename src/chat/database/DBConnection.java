package chat.database;

import chat.database.entity.UserEntity;
import chat.database.mappers.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DBConnection {

    private static SqlSessionFactory getFactory() {
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

    public static <E> boolean persist(E entity) {
        boolean res = false;
        SqlSessionFactory sqlSessionFactory = getFactory();
        if (sqlSessionFactory != null) {
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                if (entity instanceof UserEntity) {
                    UserMapper userMapper = session.getMapper(UserMapper.class);
                    res = userMapper.save((UserEntity) entity);
                }
            }
        }
        return res;
    }

    public static List<UserEntity> getUsers(String alias){
        List<UserEntity> users = new ArrayList<>();
        SqlSessionFactory sqlSessionFactory = getFactory();
        if (sqlSessionFactory != null) {
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                UserMapper userMapper = session.getMapper(UserMapper.class);
                users = getUsersByAlias(alias, userMapper);
            }
        }
        return users;
    }

    private static List<UserEntity> getUsersByAlias(String alias, UserMapper userMapper){
        List<UserEntity> list = new ArrayList<>();
        if ("all".equals(alias)){
            list = userMapper.getAllUsers();
        } else if (alias.startsWith("group_")){
            List<String> ids = userMapper.getGroupUserIds(alias.substring(7));
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (String id : ids){
                sb.append("'").append(id).append("'").append(i == ids.size()-1 ? "" : ",");
                i++;
            }
            list = userMapper.getGroupUsers(sb.toString());
        }
        return list;
    }
}


