package com.deckmasterai.cards.services;

import com.deckmasterai.cards.client.DeckClient;
import com.deckmasterai.cards.dto.CardRequest;
import com.deckmasterai.cards.dto.CardResponse;
import com.deckmasterai.cards.exceptions.NotFoundException;
import com.deckmasterai.cards.mapper.CardMapper;
import com.deckmasterai.cards.models.Card;
import com.deckmasterai.cards.repository.CardRepository;
import com.deckmasterai.cards.strategies.FileStorageStrategy;
import com.deckmasterai.cards.strategies.MinioStorageStrategy;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    private final CardMapper cardMapper;

    private final DeckClient deckClient;
    private final FileStorageStrategy storageStrategy;

    public CardResponse create(CardRequest cardRequest) {

        var cardMapped = cardMapper.cardRequestToCard(cardRequest);

        Card saved = cardRepository.save(cardMapped);

        return cardMapper.cardToCardResponse(saved);
    }

    public CardResponse update(String id, CardRequest cardRequest) {

        var cardEntity = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        var updatedCard = cardMapper.updateCardFromRequest(cardRequest, cardEntity);


        var savedCard = cardRepository.save(updatedCard);

        return cardMapper.cardToCardResponse(savedCard);
    }

    public Page<CardResponse> getCards(Pageable pageable) {
        return cardRepository.findAll(pageable).map(cardMapper::cardToCardResponse);
    }

    public CardResponse getCardById(String id) {
        return cardRepository.findById(id)
                .map(cardMapper::cardToCardResponse)
                .orElseThrow(() -> new NotFoundException("Card not found"));
    }

    public void delete(String id) {
        cardRepository.deleteById(id);
    }

    public Object getDecksByCardId(String id) {
        var card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        return deckClient.getDecksByCardId(card.getId());
    }

    public Object getDeckByIdByCardId(String id, String deckId) {
        var card = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        Optional<String> first = card.getDeckIds().stream().filter(d -> d.equals(deckId)).findFirst();
        if (first.isEmpty()) {
            throw new NotFoundException("Deck not found for this card");
        }

        return deckClient.getDeckByIdByCardId(card.getId(), first.get());

    }

    private void handleImageUpload(Card card, MultipartFile image) {

        if (image == null || image.isEmpty()) {
            return;
        }

        if (card.getImageUrl() != null && !card.getImageUrl().isEmpty() && !card.getImageUrl().startsWith("http")) {
            storageStrategy.delete(card.getImageUrl());
        }

        String imageKey = storageStrategy.upload(image);
        card.setImageUrl(imageKey);
    }

    public CardResponse uploadCardImage(String id, MultipartFile file) {
        var card = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        handleImageUpload(card, file);

        var savedCard = cardRepository.save(card);

        return cardMapper.cardToCardResponse(savedCard);
    }
}
