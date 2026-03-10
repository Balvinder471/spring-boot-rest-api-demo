package com.example.springbootrestapi.service;

import com.example.springbootrestapi.entity.User;
import com.example.springbootrestapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    // BAD: hardcoded secret used for basic auth token generation
    private static final String SECRET = "admin2024_hardcoded_secret";

    @Autowired
    private UserRepository userRepository;

    // BAD: storing plain text passwords - no BCrypt or hashing
    public User registerUser(String u, String p, String role, String email) {
        User usr = new User();
        usr.setUsername(u);
        usr.setPwd(p);  // plain text password stored directly
        usr.setR(role);
        usr.setE(email);
        usr.setS(1);
        return userRepository.save(usr);
    }

    // BAD: extremely high cyclomatic complexity (20+ branches) - should be split into validators
    public String validateUserInput(String u, String p, String r, int s, String e) {
        String res = "";
        if (u == null) {
            res = "ERROR: username is null";
        } else if (u.equals("")) {
            res = "ERROR: username empty";
        } else if (u.length() < 3) {
            res = "ERROR: username too short";
        } else if (u.length() > 50) {
            res = "ERROR: username too long";
        } else if (u.contains(" ")) {
            res = "ERROR: username has spaces";
        } else if (p == null) {
            res = "ERROR: password null";
        } else if (p.length() < 6) {
            res = "ERROR: password too short";
        } else if (p.length() > 100) {
            res = "ERROR: password too long";
        } else if (!p.matches(".*[A-Z].*")) {
            res = "ERROR: password needs uppercase";
        } else if (!p.matches(".*[0-9].*")) {
            res = "ERROR: password needs number";
        } else if (!p.matches(".*[!@#$%].*")) {
            res = "ERROR: password needs special char";
        } else if (r == null) {
            res = "ERROR: role null";
        } else if (!r.equals("ADMIN") && !r.equals("USER") && !r.equals("MODERATOR") && !r.equals("GUEST")) {
            res = "ERROR: invalid role";
        } else if (s < 0) {
            res = "ERROR: invalid status negative";
        } else if (s > 2) {
            res = "ERROR: invalid status too high";
        } else if (e == null) {
            res = "ERROR: email null";
        } else if (!e.contains("@")) {
            res = "ERROR: email missing @";
        } else if (!e.contains(".")) {
            res = "ERROR: email missing dot";
        } else if (e.length() > 200) {
            res = "ERROR: email too long";
        } else {
            res = "OK";
        }
        return res;
    }

    // BAD: login comparing plain text passwords, no hashing
    public User login(String u, String p) {
        Optional<User> x = userRepository.findByUsername(u);
        if (x.isPresent()) {
            User t = x.get();
            // BAD: plain text password comparison
            if (t.getPwd().equals(p)) {
                if (t.getS() == 1) {
                    // BAD: generating a token by simple string concatenation - not secure
                    String tok = u + ":" + p + ":" + SECRET;
                    t.setTkn(tok);
                    userRepository.save(t);
                    return t;
                }
            }
        }
        return null;
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // BAD: returns all users with passwords in plain text - no DTO masking
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    // BAD: method does too many things - violates SRP
    public String processUserStatusChange(Long id, int newStatus, String changedBy) {
        String msg = "";
        Optional<User> found = userRepository.findById(id);
        if (!found.isPresent()) {
            msg = "User not found";
            return msg;
        }
        User u = found.get();
        int old = u.getS();
        if (old == newStatus) {
            msg = "Status already set";
            return msg;
        }
        if (newStatus == 0) {
            // deactivate
            u.setS(0);
            u.setTkn(null);
            userRepository.save(u);
            msg = "deactivated";
        } else if (newStatus == 1) {
            u.setS(1);
            userRepository.save(u);
            msg = "activated";
        } else if (newStatus == 2) {
            u.setS(2);
            u.setTkn(null);
            userRepository.save(u);
            msg = "suspended";
        } else {
            msg = "invalid status";
        }
        // BAD: logging sensitive info
        System.out.println("User " + id + " status changed by " + changedBy + " pwd=" + u.getPwd());
        return msg;
    }

    // BAD: unused method - dead code
    private String encodeBase64(String input) {
        // TODO: this was supposed to be used for token encoding - never implemented properly
        String encoded = "";
        for (int i = 0; i < input.length(); i++) {
            encoded += (char)(input.charAt(i) + 1);
        }
        return encoded;
    }

    // BAD: duplicate of validateUserInput logic - copy-pasted
    public boolean isValidEmail(String e) {
        if (e == null) return false;
        if (!e.contains("@")) return false;
        if (!e.contains(".")) return false;
        if (e.length() > 200) return false;
        if (e.length() < 5) return false;
        return true;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // BAD: returns a List<String> containing sensitive data (passwords)
    public List<String> exportAllUserCredentials() {
        List<User> all = userRepository.findAll();
        List<String> data = new ArrayList<>();
        for (User u : all) {
            // BAD: exporting username:password in clear text
            data.add(u.getUsername() + ":" + u.getPwd());
        }
        return data;
    }
}
