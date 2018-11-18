import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class InThread extends Thread {

    Socket socket;
    ArrayList<NewThread> AllThread;
    BufferedReader in;
    BufferedWriter out;

    InThread(Socket socket, ArrayList<NewThread> AllThread) {
        this.socket = socket;
        this.AllThread = AllThread;
    }

    public NewThread findNewThread(ArrayList<NewThread> AllThread, String IP, String Port) {
        int size = AllThread.size();
        int fori = 111111; //
        for (int i = 0; i < size; i++) {

            if ( (IP.equals("/" + AllThread.get(i).fieldIP.getText().toString().replaceAll(" ", ""))) &&
                    Port.equals(AllThread.get(i).fieldPort.getText().toString().replaceAll(" ", ""))) {
                fori = i;
            }

        }

        return AllThread.get(fori);
    }



    @Override
    public void run() {

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        } catch (IOException e) {
            System.out.println("Не получили потоки IO");
        }
        String line;
        String linedel[];
        String delimetr = "-";




        while (true) {

            try {
                line = in.readLine();
                System.out.println(line);

                linedel = line.split(delimetr);
                System.out.println(linedel[0]);
                System.out.println(linedel[1]);
                System.out.println(linedel[2]);
                System.out.println(linedel[3]);
                System.out.println(linedel[4]);


                try {
                    NewThread thread1 = findNewThread(AllThread, linedel[1].toString().replaceAll(" ", ""), linedel[2].toString().replaceAll(" ", ""));

                thread1.log.append(linedel[0] + "\n");
                thread1.log.setCaretPosition(thread1.log.getDocument().getLength());
                } catch (Exception e)
                {
                    System.out.println("не попал в массив");
                }
                try {

                    NewThread thread2 = findNewThread(AllThread, linedel[3].toString().replaceAll(" ", ""), linedel[4].toString().replaceAll(" ", ""));

                    thread2.log.append(linedel[0] + "\n");
                    thread2.log.setCaretPosition(thread2.log.getDocument().getLength());
                } catch (Exception e) {
                    System.out.println("не попал в массив");
                }

            } catch (IOException e) {
                System.out.println("WTF");
            }


        }

    }
}
