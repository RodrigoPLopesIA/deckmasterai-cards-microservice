package com.deckmasterai.cards.controller;


import com.deckmasterai.cards.dto.CardRequest;
import com.deckmasterai.cards.dto.CardResponse;
import com.deckmasterai.cards.services.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RequestMapping("api/v1/cards")
@RestController
@RequiredArgsConstructor
public class CardsController {


    private final CardService cardService;
    @GetMapping
    public ResponseEntity<Page<CardResponse>> index(Pageable pageable) {

        var cards = cardService.getCards(pageable);
        return ResponseEntity.ok().body(cards);
    }

    // POST /cards
    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardRequest card) {
        var cardCreated = this.cardService.create(card);
        return ResponseEntity.ok().body(cardCreated);
    }

    // GET /cards/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCardById(@PathVariable String id) {
        var card = this.cardService.getCardById(id);
        return ResponseEntity.ok().body(card);
    }
    // GET /cards/{id}/decks
    @GetMapping("/{id}/decks")
    public ResponseEntity<?> getDecksByCardId(@PathVariable String id) {
        var decks = cardService.getDecksByCardId(id);
        return ResponseEntity.ok().body(decks);
    }

    // GET /cards/{id}/decks
    @GetMapping("/{id}/decks/{deckId}")
    public ResponseEntity<?> getDeckByIdByCardId(@PathVariable String id, @PathVariable String deckId) {
        var decks = cardService.getDeckByIdByCardId(id, deckId);
        return ResponseEntity.ok().body(decks);
    }
    

    // PUT /cards/{id}
    @PutMapping("/{id}")
    public ResponseEntity<CardResponse> updateCard(@PathVariable String id,
                                     @Valid  @RequestBody CardRequest updatedCard) {

        var updatedCardResponse = this.cardService.update(id, updatedCard);
        return ResponseEntity.ok().body(updatedCardResponse);
    }

    // DELETE /cards/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable String id) {
        this.cardService.delete(id);
        return ResponseEntity.ok().build();
    }

}
