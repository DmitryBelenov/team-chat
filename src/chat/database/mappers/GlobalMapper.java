package chat.database.mappers;

import chat.database.entity.GroupEntity;
import chat.database.entity.MessageEntity;
import chat.database.entity.UserEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface GlobalMapper {
    @Insert("insert into users (id, username, nickname, user_role, email, password_hex, online, blocked, create_date) " +
            "values (#{id}, #{username}, #{nickname}, #{userRole}, #{email}, #{passwordHex}, #{online}, #{blocked}, #{createDate})")
    boolean saveUser(UserEntity user);

    @Insert("insert into groups (id, group_name, user_id, blocked, create_date) " +
            "values (#{id}, #{groupName}, #{userId}, #{blocked}, #{createDate})")
    boolean appendUserToGroup(GroupEntity group);

    @Insert("insert into messages (id, msg_text, from_id, to_id, file_name, group_msg, send_date, received) " +
            " values (#{id}, #{msgText}, #{fromId}, #{toId}, #{fileName}, #{groupMsg}, #{sendDate}, #{received})")
    boolean sendMessage(MessageEntity message);

    @Results({
            @Result(property = "userRole", column = "user_role"),
            @Result(property = "passwordHex", column = "password_hex"),
            @Result(property = "createDate", column = "create_date")
    })
    @Select("SELECT * FROM users WHERE blocked = false")
    List<UserEntity> getAllUsers();

    @Select("SELECT user_id FROM groups WHERE id = #{id}")
    List<String> getGroupUserIds(String groupId);

    @Results({
            @Result(property = "userRole", column = "user_role"),
            @Result(property = "passwordHex", column = "password_hex"),
            @Result(property = "createDate", column = "create_date")
    })
    @Select("SELECT * FROM users WHERE id IN (#{Ids})")
    List<UserEntity> getGroupUsers(String userIds);

    @Results({
            @Result(property = "msgText", column = "msg_text"),
            @Result(property = "fromId", column = "from_id"),
            @Result(property = "toId", column = "to_id"),
            @Result(property = "fileName", column = "file_name"),
            @Result(property = "groupMsg", column = "group_msg"),
            @Result(property = "sendDate", column = "send_date")
    })
    @Select("select * from messages where to_id = #{nickname} and received=false order by send_date desc")
    List<MessageEntity> getIncomingPrivateMessages(String nickname);

    @Select("select nickname from users where id = #{id}")
    String getNickNameById(String id);

    @Update("UPDATE messages SET received=true WHERE id=#{id}")
    void setReceivedMessage(String id);

    @Results({
            @Result(property = "userRole", column = "user_role"),
            @Result(property = "passwordHex", column = "password_hex"),
            @Result(property = "createDate", column = "create_date")
    })
    @Select("SELECT * FROM users WHERE nickname=#{login} and password_hex=#{password}")
    UserEntity getUser(@Param("login") String login, @Param("password") String password);
}
