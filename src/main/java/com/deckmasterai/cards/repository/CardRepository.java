package com.deckmasterai.cards.repository;

import com.deckmasterai.cards.models.Card;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CardRepository extends MongoRepository<Card, String> {
}
