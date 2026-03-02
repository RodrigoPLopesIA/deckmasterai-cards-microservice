package com.deckmasterai.cards.services;


import com.deckmasterai.cards.client.DeckClient;
import com.deckmasterai.cards.dto.CardRequest;
import com.deckmasterai.cards.dto.CardResponse;
import com.deckmasterai.cards.exceptions.NotFoundException;
import com.deckmasterai.cards.exceptions.UnauthorizedException;
import com.deckmasterai.cards.mapper.CardMapper;
import com.deckmasterai.cards.models.Card;
import com.deckmasterai.cards.repository.CardRepository;
import com.deckmasterai.cards.strategies.FileStorageStrategy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private DeckClient deckClient;

    @Mock
    private FileStorageStrategy storageStrategy;

    @InjectMocks
    private CardService cardService;

    private Card card;
    private CardRequest cardRequest;
    private CardResponse cardResponse;
    private String profileId;

    @BeforeEach
    void setUp() {
        profileId = "profile-123";

        card = Card.builder()
                .id("card-1")
                .name("Blue-Eyes")
                .profileId(profileId)
                .deckIds(List.of("deck1", "deck2"))
                .imageUrl("old-image.png")
                .build();

        cardRequest = Mockito.mock(CardRequest.class);

        cardResponse = Mockito.mock(CardResponse.class);
    }

    // =========================================
    // CREATE
    // =========================================

    @Test
    void shouldCreateCard() {

        // Arrange
        Mockito.when(cardMapper.cardRequestToCard(cardRequest))
                .thenReturn(card);

        Mockito.when(cardRepository.save(card))
                .thenReturn(card);

        Mockito.when(cardMapper.cardToCardResponse(card))
                .thenReturn(cardResponse);

        // Act
        var result = cardService.create(cardRequest, profileId);

        // Assert
        Assertions.assertThat(result).isEqualTo(cardResponse);
        Mockito.verify(cardRepository).save(card);
        Assertions.assertThat(card.getProfileId()).isEqualTo(profileId);
    }

    // =========================================
    // UPDATE
    // =========================================

    @Test
    void shouldUpdateCard() {

        // Arrange
        Mockito.when(cardRepository.findById("card-1"))
                .thenReturn(Optional.of(card));

        Mockito.when(cardMapper.updateCardFromRequest(cardRequest, card))
                .thenReturn(card);

        Mockito.when(cardRepository.save(card))
                .thenReturn(card);

        Mockito.when(cardMapper.cardToCardResponse(card))
                .thenReturn(cardResponse);

        // Act
        var result = cardService.update("card-1", cardRequest, profileId);

        // Assert
        Assertions.assertThat(result).isEqualTo(cardResponse);
        Mockito.verify(cardRepository).save(card);
    }

    @Test
    void shouldThrowUnauthorizedWhenUpdatingOtherUserCard() {

        // Arrange
        card.setProfileId("another-profile");

        Mockito.when(cardRepository.findById("card-1"))
                .thenReturn(Optional.of(card));

        // Act + Assert
        Assertions.assertThatThrownBy(() ->
                cardService.update("card-1", cardRequest, profileId)
        ).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void shouldThrowNotFoundWhenUpdating() {

        Mockito.when(cardRepository.findById("card-1"))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() ->
                cardService.update("card-1", cardRequest, profileId)
        ).isInstanceOf(NotFoundException.class);
    }

    // =========================================
    // GET BY ID
    // =========================================

    @Test
    void shouldReturnCardById() {

        Mockito.when(cardRepository.findByIdAndProfileId("card-1", profileId))
                .thenReturn(Optional.of(card));

        Mockito.when(cardMapper.cardToCardResponse(card))
                .thenReturn(cardResponse);

        var result = cardService.getCardById("card-1", profileId);

        Assertions.assertThat(result).isEqualTo(cardResponse);
    }

    @Test
    void shouldThrowNotFoundWhenGettingById() {

        Mockito.when(cardRepository.findByIdAndProfileId("card-1", profileId))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() ->
                cardService.getCardById("card-1", profileId)
        ).isInstanceOf(NotFoundException.class);
    }

    // =========================================
    // DELETE
    // =========================================

    @Test
    void shouldDeleteCard() {

        Mockito.when(cardRepository.findById("card-1"))
                .thenReturn(Optional.of(card));

        cardService.delete("card-1", profileId);

        Mockito.verify(cardRepository).deleteByIdAndProfileId("card-1", profileId);
    }

    // =========================================
    // GET DECKS
    // =========================================

    @Test
    void shouldReturnDecksByCardId() {

        Mockito.when(cardRepository.findById("card-1"))
                .thenReturn(Optional.of(card));

        Mockito.when(deckClient.getDecksByCardId("card-1"))
                .thenReturn(List.of("deck1", "deck2"));

        var result = cardService.getDecksByCardId("card-1", profileId);

        Assertions.assertThat(result).hasSize(2);
    }

    @Test
    void shouldThrowUnauthorizedWhenGettingDecks() {

        card.setProfileId("other");

        Mockito.when(cardRepository.findById("card-1"))
                .thenReturn(Optional.of(card));

        Assertions.assertThatThrownBy(() ->
                cardService.getDecksByCardId("card-1", profileId)
        ).isInstanceOf(UnauthorizedException.class);
    }

    // =========================================
    // GET SPECIFIC DECK
    // =========================================

    @Test
    void shouldReturnSpecificDeck() {

        Mockito.when(cardRepository.findById("card-1"))
                .thenReturn(Optional.of(card));

        Mockito.when(deckClient.getDeckByIdByCardId("card-1", "deck1"))
                .thenReturn("deck1-details");

        var result = cardService.getDeckByIdByCardId("card-1", "deck1", profileId);

        Assertions.assertThat(result).isEqualTo("deck1-details");
    }

    @Test
    void shouldThrowWhenDeckNotFound() {

        Mockito.when(cardRepository.findById("card-1"))
                .thenReturn(Optional.of(card));

        Assertions.assertThatThrownBy(() ->
                cardService.getDeckByIdByCardId("card-1", "deckX", profileId)
        ).isInstanceOf(NotFoundException.class);
    }

    // =========================================
    // UPLOAD IMAGE
    // =========================================

    @Test
    void shouldUploadImage() {

        MultipartFile file = Mockito.mock(MultipartFile.class);

        Mockito.when(file.isEmpty()).thenReturn(false);

        Mockito.when(cardRepository.findById("card-1"))
                .thenReturn(Optional.of(card));

        Mockito.when(storageStrategy.upload(file))
                .thenReturn("new-image.png");

        Mockito.when(cardRepository.save(card))
                .thenReturn(card);

        Mockito.when(cardMapper.cardToCardResponse(card))
                .thenReturn(cardResponse);

        var result = cardService.uploadCardImage("card-1", file);

        Assertions.assertThat(result).isEqualTo(cardResponse);

        Mockito.verify(storageStrategy).upload(file);
        Mockito.verify(cardRepository).save(card);
    }
}
