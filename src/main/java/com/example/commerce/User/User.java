package com.example.commerce.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@CompoundIndex(def = "{'tenantId':1, 'email':1}", unique = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    private String tenantId;

    private String email;
    private String passwordHash;

    @Builder.Default
    private int tokenVersion = 0;
}