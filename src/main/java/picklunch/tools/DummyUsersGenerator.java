package picklunch.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import picklunch.model.entity.User;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static picklunch.config.LoadUsersJobConfig.*;

// For testing and demo purpose
public class DummyUsersGenerator {

    private static final String[] FIRST_NAMES = {
            "Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Heidi", "Ivan", "Judy",
            "Kevin", "Linda", "Mike", "Nancy", "Oscar", "Pamela", "Quinn", "Rachel", "Steve", "Tina",
            "Ursula", "Victor", "Wendy", "Xavier", "Yara", "Zack"
    };

    private static final String[] LAST_NAMES = {
            "Smith", "Jones", "Williams", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor", "Anderson",
            "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson", "Clark"
    };

    public static void main(String[] args) throws Exception {
        System.out.println("Running Dummy Users Generator");

        int count = 20;
        String filename = "users.csv";

        List<User> users = generateRandomUsers(count);
        writeUsersToCsvFile(users, filename);
        System.out.printf("Saved %d Dummy Users in %s\n", count, filename);
    }

    private static List<User> generateRandomUsers(int count) {
        List<User> users = new ArrayList<>();
        Random random = new Random();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        for (int i = 0; i < count; i++) {
            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            users.add(User.builder()
                    .displayName(firstName + " " + lastName)
                    .username("user" + (i + 1))
                    .encodedPassword(passwordEncoder.encode("1111"))
                    .build()
            );
        }
        return users;
    }

    private static void writeUsersToCsvFile(List<User> users, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writeCsvLine(writer, List.of(
                    FIELD_USERNAME,
                    FIELD_DISPLAY_NAME,
                    FIELD_ENCODED_PASSWORD
            ));

            for (User user : users) {
                writeCsvLine(writer, List.of(
                        user.getUsername(),
                        user.getDisplayName(),
                        user.getEncodedPassword()
                ));
            }
        }
    }

    private static void writeCsvLine(Writer writer, List<String> fields) throws IOException {
        writer.write(String.join(",", fields));
        writer.write(System.lineSeparator());
    }

}
