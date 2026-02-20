package com.deckmasterai.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.deckmasterai.cards.dto.CardRequest;
import com.deckmasterai.cards.dto.CardResponse;
import com.deckmasterai.cards.models.Card;


@Mapper
public interface CardMapper {
    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);
    
    CardResponse cardToCardResponse(Card card);
    Card cardRequestToCard(CardRequest cardRequest);
    Card cardResponseToCard(CardResponse cardResponse);
    CardRequest cardToCardRequest(Card card);

    Card updateCardFromRequest(CardRequest cardRequest, Card card);
}