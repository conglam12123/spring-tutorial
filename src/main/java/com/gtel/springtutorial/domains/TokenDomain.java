package com.gtel.springtutorial.domains;

public interface TokenDomain {
    String genToken(String userName);

    public String validateToken(String token);

    void extendTTL (String token);
}
