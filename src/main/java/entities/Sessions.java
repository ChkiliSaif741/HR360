package entities;

public class Sessions {
    private static Sessions instance;
    private int idUtilisateur;

    // 🔒 Constructeur privé pour empêcher l'instanciation directe
    private Sessions(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    // 📌 Méthode pour obtenir ou créer l'instance
    public static Sessions getInstance(int idUtilisateur) {
        if (instance == null) {
            instance = new Sessions(idUtilisateur);
        }
        return instance;
    }

    // 📌 Méthode pour obtenir l'instance sans créer
    public static Sessions getInstance() {
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
