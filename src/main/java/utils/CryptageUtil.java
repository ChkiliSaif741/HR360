package utils;

import org.mindrot.jbcrypt.BCrypt;

public class CryptageUtil {

    /**
     * Crypte un mot de passe en utilisant BCrypt.
     *
     * @param motDePasse Le mot de passe en clair.
     * @return Le mot de passe crypté.
     */
    public static String crypterMotDePasse(String motDePasse) {
        return BCrypt.hashpw(motDePasse, BCrypt.gensalt());
    }

    /**
     * Vérifie si un mot de passe en clair correspond à un mot de passe crypté.
     *
     * @param motDePasseClair Le mot de passe en clair.
     * @param motDePasseCrypte Le mot de passe crypté.
     * @return true si les mots de passe correspondent, sinon false.
     */
    public static boolean verifierMotDePasse(String motDePasseClair, String motDePasseCrypte) {
        return BCrypt.checkpw(motDePasseClair, motDePasseCrypte);
    }
}
