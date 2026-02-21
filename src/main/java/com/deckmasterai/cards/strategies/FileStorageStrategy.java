package com.deckmasterai.cards.strategies;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageStrategy {

    String upload(MultipartFile file);

    void delete(String fileName);
}
