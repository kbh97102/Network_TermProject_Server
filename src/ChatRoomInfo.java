import java.util.ArrayList;

public class ChatRoomInfo {

    private String room_id;
    private ArrayList<String> user_ids;

    public ChatRoomInfo() {
        room_id = String.valueOf(hashCode());
    }

    public void setUser_ids(ArrayList<String> user_ids) {
        this.user_ids = new ArrayList<>();
        this.user_ids.addAll(user_ids);
    }

    public String getRoom_id() {
        return room_id;
    }

    public ArrayList<String> getUser_ids() {
        return user_ids;
    }
}
