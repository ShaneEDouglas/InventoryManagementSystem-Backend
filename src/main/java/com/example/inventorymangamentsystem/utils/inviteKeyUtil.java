package com.example.inventorymangamentsystem.utils;

import jakarta.persistence.PrePersist;

import java.util.UUID;

public class inviteKeyUtil {

    public static String generateInviteKey() {
        return UUID.randomUUID().toString();
    }


}
