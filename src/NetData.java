import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class NetData {

    private JSONObject data;

    private NetData(){

    }

    public static class Builder{

        private String name = "no";
        private String userId;
        private String type;
        private String content;
        private String chatRoomId;
        private ArrayList<String> list;

        public Builder(){
            list = new ArrayList<>();
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setList(ArrayList<String> list) {
            this.list = list;
            return this;
        }

        public Builder setRoomId(String id){
            this.chatRoomId = id;
            return this;
        }

        public Builder setUserId(String id){
            this.userId = id;
            return this;
        }

        public NetData build() {
            NetData clientData = new NetData();
            clientData.data = new JSONObject();
            try{
                clientData.data.put("name", name);
                clientData.data.put("type", type);
                clientData.data.put("content", content);
                clientData.data.put("list", list);
                clientData.data.put("roomId", chatRoomId);
                clientData.data.put("userId", userId);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return clientData;
        }


        public NetData parseReceivedData(String data)  {
            NetData receivedData = new NetData();
            try{
                receivedData.data = new JSONObject(data);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return receivedData;
        }

        public NetData buildList(){
            NetData clientData = new NetData();
            clientData.data = new JSONObject();
            try{
                clientData.data.put("name", name);
                clientData.data.put("type", type);
                clientData.data.put("content", list);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return clientData;
        }
    }

    public JSONObject getData(){
        return data;
    }

    public String getType(){
        try {
            return (String) data.get("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getName(){
        try {
            return (String) data.get("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getContent(){
        try {
            return (String) data.get("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getList(){
        try {
            JSONArray array = (JSONArray) data.getJSONArray("list");
            ArrayList<String> arr = new ArrayList<>();
            for (int i=0;i<array.length();i++){
                String data = array.getString(i);
                String test = data.replace("[", "").replace("]","");
                System.out.println(data+" "+test);
                arr.add(test);
            }
            return arr;
        } catch (JSONException e) {
            e.printStackTrace();
            ArrayList<String> arr = new ArrayList<>();
            String test = data.getString("list").replace("[", "").replace("]","");
            arr.add(test);
            System.out.println("Error "+ arr.get(0));
            return arr;

        }
    }

    public String getUserId(){
        try {
            return (String) data.get("userId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRoomId(){
        try {
            return (String) data.get("roomId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ByteBuffer parseByteBuffer(){
        return ByteBuffer.wrap(data.toString().getBytes());
    }
}
