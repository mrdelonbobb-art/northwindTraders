import java.sql.*;
import java.util.Scanner;
//imported data source
import org.apache.commons.dbcp2.BasicDataSource;

public class app {

    public static void main(String[] args) {

        // Need username and password for app to run
        if (args.length != 2) {
            System.out.println("Application needs username and password for DB");
            System.exit(1);
        }

        // Get username and password from args[]
        String username = args[0];
        String password = args[1];

        // Create scanner for user input
        Scanner myScanner = new Scanner(System.in);

        //get the connection from the datasource
        try (
                //create the basic datasource
                BasicDataSource basicDataSource = new BasicDataSource();
        ){

            //set configuration
            basicDataSource.setUrl("jdbc:mysql://localhost:3306/northwind");
            basicDataSource.setUsername(username);
            basicDataSource.setPassword(password);



            while (true) {
                System.out.println("""
                        What do you want to do?
                            1) Display All Products
                            0) Exit the app
                        """);

                switch (myScanner.nextInt()) {
                    case 1 -> displayAllProducts(basicDataSource);
                    case 0 -> {
                        System.out.println("Goodbye!");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice");
                }
            }

        } catch (SQLException e) {
            System.out.println("Unable to connect to database");
            System.exit(1);
        }
    }

    public static void displayAllProducts(BasicDataSource basicDataSource){

        //we gots to try to run a query and get the results with a prepared statement
        try(
                //get a connection from the pool
                Connection connection = basicDataSource.getConnection();

                //create the prepared statment using the passed in connection
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        SELECT
                            ProductName,
                            UnitPrice,
                            UnitsInStock,
                            (UnitPrice * UnitsInStock) AS InventoryValue
                        FROM
                            Products
                        ORDER BY
                            ProductName
                        """

                ))
                {

            try( ResultSet results = preparedStatement.executeQuery()){
                //print the results
                printResults(results);
            }catch(SQLException e){
                System.out.println("stuff hit the fan");
            }



        }catch (SQLException e){
            System.out.println("Could not get all the products");
            System.exit(1);
        }

    }

    private static void printResults(ResultSet results) throws SQLException {
        System.out.println("\nProduct Name | Price | In Stock");
        System.out.println("--------------------------------");

        while (results.next()) {
            String name = results.getString("ProductName");
            double price = results.getDouble("UnitPrice");
            int stock = results.getInt("UnitsInStock");

            System.out.printf("%s | %.2f | %d%n", name, price, stock);
        }
    }
}