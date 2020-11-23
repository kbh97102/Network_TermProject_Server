import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Client {

    private String id;
    private SocketChannel socket;
    private String name;
    private ExecutorService executor;
    private Consumer<WorkData> addQueue;
    private NetData.Builder dataBuilder;
    private Future<?> testFuture;

    public Client(SocketChannel socket) {
        this.socket = socket;
        executor = Executors.newFixedThreadPool(10);
        dataBuilder = new NetData.Builder();
        id = String.valueOf(hashCode());
    }

    public String getId() {
        return id;
    }

    public void setAddQueue(Consumer<WorkData> addQueue) {
        this.addQueue = addQueue;
    }

    public SocketChannel getSocket() {
        return socket;
    }

    public void setSocket(SocketChannel socket) {
        this.socket = socket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void read() {
        executor.execute(() -> {
            while (socket.isConnected()) {
                ByteBuffer buffer = ByteBuffer.allocate(50000);
                try {
                    int rect = socket.read(buffer);
                    if (rect <= 1){
                        return;
                    }
                    buffer.flip();
                    byte[] arr = new byte[buffer.limit()];
                    buffer.get(arr, 0, buffer.limit());
                    buffer.flip();
                    NetData receivedHead = dataBuilder.parseReceivedData(new String(arr));
                    int size = Integer.parseInt(receivedHead.getContent());

                    ByteBuffer dataBuffer = ByteBuffer.allocate(size);

                    while(buffer.hasRemaining()){
                        socket.read(dataBuffer);
                    }

                    addQueue.accept(new WorkData(dataBuilder.parseReceivedData(new String(dataBuffer.array())), this));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void write(NetData data) {
        executor.execute(() -> {
            try {
                ByteBuffer buffer = data.parseByteBuffer();
                int rect = socket.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void disconnect(){
        executor.shutdownNow();
    }
}
