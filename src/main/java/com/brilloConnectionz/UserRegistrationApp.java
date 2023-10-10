package com.brilloConnectionz;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import jakarta.xml.bind.DatatypeConverter;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class UserRegistrationApp {

    private static String username;
    private static String email;
    private static String password;
    private static String dob;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your username: ");
        username = scanner.nextLine();

        System.out.print("Enter your email: ");
        email = scanner.nextLine();

        System.out.print("Enter your password (must have at least 1 upper case, 1 special character e.g @#$%^&*, 1 number and must be minimum of 8 characters: ");
        password = scanner.nextLine();

        System.out.print("Enter your DOB (example: 1980-12-02, you must be 16 years old or older) : ");
        dob = scanner.nextLine();

        scanner.close();

        CompletableFuture<Boolean> usernameValidation = CompletableFuture.supplyAsync(() -> validateUsername(username));
        CompletableFuture<Boolean> emailValidation = CompletableFuture.supplyAsync(() -> validateEmail(email));
        CompletableFuture<Boolean> passwordValidation = CompletableFuture.supplyAsync(() -> validatePassword(password));
        CompletableFuture<Boolean> dobValidation = CompletableFuture.supplyAsync(() -> validateDateOfBirth(dob));
        CompletableFuture<Void> allOf = CompletableFuture.allOf(usernameValidation, emailValidation, passwordValidation, dobValidation);

        allOf.thenRun(() -> {
           boolean valid =  usernameValidation.join() && emailValidation.join() && passwordValidation.join() && dobValidation.join();
            System.out.println(valid);
            if (valid) {
                String jwtToken = createToken(username);
                System.out.println("JWT Token: " + jwtToken);

                String result = verifyJWT(jwtToken, username);
                System.out.println("Verification result: " + result);
            } else {
                System.out.println("Validation failed:");
                if (!usernameValidation.join()) {
                    System.out.println("Username: username cannot be empty");
                }
                if (!emailValidation.join()) {
                    System.out.println("Email: Please input a valid email");
                }
                if (!passwordValidation.join()) {
                    System.out.println("Password: Password not strong");
                }
                if (!dobValidation.join()) {
                    System.out.println("Date of Birth: Please input a valid date of birth. You must be 16 years old or older");
                }
            }
        }).join();
    }

    private static boolean validateUsername(String username) {
        return username != null && username.length() >= 4;
    }

    private static boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
            String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
            Pattern pattern = Pattern.compile(regex);
            return pattern.matcher(email).matches();
        }


    private static boolean validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$");
    }

    private static boolean validateDateOfBirth(String dob) {
        if (dob == null || dob.isEmpty()) {
            return false;
        }
        LocalDate birthDate = LocalDate.parse(dob);
        LocalDate today = LocalDate.now();
        return birthDate.plusYears(16).isBefore(today);
    }

    public static String generateSecret(){
        return DatatypeConverter.printBase64Binary(new byte[512/8]);
    }
    public static Key generateKey(){
        byte[] secretKeyInBytes = DatatypeConverter.parseBase64Binary(generateSecret());
        return new SecretKeySpec(secretKeyInBytes, "HmacSHA512");
    }
    private static String createToken(String subject){
        Map<String, Object> claims = new HashMap<>();
        claims.put("User", subject);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.valueOf(900000)))
                .signWith(generateKey(), SignatureAlgorithm.HS512)
                .compact();

    }
    public static String extractUsername(String token){
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    private static Claims extractAllClaims(String token){
        return Jwts.parserBuilder().setSigningKey(generateKey()).build()
                .parseClaimsJws(token)
                .getBody();
    }
    private static String verifyJWT(String jwtToken, String username) {
        return Objects.equals(extractUsername(jwtToken), username) ? "verification pass" :
                "verification fails";
    }

}
