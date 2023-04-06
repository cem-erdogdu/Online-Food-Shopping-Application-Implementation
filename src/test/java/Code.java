import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class EchoThread extends Thread {
    protected Socket socket;

    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    public EchoThread(Socket clientSocket) {
        this.socket = clientSocket;

        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String line;
        while (true) {
            try {

                line = bufferedReader.readLine();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    return;
                } else {
                    inputData(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void inputData(String data){
        System.out.println(data);
        String[] separated = data.split(",");

        if(separated[0].equals("0")){
            processMessage(separated[1]);
        } else if(separated[1].equals("0")){
            processCommand(separated[1]);
        }
    }
    //SEND
    public void sendCommand(String data) throws IOException {
        bufferedWriter.write(1 + "," + data + "\n");
        bufferedWriter.flush();
    }

    public void sendMessage(String data) throws IOException {
        bufferedWriter.write(0 + "," + data + "\n");
        bufferedWriter.flush();
    }

    private void sendDirectly(String data) throws IOException {
        bufferedWriter.write(data);
        bufferedWriter.flush();
    }


    //PROCESS
    private void processCommand(String data){
        String[] separated = data.split(":");

        if(separated[0].equals("processOrder")){
            processOrderToMarket(separated[1]);
        }
    }

    private void processMessage(String data){
        System.out.println(2);
        System.out.println(data);
    }

    //CUSTOMERTOMARKETCODES

    public void processOrderToMarket(String data){
        //Server.marketThread.sendDirectly(data);
    }

    public static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        while (true) {
            int b = in.read();
            if (b < 0) {
                throw new IOException("Data truncated");
            }
            if (b == 0x0A) {
                break;
            }
            buffer.write(b);
        }
        return new String(buffer.toByteArray(), "UTF-8");
    }
}