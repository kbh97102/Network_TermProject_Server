import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Server {

    private ServerSocketChannel server;
    private Vector<Client> clientList;
    private ArrayList<ChatRoomInfo> chatRoomInfos;
    private NetData.Builder dataBuilder;
    private ExecutorService executor;
    private ConcurrentLinkedQueue<WorkData> workQueue;
    private HashMap<String, Consumer<WorkData>> workMap;

    public Server() {

        dataBuilder = new NetData.Builder();
        clientList = new Vector<>();
        executor = Executors.newFixedThreadPool(10);
        workQueue = new ConcurrentLinkedQueue<>();
        workMap = new HashMap<>();
        chatRoomInfos = new ArrayList<>();
        initWorkMap();

        initSocket();
    }

    private void initSocket() {
        try {
            server = ServerSocketChannel.open();

            server.bind(new InetSocketAddress(10101));
            server.configureBlocking(true);
            acceptStart();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptStart() {
        startWork();
        executor.execute(() -> {
            while (server.isOpen()) {
                try {
                    System.out.println("Waiting Client");
                    SocketChannel clientSocket = server.accept();
                    System.out.println("Client " + clientSocket.hashCode() + "is connected");
                    Client client = new Client(clientSocket);
                    client.setAddQueue(this::addWork);
                    clientList.add(client);
                    NetData mainData = dataBuilder.setType("clientId")
                            .setContent(client.getId())
                            .build();
                    NetData header = dataBuilder.setContent(String.valueOf(mainData.toString().length())).buildHeader();
                    client.write(header);
                    client.write(mainData);
                    client.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addWork(WorkData workData) {
        workQueue.add(workData);
    }

    private void startWork() {
        executor.execute(() -> {
            while (true) {
                WorkData workData = workQueue.poll();
                if (Objects.nonNull(workData)) {
                    workWithType(workData);
                }
            }
        });
    }

    private void workWithType(WorkData data) {
        workMap.get(data.getData().getType()).accept(data);
    }

    private void initWorkMap() {
        workMap.put("requestList", this::sendUserList);
        workMap.put("text", this::sendText);
        workMap.put("image", this::sendText);
        workMap.put("requestAdd", this::requestAdd);
        workMap.put("setName", this::setName);
        workMap.put("disconnect", this::disconnect);
    }

    private void sendUserList(WorkData workData) {
        ArrayList<String> clientIdList = new ArrayList<>();
        for (Client client : clientList) {
            if(!client.getId().equals(workData.getData().getUserId())){
                clientIdList.add(client.getId());
            }
        }
        NetData mainData = dataBuilder.setType("requestList")
                .setList(clientIdList)
                .build();
        NetData header = dataBuilder.setContent(String.valueOf(mainData.toString().length())).buildHeader();
        workData.getSrcSocket().write(header);
        workData.getSrcSocket().write(mainData);
    }

    private void sendText(WorkData workData) {
        for (ChatRoomInfo info : chatRoomInfos) {
            if (info.getRoom_id().equals(workData.getData().getRoomId())) {
                for (Client client : getClientFromId(info.getUser_ids())) {
                    if (clientList.contains(client) && !client.getId().equals(workData.getSrcSocket().getId())) {
                        NetData header = dataBuilder.setContent(String.valueOf(workData.getData().toString().length())).buildHeader();
                        client.write(header);
                        client.write(workData.getData());
                    }
                }
                break;
            }
        }
    }

    private void requestAdd(WorkData workData) {
        ChatRoomInfo chatRoomInfo = new ChatRoomInfo();
        chatRoomInfo.setUser_ids(workData.getData().getList());
        chatRoomInfos.add(chatRoomInfo);
        for (Client client : getClientFromId(workData.getData().getList())) {
            NetData mainData = dataBuilder.setType("requestAdd")
                    .setContent(chatRoomInfo.getRoom_id())
                    .build();
            NetData header = dataBuilder.setContent(String.valueOf(mainData.toString().length())).buildHeader();
            client.write(header);
            client.write(mainData);
        }
    }

    private void setName(WorkData workData) {
        for (Client client : clientList) {
            if (client.getId().equals(workData.getData().getUserId())) {
                client.setName(workData.getData().getName());
                break;
            }
        }
    }

    private ArrayList<Client> getClientFromId(ArrayList<String> ids) {
        ArrayList<Client> users = new ArrayList<>();
        for (Client client : clientList) {
            for (String id : ids) {
                if (client.getId().equals(id)) {
                    users.add(client);
                    break;
                }
            }
        }
        return users;
    }

    private void disconnect(WorkData workData){
        System.out.println("Disconnect in"+" src id "+workData.getSrcSocket().getId());
        workData.getSrcSocket().disconnect();
        clientList.remove(workData.getSrcSocket());
    }

    public void writeImage() {
//        executor.execute(() -> {
//            try {
//                BufferedImage img = ImageIO.read(new File("icon.jpg"));
//                ImageIO.write(img, "jpg", client.socket().getOutputStream());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
    }

}
