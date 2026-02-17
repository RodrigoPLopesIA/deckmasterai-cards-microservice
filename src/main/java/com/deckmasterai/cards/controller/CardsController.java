package com.deckmasterai.cards.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/cards")
@RestController
public class CardsController {


    @GetMapping
    public ResponseEntity index() {
        return ResponseEntity.ok().build();
    }

    // POST /cards
    @PostMapping
    public ResponseEntity createCard(@RequestBody String card) {
        return ResponseEntity.ok().build();
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
