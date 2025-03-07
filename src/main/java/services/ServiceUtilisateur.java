package services;

import entities.Utilisateur;
import interfaces.IService;
import org.mindrot.jbcrypt.BCrypt;
import utils.CryptageUtil;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUtilisateur implements IService<Utilisateur> {

    Connection connection;

    public ServiceUtilisateur() {
        connection = MyDatabase.getInstance().getConnection();
        if (connection == null) {
            System.err.println("Erreur : la connexion est null !");
        }
    }
    public Utilisateur authentifier1(String email, String password) throws SQLException {
        String query = "SELECT id, nom, prenom, email, password, role, image FROM utilisateur WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String motDePasseStocke = resultSet.getString("password");
                    System.out.println("Mot de passe stocké : " + motDePasseStocke); // Debugging

                    // Vérifier si le mot de passe est crypté avec BCrypt
                    if (motDePasseStocke != null && motDePasseStocke.startsWith("$2a$")) {
                        System.out.println("Mot de passe crypté avec BCrypt détecté"); // Debugging
                        if (BCrypt.checkpw(password, motDePasseStocke)) {
                            return creerUtilisateurDepuisResultSet(resultSet);
                        }
                    }
                    // Vérifier si le mot de passe est crypté avec SHA-256 (format sel:hash)
                    else if (motDePasseStocke != null && motDePasseStocke.contains(":")) {
                        System.out.println("Mot de passe crypté avec SHA-256 détecté"); // Debugging
                        if (CryptageUtil.verifierMotDePasse(password, motDePasseStocke)) {
                            return creerUtilisateurDepuisResultSet(resultSet);
                        }
                    }
                    // Vérifier si le mot de passe est en clair
                    else {
                        System.out.println("Mot de passe en clair détecté"); // Debugging
                        if (CryptageUtil.verifierMotDePasse(password, motDePasseStocke)) {
                            // Crypter le mot de passe avec BCrypt et mettre à jour la base de données
                            String motDePasseCrypte = BCrypt.hashpw(password, BCrypt.gensalt());
                            mettreAJourMotDePasse(resultSet.getInt("id"), motDePasseCrypte);
                            return creerUtilisateurDepuisResultSet(resultSet);
                        }
                    }
                }
            }
        }
        return null;
    }

    public void migrerMotsDePasseVersBCrypt() throws SQLException {
        String query = "SELECT id, password FROM utilisateur";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String motDePasseStocke = resultSet.getString("password");

                // Vérifier si le mot de passe est crypté avec SHA-256
                if (motDePasseStocke != null && motDePasseStocke.contains(":")) {
                    System.out.println("Migration du mot de passe pour l'utilisateur ID : " + id);
                    String motDePasseClair = extraireMotDePasseClair(motDePasseStocke); // Extraire le mot de passe en clair (si possible)
                    if (motDePasseClair != null) {
                        String motDePasseCrypte = BCrypt.hashpw(motDePasseClair, BCrypt.gensalt());
                        mettreAJourMotDePasse(id, motDePasseCrypte);
                        System.out.println("Mot de passe migré avec succès pour l'utilisateur ID : " + id);
                    } else {
                        System.out.println("Impossible de migrer le mot de passe pour l'utilisateur ID : " + id);
                    }
                }
            }
        }
    }

    private String extraireMotDePasseClair(String motDePasseCrypte) {
        // Implémentez cette méthode si vous pouvez extraire le mot de passe en clair à partir du hash SHA-256
        // Sinon, vous devrez demander aux utilisateurs de réinitialiser leur mot de passe
        return null;
    }


    /*public Utilisateur authentifier1(String email, String password) throws SQLException {
        String query = "SELECT id, nom, prenom, email, password, role, image FROM utilisateur WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String motDePasseStocke = resultSet.getString("password");
                    try {
                        if (motDePasseStocke.startsWith("$2a$")) {
                            if (CryptageUtil.verifierMotDePasse(password, motDePasseStocke)) {
                                return creerUtilisateurDepuisResultSet(resultSet);
                            }
                        } else {
                            System.out.println(motDePasseStocke);
                            if (password.equals(motDePasseStocke)) {
                                String motDePasseCrypte = CryptageUtil.crypterMotDePasse(password);
                                mettreAJourMotDePasse(resultSet.getInt("id"), motDePasseCrypte);
                                return creerUtilisateurDepuisResultSet(resultSet);
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid password hash for user: " + email);
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }*/

    private Utilisateur creerUtilisateurDepuisResultSet(ResultSet resultSet) throws SQLException {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(resultSet.getInt("id"));
        utilisateur.setNom(resultSet.getString("nom"));
        utilisateur.setPrenom(resultSet.getString("prenom"));
        utilisateur.setEmail(resultSet.getString("email"));
        utilisateur.setRole(resultSet.getString("role"));
        utilisateur.setImgSrc(resultSet.getString("image"));
        return utilisateur;
    }

    private void mettreAJourMotDePasse(int id, String motDePasseCrypte) throws SQLException {
        String updateQuery = "UPDATE utilisateur SET password = ? WHERE id = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, motDePasseCrypte);
            updateStatement.setInt(2, id);
            updateStatement.executeUpdate();
        }
    }





    @Override
    public void ajouter(Utilisateur utilisateur) throws SQLException {
        // Crypter le mot de passe avant de l'insérer dans la base de données
        String motDePasseCrypte = CryptageUtil.crypterMotDePasse(utilisateur.getPassword());

        if (utilisateur.getRole().equals("Employe")) {
            String req = "insert into utilisateur (nom, prenom, email, password, role, image, salaire, poste) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(req)) {
                statement.setString(1, utilisateur.getNom());
                statement.setString(2, utilisateur.getPrenom());
                statement.setString(3, utilisateur.getEmail());
                statement.setString(4, motDePasseCrypte); // Utiliser le mot de passe crypté
                statement.setString(5, utilisateur.getRole());
                statement.setString(6, utilisateur.getImgSrc());
                statement.setFloat(7, utilisateur.getSalaire());
                statement.setString(8, utilisateur.getPoste());
                statement.executeUpdate();
                System.out.println("Utilisateur ajouté !");
            }
        } else {
            String req = "insert into utilisateur (nom, prenom, email, password, role, image, competence) " +
                    "values (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(req)) {
                statement.setString(1, utilisateur.getNom());
                statement.setString(2, utilisateur.getPrenom());
                statement.setString(3, utilisateur.getEmail());
                statement.setString(4, motDePasseCrypte); // Utiliser le mot de passe crypté
                statement.setString(5, utilisateur.getRole());
                statement.setString(6, utilisateur.getImgSrc());
                statement.setString(7, utilisateur.getCompetence());
                statement.executeUpdate();
                System.out.println("Utilisateur ajouté !");
            }
        }
    }


    public boolean emailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0;
        }
        return false;
    }


    public void modifier1(Utilisateur utilisateur) throws SQLException {
        // Requête SQL sans la colonne 'password'
        String req = "UPDATE utilisateur SET nom=?, prenom=?, email=?, poste=?, salaire=?, image=? WHERE id=?";
        PreparedStatement statement = connection.prepareStatement(req);

        // Définir les valeurs des colonnes
        statement.setString(1, utilisateur.getNom());
        statement.setString(2, utilisateur.getPrenom());
        statement.setString(3, utilisateur.getEmail());
        statement.setString(4, utilisateur.getPoste()); // Ajouter le poste
        statement.setFloat(5, utilisateur.getSalaire()); // Ajouter le salaire
        statement.setString(6, utilisateur.getImgSrc()); // Ajouter l'image
        statement.setInt(7, utilisateur.getId()); // L'ID pour localiser l'utilisateur à modifier

        // Exécution de la mise à jour
        int rowsUpdated = statement.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("Utilisateur modifié avec succès !");
        } else {
            System.out.println("Aucune modification effectuée !");
        }
    }

    @Override
    public void modifier(Utilisateur utilisateur) throws SQLException {
        // Requête SQL sans la colonne 'role'
        String req = "UPDATE utilisateur SET nom=?, prenom=?, email=?, password=?, image=? WHERE id=?";
        PreparedStatement statement = connection.prepareStatement(req);

        // Définir les valeurs des colonnes
        statement.setString(1, utilisateur.getNom());
        statement.setString(2, utilisateur.getPrenom());
        statement.setString(3, utilisateur.getEmail());
        statement.setString(4, utilisateur.getPassword()); // Ajouter le mot de passe
        statement.setString(5, utilisateur.getImgSrc()); // Ajouter l'image
        statement.setInt(6, utilisateur.getId()); // L'ID pour localiser l'utilisateur à modifier

        // Exécution de la mise à jour
        int rowsUpdated = statement.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("Utilisateur modifié avec succès !");
        } else {
            System.out.println("Aucune modification effectuée !");
        }
    }

    public void modifier_candidat(Utilisateur utilisateur) throws SQLException {
        // Requête SQL sans la colonne 'role'
        String req = "UPDATE utilisateur SET nom=?, prenom=?, email=?, image=? WHERE id=?";
        PreparedStatement statement = connection.prepareStatement(req);

        // Définir les valeurs des colonnes
        statement.setString(1, utilisateur.getNom());
        statement.setString(2, utilisateur.getPrenom());
        statement.setString(3, utilisateur.getEmail());
        statement.setString(4, utilisateur.getImgSrc()); // Ajouter l'image
        statement.setInt(5, utilisateur.getId()); // L'ID pour localiser l'utilisateur à modifier

        // Exécution de la mise à jour
        int rowsUpdated = statement.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("Candidat modifié avec succès !");
        } else {
            System.out.println("Aucune modification effectuée !");
        }
    }


    public void modifier_employe(Utilisateur utilisateur) throws SQLException {
        // Requête SQL sans la colonne 'role'
        String req = "UPDATE utilisateur SET nom=?, prenom=?, email=?, image=? WHERE id=?";
        PreparedStatement statement = connection.prepareStatement(req);

        // Définir les valeurs des colonnes
        statement.setString(1, utilisateur.getNom());
        statement.setString(2, utilisateur.getPrenom());
        statement.setString(3, utilisateur.getEmail());
        statement.setString(4, utilisateur.getImgSrc()); // Ajouter l'image
        statement.setInt(5, utilisateur.getId()); // L'ID pour localiser l'utilisateur à modifier

        // Exécution de la mise à jour
        int rowsUpdated = statement.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("Utilisateur modifié avec succès !");
        } else {
            System.out.println("Aucune modification effectuée !");
        }
    }



    @Override
    public void supprimer(int id) throws SQLException {

        String req = "delete from utilisateur where id=?";
        PreparedStatement statement = connection.prepareStatement(req);
        statement.setInt(1, id);
        statement.executeUpdate();
        System.out.println("Utilisateur supprimé");
    }

    @Override
    public List<Utilisateur> afficher() throws SQLException {

        List<Utilisateur> utilisateurs = new ArrayList<>();
        String req = "select * from utilisateur";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(req);

        while (resultSet.next()) {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(resultSet.getInt("id"));
            utilisateur.setNom(resultSet.getString("nom"));
            utilisateur.setPrenom(resultSet.getString("prenom"));
            utilisateur.setEmail(resultSet.getString("email"));
            utilisateur.setRole(resultSet.getString("role"));
            utilisateur.setImgSrc(resultSet.getString("image"));
            utilisateurs.add(utilisateur);
        }
        return utilisateurs;
    }

    public Utilisateur getUserByEmail(String email) {
        String req = "SELECT * FROM utilisateur WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
        }
        return null;
    }

    public Utilisateur getUserById(int id) {
        String req = "SELECT * FROM utilisateur WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
        }
        return null;
    }

    public Utilisateur getUserForPasswordReset(String username, String email) throws SQLException {
        String query = "SELECT * FROM utilisateur WHERE nom = ? AND email = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }
        }
        return null; // Si aucun utilisateur ne correspond
    }

    public void modifierMotDePasse(Utilisateur utilisateur) throws SQLException {
        String req = "UPDATE utilisateur SET password = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, utilisateur.getPassword()); // Pas de hashage, comme demandé
            ps.setInt(2, utilisateur.getId());
            ps.executeUpdate();
        }
    }

    public void updateRole(Utilisateur utilisateur) throws SQLException {
        // Requête SQL sans la colonne 'password'
        String req = "UPDATE utilisateur SET role=? WHERE id=?";
        PreparedStatement statement = connection.prepareStatement(req);

        // Définir les valeurs des colonnes
        statement.setString(1, utilisateur.getRole());
        statement.setInt(2, utilisateur.getId()); // L'ID pour localiser l'utilisateur à modifier

        // Exécution de la mise à jour
        int rowsUpdated = statement.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("Utilisateur modifié avec succès !");
        } else {
            System.out.println("Aucune modification effectuée !");
        }
    }


}




