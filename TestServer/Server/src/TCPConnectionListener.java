public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection tcpConnection); // готовое соединение
    void onReceiveString(TCPConnection tcpConnection, String value); // соединение приняло строчку
    void onDisconnect(TCPConnection tcpConnection); // соединение разорвалось
    void onException(TCPConnection tcpConnection, Exception e); // исключение


}
