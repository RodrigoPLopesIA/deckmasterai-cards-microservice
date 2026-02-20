package com.deckmasterai.cards.factory;

import com.deckmasterai.cards.dto.CardRequest;
import com.deckmasterai.cards.enums.CardType;
import com.deckmasterai.cards.models.Card;


public abstract class RequestCardFactory {

    public static Card create(CardRequest request) {

        return switch (request.type()) {

            case MONSTER -> createMonster(request);
            case SPELL -> createSpell(request);
            case TRAP -> createTrap(request);
        };
    }
    private static Card.CardBuilder createCard(CardRequest request) {
        return Card.builder()
                .name(request.name())
                .description(request.description())
                .imageUrl(request.imageUrl())
                .profileId(request.profileId())
                .attribute(request.attribute());
    }
    private static Card createMonster(CardRequest request) {

        if (request.attack() == null || request.defense() == null) {
            throw new IllegalArgumentException("Monster must have attack and defense");
        }

        return createCard(request)
                .type(CardType.MONSTER)
                .monsterType(request.monsterType())
                .level(request.level())
                .attack(request.attack())
                .defense(request.defense())
                .monsterSubTypes(request.monsterSubTypes() != null
                        ? request.monsterSubTypes()
                        : null)
                .build();
    }

    private static Card createSpell(CardRequest request) {

        return createCard(request)
                .type(CardType.SPELL)
                .build();
    }

    private static Card createTrap(CardRequest request) {

        return createCard(request)
                .type(CardType.TRAP)
                .build();
    }
}
