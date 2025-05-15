package utils;

import org.mindrot.jbcrypt.BCrypt;

public class CryptageUtil {

    /**
     * Crypte un mot de passe en format compatible Symfony
     */
    public static String crypterMotDePasse(String motDePasse) {
        // Solution 100% compatible qui fonctionne avec jBCrypt
        String hash = BCrypt.hashpw(motDePasse, BCrypt.gensalt(13));

        // Conversion du hash en format Symfony (après génération)
        return convertToSymfonyFormat(hash);
    }

    /**
     * Vérifie un mot de passe contre un hash Symfony
     */
    public static boolean verifierMotDePasse(String motDePasseClair, String motDePasseCrypte) {
        // Conversion du format Symfony vers format jBCrypt
        String jbcryptHash = convertToJBCryptFormat(motDePasseCrypte);
        return BCrypt.checkpw(motDePasseClair, jbcryptHash);
    }

    private static String convertToSymfonyFormat(String jbcryptHash) {
        return jbcryptHash.replaceFirst("\\$2a\\$", "\\$2y\\$");
    }

    private static String convertToJBCryptFormat(String symfonyHash) {
        return symfonyHash.replaceFirst("\\$2y\\$", "\\$2a\\$");
    }
}