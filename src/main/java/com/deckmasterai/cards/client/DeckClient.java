package com.deckmasterai.cards.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient(name = "deck-service", url = "http://localhost:8081")
public interface DeckClient {
    
    @GetMapping("/api/v1/card/{cardId}/decks")
    public String getDecksByCardId(String cardId);
}
