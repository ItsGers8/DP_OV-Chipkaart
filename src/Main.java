import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/ovchip?user=postgres&password=PASSWORDHERE";
        try {
            ReizigerDAO reizigerDAO = new ReizigerDAOPsql(DriverManager.getConnection(url));
            reizigerDAO.delete(reizigerDAO.findById(77));
            testReizigerDAO(reizigerDAO);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void testReizigerDAO(ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        // Haal alle reizigers op uit de database
        ArrayList<Reiziger> reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();

        // Maak een nieuwe reiziger aan en persisteer deze in de database
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", LocalDate.of(1981, 3, 14));
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");
    }
}
