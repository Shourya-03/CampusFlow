import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashCheck {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("Valid BCrypt hash for 'admin123':");
        System.out.println(encoder.encode("admin123"));

        System.out.println("Valid BCrypt hash for 'teacher123':");
        System.out.println(encoder.encode("teacher123"));

        System.out.println("Valid BCrypt hash for 'student123':");
        System.out.println(encoder.encode("student123"));
    }
}
