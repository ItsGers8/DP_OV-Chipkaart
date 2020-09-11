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
            ReizigerDAO rdao = new ReizigerDAOPsql(DriverManager.getConnection(url));
            OVChipkaartDAOPsql odao = new OVChipkaartDAOPsql(DriverManager.getConnection(url));
            odao.delete(odao.findByKaartnummer(8));
            rdao.delete(rdao.findById(77));
            testOVKaart(rdao, odao);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    private static void testOVKaart(ReizigerDAO rdao, OVChipkaartDAOPsql odao) throws SQLException {
        System.out.println("\n---------- Test OVDAO -------------");
        Reiziger reiziger = new Reiziger(77, "G", "", "Mak", LocalDate.of(2000, 10, 8));
        reiziger.addOVChipkaart(new OVChipkaart(8, Date.valueOf("2025-05-12"), 2, 22.0, 77));
        rdao.save(reiziger);
        System.out.println("\n---------- Alle reizigers -------------");
        rdao.findAll().forEach(System.out::println);
        System.out.println("\n---------- Alle OV-Kaarten -------------");
        odao.findAll().forEach(System.out::println);
        System.out.println("\n---------- Alle OV-kaarten van de nieuwe reiziger -------------");
        System.out.println(odao.findByReiziger(reiziger));
    }
}
