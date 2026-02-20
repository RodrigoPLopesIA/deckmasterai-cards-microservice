package com.deckmasterai.cards.factory;

import com.deckmasterai.cards.dto.CardRequest;
import com.deckmasterai.cards.dto.CardResponse;
import com.deckmasterai.cards.enums.CardType;
import com.deckmasterai.cards.models.Card;

public class ResponseCardFactory {

    public static CardResponse response(Card card) {

        return switch (card.getType()) {

            case MONSTER -> responseMonster(card);
            case SPELL -> responseSpell(card);
            case TRAP -> responseTrap(card);
        };
    }
    private static CardResponse.CardResponseBuilder responseCard(Card card) {
        return CardResponse.builder()
                .name(card.getName())
                .description(card.getDescription())
                .imageUrl(card.getImageUrl())
                .profileId(card.getProfileId())
                .attribute(card.getAttribute());
    }
    private static CardResponse responseMonster(Card card) {

        return responseCard(card)
                .type(CardType.MONSTER)
                .monsterType(card.getMonsterType())
                .level(card.getLevel())
                .attack(card.getAttack())
                .defense(card.getDefense())
                .monsterSubTypes(card.getMonsterSubTypes() != null
                        ? card.getMonsterSubTypes()
                        : null)
                .build();
    }

    private static CardResponse responseSpell(Card card) {

        return responseCard(card)
                .type(CardType.SPELL)
                .build();
    }

    private static CardResponse responseTrap(Card card) {

        return responseCard(card)
                .type(CardType.TRAP)
                .build();
    }
}
