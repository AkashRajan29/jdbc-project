
package busreservation;

import java.sql.*;
import java.util.Scanner;

public class Busreservation {

    static Scanner sc = new Scanner(System.in);

    // =====================================================
    // 1. SEARCH BUSES
    // =====================================================

    public static void searchBuses() {

        String sql =
                "SELECT * FROM buses " +
                "WHERE source = ? AND destination = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter Source: ");
            String source = sc.nextLine();

            System.out.print("Enter Destination: ");
            String destination = sc.nextLine();

            ps.setString(1, source);
            ps.setString(2, destination);

            ResultSet rs = ps.executeQuery();

            System.out.println();
            System.out.println(
                    "ID\tBus No\t\tBus Name\tAvailable Seats");

            System.out.println(
                    "-------------------------------------------------------");

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.println(
                        rs.getInt("bus_id") + "\t" +
                        rs.getString("bus_number") + "\t" +
                        rs.getString("bus_name") + "\t" +
                        rs.getInt("available_seats"));
            }

            if (!found) {

                System.out.println(
                        "No buses found.");
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error: " + e.getMessage());
        }
    }


    // =====================================================
    // 2. BOOK BUS TICKET
    // =====================================================

    public static void bookTicket() {

        Connection con = null;

        try {

            con = DBConnection.getConnection();

            // Start transaction
            con.setAutoCommit(false);

            System.out.print("Enter Bus ID: ");
            int busId = sc.nextInt();
            sc.nextLine();

            System.out.print("Enter Passenger Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Passenger Age: ");
            int age = sc.nextInt();
            sc.nextLine();

            System.out.print("Enter Gender: ");
            String gender = sc.nextLine();

            System.out.print("Enter Phone: ");
            String phone = sc.nextLine();

            System.out.print("Enter Seat Number: ");
            int seatNumber = sc.nextInt();
            sc.nextLine();


            // ---------------------------------------------
            // Check seat availability
            // ---------------------------------------------

            String checkBus =
                    "SELECT available_seats " +
                    "FROM buses " +
                    "WHERE bus_id = ? " +
                    "FOR UPDATE";

            PreparedStatement psCheck =
                    con.prepareStatement(checkBus);

            psCheck.setInt(1, busId);

            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) {

                System.out.println(
                        "Bus not found.");

                con.rollback();
                return;
            }

            int availableSeats =
                    rs.getInt("available_seats");

            if (availableSeats <= 0) {

                System.out.println(
                        "No seats available.");

                con.rollback();
                return;
            }


            // ---------------------------------------------
            // Check whether seat is already booked
            // ---------------------------------------------

            String checkSeat =
                    "SELECT booking_id " +
                    "FROM bookings " +
                    "WHERE bus_id = ? " +
                    "AND seat_number = ?";

            PreparedStatement psSeat =
                    con.prepareStatement(checkSeat);

            psSeat.setInt(1, busId);
            psSeat.setInt(2, seatNumber);

            ResultSet seatResult =
                    psSeat.executeQuery();

            if (seatResult.next()) {

                System.out.println(
                        "Seat already booked.");

                con.rollback();
                return;
            }


            // ---------------------------------------------
            // Insert passenger
            // ---------------------------------------------

            String passengerSql =
                    "INSERT INTO passengers " +
                    "(name, age, gender, phone) " +
                    "VALUES (?, ?, ?, ?)";

            PreparedStatement psPassenger =
                    con.prepareStatement(
                            passengerSql,
                            Statement.RETURN_GENERATED_KEYS);

            psPassenger.setString(1, name);
            psPassenger.setInt(2, age);
            psPassenger.setString(3, gender);
            psPassenger.setString(4, phone);

            psPassenger.executeUpdate();

            ResultSet generatedKeys =
                    psPassenger.getGeneratedKeys();

            generatedKeys.next();

            int passengerId =
                    generatedKeys.getInt(1);


            // ---------------------------------------------
            // Insert booking
            // ---------------------------------------------

            String bookingSql =
                    "INSERT INTO bookings " +
                    "(bus_id, passenger_id, seat_number) " +
                    "VALUES (?, ?, ?)";

            PreparedStatement psBooking =
                    con.prepareStatement(
                            bookingSql,
                            Statement.RETURN_GENERATED_KEYS);

            psBooking.setInt(1, busId);
            psBooking.setInt(2, passengerId);
            psBooking.setInt(3, seatNumber);

            psBooking.executeUpdate();

            ResultSet bookingKeys =
                    psBooking.getGeneratedKeys();

            bookingKeys.next();

            int bookingId =
                    bookingKeys.getInt(1);


            // ---------------------------------------------
            // Reduce available seats
            // ---------------------------------------------

            String updateBus =
                    "UPDATE buses " +
                    "SET available_seats = " +
                    "available_seats - 1 " +
                    "WHERE bus_id = ?";

            PreparedStatement psUpdate =
                    con.prepareStatement(updateBus);

            psUpdate.setInt(1, busId);

            psUpdate.executeUpdate();


            // ---------------------------------------------
            // Commit transaction
            // ---------------------------------------------

            con.commit();

            System.out.println();
            System.out.println(
                    "======================================");

            System.out.println(
                    "Ticket booked successfully!");

            System.out.println(
                    "Booking ID     : " + bookingId);

            System.out.println(
                    "Passenger Name  : " + name);

            System.out.println(
                    "Seat Number     : " + seatNumber);

            System.out.println(
                    "======================================");

        } catch (Exception e) {

            try {

                if (con != null) {
                    con.rollback();
                }

            } catch (SQLException ex) {

                System.out.println(
                        "Rollback error: " +
                        ex.getMessage());
            }

            System.out.println(
                    "Booking failed: " +
                    e.getMessage());

        } finally {

            try {

                if (con != null) {
                    con.close();
                }

            } catch (SQLException e) {

                e.printStackTrace();
            }
        }
    }


    // =====================================================
    // 3. CANCEL TICKET
    // =====================================================

    public static void cancelTicket() {

        Connection con = null;

        try {

            con = DBConnection.getConnection();

            con.setAutoCommit(false);

            System.out.print(
                    "Enter Booking ID: ");

            int bookingId = sc.nextInt();
            sc.nextLine();


            // Find bus ID
            String findBooking =
                    "SELECT bus_id " +
                    "FROM bookings " +
                    "WHERE booking_id = ?";

            PreparedStatement psFind =
                    con.prepareStatement(findBooking);

            psFind.setInt(1, bookingId);

            ResultSet rs =
                    psFind.executeQuery();

            if (!rs.next()) {

                System.out.println(
                        "Booking not found.");

                con.rollback();
                return;
            }

            int busId =
                    rs.getInt("bus_id");


            // Delete booking
            String deleteSql =
                    "DELETE FROM bookings " +
                    "WHERE booking_id = ?";

            PreparedStatement psDelete =
                    con.prepareStatement(deleteSql);

            psDelete.setInt(1, bookingId);

            psDelete.executeUpdate();


            // Increase available seats
            String updateBus =
                    "UPDATE buses " +
                    "SET available_seats = " +
                    "available_seats + 1 " +
                    "WHERE bus_id = ?";

            PreparedStatement psUpdate =
                    con.prepareStatement(updateBus);

            psUpdate.setInt(1, busId);

            psUpdate.executeUpdate();


            con.commit();

            System.out.println(
                    "Ticket cancelled successfully.");

        } catch (Exception e) {

            try {

                if (con != null) {
                    con.rollback();
                }

            } catch (SQLException ex) {

                ex.printStackTrace();
            }

            System.out.println(
                    "Cancellation failed: " +
                    e.getMessage());

        } finally {

            try {

                if (con != null) {
                    con.close();
                }

            } catch (SQLException e) {

                e.printStackTrace();
            }
        }
    }


    // =====================================================
    // 4. VIEW BOOKINGS
    // =====================================================

    public static void viewBookings() {

        String sql =
                "SELECT b.booking_id, " +
                "p.name, " +
                "bus.bus_number, " +
                "bus.bus_name, " +
                "bus.source, " +
                "bus.destination, " +
                "b.seat_number, " +
                "b.booking_date " +

                "FROM bookings b " +

                "JOIN passengers p " +
                "ON b.passenger_id = p.passenger_id " +

                "JOIN buses bus " +
                "ON b.bus_id = bus.bus_id " +

                "ORDER BY b.booking_id";

        try (Connection con =
                     DBConnection.getConnection();
             Statement st =
                     con.createStatement();
             ResultSet rs =
                     st.executeQuery(sql)) {

            System.out.println();
            System.out.println(
                    "==============================================================");

            System.out.println(
                    "ID\tPassenger\tBus\t\tSeat\tRoute");

            System.out.println(
                    "==============================================================");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("booking_id") + "\t" +
                        rs.getString("name") + "\t" +
                        rs.getString("bus_number") + "\t" +
                        rs.getInt("seat_number") + "\t" +
                        rs.getString("source") +
                        " -> " +
                        rs.getString("destination"));
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error: " + e.getMessage());
        }
    }


    // =====================================================
    // MAIN MENU
    // =====================================================

    public static void main(String[] args) {

        while (true) {

            System.out.println();
            System.out.println(
                    "===== BUS RESERVATION SYSTEM =====");

            System.out.println(
                    "1. Search Buses");

            System.out.println(
                    "2. Book Ticket");

            System.out.println(
                    "3. Cancel Ticket");

            System.out.println(
                    "4. View Bookings");

            System.out.println(
                    "5. Exit");

            System.out.print(
                    "Enter Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    searchBuses();
                    break;

                case 2:
                    bookTicket();
                    break;

                case 3:
                    cancelTicket();
                    break;

                case 4:
                    viewBookings();
                    break;

                case 5:

                    System.out.println(
                            "Thank you!");

                    sc.close();

                    System.exit(0);

                    break;

                default:

                    System.out.println(
                            "Invalid choice.");
            }
        }
    }
}
