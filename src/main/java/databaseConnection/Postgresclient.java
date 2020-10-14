package databaseConnection;

import java.sql.*;

public class Postgresclient {

    static Connection connection = null;
    static Statement statement = null;
    static ResultSet resultSet = null;

    public static void initiate_connection(){
        if(connection!=null) {
            System.out.println("using existing connection");
            return;
        }
        try {
            System.out.println("envvar ="+ System.getenv());
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:3307"+ "/speech_recognition_data_catalog_test",
                            System.getenv("POSTGRES_USER"), System.getenv("POSTGRES_PASSWORD"));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    public static void close_conneciton() throws SQLException {
        statement.close();
        connection.close();

    }

    public static ResultSet select_query (String query) {
    try {
        initiate_connection();
        statement =connection.createStatement();
        resultSet = statement.executeQuery(query);
        return resultSet ;
    }
       catch (Exception e){
           e.printStackTrace();
           System.err.println(e.getClass().getName()+": "+e.getMessage());
           System.exit(0);
       }
        return null;
    }


    public static void delete_data(String deletequery)
    {
        try {
            initiate_connection();
            statement =connection.createStatement();
            statement.executeUpdate(deletequery);
        }
        catch (Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }


}
