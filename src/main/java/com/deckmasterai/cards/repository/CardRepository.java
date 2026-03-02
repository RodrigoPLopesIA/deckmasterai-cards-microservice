package com.deckmasterai.cards.repository;

import com.deckmasterai.cards.models.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.nio.channels.FileChannel;
import java.util.Optional;

public interface CardRepository extends MongoRepository<Card, String> {

    Page<Card> findByProfileId(String profileId, Pageable pageable);

    Optional<Card> findByIdAndProfileId(String id, String profileId);

    void deleteByIdAndProfileId(String id, String profileId);
}
