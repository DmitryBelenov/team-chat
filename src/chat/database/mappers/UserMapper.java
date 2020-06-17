package chat.database.mappers;

import chat.database.entity.UserEntity;
import org.apache.ibatis.annotations.Insert;

public interface UserMapper {
    @Insert("insert into users (id, username, nickname, user_role, email, password_hex, online, blocked, create_date) " +
            "values (#{id}, #{username}, #{nickname}, #{userRole}, #{email}, #{passwordHex}, #{online}, #{blocked}, #{createDate})")
    boolean save(UserEntity user);


}
