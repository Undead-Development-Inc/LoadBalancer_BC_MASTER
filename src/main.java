public class main {

    public static void main(String[] args){
        Thread Network_Check = new Thread(Network::Network_Check);
        Thread Network_GET = new Thread(Network::Network_GET);
        Thread Net_Status = new Thread(Network::Status);
        Network_Check.start();
        Network_GET.start();
        Net_Status.start();
    }
}
