public class WorkData {

    private NetData data;
    private Client srcSocket;

    public WorkData(NetData data, Client srcSocket) {
        this.data = data;
        this.srcSocket = srcSocket;
    }

    public NetData getData() {
        return data;
    }

    public Client getSrcSocket() {
        return srcSocket;
    }
}
