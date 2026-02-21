package com.deckmasterai.cards.services;


import com.deckmasterai.cards.client.DeckClient;
import com.deckmasterai.cards.dto.CardRequest;
import com.deckmasterai.cards.dto.CardResponse;
import com.deckmasterai.cards.enums.CardType;
import com.deckmasterai.cards.enums.MonsterSubType;
import com.deckmasterai.cards.enums.MonsterType;
import com.deckmasterai.cards.exceptions.NotFoundException;
import com.deckmasterai.cards.mapper.CardMapper;
import com.deckmasterai.cards.models.Card;
import com.deckmasterai.cards.repository.CardRepository;
import com.deckmasterai.cards.strategies.FileStorageStrategy;
import org.assertj.core.api.Assertions;
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
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Optional;

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
                .id("card123")
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
    void getAllCards() {

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
    @DisplayName("POST should create a card")
    void createCard() {
        Mockito.when(cardMapper.cardToCardResponse(Mockito.any(Card.class)))
                .thenReturn(cardResponse);
        Mockito.when(cardMapper.cardRequestToCard(Mockito.any(CardRequest.class))).thenReturn(card);
        Mockito.when(cardRepository.save(Mockito.any(Card.class))).thenReturn(card);

        var card = cardService.create(cardRequest);

        Assertions.assertThat(card).isNotNull();
        Mockito.verify(cardRepository).save(Mockito.any(Card.class));
    }

    @Test
    @DisplayName("PUT should update a card")
    void updateCardTest() {

        // Arrange
        Mockito.when(cardMapper.updateCardFromRequest(Mockito.any(CardRequest.class), Mockito.any(Card.class))).thenReturn(card);
        Mockito.when(cardMapper.cardToCardResponse(Mockito.any(Card.class)))
                .thenReturn(cardResponse);
        Mockito.when(cardRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(card));
        Mockito.when(cardRepository.save(Mockito.any(Card.class))).thenReturn(card);

        // Act + Assert

        var card = cardService.update("1234", cardRequest);


        // Verify
        Assertions.assertThat(card).isNotNull();
        Mockito.verify(cardRepository).findById("1234");
    }


    @Test
    @DisplayName("PUT update a card should return not found")
    void updateCardTest_shouldReturnNotFound() {

        // Arrange
        Mockito.when(cardRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.empty());

        // Act + Assert
        Assertions.assertThatThrownBy(() ->
                        cardService.update("1234", cardRequest)
                )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Card not found");

        // Verify
        Mockito.verify(cardRepository).findById("1234");
    }

    @Test
    @DisplayName("DELETE should delete a card")
    void deleteCard() {
        Mockito.doNothing().when(cardRepository).deleteById(Mockito.anyString());

        cardService.delete("1234");
        Mockito.verify(cardRepository).deleteById("1234");
    }

    @Test
    @DisplayName("UPLOAD should upload image and update card")
    void uploadCardImage_shouldUploadSuccessfully() {

        // Arrange
        MultipartFile file = Mockito.mock(MultipartFile.class);

        Mockito.when(file.isEmpty()).thenReturn(false);

        Mockito.when(cardRepository.findById("1234"))
                .thenReturn(Optional.of(card));

        Mockito.when(fileStorageStrategy.upload(file))
                .thenReturn("new-image-key.png");

        Mockito.when(cardRepository.save(Mockito.any(Card.class)))
                .thenReturn(card);

        Mockito.when(cardMapper.cardToCardResponse(Mockito.any(Card.class)))
                .thenReturn(cardResponse);

        // Act
        var result = cardService.uploadCardImage("1234", file);

        // Assert
        Assertions.assertThat(result).isNotNull();

        Mockito.verify(fileStorageStrategy).upload(file);
        Mockito.verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("GET decks by card id should return decks")
    void getDecksByCardId_shouldReturnDecks() {

        // Arrange
        List<String> decksMock = List.of("deck1", "deck2");
        Mockito.when(cardRepository.findById("card123"))
                .thenReturn(Optional.of(card));
        Mockito.when(deckClient.getDecksByCardId("card123"))
                .thenReturn(decksMock);

        // Act
        Object result = cardService.getDecksByCardId("card123");

        // Assert
        Assertions.assertThat(result).isEqualTo(decksMock);
        Mockito.verify(cardRepository).findById("card123");
        Mockito.verify(deckClient).getDecksByCardId("card123");
    }

    @Test
    @DisplayName("GET decks by card id should throw when card not found")
    void getDecksByCardId_shouldThrowWhenNotFound() {

        Mockito.when(cardRepository.findById("card123"))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() ->
                        cardService.getDecksByCardId("card123")
                ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Card not found");

        Mockito.verify(cardRepository).findById("card123");
        Mockito.verifyNoInteractions(deckClient);
    }

    @Test
    @DisplayName("GET deck by id by card id should return deck")
    void getDeckByIdByCardId_shouldReturnDeck() {

        // Arrange
        String deckId = "deck1";
        card.setDeckIds(List.of("deck1", "deck2"));
        String deckMock = "deck1-details";

        Mockito.when(cardRepository.findById("card123"))
                .thenReturn(Optional.of(card));
        Mockito.when(deckClient.getDeckByIdByCardId("card123", deckId))
                .thenReturn(deckMock);

        // Act
        Object result = cardService.getDeckByIdByCardId("card123", deckId);

        // Assert
        Assertions.assertThat(result).isEqualTo(deckMock);
        Mockito.verify(cardRepository).findById("card123");
        Mockito.verify(deckClient).getDeckByIdByCardId("card123", deckId);
    }

    @Test
    @DisplayName("GET deck by id by card id should throw if deck not found")
    void getDeckByIdByCardId_shouldThrowDeckNotFound() {

        // Arrange
        card.setDeckIds(List.of("deck1", "deck2"));

        Mockito.when(cardRepository.findById("card123"))
                .thenReturn(Optional.of(card));

        // Act + Assert
        Assertions.assertThatThrownBy(() ->
                        cardService.getDeckByIdByCardId("card123", "deck3")
                ).isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Deck not found for this card");

        Mockito.verify(cardRepository).findById("card123");
        Mockito.verifyNoInteractions(deckClient);
    }

    @Test
    @DisplayName("GET deck by id by card id should throw if card not found")
    void getDeckByIdByCardId_shouldThrowCardNotFound() {

        Mockito.when(cardRepository.findById("card123"))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() ->
                        cardService.getDeckByIdByCardId("card123", "deck1")
                ).isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Card not found");

        Mockito.verify(cardRepository).findById("card123");
        Mockito.verifyNoInteractions(deckClient);
    }
}
