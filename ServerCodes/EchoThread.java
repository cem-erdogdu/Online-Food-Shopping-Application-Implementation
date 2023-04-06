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
        String[] separated = data.split(",");

        if(separated[0].equals("0")){
            processMessage(separated[1]);
        } else if(separated[0].equals("1")){
            processCommand(separated[1]);
        }
    }
    //SEND
    public void sendCommand(String data) throws IOException {
        new Thread(() -> {
            try {
                System.out.println(data);
                bufferedWriter.write(1 + "," + data + "\n");
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        
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
        if(separated[0].equals("createOrder")){
            processOrderToMarket(separated[1]);
        } else if(separated[0].equals("processOrder")){
            processOrderToCarrier(separated[1]);
        } else if(separated[0].equals("orderResponse")){
            //Did market approved
            processOrderToCarrier(separated[1]);
        } else if(separated[0].equals("orderCompleted")){
            //Did market approved
            processDeliveryToMarketAndCarrier();
        }
    }

    private void processMessage(String data){
        System.out.println(2);
        System.out.println(data);
    }

    //CUSTOMERTOMARKETCODES

    public void processOrderToMarket(String data){
        try {
            Server.marketThread.sendCommand("processOrder:" + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //MARKETTOCARRIERCODES

    public void processOrderToCarrier(String data){
        String[] separated = data.split("&");
        String resultRemovedData = data.substring(2, data.length());
        try {
            if(separated[0].equals("1")){
                //ORDER ACCEPTED SENT TO CARRIER
                Server.carrierThread.sendCommand("processCarrier:" + resultRemovedData);
                Server.customerThread.sendCommand("marketAccepted:1");
            } else{
                //ORDER REFUSED SENT TO CUSTOMER
                Server.customerThread.sendCommand("marketRefused:1");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //CARRIERTOCUSTOMERANDMARKETCODES

    public void processDeliveryToMarketAndCarrier(){
        try {
            Server.marketThread.sendCommand("orderCompletedReceive:1");
            Server.customerThread.sendCommand("orderCompletedReceive:1");
        } catch (IOException e) {
            e.printStackTrace();
        }
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