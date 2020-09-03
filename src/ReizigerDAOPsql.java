import org.postgresql.util.PSQLException;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO {
    private Connection conn;

    public ReizigerDAOPsql(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean save(Reiziger reiziger) {
        String s = "INSERT INTO reiziger(reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum) VALUES (?, ?, ?, ?, ?)";
        return prepare(reiziger, s);
    }

    @Override
    public boolean update(Reiziger reiziger) {
        String s = "UPDATE reiziger SET reiziger_id = ?, voorletters = ?, tussenvoegsel = ?, achternaam = ?, geboortedatum = ?";
        return prepare(reiziger, s);
    }

    @Override
    public boolean delete(Reiziger reiziger) {
        String sql = "SELECT kaart_nummer FROM ov_chipkaart WHERE reiziger_id = ?";
        String s1 = "DELETE FROM  ov_chipkaart WHERE reiziger_id = ?";
        String s2 = "DELETE FROM  adres WHERE reiziger_id = ?";
        String s3 = "DELETE FROM reiziger WHERE reiziger_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, reiziger.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM ov_chipkaart_product WHERE kaart_nummer = ?");
                preparedStatement.setObject(1, Integer.valueOf(rs.getString(1)));
                preparedStatement.executeUpdate();
            }
            PreparedStatement p1 = conn.prepareStatement(s1);
            PreparedStatement p2 = conn.prepareStatement(s2);
            PreparedStatement p3 = conn.prepareStatement(s3);
            p1.setObject(1, reiziger.getId());
            p2.setObject(1, reiziger.getId());
            p3.setObject(1, reiziger.getId());
            p1.executeUpdate();
            p2.executeUpdate();
            return p3.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean prepare(Reiziger reiziger, String s) {
        try {
            PreparedStatement p = conn.prepareStatement(s);
            p.setObject(1, reiziger.getId());
            p.setObject(2, reiziger.getVoorletters());
            p.setObject(3, reiziger.getTussenvoegsel());
            p.setObject(4, reiziger.getAchternaam());
            p.setObject(5, java.sql.Date.valueOf(reiziger.getGeboortedatum()));
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Reiziger findById(int id) {
        String sql = "SELECT * FROM reiziger WHERE reiziger_id = " + id;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.getMetaData().getColumnCount() > 0) {
                try {
                    rs.next();
                    Reiziger reiziger = new Reiziger(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getDate(5).toLocalDate()
                    );
                    rs.close();
                    st.close();
                    return reiziger;
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

    @Override
    public ArrayList<Reiziger> findByGbdatum(String datum) {
        ArrayList<Reiziger> alleReizigers = new ArrayList<>();
        for (Reiziger reiziger : findAll()) {
            if (reiziger.getGeboortedatum().toString().equals(datum)) {
                alleReizigers.add(reiziger);
            }
        }
        return alleReizigers;
    }

    @Override
    public ArrayList<Reiziger> findAll() {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM reiziger;");
            ArrayList<Reiziger> alleReizigers = new ArrayList<>();
            while (rs.next()) {
                alleReizigers.add(findById(rs.getInt(1)));
            }
            return alleReizigers;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
