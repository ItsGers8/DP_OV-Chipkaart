import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {
    private Connection conn;

    public OVChipkaartDAOPsql(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean save(OVChipkaart ovChipkaart) {
        String s = "INSERT INTO ov_chipkaart(kaart_nummer, geldig_tot, klasse, saldo, reiziger_id) VALUES (?, ?, ?, ?, ?);";
        return prepare(ovChipkaart, s);
    }

    @Override
    public boolean update(OVChipkaart ovChipkaart) {
        String s = "UPDATE ov_chipkaart SET kaart_nummer = ?, geldig_tot = ?, klasse = ?, saldo = ?, reiziger_id = ?;";
        return prepare(ovChipkaart, s);
    }

    private boolean prepare(OVChipkaart ovChipkaart, String s) {
        try {
            PreparedStatement p = conn.prepareStatement(s);
            p.setInt(1, ovChipkaart.getKaart_nummer());
            p.setDate(2, ovChipkaart.getGeldig_tot());
            p.setInt(3, ovChipkaart.getKlasse());
            p.setDouble(4, ovChipkaart.getSaldo());
            p.setInt(5, ovChipkaart.getReiziger_id());
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(OVChipkaart ovChipkaart) {
        try {
            String s = "DELETE FROM ov_chipkaart WHERE kaart_nummer = ?";
            PreparedStatement p = conn.prepareStatement(s);
            p.setInt(1, ovChipkaart.getKaart_nummer());
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public OVChipkaart findByKaartnummer(int kaart_nummer) {
        String sql = "SELECT * FROM ov_chipkaart WHERE kaart_nummer = " + kaart_nummer;
        try {
            PreparedStatement p = conn.prepareStatement(sql);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.getMetaData().getColumnCount() > 0) {
                try {
                    rs.next();
                    OVChipkaart ovChipkaart = new OVChipkaart(
                            rs.getInt(1),
                            rs.getDate(2),
                            rs.getInt(3),
                            rs.getDouble(4),
                            rs.getInt(5)
                    );
                    rs.close();
                    st.close();
                    return ovChipkaart;
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
    public ArrayList<OVChipkaart> findByReiziger(Reiziger reiziger) {
        ArrayList<OVChipkaart> gevondenKaarten = new ArrayList<>();
        for (OVChipkaart ovChipkaart : findAll()) {
            if (ovChipkaart.getReiziger_id() == reiziger.getId()) {
                gevondenKaarten.add(ovChipkaart);
            }
        }
        return gevondenKaarten.size() > 0 ? gevondenKaarten : null;
    }

    @Override
    public ArrayList<OVChipkaart> findAll() {
        ArrayList<OVChipkaart> alleOVChipkaarten = new ArrayList<>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM ov_chipkaart;");
            while (rs.next()) {
                alleOVChipkaarten.add(findByKaartnummer(rs.getInt(1)));
            }
            return alleOVChipkaarten;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateKaarten(Reiziger reiziger) {
        ArrayList<OVChipkaart> kaartenOnline = findByReiziger(reiziger);
        for (OVChipkaart ovChipkaart : reiziger.getOVChipkaarten()) {
            if (!kaartenOnline.contains(ovChipkaart)) {
                save(ovChipkaart);
            } else {
                update(ovChipkaart);
            }
        }
        for (OVChipkaart ovChipkaart : kaartenOnline) {
            if (!reiziger.getOVChipkaarten().contains(ovChipkaart)) {
                delete(ovChipkaart);
            }
        }
    }
}
