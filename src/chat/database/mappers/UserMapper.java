package chat.database.mappers;

import chat.database.entity.UserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper {
    @Insert("insert into users (id, username, nickname, user_role, email, password_hex, online, blocked, create_date) " +
            "values (#{id}, #{username}, #{nickname}, #{userRole}, #{email}, #{passwordHex}, #{online}, #{blocked}, #{createDate})")
    boolean save(UserEntity user);

    @Select("SELECT * FROM users WHERE blocked = false")
    List<UserEntity> getAllUsers();

    @Select("SELECT user_id FROM groups WHERE id = #{id}")
    List<String> getGroupUserIds(String groupId);

    @Select("SELECT * FROM users WHERE id IN (#{Ids})")
    List<UserEntity> getGroupUsers(String userIds);
}
