package entities;

public class Session {
    private static Session instance;
    private int idUtilisateur;

    // 🔒 Constructeur privé pour empêcher l'instanciation directe
    private Session(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    // 📌 Méthode pour obtenir ou créer l'instance
    public static Session getInstance(int idUtilisateur) {
        if (instance == null) {
            instance = new Session(idUtilisateur);
        }
        return instance;
    }

    // 📌 Méthode pour obtenir l'instance sans créer
    public static Session getInstance() {
        return instance;
    }

    // 📌 Getter pour l'ID utilisateur
    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    // 📌 Pour déconnecter l'utilisateur
    public static void destroySession() {
        instance = null;
    }
}
