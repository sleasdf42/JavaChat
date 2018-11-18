import java.sql.*;
import java.util.Properties;

public class DataBase {

    static String username = "root";
    static String pass = "12345";
    static String connectionURL = "jdbc:mysql://localhost:3306/ForUsers";

    static Statement statement;


    Properties properties = new Properties();

    public void setproperties() throws ClassNotFoundException {

        properties.setProperty("user", username);
        properties.setProperty("password", pass);
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");

        Class.forName("com.mysql.jdbc.Driver");
    }


    public Boolean findLoginAndPass(String log, String pass) throws SQLException, ClassNotFoundException {
        Boolean Logg = false;

        setproperties();

        try(Connection connection = DriverManager.getConnection(connectionURL, properties))
        {
            System.out.println("We're connected");
            statement = connection.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM Users");

            while (res.next()) {

                String login = res.getString(2).replaceAll(" ", "");
                String password = res.getString(3).replaceAll(" ", "");

                if (login.equals(log) && password.equals(pass)) Logg = true;
            }

        } catch (SQLException e1) {
            System.out.println("Not Connected " + e1);
        }

        return Logg;
    }
}
