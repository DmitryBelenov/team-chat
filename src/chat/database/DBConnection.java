package chat.database;

import chat.database.entity.UserEntity;
import chat.database.mappers.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

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


}


