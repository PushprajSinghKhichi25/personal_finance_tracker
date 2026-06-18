package org.example.util;

import org.example.entity.User;
import org.springframework.boot.SpringApplication;
import org.springframework.security.core.Authentication;

public class SecurityUtil
{
    public static User extractUser(Authentication authentication){
        if(authentication==null) throw new IllegalArgumentException("Authentication is null");
        Object pricipal=authentication.getPrincipal();

        if(pricipal instanceof User) return (User) pricipal;
        else{
            throw new IllegalArgumentException(
                    "Invalid authentication principal. Expected User but got"+
                            (pricipal !=null ? pricipal.getClass().getName() : "null")
            );
        }
    }
}
