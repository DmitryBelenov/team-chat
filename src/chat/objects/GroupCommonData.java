package chat.objects;

import java.io.Serializable;
import java.util.List;

public class GroupCommonData implements Serializable {

    private static final long serialVersionUID = 2257846952896046093L;

    private List<String> groupUsers;
    private String creatorId;
    private String groupName;

    public GroupCommonData(List<String> groupUsers, String creatorId, String groupName) {
        this.groupUsers = groupUsers;
        this.creatorId = creatorId;
        this.groupName = groupName;
    }

    public List<String> getGroupUsers() {
        return groupUsers;
    }

    public void setGroupUsers(List<String> groupUsers) {
        this.groupUsers = groupUsers;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
