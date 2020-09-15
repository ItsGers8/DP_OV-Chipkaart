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
            Connection conn = DriverManager.getConnection(url);
            ReizigerDAO rdao = new ReizigerDAOPsql(conn);
            AdresDAOPsql adao = new AdresDAOPsql(conn);
            OVChipkaartDAOPsql odao = new OVChipkaartDAOPsql(conn);
            testReizigerDAO(rdao);
            testAdresDAO(adao);
            testOVKaart(odao);
            odao.delete(odao.findByKaartnummer(8));
            adao.delete(adao.findById(6));
            rdao.delete(rdao.findById(77));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void testReizigerDAO(ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test ReizigerDAO -------------");
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        rdao.findAll().forEach(System.out::println);
        System.out.println();

        Reiziger sietske = new Reiziger(77, "S", "", "Boers", LocalDate.of(1981, 3, 14));
        rdao.save(sietske);

        System.out.println("[Test] ReizigerDAO.findAll() geeft na de toevoeging de volgende reizigers:");
        rdao.findAll().forEach(System.out::println);
    }

    private static void testAdresDAO(AdresDAOPsql adao) throws SQLException {
        System.out.println("\n---------- Test AdresDAO -------------");
        System.out.println("[Test] AdresDAO.findAll() geeft de volgende adressen:");
        adao.findAll().forEach(System.out::println);
        System.out.println();

        Adres adres = new Adres(6, "3451XK", "28", "Medistraat", "Utrecht", 77);
        adao.save(adres);

        System.out.println("[Test] AdresDAO.findAll() geeft na de toevoeging de volgende adressen:");
        adao.findAll().forEach(System.out::println);
        System.out.println();
    }

    private static void testOVKaart(OVChipkaartDAOPsql odao) throws SQLException {
        System.out.println("\n---------- Test OVDAO -------------");
        System.out.println("[Test] OVChipkaartDAOPsql.findAll() geeft de volgende OV kaarten:");
        odao.findAll().forEach(System.out::println);
        System.out.println();

        OVChipkaart ovChipkaart = new OVChipkaart(8, Date.valueOf("2025-05-12"), 2, 22.0, 77);
        odao.save(ovChipkaart);

        System.out.println("[Test] OVChipkaartDAOPsql.findAll() geeft na de toevoeging de volgende OV kaarten:");
        odao.findAll().forEach(System.out::println);
        System.out.println();
    }
}
