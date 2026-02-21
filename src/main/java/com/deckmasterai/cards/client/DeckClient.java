package com.deckmasterai.cards.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient(name = "deck-service", url = "http://localhost:8081")
public interface DeckClient {
    
    @GetMapping("/api/v1/card/{cardId}/decks")
    public List<String> getDecksByCardId(@PathVariable String cardId);


    @GetMapping("/api/v1/card/{cardId}/decks/{deckId}")
    public String getDeckByIdByCardId(@PathVariable String cardId, @PathVariable String deckId);
}
