package com.deckmasterai.cards.controller;


import com.deckmasterai.cards.models.Card;
import com.deckmasterai.cards.services.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/cards")
@RestController
@RequiredArgsConstructor
public class CardsController {


    private final CardService cardService;
    @GetMapping
    public ResponseEntity<List<Card>> index() {

        var cards = cardService.getCards();
        return ResponseEntity.ok().body(cards);
    }

    // POST /cards
    @PostMapping
    public ResponseEntity<Card> createCard(@RequestBody Card card) {
        var cardCreated = this.cardService.create(card);
        return ResponseEntity.ok().body(cardCreated);
    }

    // GET /cards/{id}
    @GetMapping("/{id}")
    public ResponseEntity getCardById(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }

    // PUT /cards/{id}
    @PutMapping("/{id}")
    public ResponseEntity updateCard(@PathVariable String id,
                                           @RequestBody String updatedCard) {

        return ResponseEntity.ok().build();
    }

    // DELETE /cards/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCard(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }

}
