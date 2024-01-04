package com.example.demo.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum ProfileEnum {

    ADMIN(1, "ROLE_ADMIN"),
    USER(2, "ROLE_USER");

    private final Integer code;
    private final String role;

    public static ProfileEnum returnsEnum(Integer value) {
        if (Objects.isNull(value)) { return null; }

        for (ProfileEnum profileEnum: ProfileEnum.values()) {
            if (value.equals(profileEnum.getCode())) {
                return profileEnum;
            }
        }

        throw new IllegalArgumentException("Codigo invalido: " + value);

    }

}
