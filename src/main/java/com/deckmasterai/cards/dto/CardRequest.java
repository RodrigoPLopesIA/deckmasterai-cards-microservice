package com.deckmasterai.cards.dto;

import com.deckmasterai.cards.enums.CardType;
import com.deckmasterai.cards.enums.MonsterSubType;
import com.deckmasterai.cards.enums.MonsterType;
import jakarta.validation.constraints.*;

import java.util.List;

public record CardRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Type is required")
        CardType type,

        @NotBlank(message = "Attribute is required")
        String attribute,

        @NotNull(message = "Level is required")
        @Min(value = 0, message = "Level must be positive")
        Integer level,

        @NotNull(message = "Attack is required")
        @Min(value = 0, message = "Attack must be positive")
        Integer attack,

        @NotNull(message = "Defense is required")
        @Min(value = 0, message = "Defense must be positive")
        Integer defense,

        @NotBlank(message = "ProfileId is required")
        String profileId,

        @NotBlank(message = "ImageUrl is required")
        String imageUrl,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "MonsterType is required")
        MonsterType monsterType,

        @NotEmpty(message = "MonsterSubTypes are required")
        List<MonsterSubType> monsterSubTypes

) {}

