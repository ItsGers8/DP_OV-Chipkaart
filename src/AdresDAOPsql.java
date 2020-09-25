import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;

public class AdresDAOPsql implements AdresDAO {
    private Connection conn;

    public AdresDAOPsql(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean save(Adres adres) {
        String s = "INSERT INTO adres(adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id) VALUES (?, ?, ?, ?, ?, ?);";
        return prepare(adres, s);
    }

    @Override
    public boolean update(Adres adres) {
        String s = "UPDATE adres SET adres_id=?, postcode=?, huisnummer=?, straat=?, " +
                "woonplaats=?, reiziger_id=? WHERE adres_id=" + adres.getAdres_id() + ";";
        return prepare(adres, s);
    }

    private boolean prepare(Adres adres, String s) {
        try {
            PreparedStatement p = conn.prepareStatement(s);
            p.setInt(1, adres.getAdres_id());
            p.setString(2, adres.getPostcode());
            p.setString(3, adres.getHuisnummer());
            p.setString(4, adres.getStraat());
            p.setString(5, adres.getWoonplaats());
            p.setInt(6, adres.getReiziger_id());
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Adres adres) {
        try {
            String s = "DELETE FROM  adres WHERE adres_id = ?";
            PreparedStatement p = conn.prepareStatement(s);
            p.setInt(1, adres.getAdres_id());
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Adres findByReiziger(Reiziger reiziger) {
        for (Adres adres : findAll()) {
            if (adres.getReiziger_id() == reiziger.getId()) {
                return adres;
            }
        }
        return null;
    }

    @Override
    public ArrayList<Adres> findAll() {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM adres;");
            ArrayList<Adres> alleAdressen = new ArrayList<>();
            while (rs.next()) {
                alleAdressen.add(findById(rs.getInt(1)));
            }
            return alleAdressen;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Adres findById(int id) {
        String sql = "SELECT * FROM adres WHERE adres_id = " + id;
        try {
            PreparedStatement p = conn.prepareStatement(sql);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.getMetaData().getColumnCount() > 0) {
                try {
                    rs.next();
                    Adres adres = new Adres(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getInt(6)
                    );
                    rs.close();
                    st.close();
                    return adres;
                } catch (PSQLException e) {
                    return null;
                }
            }
            else return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
