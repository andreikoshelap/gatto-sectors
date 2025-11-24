package com.gatto.sector.service;

import com.gatto.sector.entity.Sector;
import com.gatto.sector.error.SectorDoesNotExistException;
import com.gatto.sector.repository.SectorRepository;
import com.gatto.sector.view.SectorView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SectorServiceTest {

    @Mock
    private SectorRepository repository;

    @InjectMocks
    private SectorService service;

    @Test
    @DisplayName("getAllSectors() maps entities to SectorView with all fields including parentId")
    void getAllSectors_mapsEntitiesToViews() {
        Sector parent = new Sector();
        parent.setId(1L);
        parent.setName("Parent");

        Sector child = new Sector();
        child.setId(2L);
        child.setName("Child");
        child.setParent(parent);

        when(repository.findAll()).thenReturn(List.of(parent, child));

        List<SectorView> views = service.getAllSectors();

        assertThat(views).hasSize(2);

        SectorView v1 = views.get(0);
        assertThat(v1.id()).isEqualTo(1L);
        assertThat(v1.name()).isEqualTo("Parent");
        assertThat(v1.parentId()).isNull();

        SectorView v2 = views.get(1);
        assertThat(v2.id()).isEqualTo(2L);
        assertThat(v2.name()).isEqualTo("Child");
        assertThat(v2.parentId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findById() returns SectorView when sector exists")
    void findById_present() {
        Sector sector = new Sector();
        sector.setId(10L);
        sector.setName("IT");

        when(repository.findById(10L)).thenReturn(Optional.of(sector));

        Optional<SectorView> result = service.findById(10L);

        assertThat(result).isPresent();
        SectorView view = result.get();
        assertThat(view.id()).isEqualTo(10L);
        assertThat(view.name()).isEqualTo("IT");
        assertThat(view.parentId()).isNull();
    }

    @Test
    @DisplayName("findById() returns empty Optional when sector doesn't exist")
    void findById_empty() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<SectorView> result = service.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("deleteById() deletes sector when it exists")
    void deleteById_deletesWhenExists() {
        Sector sector = new Sector();
        sector.setId(5L);

        when(repository.findById(5L)).thenReturn(Optional.of(sector));

        service.deleteById(5L);

        verify(repository).delete(sector);
    }

    @Test
    @DisplayName("deleteById() throws SectorDoesNotExistException when sector not found")
    void deleteById_throwsWhenNotFound() {
        when(repository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(SectorDoesNotExistException.class,
                () -> service.deleteById(123L));

        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("create() creates sector without parent and returns correct SectorView")
    void create_withoutParent() {
        Sector toSave = new Sector();
        toSave.setName("Root");

        Sector saved = new Sector();
        saved.setId(7L);
        saved.setName("Root");

        when(repository.save(any(Sector.class))).thenReturn(saved);

        SectorView input = new SectorView(null, "Root", null);

        SectorView result = service.create(input);

        // verify that save received the correct name and parent == null
        ArgumentCaptor<Sector> captor = ArgumentCaptor.forClass(Sector.class);
        verify(repository).save(captor.capture());
        Sector entityPassed = captor.getValue();
        assertThat(entityPassed.getName()).isEqualTo("Root");
        assertThat(entityPassed.getParent()).isNull();

        assertThat(result.id()).isEqualTo(7L);
        assertThat(result.name()).isEqualTo("Root");
        assertThat(result.parentId()).isNull();
    }

    @Test
    @DisplayName("create() throws SectorDoesNotExistException if parentId refers to non-existing sector")
    void create_parentNotFound() {
        SectorView input = new SectorView(null, "Child", 999L);

        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(SectorDoesNotExistException.class,
                () -> service.create(input));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("create() sets parent if parentId is valid")
    void create_withParent() {
        Sector parent = new Sector();
        parent.setId(1L);
        parent.setName("Parent");

        Sector saved = new Sector();
        saved.setId(2L);
        saved.setName("Child");
        saved.setParent(parent);

        when(repository.findById(1L)).thenReturn(Optional.of(parent));
        when(repository.save(any(Sector.class))).thenReturn(saved);

        SectorView input = new SectorView(null, "Child", 1L);

        SectorView result = service.create(input);

        ArgumentCaptor<Sector> captor = ArgumentCaptor.forClass(Sector.class);
        verify(repository).save(captor.capture());
        Sector entityPassed = captor.getValue();

        assertThat(entityPassed.getName()).isEqualTo("Child");
        assertThat(entityPassed.getParent()).isEqualTo(parent);

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.name()).isEqualTo("Child");
        assertThat(result.parentId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("update() throws SectorDoesNotExistException when id in view == null")
    void update_throwsWhenViewIdNull() {
        SectorView view = new SectorView(null, "X", null);

        assertThrows(SectorDoesNotExistException.class,
                () -> service.update(1L, view));

        verify(repository, never()).findById(any());
    }

    @Test
    @DisplayName("update() throws SectorDoesNotExistException when id in path and view mismatch")
    void update_throwsWhenIdsMismatch() {
        SectorView view = new SectorView(2L, "X", null);

        assertThrows(SectorDoesNotExistException.class,
                () -> service.update(1L, view));

        verify(repository, never()).findById(any());
    }

    @Test
    @DisplayName("update() throws SectorDoesNotExistException when entity not found in repository")
    void update_throwsWhenEntityNotFound() {
        SectorView view = new SectorView(10L, "X", null);

        when(repository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(SectorDoesNotExistException.class,
                () -> service.update(10L, view));
    }

    @Test
    @DisplayName("update() returns SectorView.fromEntity(saved) on successful update")
    void update_ok() {
        Sector existing = new Sector();
        existing.setId(5L);
        existing.setName("Old");

        Sector saved = new Sector();
        saved.setId(5L);
        saved.setName("New");

        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(saved);

        SectorView view = new SectorView(5L, "New", null);

        SectorView result = service.update(5L, view);

        verify(repository).findById(5L);
        verify(repository).save(existing);

        assertThat(result.id()).isEqualTo(5L);
        assertThat(result.name()).isEqualTo("New");
        assertThat(result.parentId()).isNull();
    }
}
