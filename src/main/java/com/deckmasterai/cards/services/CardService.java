package com.deckmasterai.cards.services;


import com.deckmasterai.cards.client.DeckClient;
import com.deckmasterai.cards.dto.CardRequest;
import com.deckmasterai.cards.dto.CardResponse;
import com.deckmasterai.cards.models.Card;
import com.deckmasterai.cards.repository.CardRepository;
import com.deckmasterai.mapper.CardMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;


    private final CardMapper cardMapper;

    private final DeckClient deckClient;
    public Page<CardResponse> getCards(Pageable pageable) {
        return cardRepository.findAll(pageable).map(cardMapper::cardToCardResponse);
    }

    public CardResponse create(CardRequest cardRequest){
        var cardMapped = cardMapper.cardRequestToCard(cardRequest);
        Card saved = cardRepository.save(cardMapped);
        return cardMapper.cardToCardResponse(saved);
    }
    
    public CardResponse getCardById(String id) {
        return cardRepository.findById(id)
                .map(cardMapper::cardToCardResponse)
                .orElseThrow(() -> new RuntimeException("Card not found"));
    }

    public CardResponse update(String id, CardRequest cardRequest) {
        var cardEntity = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        var updatedCard = cardMapper.updateCardFromRequest(cardRequest, cardEntity);
        var savedCard = cardRepository.save(updatedCard);
        return cardMapper.cardToCardResponse(savedCard);
    }

    public void delete(String id) {
        cardRepository.deleteById(id);
    }

    public Object getDecksByCardId(String id) {
        var card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        return deckClient.getDecksByCardId(card.getId());
    }
}
