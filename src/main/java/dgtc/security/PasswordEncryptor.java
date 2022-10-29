package dgtc.security;

public interface PasswordEncryptor {

  /** Encrypt a raw password */
  String encrypt(String rawPassword);

  boolean match(String rawPassword, String hashPassword);
}
