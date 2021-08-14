import com.mysql.cj.x.protobuf.MysqlxExpr;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Network {

    public static ArrayList<String> IPs = new ArrayList<>();

    //THIS IS MASTER CHECKING VERS OFTHERES
    public static void Network_Check() {
        while (true) {
            String Curr_IP = "";
            try {
                for (String IP : IPs) {
                    System.out.println("Checking IP: " + IP);
                    Curr_IP += IP;
                    Socket socket = new Socket(IP, 900);
                    socket.setSoTimeout(60);

                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                    String Master_Verr = (String) objectInputStream.readObject();

                    if (Master_Verr.matches(Curr_Ver())) {
                        System.out.println("Host: " + socket.getInetAddress() + " Matches Ver");
                        objectOutputStream.writeObject("good");
                    } else {
                        objectOutputStream.writeObject("Remove");
                        IPs.remove(socket.getInetAddress().toString());
                    }

                    socket.close();

                }
            } catch (SocketTimeoutException socketTimeoutException) {
                System.out.println(socketTimeoutException);
                IPs.remove(Curr_IP);
            } catch (UnknownHostException unknownHostException) {
                IPs.remove(Curr_IP);
            } catch (IOException ioException) {

            } catch (ClassNotFoundException e) {

            }
        }
    }
    //THIS IS FOR MASTER PING SERVER
    public static void Network_Connect(){
        ArrayList<String> current = new ArrayList<>(1);
        while(true){
            try{
                for(String mIP: IPs){
                    current.add(mIP);
                    System.out.println("Connecting to " + mIP);
                    Socket socket = new Socket(mIP, 10000);
                    current.remove(1);

                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                    oos.writeObject(FreeDomCrypto.Ver);

                    oos.close();
                    ois.close();
                    socket.close();
                }
            }catch (Exception ex){
                IPs.remove(current.get(1));
                System.out.println("Removing: "+ current.get(1));
                current.remove(1);
            }
        }
    }
    public static void Network_GET() {  ///THIS IS THE MASTER PING SERVER
        while (true) {
            try {
                ServerSocket ss = new ServerSocket(10000);
                System.out.println("Waiting for Connection from a server");
                Socket socket = ss.accept();
                System.out.println("GOT connection from IP: "+ socket.getInetAddress());

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                if(socket.isConnected()){


                String Master_Ver = (String) ois.readObject();
                ArrayList<String> Temp_List = new ArrayList<>();

                System.out.println("Current WW Ver: "+ Curr_Ver());
                System.out.println("Current Requested Server Ver: "+ Master_Ver);
                System.out.println("MATCHES: "+ Curr_Ver().matches(Master_Ver));

                if (Master_Ver.equals(Curr_Ver())) {
                    System.out.println("VER MATCHED - CHECKING IF MASTER IN LIST");
                    if (!IPs.contains(socket.getInetAddress().toString())) {
                        IPs.add(socket.getInetAddress().toString());
                        System.out.println("MASTER VALIDATED: "+ socket.getInetAddress());
                    }else {
                        System.out.println("Master Not Added -- Already IN LIST");
                    }
                    for (String IP : IPs) {
                        if (!IP.matches(socket.getInetAddress().toString())) {
                            Temp_List.add(IP);
                            System.out.println("Added Master to temp list: "+ IP);
                        }
                    }
                    oos.write(1);
                    }
                    if(Temp_List.isEmpty()){
                        oos.writeObject(true);
                    }else {
                        oos.writeObject(false);
                        oos.writeObject(Temp_List);
                    }
                } else {
                    oos.write(0); //0 IS FALSE -NOT ADDED
                }

                oos.close();
                ois.close();
                socket.close();


            } catch (Exception ex) {

            }
        }
    }
    public static void GETIPs(){ //THIS WELL BE FOR CONNECTING TO DNS IP HANDOUT VIA ENCRYPTED MESSAGE
        while(true){
            try{
                Socket socket = new Socket("cdn1.qcnetworks.ca", 20);

                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                oos.writeObject(FreeDomCrypto.Ver);

                Object result = (Object) ois.readObject();

                if(result instanceof ArrayList){
                    ArrayList<String> DNSIPs = (ArrayList<String>) result;
                    for (String Master: DNSIPs){
                        if(!IPs.contains(Master)){
                            IPs.add(Master);
                            System.out.println("Added Master IP: "+ Master);
                        }
                    }
                    oos.close();
                    ois.close();
                    socket.close();
                }

                if(result instanceof Boolean){
                    oos.close();
                    ois.close();
                    socket.close();
                    System.out.println("No Data Recived");
                }

            }catch (Exception ex){

            }
        }
    }

    //THIS IS THE CURRENT (WWW NETWORK VER) ACTIVLY REPORTED
    public static String Curr_Ver(){
        String Ver = "";
        try{
            URL url = new URL("http://qcnetworks.ca/master/verr.php");

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;

            while ((line = in.readLine()) !=  null){
                System.out.println("Current Server Ver is: "+ line);
                Ver += line;
            }
        }catch (Exception exception){
            System.out.println(exception);
        }
        return Ver;
    }

    public static void Status(){
        try {
            while (true) {
                if(!IPs.isEmpty()) {
                    System.out.println("Current IP's: ");
                }
                TimeUnit.SECONDS.sleep(30);
                if (!IPs.isEmpty()) {
                    for (String IP : IPs) {
                        System.out.println(IP);
                    }
                }
                System.console().flush();
            }
        }catch (Exception ex){
            System.out.println(ex);
        }
    }
}
