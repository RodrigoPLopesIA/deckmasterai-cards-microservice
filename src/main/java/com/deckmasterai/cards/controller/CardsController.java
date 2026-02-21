package com.deckmasterai.cards.controller;

import com.deckmasterai.cards.dto.CardRequest;
import com.deckmasterai.cards.dto.CardResponse;
import com.deckmasterai.cards.services.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RequestMapping("api/v1/cards")
@RestController
@RequiredArgsConstructor
public class CardsController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<Page<CardResponse>> index(Pageable pageable, @AuthenticationPrincipal Jwt jwt) {

        var cards = cardService.getCards(pageable, jwt.getSubject());
        return ResponseEntity.ok().body(cards);
    }

    @PostMapping("/upload/{cardId}")
    public ResponseEntity<CardResponse> uploadCardImage(
            @PathVariable String cardId,
            @RequestParam("file") MultipartFile file) {
        var updatedCard = cardService.uploadCardImage(cardId, file);
        return ResponseEntity.ok().body(updatedCard);
    }
    
    // POST /cards
    @PostMapping()
    public ResponseEntity<CardResponse> createCard(
            @RequestBody @Valid CardRequest card, @AuthenticationPrincipal Jwt jwt) {

        var cardCreated = this.cardService.create(card, jwt.getSubject());
        return ResponseEntity.ok().body(cardCreated);
    }

    // PUT /cards/{id}
    @PutMapping(value = "/{id}")
    public ResponseEntity<CardResponse> updateCard(
            @PathVariable String id,
            @RequestBody @Valid CardRequest updatedCard,
            @AuthenticationPrincipal Jwt jwt) {
        var updatedCardResponse = this.cardService.update(id, updatedCard, jwt.getSubject());
        return ResponseEntity.ok().body(updatedCardResponse);
    }

    // GET /cards/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCardById(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        var card = this.cardService.getCardById(id, jwt.getSubject());
        return ResponseEntity.ok().body(card);
    }

    // GET /cards/{id}/decks
    @GetMapping("/{id}/decks")
    public ResponseEntity<?> getDecksByCardId(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        var decks = cardService.getDecksByCardId(id, jwt.getSubject());
        return ResponseEntity.ok().body(decks);
    }

    // GET /cards/{id}/decks
    @GetMapping("/{id}/decks/{deckId}")
    public ResponseEntity<?> getDeckByIdByCardId(@PathVariable String id, @PathVariable String deckId, @AuthenticationPrincipal Jwt jwt) {
        var decks = cardService.getDeckByIdByCardId(id, deckId, jwt.getSubject());
        return ResponseEntity.ok().body(decks);
    }

    // DELETE /cards/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        this.cardService.delete(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

}
