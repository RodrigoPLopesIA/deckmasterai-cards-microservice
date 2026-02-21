package com.deckmasterai.cards.services;


import com.deckmasterai.cards.client.DeckClient;
import com.deckmasterai.cards.dto.CardRequest;
import com.deckmasterai.cards.dto.CardResponse;
import com.deckmasterai.cards.enums.CardType;
import com.deckmasterai.cards.enums.MonsterSubType;
import com.deckmasterai.cards.enums.MonsterType;
import com.deckmasterai.cards.mapper.CardMapper;
import com.deckmasterai.cards.models.Card;
import com.deckmasterai.cards.repository.CardRepository;
import com.deckmasterai.cards.strategies.FileStorageStrategy;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;


import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {


    @Mock
    CardRepository cardRepository;

    @Mock
    CardMapper cardMapper;

    @Mock
    DeckClient deckClient;

    @Mock
    FileStorageStrategy  fileStorageStrategy;

    @InjectMocks
    CardService cardService;

    CardResponse cardResponse;
    CardRequest cardRequest;
    Card card;

    @BeforeEach()
    void setUp() {
        card = Card.builder()
                .name("Blue-Eyes White Dragon")
                .type(CardType.MONSTER)
                .attribute("LIGHT")
                .level(8)
                .attack(3000)
                .defense(2500)
                .profileId("profile123")
                .imageUrl("https://image-url.com/blue-eyes.png")
                .description("This legendary dragon is a powerful engine of destruction.")
                .monsterType(MonsterType.DRAGON)
                .monsterSubTypes(List.of(MonsterSubType.NORMAL))
                .deckIds(List.of("deck1", "deck2"))
                .build();
        cardResponse = CardResponse.builder()
                .id("card123")
                .name("Blue-Eyes White Dragon")
                .type(CardType.MONSTER)
                .attribute("LIGHT")
                .level(8)
                .attack(3000)
                .defense(2500)
                .profileId("profile123")
                .deckIds(List.of("deck1", "deck2"))
                .imageUrl("https://image-url.com/blue-eyes.png")
                .description("Legendary dragon with immense power.")
                .monsterType(MonsterType.DRAGON)
                .monsterSubTypes(List.of(MonsterSubType.NORMAL))
                .build();

        cardRequest = new CardRequest(
                "Dark Magician",
                CardType.MONSTER,
                "DARK",
                7,
                2500,
                2100,
                "profile123",
                List.of("deck1"),
                "https://image-url.com/dark-magician.png",
                "The ultimate wizard in terms of attack and defense.",
                MonsterType.SPELLCASTER,
                List.of(MonsterSubType.NORMAL)
        );
    }

    @Test
    @DisplayName("GET all cards")
    void getAllCardsTest() {

        // Arrange
        Page<Card> page = new PageImpl<>(List.of(card));

        Mockito.when(cardRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(page);

        Mockito.when(cardMapper.cardToCardResponse(Mockito.any(Card.class)))
                .thenReturn(cardResponse);

        // Act
        Page<CardResponse> result = cardService.getCards(PageRequest.of(0, 10));

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).hasSize(1);
        Assertions.assertThat(result.getContent().get(0).id())
                .isEqualTo("card123");

        Mockito.verify(cardRepository)
                .findAll(PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("POST create a card")
    void createCardTest(){
        Mockito.when(cardMapper.cardRequestToCard(Mockito.any(CardRequest.class))).thenReturn(card);
        Mockito.when(cardMapper.cardToCardResponse(Mockito.any(Card.class)))
                .thenReturn(cardResponse);
        Mockito.when(cardRepository.save(Mockito.any(Card.class))).thenReturn(card);

        var card = cardService.create(cardRequest);

        Assertions.assertThat(card).isNotNull();

    }
}
