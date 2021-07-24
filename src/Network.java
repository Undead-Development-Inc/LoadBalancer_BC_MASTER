import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Network {

    public static ArrayList<String> IPs = new ArrayList<>();


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

    public static void Network_GET() {
        while (true) {
            try {
                ServerSocket ss = new ServerSocket(10000);
                System.out.println("Waiting for Connection from a server");
                Socket socket = ss.accept();
                System.out.println("GOT connection from IP: "+ socket.getInetAddress());

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                String Master_Ver = (String) ois.readObject();
                ArrayList<String> Temp_List = new ArrayList<>();

                System.out.println("Current WW Ver: "+ Curr_Ver());

                if (Master_Ver.contains(Curr_Ver())) {
                    if (!IPs.contains(socket.getInetAddress().toString())) {
                        IPs.add(socket.getInetAddress().toString());
                        System.out.println("MASTER VALIDATED: "+ socket.getInetAddress());
                    }
                    for (String IP : IPs) {
                        if (!IP.matches(socket.getInetAddress().toString())) {
                            Temp_List.add(IP);
                            System.out.println("Added Master to temp list: "+ IP);
                        }
                    }
                    oos.write(1);
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
