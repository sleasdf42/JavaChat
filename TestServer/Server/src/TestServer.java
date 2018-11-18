import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class TestServer {
    public static void main(String[] args) {

        final int PORT = 8189;

        Socket socket;

        System.out.println("Hello, I'm a server");

            try {
                ServerSocket serverSocket = new ServerSocket(PORT);

                while (true) {
                    socket = serverSocket.accept();
                    System.out.println("Подключился новый пользователь " + socket.getInetAddress() + " || " + socket.getPort());
                    (new NewThread(socket)).start();
                }
            } catch (IOException e) {
                System.out.println("Не удалось создать порт на сервере");
            }

            }



    }

class NewThread extends Thread {

    Socket socket;
    BufferedWriter out;
    BufferedReader in;

     static final ArrayList<NewThread> connections = new ArrayList<>(); // private?

    NewThread(Socket socket) {
        this.socket = socket;

    }
    @Override
    public void run() {
        try {


            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));

                connections.add(this);

                System.out.println("Hello " + socket.getInetAddress() + " || " + socket.getPort());

                String line = "";
                String delimeter = "-"; // разделитель
                String[] linedel;


            while (!NewThread.this.isInterrupted() && (line != null)) {

                line = in.readLine();

               System.out.println("Мы получили строку... Ура! " + line);

                 // делим строку по разделителю delimeter на 3 (IP PORT TEXT)
                try {
                    linedel = line.split(delimeter);
                }
                catch (RuntimeException e) {
                    break; // ..................................
                }
                if (linedel.length != 3) break; // .................................................доделать
                String IP = linedel[0].replaceAll(" ", "");
                String strport = linedel[1].replaceAll(" ", "");

                int port = Integer.parseInt(strport);

               // System.out.println("port = " + port);

                if (IP.equals("0.0.0.0.")) //  не робит...............................................
                 sendToAllIP(linedel[2]); else if (line != null) {

                     int num = findInetAddr(connections, IP, port);
                    if (num == 111111) {
                        System.out.println("Нет такого челобрека в сети");
                        NewThread.this.out.write(linedel[2] + "\r\n");
                        NewThread.this.out.flush();

                    } else {
                     connections.get(num).out.write(linedel[2] + " - " + connections.get(num).socket.getInetAddress()
                             + " - " + connections.get(num).socket.getPort() + " - " + NewThread.this.socket.getInetAddress() +
                             " - " + NewThread.this.socket.getPort() +"\r\n");

                   // System.out.println(" Вот что отправил сервер " + linedel[2] + " - " + connections.get(num).socket.getInetAddress()
                     //       + " - " + connections.get(num).socket.getPort() + "\r\n");

                     connections.get(num).out.flush();

                     NewThread.this.out.write(linedel[2] + " - " + connections.get(num).socket.getInetAddress()
                             + " - " + connections.get(num).socket.getPort() + " - " + NewThread.this.socket.getInetAddress() +
                             " - " + NewThread.this.socket.getPort() + "\r\n");
                     NewThread.this.out.flush();

                   // System.out.println("Вот ято отправил еще сервер " + linedel[2] + " - " + NewThread.this.socket.getInetAddress()
                     //       + " - " + NewThread.this.socket.getPort() + "\r\n");


                    System.out.println("Мы отправили строчку");
                    }
                 }

                }

                 System.out.println("Мы потеряли бойца " + socket.getInetAddress() + " || " + socket.getPort());
                connections.remove(this);


        } catch (IOException e) {
            System.out.println("Somethings wrong!!...");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println(" Не могу закрыть сокет!!! ");
            }
        }

    }

    synchronized int findInetAddr(ArrayList<NewThread> connections, String s1, int s2) {

        int size = connections.size();
        int port = port = 111111;
        for (int i = 0; i < size; i++) {
            if (connections.get(i).socket.getInetAddress().toString().equals(s1) && (connections.get(i).socket.getPort() == s2))
            { port = i;
            //System.out.println(" Смотри сюда, если все верно " + i + " номер " + connections.get(i).socket.getInetAddress() + "   " + connections.get(i).socket.getPort());
            }
         //System.out.println(" Смотри сюда " + i + " номер " + connections.get(i).socket.getInetAddress() + "   " + connections.get(i).socket.getPort());
        }
       // System.out.println("Я нашел нужный поток "  +  port);
        //System.out.println(port);
        return port;
    }


   synchronized void sendString (String value) {
        try {
            out.write( value + "\r\n"); // отправляем в поток данные
            out.flush(); // сбрасываем все из потока
        } catch (IOException e) {
            System.out.println("AAAAAAAAAAAAA");
        }
    }

    synchronized void sendToAllIP(String value) {
        System.out.println(value);
        final int size = connections.size();
        for (int i = 0; i < size; i++) {
            connections.get(i).sendString(value);
        }

    }
}