package interfaces;

import entities.Entretien;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.List;

public interface IService <T>{
    public void ajouter(T t) throws SQLException, GeneralSecurityException, IOException;
    public void modifier(T t) throws SQLException;
    public void supprimer(int id) throws SQLException;
    public List<T> afficher() throws SQLException;

}
