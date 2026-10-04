package banking_application;


import java.sql.*;
import java.util.Scanner;



public class Banking_application {

    static Scanner sc = new Scanner(System.in);

    public static void createAccount() {

        String customerSql =
                "INSERT INTO customers(name, phone) VALUES (?, ?)";

        String accountSql =
                "INSERT INTO accounts(customer_id, account_number, balance) " +
                "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            System.out.print("Enter Customer Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Phone: ");
            String phone = sc.nextLine();

            PreparedStatement ps1 =
                    con.prepareStatement(customerSql,
                            Statement.RETURN_GENERATED_KEYS);

            ps1.setString(1, name);
            ps1.setString(2, phone);
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();
            rs.next();

            int customerId = rs.getInt(1);

            System.out.print("Enter Account Number: ");
            String accountNumber = sc.nextLine();

            System.out.print("Enter Initial Deposit: ");
            double balance = sc.nextDouble();
            sc.nextLine();

            PreparedStatement ps2 =
                    con.prepareStatement(accountSql);

            ps2.setInt(1, customerId);
            ps2.setString(2, accountNumber);
            ps2.setDouble(3, balance);

            ps2.executeUpdate();

            System.out.println(
                    "Account created successfully!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public static void deposit() {

        try (Connection con = DBConnection.getConnection()) {

            System.out.print("Enter Account Number: ");
            String account = sc.nextLine();

            System.out.print("Enter Deposit Amount: ");
            double amount = sc.nextDouble();
            sc.nextLine();

            String sql =
                    "UPDATE accounts SET balance = balance + ? " +
                    "WHERE account_number = ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setDouble(1, amount);
            ps.setString(2, account);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("Deposit successful!");
            else
                System.out.println("Account not found.");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public static void withdraw() {

        try (Connection con = DBConnection.getConnection()) {

            System.out.print("Enter Account Number: ");
            String account = sc.nextLine();

            System.out.print("Enter Withdrawal Amount: ");
            double amount = sc.nextDouble();
            sc.nextLine();

            String sql =
                    "UPDATE accounts " +
                    "SET balance = balance - ? " +
                    "WHERE account_number = ? AND balance >= ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setDouble(1, amount);
            ps.setString(2, account);
            ps.setDouble(3, amount);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("Withdrawal successful!");
            else
                System.out.println(
                        "Insufficient balance or account not found.");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public static void transfer() {

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            System.out.print("From Account: ");
            String from = sc.nextLine();

            System.out.print("To Account: ");
            String to = sc.nextLine();

            System.out.print("Transfer Amount: ");
            double amount = sc.nextDouble();
            sc.nextLine();

            String debit =
                    "UPDATE accounts " +
                    "SET balance = balance - ? " +
                    "WHERE account_number = ? AND balance >= ?";

            String credit =
                    "UPDATE accounts " +
                    "SET balance = balance + ? " +
                    "WHERE account_number = ?";

            PreparedStatement ps1 =
                    con.prepareStatement(debit);

            ps1.setDouble(1, amount);
            ps1.setString(2, from);
            ps1.setDouble(3, amount);

            int debitRows = ps1.executeUpdate();

            if (debitRows == 0) {
                con.rollback();
                System.out.println(
                        "Transfer failed: Insufficient balance.");
                return;
            }

            PreparedStatement ps2 =
                    con.prepareStatement(credit);

            ps2.setDouble(1, amount);
            ps2.setString(2, to);

            int creditRows = ps2.executeUpdate();

            if (creditRows == 0) {
                con.rollback();
                System.out.println(
                        "Transfer failed: Receiver not found.");
                return;
            }

            con.commit();

            System.out.println(
                    "Transfer completed successfully!");

        } catch (Exception e) {
            System.out.println("Transaction failed.");
        }
    }


    public static void main(String[] args) {

        while (true) {

            System.out.println("\n===== BANKING SYSTEM =====");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Fund Transfer");
            System.out.println("5. Exit");

            System.out.print("Enter Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    createAccount();
                    break;

                case 2:
                    deposit();
                    break;

                case 3:
                    withdraw();
                    break;

                case 4:
                    transfer();
                    break;

                case 5:
                    System.out.println("Thank You!");
                    System.exit(0);

                default:
                    System.out.println("Invalid Choice");
            }
        }
    }
}