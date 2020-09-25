import org.postgresql.util.PSQLException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;


public class Main {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Wachtwoord: ");
            String url = "jdbc:postgresql://localhost:5432/ovchip?user=postgres&password=" + br.readLine();
            try {
                Connection conn = DriverManager.getConnection(url);
                testFunction(conn);
            } catch (PSQLException e) {
                System.out.println("Login failed");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void testFunction(Connection conn) throws SQLException {
        ReizigerDAO rdao = new ReizigerDAOPsql(conn);
        AdresDAOPsql adao = new AdresDAOPsql(conn);
        OVChipkaartDAOPsql odao = new OVChipkaartDAOPsql(conn);
        ProductDAOPsql pdao = new ProductDAOPsql(conn);
        testReizigerDAO(rdao);
        testAdresDAO(adao);
        testOVKaart(odao);
        testProductDAO(pdao);
        testNieuweProductDAO(pdao, odao);
        pdao.delete(pdao.findByProductNummer(7));
        odao.delete(odao.findByKaartnummer(8));
        adao.delete(adao.findById(6));
        rdao.delete(rdao.findById(77));
    }

    private static void testReizigerDAO(ReizigerDAO rdao) throws SQLException {
//        Input
        System.out.println("\n---------- Test ReizigerDAO -------------");
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        rdao.findAll().forEach(System.out::println);
        System.out.println();

//        Nieuwe reiziger maken
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", LocalDate.of(1981, 3, 14));
        rdao.save(sietske);

//        Output
        System.out.println("[Test] ReizigerDAO.findAll() geeft na de toevoeging de volgende reizigers:");
        rdao.findAll().forEach(System.out::println);
    }

    private static void testAdresDAO(AdresDAOPsql adao) throws SQLException {
//        Input
        System.out.println("\n---------- Test AdresDAO -------------");
        System.out.println("[Test] AdresDAO.findAll() geeft de volgende adressen:");
        adao.findAll().forEach(System.out::println);
        System.out.println();

//        Nieuw adres maken
        Adres adres = new Adres(6, "3451XK", "28", "Medistraat", "Utrecht", 77);
        adao.save(adres);

//        Output
        System.out.println("[Test] AdresDAO.findAll() geeft na de toevoeging de volgende adressen:");
        adao.findAll().forEach(System.out::println);
        System.out.println();
    }

    private static void testOVKaart(OVChipkaartDAOPsql odao) throws SQLException {
//        Input
        System.out.println("\n---------- Test OVDAO -------------");
        System.out.println("[Test] OVChipkaartDAOPsql.findAll() geeft de volgende OV kaarten:");
        odao.findAll().forEach(System.out::println);
        System.out.println();

//        Nieuwe OV-Chipkaart maken
        OVChipkaart ovChipkaart = new OVChipkaart(8, Date.valueOf("2025-05-12"), 2, 22.0, 77);
        odao.save(ovChipkaart);

//        Output
        System.out.println("[Test] OVChipkaartDAOPsql.findAll() geeft na de toevoeging de volgende OV kaarten:");
        odao.findAll().forEach(System.out::println);
        System.out.println();
    }

    private static void testProductDAO(ProductDAOPsql pdao) throws SQLException {
//        Input
        System.out.println("\n---------- Test ProductDAO -------------");
        System.out.println("[Test] ProductDAOPsql.findAll() geeft de volgende producten:");
        pdao.findAll().forEach(System.out::println);
        System.out.println();

//        Nieuw product maken
        Product product = new Product(7, "Seniorenkorting", "Omdat je bijna dood bent mag je wat goedkoper met de trein", 10.5);
        pdao.save(product);

//        Output
        System.out.println("[Test] ProductDAOPsql.findAll() geeft na de toevoeging de volgende producten:");
        pdao.findAll().forEach(System.out::println);
        System.out.println();
    }

    private static void testNieuweProductDAO(ProductDAOPsql pdao, OVChipkaartDAOPsql odao) {
//        Input
        System.out.println("\n---------- Test ProductDAO 2 -------------");
        System.out.println("[Test] ProductDAOPsql.findAll() geeft de volgende producten:");
        pdao.findAll().forEach(System.out::println);
        System.out.println();

//        Ophalen oude producten en OV-kaarten
        Product product = pdao.findByProductNummer(7);
        OVChipkaart ovChipkaart = odao.findByKaartnummer(8);

//        OV-kaart en product aan elkaar linken
        product.addToOVKaarten(ovChipkaart);
        ovChipkaart.addToProducten(product);

//        Een update sturen naar het systeem
        pdao.update(product);
        odao.update(ovChipkaart);

//        Output
        System.out.println("[Test] ProductDAOPsql.findAll() geeft na de toevoeging de volgende producten:");
        pdao.findAll().forEach(System.out::println);
        System.out.println();
    }
}
