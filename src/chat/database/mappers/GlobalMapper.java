package chat.database.mappers;

import chat.database.entity.GroupEntity;
import chat.database.entity.MessageEntity;
import chat.database.entity.UserEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    @Select("SELECT * FROM users WHERE blocked = false")
    List<UserEntity> getAllUsers();

    @Select("SELECT user_id FROM groups WHERE id = #{id}")
    List<String> getGroupUserIds(String groupId);

    @Select("SELECT * FROM users WHERE id IN (#{Ids})")
    List<UserEntity> getGroupUsers(String userIds);

    @Select("select * from messages where to_id = #{nickname} order by send_date desc")
    List<MessageEntity> getIncomingPrivateMessages(String nickname);

    @Select("select nickname from users where id = #{id}")
    String getNickNameById(String id);

    @Update("update messages set received = true where id in (#{ids})")
    void setReceivedMessages(String ids);
}
