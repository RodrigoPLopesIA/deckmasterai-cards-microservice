package com.deckmasterai.cards.models;


import com.deckmasterai.cards.enums.CardType;
import com.deckmasterai.cards.enums.MonsterSubType;
import com.deckmasterai.cards.enums.MonsterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cards")
public class Card {


    @Id
    private String id;

    private String name;
    private CardType type;
    private String attribute;
    private Integer level;

    private Integer attack;
    private Integer defense;
    private String profileId;

    private String imageUrl;

    private String description;

    // opcionais
    private MonsterType monsterType;
    private List<MonsterSubType> monsterSubTypes;
}
