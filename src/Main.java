import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/ovchip?user=postgres&password=PASSWORDHERE";
        try {
            ReizigerDAOPsql reizigerDAOPsql = new ReizigerDAOPsql(DriverManager.getConnection(url));
//            testReizigerDAO(new ReizigerDAOPsql(DriverManager.getConnection(url)));
//            System.out.println(reizigerDAOPsql.save(new Reiziger(6, "G", null, "Mak", LocalDate.of(2002, 12, 3))));
//            System.out.println(reizigerDAOPsql.delete(reizigerDAOPsql.findById(6)));
//            System.out.println(reizigerDAOPsql.findById(5) == null ? "Niet gevonden" : reizigerDAOPsql.findById(5));
            for (Reiziger reiziger : reizigerDAOPsql.findByGbdatum("2002-12-03")) {
                System.out.println(reiziger.toString());
            }
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
        String gbdatum = "1981-03-14";
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", LocalDate.of(1981, 3, 14));
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");
    }
}
