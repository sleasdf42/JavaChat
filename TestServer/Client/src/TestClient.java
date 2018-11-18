import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class TestClient extends JFrame implements ActionListener{


    private final static String IP_ADDR = "192.168.10.4";

    private final static int PORT = 8189;
    private final static int WIDTH = 600;
    private final static int HEIGHT = 400;

    static ArrayList<NewThread> AllThread = new ArrayList<>();

     static BufferedReader in;
     static BufferedWriter out;

    static Socket socket;

    static AuthAndLog Auth; //..........................

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Auth = new AuthAndLog();
               // new TestClient();

                try {
                    socket = new Socket(IP_ADDR, PORT);
                   // in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
                     out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));

                    new InThread(socket, AllThread).start();


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


    }


    private final JTextArea log = new JTextArea();
    private final JTextField fieldNick = new JTextField(Auth.Log);
    private final JTextField fieldinput = new JTextField();
    private final JTextField fieldIP = new JTextField("192.168.10.4");
    private final JTextField fieldPort = new JTextField(" Port ");
    private final JTextField fieldNickName = new JTextField(" NickName ");
    private final JButton btn = new JButton("connect");

    String sIP;
    String sPort;
    String sNick;


    TestClient() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);

        setLocationRelativeTo(null);
        add (log, BorderLayout.CENTER);
        add(fieldinput, BorderLayout.SOUTH);

        JPanel p = new JPanel();
        add(p, BorderLayout.NORTH);

        p.setLayout(new FlowLayout());

        p.add(fieldNick);
        p.add(fieldIP);
        p.add(fieldPort);
        p.add(fieldNickName);
        p.add(btn);

        btn.addActionListener(this);

        log.setEditable(false);
        log.setLineWrap(true);

        setVisible(true);


    }

    ArrayList<String> IP = new ArrayList<>();
    ArrayList<String> Port = new ArrayList<>();


    public Boolean IPExist(ArrayList<String> arrIP, String exIP ) {

        Boolean vall = false;
        int sizeArrIp = arrIP.size();

        for (int i = 0; i < sizeArrIp; i++) {
           if (arrIP.get(i).equals(exIP)) {
               vall = true;
            System.out.println("Проверяю...... true");
           }

        }
        System.out.println("IP = " + vall);
        return vall;

    }
   public Boolean PortExist(ArrayList<String> arrPort,String sPort) {
        Boolean vall = false;
        int sizeArrPort = arrPort.size();

        for (int i = 0; i < sizeArrPort; i++) {
            if (arrPort.get(i).equals(sPort)) {
                System.out.println("Проверяю...... true"); //....................проверить еще раз, что-то не так
            vall = true;
            }
        }
        System.out.println("Нашел Port = " + vall);
        return vall;
    }


    @Override
    public synchronized void actionPerformed(ActionEvent e) {

        sIP = fieldIP.getText().toString().replaceAll(" ", "");
        sPort = fieldPort.getText().toString().replaceAll(" ", "");
        sNick = fieldNick.getText().toString().replaceAll(" ", "");

        if (IPExist(IP, sIP) == false || PortExist(Port, sPort) == false) {
            IP.add(sIP);
            Port.add(sPort);
        NewThread thread = new NewThread(socket, sNick, sIP, sPort, IP, Port, out, in); //...................
            AllThread.add(thread);
        } else {
            log.append("This dialog else be....");
            log.setCaretPosition(log.getDocument().getLength());
        }


    }
}
class NewThread implements ActionListener {

    Socket socket;
    BufferedReader in;
    BufferedWriter out;


    JTextField fieldNick;
    JTextArea log;
    JTextField fieldinput;
    JTextField fieldIP;
    JTextField fieldPort; // решить проблему то что эти переменные общие для всех

    NewThread(Socket socket, String sNick, String sIP, String sPort, ArrayList<String> arrayIP, ArrayList<String> arrayPort, BufferedWriter out, BufferedReader in) {

      this.out = out;
      this.in = in;

      fieldNick = new JTextField("admin");
      log = new JTextArea();
      fieldinput = new JTextField();
      fieldIP = new JTextField("192.168.10.4");
      fieldPort = new JTextField(" Port ");

        this.socket = socket;


        JFrame jf = new JFrame();

        jf.setSize(250, 300);
        jf.setVisible(true);

        jf.add(fieldinput, BorderLayout.SOUTH);
        jf.add(log, BorderLayout.CENTER);

        JPanel p = new JPanel();
        jf.add(p, BorderLayout.NORTH);
        p.add(fieldNick);
        p.add(fieldIP);
        p.add(fieldPort);

        fieldPort.setText(sPort);
        fieldIP.setText(sIP);
        fieldNick.setText(sNick);

        fieldinput.addActionListener(this);

        jf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { // отслеживаем закрытие окна
                arrayIP.remove(sIP);
                arrayPort.remove(sPort);

                System.out.println("Удаляю из массива все что надо...");

            }
        });

    }



    public synchronized String getFieldText(JTextField field) {
        return field.getText().toString().replaceAll(" ", "");
    }



    @Override
    public synchronized void actionPerformed(ActionEvent e) {

        String sFinal;

        String sMsg = fieldinput.getText();
       // String sIP = fieldIP.getText().replaceAll(" ", ""); // считывается не из того потока ||
        String sIP = getFieldText(fieldIP);
        //String sPort = fieldPort.getText().replaceAll(" ", "");
        String sPort = getFieldText(fieldPort);
        String sNick = fieldNick.getText().replaceAll(" ", "");


        sFinal = "/" + sIP + " - " + sPort + " - " + sNick + ": " + sMsg;

        System.out.println(sFinal);

        try {
            out.write(sFinal + "\r\n");
            out.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        this.fieldinput.setText(null);
    }

   /* private synchronized void printMsg(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    } */

}

