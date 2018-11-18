import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class AuthAndLog extends JFrame implements ActionListener{

    private final JTextArea log = new JTextArea();
    private final JTextField fieldLogin = new JTextField("admin");
    private final JTextField fieldPass = new JTextField("admin");
    private final JButton btn = new JButton("OK");

    private final static int WIDTH = 600;
    private final static int HEIGHT = 400;

    AuthAndLog() {

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);

        setLocationRelativeTo(null);
        add (log, BorderLayout.CENTER);
        add(btn, BorderLayout.SOUTH);

        JPanel p = new JPanel();
        add(p, BorderLayout.NORTH);

        p.setLayout(new FlowLayout());

        p.add(fieldLogin);
        p.add(fieldPass);

        btn.addActionListener(this);

        log.setEditable(false);
        log.setLineWrap(true);

        setVisible(true);
    }

    DataBase db = new DataBase();
    String Log;

    @Override
    public void actionPerformed(ActionEvent e) {


        Log = fieldLogin.getText().toString().replaceAll(" ", "");
        String pass = fieldPass.getText().toString().replaceAll(" ", "");


        try {
            if (db.findLoginAndPass(Log, pass)) {
                this.dispose();
                System.out.println("All Right...");
                new TestClient();
            }
            else {
                System.out.println("Password or login incorrect...");
                log.append("Password or login incorrect...");
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

    }
}
