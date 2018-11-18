import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection { // это наше одно соединение

   private final TCPConnectionListener eventListener; // наш слушвтель событий

   private final Socket socket;
   private final Thread rxThread; // 1 поток - 1 клиент, 1 поток может слушать входящее соединение и генерировать событие

   private final BufferedReader in; // работают со строками, а не с байтами
   private final BufferedWriter out; // как sysout

    public TCPConnection (TCPConnectionListener eventListener, String ipAddr, int port) throws IOException {
        this(eventListener, new Socket(ipAddr, port)); // вызываем из этого конструктора 2 конструктор
    }


    public TCPConnection (TCPConnectionListener eventListener, Socket socket) throws IOException { // принимает на вход Сокет и делает его текущим
        this.eventListener = eventListener;
        this.socket = socket;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));

        rxThread = new Thread(new Runnable() {
            @Override
            public void run() { //  поток
                try {
                    eventListener.onConnectionReady(TCPConnection.this); // передали экземпляр класса
                    while (!rxThread.isInterrupted()) {
                        String msg = in.readLine(); // читаем строчку
                        eventListener.onReceiveString(TCPConnection.this, msg);
                    }

                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                }
                finally { // обязательно нужно закрыть сокет
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start(); // стартуем

    }

    public synchronized void sendString(String value ) {
        try {
            out.write(value + "\r\n");
            out.flush(); // чтобы сбросить буфер
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    @Override // вызывается каждый раз когда нужно представить обьект в текстовом варианте
    public String toString() { // переопределяем метод
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
