import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Wachtwoord: ");
            String url = "jdbc:postgresql://localhost:5432/ovchip?user=postgres&password=" + br.readLine();
            ReizigerDAO reizigerDAO = new ReizigerDAOPsql(DriverManager.getConnection(url));
            AdresDAOPsql adao = new AdresDAOPsql(DriverManager.getConnection(url));
            reizigerDAO.delete(reizigerDAO.findById(77));
            testReizigerEnAdresDAO(reizigerDAO, adao);
//            AdresDAOPsql adresDAO = new AdresDAOPsql(DriverManager.getConnection(url));
//            ReizigerDAOPsql reizigerDAOPsql = new ReizigerDAOPsql(DriverManager.getConnection(url));
//            for (Adres adres : adresDAO.findAll()) {
//                System.out.println(adres);
//            }
//            System.out.println(adresDAO.findByReiziger(reizigerDAOPsql.findById(1)));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    private static void testReizigerEnAdresDAO(ReizigerDAO rdao, AdresDAOPsql adao) throws SQLException {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        // Haal alle reizigers op uit de database
        ArrayList<Reiziger> reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();

        System.out.println("[Test] AdresDAO.findAll() geeft de volgende reizigers:");
        for (Adres adres : adao.findAll()) {
            System.out.println(adres);
        }
        System.out.println();

        // Maak een nieuwe reiziger aan en persisteer deze in de database
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", LocalDate.of(1981, 3, 14));
        Adres adres = new Adres(78, "1234AB", "12", "Johan de Wittstraat", "Utrecht", 77);
        sietske.setAdres(adres);
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske); // Deze functie maakt automatisch gebruik van de adao.save() functie als er een adres is
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println("\n[Test] Alle adressen:");
        for (Adres a : adao.findAll()) {
            System.out.println(a);
        }
        System.out.println();
    }
}
