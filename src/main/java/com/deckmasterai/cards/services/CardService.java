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

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final DeckClient deckClient;
    private final FileStorageStrategy storageStrategy;

    public CardResponse create(CardRequest cardRequest, String profileId) {
        var card = cardMapper.cardRequestToCard(cardRequest);
        card.setProfileId(profileId);

        var saved = cardRepository.save(card);
        return cardMapper.cardToCardResponse(saved);
    }

    public CardResponse update(String id, CardRequest request, String profileId) {

        var card = findCardById(id);

        validateOwnership(card, profileId);

        var updated = cardMapper.updateCardFromRequest(request, card);
        var saved = cardRepository.save(updated);

        return cardMapper.cardToCardResponse(saved);
    }

    public Page<CardResponse> getCards(Pageable pageable, String profileId) {
        return cardRepository
                .findByProfileId(profileId, pageable)
                .map(cardMapper::cardToCardResponse);
    }

    public CardResponse getCardById(String id, String profileId) {
        return cardRepository
                .findByIdAndProfileId(id, profileId)
                .map(cardMapper::cardToCardResponse)
                .orElseThrow(() -> new NotFoundException("Card not found"));
    }

    public void delete(String id, String profileId) {

        var card = findCardById(id);
        validateOwnership(card, profileId);

        cardRepository.deleteByIdAndProfileId(id, profileId);
    }

    public List<String> getDecksByCardId(String id, String profileId) {

        var card = findCardById(id);
        validateOwnership(card, profileId);

        return deckClient.getDecksByCardId(card.getId());
    }

    public String getDeckByIdByCardId(String id, String deckId, String profileId) {

        var card = findCardById(id);
        validateOwnership(card, profileId);

        if (card.getDeckIds() == null ||
                card.getDeckIds().stream().noneMatch(d -> d.equals(deckId))) {
            throw new NotFoundException("Deck not found for this card");
        }

        return deckClient.getDeckByIdByCardId(card.getId(), deckId);
    }

    public CardResponse uploadCardImage(String id, MultipartFile file) {

        var card = findCardById(id);

        handleImageUpload(card, file);

        var saved = cardRepository.save(card);
        return cardMapper.cardToCardResponse(saved);
    }

    // ================================
    // PRIVATE METHODS
    // ================================

    private Card findCardById(String id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card not found"));
    }

    private void validateOwnership(Card card, String profileId) {
        if (!card.getProfileId().equals(profileId)) {
            throw new UnauthorizedException("You cannot access this card");
        }
    }

    private void handleImageUpload(Card card, MultipartFile image) {

        if (image == null || image.isEmpty()) {
            return;
        }

        if (card.getImageUrl() != null &&
                !card.getImageUrl().isBlank() &&
                !card.getImageUrl().startsWith("http")) {

            storageStrategy.delete(card.getImageUrl());
        }

        String imageKey = storageStrategy.upload(image);
        card.setImageUrl(imageKey);
    }
}