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

    @Results({
            @Result(property = "userId", column = "user_id")
    })
    @Select("SELECT user_id FROM groups WHERE group_name = #{groupName}")
    List<String> getGroupUserIds(String groupName);

    @Results({
            @Result(property = "userRole", column = "user_role"),
            @Result(property = "passwordHex", column = "password_hex"),
            @Result(property = "createDate", column = "create_date")
    })
    @Select("SELECT * FROM users WHERE id=#{Id}")
    UserEntity getGroupUser(String userId);

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

    @Select("select id from users where nickname = #{nickName}")
    String getIdByNickName(String nickName);

    @Results({
            @Result(property = "groupName", column = "group_name"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "createDate", column = "create_date")
    })
    @Select("select * from groups where group_name = #{groupName}")
    List<GroupEntity> getGroupDataByName(String groupName);

    @Update("UPDATE messages SET received=true WHERE id=#{id}")
    void setReceivedMessage(String id);

    @Results({
            @Result(property = "userRole", column = "user_role"),
            @Result(property = "passwordHex", column = "password_hex"),
            @Result(property = "createDate", column = "create_date")
    })
    @Select("SELECT * FROM users WHERE nickname=#{login} and password_hex=#{password}")
    UserEntity getUser(@Param("login") String login, @Param("password") String password);

    @Results({
            @Result(property = "groupName", column = "group_name")
    })
    @Select("SELECT group_name FROM groups WHERE user_id=#{userId} AND blocked=false")
    List<String> getUserGroupNames(String userId);

    @Results({
            @Result(property = "msgText", column = "msg_text"),
            @Result(property = "fromId", column = "from_id"),
            @Result(property = "toId", column = "to_id"),
            @Result(property = "fileName", column = "file_name"),
            @Result(property = "groupMsg", column = "group_msg"),
            @Result(property = "sendDate", column = "send_date")
    })
    @Select("select * from messages where to_id=#{group} AND group_msg=true order by send_date ASC")
    List<MessageEntity> getIncomingGroupMessage(String group);

    @Results({
            @Result(property = "groupName", column = "group_name"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "createDate", column = "create_date")
    })
    @Select("SELECT * FROM groups WHERE user_id=#{userId}")
    List<GroupEntity> getUserGroups(String userId);
}
