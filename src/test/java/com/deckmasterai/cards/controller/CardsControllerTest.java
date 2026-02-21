package com.deckmasterai.cards.controller;

import com.deckmasterai.cards.dto.CardRequest;
import com.deckmasterai.cards.dto.CardResponse;
import com.deckmasterai.cards.enums.CardType;
import com.deckmasterai.cards.enums.MonsterSubType;
import com.deckmasterai.cards.enums.MonsterType;
import com.deckmasterai.cards.models.Card;
import com.deckmasterai.cards.services.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardsController.class)
class CardsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @Autowired
    private ObjectMapper objectMapper;

    CardRequest request;
    CardResponse response;
    Card card;
    MockMultipartFile file;
    String profileId;
    String cardId;
    @BeforeEach
    void setUp() {
        cardId = "card-1";
        request = CardRequest.builder()
                .name("Dragão Branco de Olhos Azuis")
                .type(CardType.MONSTER)                .attribute("LUZ")
                .level(8)
                .attack(3000)
                .defense(2500)
                .profileId("user-123")
                .imageUrl("https://deckmasterai.com/images/blue-eyes.jpg")
                .description("Um dragão lendário com poder devastador.")
                .monsterType(MonsterType.DRAGON)
                .monsterSubTypes(List.of(MonsterSubType.NORMAL))
                .build();
        response = CardResponse.builder()
                .id("card-1")
                .name("Dragão Branco de Olhos Azuis")
                .type(CardType.MONSTER)                .attribute("LUZ")
                .level(8)
                .attack(3000)
                .defense(2500)
                .profileId("user-123")
                .imageUrl("https://deckmasterai.com/images/blue-eyes.jpg")
                .description("Um dragão lendário com poder devastador.")
                .monsterType(MonsterType.DRAGON)
                .monsterSubTypes(List.of(MonsterSubType.NORMAL))
                .build();
        file = new MockMultipartFile(
                "file",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image-content".getBytes()
        );
        profileId = "user-123";
    }
    @Test
    @DisplayName("Should return paginated cards")
    @WithMockUser
    void shouldReturnCards() throws Exception {


        when(cardService.getCards(any(), eq(profileId))).thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/v1/cards")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(profileId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(response.id()))
                .andExpect(jsonPath("$.content[0].name").value(response.name()));

        verify(cardService).getCards(any(), eq(profileId));
    }

    @Test
    @DisplayName("Should create a card")
    @WithMockUser
    void shouldCreateCard() throws Exception {


        when(cardService.create(any(CardRequest.class), eq(profileId))).thenReturn(response);

        mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(profileId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.name").value(response.name()));

        verify(cardService).create(any(CardRequest.class), eq(profileId));
    }

    @Test
    @DisplayName("Should update a card")
    @WithMockUser
    void shouldUpdateCard() throws Exception {

        when(cardService.update(eq(cardId), any(CardRequest.class), eq(profileId)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/cards/card-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(profileId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.name").value(response.name()));

        verify(cardService).update(eq(cardId), any(CardRequest.class), eq(profileId));
    }

    @Test
    @DisplayName("Should delete a card")
    @WithMockUser
    void shouldDeleteCard() throws Exception {
        doNothing().when(cardService).delete(cardId, profileId);

        mockMvc.perform(delete("/api/v1/cards/card-1")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(profileId))))
                .andExpect(status().isNoContent());

        verify(cardService).delete(cardId, profileId);
    }

    @Test
    @DisplayName("Should upload card image")
    @WithMockUser
    void shouldUploadCardImage() throws Exception {
        when(cardService.uploadCardImage(eq(cardId), any())).thenReturn(response);

        mockMvc.perform(multipart("/api/v1/cards/upload/card-1")
                        .file(file)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(profileId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId));

        verify(cardService).uploadCardImage(eq(cardId), any());
    }
}