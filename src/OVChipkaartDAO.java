import java.util.ArrayList;

public interface OVChipkaartDAO {
    boolean save(OVChipkaart ovChipkaart);
    boolean update(OVChipkaart ovChipkaart);
    boolean delete(OVChipkaart ovChipkaart);
    OVChipkaart findByKaartnummer(int kaart_nummer);
    ArrayList<OVChipkaart> findByReiziger(Reiziger reiziger);
    ArrayList<OVChipkaart> findAll();
}
