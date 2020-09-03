import java.util.ArrayList;
import java.util.List;

public interface ReizigerDAO {
    boolean save(Reiziger reiziger);
    boolean update(Reiziger reiziger);
    boolean delete(Reiziger reiziger);
    Reiziger findById(int id);
    ArrayList<Reiziger> findByGbdatum(String datum);
    ArrayList<Reiziger> findAll();
}
