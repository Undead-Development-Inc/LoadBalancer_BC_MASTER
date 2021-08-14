public class main {

    public static void main(String[] args) throws Exception {
        StringUtil.HASH();
        new G_BLOCK().SETUP_GBLOCK();
        new TEST_SEQ().Test_Chain();
        Thread API_NET = new Thread(Net::APINETWORK);
        Thread Network_PING = new Thread(Network::Network_Connect);
        Thread Network_Check = new Thread(Network::Network_Check);
        Thread Network_GET = new Thread(Network::Network_GET);
        Thread Net_Status = new Thread(Network::Status);
        Thread DNS_NET = new Thread(Network::GETIPs);
        DNS_NET.start();
        API_NET.start();
        Network_Check.start();
        Network_GET.start();
        Network_PING.start();
        Net_Status.start();
    }
}
