import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Client {

    private String id;
    private SocketChannel socket;
    private String name;
    private Executor executor;
    private Consumer<WorkData> addQueue;
    private NetData.Builder dataBuilder;

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
                ByteBuffer buffer = ByteBuffer.allocate(100000000);
                try {
                    socket.read(buffer);
                    buffer.flip();
                    byte[] arr = new byte[buffer.limit()];
                    buffer.get(arr, 0, buffer.limit());
                    buffer.flip();
                    addQueue.accept(new WorkData(dataBuilder.parseReceivedData(new String(arr)), this));
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
                socket.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
