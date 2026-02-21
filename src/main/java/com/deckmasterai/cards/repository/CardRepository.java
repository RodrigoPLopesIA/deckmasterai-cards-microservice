package com.deckmasterai.cards.repository;

import com.deckmasterai.cards.models.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CardRepository extends MongoRepository<Card, String> {


    Page<Card> findAll(Pageable pageable, String profileId);

    Optional<Card> findById(String s, String profileId);

    void deleteById(String id, String profileId);
}
