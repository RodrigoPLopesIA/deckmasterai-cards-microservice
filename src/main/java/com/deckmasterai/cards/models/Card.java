package com.deckmasterai.cards.models;


import com.deckmasterai.cards.enums.MonsterSubType;
import com.deckmasterai.cards.enums.MonsterType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "cards")
public class Card {


    @Id
    private String id;

    private String name;
    private String type;
    private String attribute;
    private Integer level;

    private Integer attack;
    private Integer defense;

    private String imageUrl;

    private String description;

    // opcionais
    private MonsterType monsterType;
    private List<MonsterSubType> monsterSubTypes;
}
