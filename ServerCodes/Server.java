import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static EchoThread customerThread, carrierThread, marketThread;
    public static void main(String[] args) {
        Socket socket = null;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4545);
            System.out.println("Server is up and running!");
            while(true) {
                socket = serverSocket.accept();
                System.out.println("Connected to the " + socket.getRemoteSocketAddress());
                
                try {
                    EchoThread thread = new EchoThread(socket);

                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                    String utf8string = EchoThread.readLine(dataInputStream);

                    if(utf8string.equals("0")){
                        marketThread = thread;
                        System.out.print("A market connected to the system!");
                    } else if(utf8string.equals("1")){
                        customerThread = thread;
                        System.out.print("A customer connected to the system!");
                    } if(utf8string.equals("2")){
                        carrierThread = thread;
                        System.out.print("A carrier connected to the system!");
                    }

                    thread.start();

                    thread.sendCommand("clientConnected:"+utf8string);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException excep)
                {
                    excep.printStackTrace(System.err);
                }
            }
        }
    }
}