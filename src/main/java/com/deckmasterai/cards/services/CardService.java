package com.deckmasterai.cards.services;


import com.deckmasterai.cards.models.Card;
import com.deckmasterai.cards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;


    public List<Card> getCards() {
        return cardRepository.findAll();
    }

    public Card create(Card card){
        return  cardRepository.save(card);
    }
}
