import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;

public class ProductDAOPsql implements ProductDAO {
    private Connection conn;

    public ProductDAOPsql(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean save(Product product) {
        OVChipkaartDAOPsql odao = new OVChipkaartDAOPsql(conn);
        product.getOvChipkaarten().forEach(ovChipkaart -> odao.saver(product, ovChipkaart));
        String s = "INSERT INTO product(product_nummer, naam, beschrijving, prijs) VALUES (?, ?, ?, ?);";
        return prepare(product, s);
    }

    @Override
    public boolean update(Product product) {
        ovKaartenFromDB(product);
        String s = "UPDATE product SET product_nummer = ?, naam = ?, " +
                "beschrijving = ?, prijs = ? WHERE product_nummer = " + product.getProduct_nummer() + ";";
        return prepare(product, s);
    }

    @Override
    public boolean delete(Product product) {
        try {
            String s = "DELETE FROM ov_chipkaart_product WHERE product_nummer = ?; " +
                    "DELETE FROM product WHERE product_nummer = " + product.getProduct_nummer() + ";";
            PreparedStatement p = conn.prepareStatement(s);
            p.setInt(1, product.getProduct_nummer());
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void ovKaartenFromDB(Product product) {
        ArrayList<OVChipkaart> ovChipkaarten = new ArrayList<>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT * FROM ov_chipkaart_product WHERE product_nummer = " + product.getProduct_nummer());
            OVChipkaartDAOPsql odao = new OVChipkaartDAOPsql(conn);
            if (rs.getMetaData().getColumnCount() > 0) {
                while (rs.next()) {
                    OVChipkaart ovChipkaart = odao.findByKaartnummer(rs.getInt(1));
                    if (!(product.getOvChipkaarten().contains(ovChipkaart))) {
                        deleteOVProduct(product, ovChipkaart);
                    } else ovChipkaarten.add(ovChipkaart);
                }
                product.getOvChipkaarten().forEach(ovChipkaart -> {
                    if (!(ovChipkaarten.contains(ovChipkaart))) {
                        odao.saver(product, ovChipkaart);
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteOVProduct(Product product, OVChipkaart ovChipkaart) {
        try {
            PreparedStatement p = conn.prepareStatement("DELETE FROM ov_chipkaart_product WHERE kaart_nummer = ? AND product_nummer = ?");
            p.setInt(1, ovChipkaart.getKaart_nummer());
            p.setInt(2, product.getProduct_nummer());
            p.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean prepare(Product product, String s) {
        try {
            PreparedStatement p = conn.prepareStatement(s);
            p.setInt(1, product.getProduct_nummer());
            p.setString(2, product.getNaam());
            p.setString(3, product.getBeschrijving());
            p.setDouble(4, product.getPrijs());
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public ArrayList<Product> findAll() {
        ArrayList<Product> producten = new ArrayList<>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM product;");
            while (rs.next()) {
                producten.add(findByProductNummer(rs.getInt(1)));
            }
            return producten;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Product findByProductNummer(int id) {
        String sql = "SELECT * FROM product WHERE product_nummer = " + id;
        try {
            PreparedStatement p = conn.prepareStatement(sql);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.getMetaData().getColumnCount() > 0) {
                try {
                    rs.next();
                    Product product = new Product(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getDouble(4)
                    );
                    product.setOvChipkaarten(getOVKaarten(product));
                    rs.close();
                    st.close();
                    return product;
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

    public ArrayList<OVChipkaart> getOVKaarten(Product product) {
        ArrayList<OVChipkaart> kaarten = new ArrayList<>();
        try {
            OVChipkaartDAOPsql odao = new OVChipkaartDAOPsql(conn);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM ov_chipkaart_product WHERE product_nummer = " + product.getProduct_nummer());
            while (rs.next()) {
                kaarten.add(odao.findByKaartnummer(rs.getInt(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kaarten;
    }
}
