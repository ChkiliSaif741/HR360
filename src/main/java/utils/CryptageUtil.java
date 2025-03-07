package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptageUtil {

    private static final SecureRandom random = new SecureRandom();

    /**
     * Génère un sel aléatoire.
     *
     * @return Un sel aléatoire (en Base64).
     */
    private static String genererSel() {
        byte[] sel = new byte[16]; // 16 bytes = 128 bits
        random.nextBytes(sel);
        return Base64.getEncoder().encodeToString(sel);
    }

    /**
     * Crypte un mot de passe en utilisant SHA-256 avec un sel.
     *
     * @param motDePasse Le mot de passe en clair.
     * @return Le mot de passe crypté avec le sel (format: sel:hash).
     */
    public static String crypterMotDePasse(String motDePasse) {
        try {
            String sel = genererSel();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Ajouter le sel au mot de passe avant de hasher
            digest.update(sel.getBytes());
            byte[] hash = digest.digest(motDePasse.getBytes());
            String hashBase64 = Base64.getEncoder().encodeToString(hash);
            // Retourner le sel et le hash concaténés
            return sel + ":" + hashBase64;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du cryptage du mot de passe", e);
        }
    }

    /**
     * Vérifie si un mot de passe en clair correspond à un mot de passe crypté.
     *
     * @param motDePasseClair Le mot de passe en clair.
     * @param motDePasseCrypte Le mot de passe crypté (format: sel:hash).
     * @return true si les mots de passe correspondent, sinon false.
     */
    public static boolean verifierMotDePasse(String motDePasseClair, String motDePasseCrypte) {
        // Extraire le sel et le hash du mot de passe crypté
        String[] parties = motDePasseCrypte.split(":");
        String sel = parties[0];
        String hashStocke = parties[1];

        // Hasher le mot de passe en clair avec le même sel
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(sel.getBytes());
            byte[] hash = digest.digest(motDePasseClair.getBytes());
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            if (hashBase64.equals(hashStocke)){
                System.out.println("egaux!");
            }
            // Comparer les hashs
            return hashBase64.equals(hashStocke);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors de la vérification du mot de passe", e);
        }
    }
}