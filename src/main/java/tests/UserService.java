package tests;

import java.util.Arrays;
import java.util.List;

public class UserService {
    public static List<TempUser> getAllUsers() {
        return Arrays.asList(
                new TempUser(1,"chkili saif", "chkilisaif776@gmail.com"),
                new TempUser(3,"chekili saih", "chekili.saih@esprit.tn"),
                new TempUser(4,"saif chkili", "saifchkili205@gmail.com")
        );
    }
    public static TempUser getUserById(int id) {
        List<TempUser> tempUsers=getAllUsers();
        for (TempUser tempUser : tempUsers) {
            if (tempUser.getId() == id) {
                return tempUser;
            }
        }
        return null;
    }
}

