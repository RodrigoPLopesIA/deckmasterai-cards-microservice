package com.deckmasterai.cards.dto;

import com.deckmasterai.cards.enums.CardType;
import com.deckmasterai.cards.enums.MonsterSubType;
import com.deckmasterai.cards.enums.MonsterType;
import lombok.Builder;

import java.util.List;

@Builder
public record CardResponse(
        String id,
        String name,
        CardType type,
        String attribute,
        Integer level,
        Integer attack,
        Integer defense,
        String profileId,
        List<String> deckIds,
        String imageUrl,
        String description,
        MonsterType monsterType,

        List<MonsterSubType> monsterSubTypes

) {
}
