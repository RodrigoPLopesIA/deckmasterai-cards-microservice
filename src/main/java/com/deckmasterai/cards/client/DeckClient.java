package com.deckmasterai.cards.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "deck-service", url = "http://localhost:8081")
public interface DeckClient {

    @GetMapping("/api/v1/decks")
    List<String> getDecksByCardId(@RequestParam("cardId") String cardId);


    @GetMapping("/api/v1/decks/{deckId}/card/{cardId}")
    public String getDeckByIdByCardId(@PathVariable String cardId, @PathVariable String deckId);
}
