package com.gatto.sector.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatto.sector.service.SectorService;
import com.gatto.sector.view.SectorView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SectorController.class)
class SectorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SectorService sectorService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/sectors -> 200 OK + sectors list")
    void getAll_returnsList() throws Exception {
        List<SectorView> sectors = List.of(
                new SectorView(1L, "Sector 1", null),
                new SectorView(2L, "Sector 2", 1L)
        );
        given(sectorService.getAllSectors()).willReturn(sectors);

        mockMvc.perform(get("/api/sectors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Sector 1"))
                .andExpect(jsonPath("$[1].parentId").value(1L));
    }

    @Test
    @DisplayName("GET /api/sectors/{id} -> 200 OK, if sector found")
    void getById_found() throws Exception {
        long id = 10L;
        SectorView view = new SectorView(id, "IT", null);
        given(sectorService.findById(id)).willReturn(Optional.of(view));

        mockMvc.perform(get("/api/sectors/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("IT"));
    }

    @Test
    @DisplayName("GET /api/sectors/{id} -> 404, if sector not found")
    void getById_notFound() throws Exception {
        long id = 42L;
        given(sectorService.findById(id)).willReturn(Optional.empty()); // orElseThrow() -> NoSuchElementException

        mockMvc.perform(get("/api/sectors/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/sectors/{id} -> 409, if ObjectOptimisticLockingFailureException")
    void getById_conflictOnOptimisticLock() throws Exception {
        long id = 5L;
        given(sectorService.findById(id))
                .willThrow(new ObjectOptimisticLockingFailureException("Sector", id));

        mockMvc.perform(get("/api/sectors/{id}", id))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /api/sectors/{id} -> 200 OK, successful update")
    void update_ok() throws Exception {
        long id = 7L;
        SectorView payload = new SectorView(id, "Updated", null);
        SectorView saved = new SectorView(id, "Updated", null);

        given(sectorService.update(eq(id), any(SectorView.class))).willReturn(saved);

        mockMvc.perform(put("/api/sectors/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    @DisplayName("PUT /api/sectors/{id} -> 404, if NoSuchElementException")
    void update_notFound() throws Exception {
        long id = 100L;
        SectorView payload = new SectorView(id, "Does not exist", null);

        given(sectorService.update(eq(id), any(SectorView.class)))
                .willThrow(new NoSuchElementException());

        mockMvc.perform(put("/api/sectors/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/sectors/{id} -> 409 if ObjectOptimisticLockingFailureException")
    void update_conflictOnOptimisticLock() throws Exception {
        long id = 200L;
        SectorView payload = new SectorView(id, "Conflict", null);

        given(sectorService.update(eq(id), any(SectorView.class)))
                .willThrow(new ObjectOptimisticLockingFailureException("Sector", id));

        mockMvc.perform(put("/api/sectors/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("DELETE /api/sectors/{id} -> 204 if successful delete")
    void delete_ok() throws Exception {
        long id = 11L;

        mockMvc.perform(delete("/api/sectors/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/sectors/{id} -> 404, if NoSuchElementException")
    void delete_notFound() throws Exception {
        long id = 12L;
        doThrow(new NoSuchElementException())
                .when(sectorService).deleteById(id);

        mockMvc.perform(delete("/api/sectors/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/sectors/{id} -> 409 if ObjectOptimisticLockingFailureException")
    void delete_conflictOnOptimisticLock() throws Exception {
        long id = 13L;
        doThrow(new ObjectOptimisticLockingFailureException("Sector", id))
                .when(sectorService).deleteById(id);

        mockMvc.perform(delete("/api/sectors/{id}", id))
                .andExpect(status().isConflict());
    }
}
