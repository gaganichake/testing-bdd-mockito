package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.model.Pet;
import guru.springframework.sfgpetclinic.model.Visit;
import guru.springframework.sfgpetclinic.services.PetService;
import guru.springframework.sfgpetclinic.services.VisitService;
import guru.springframework.sfgpetclinic.services.map.PetMapService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class VisitControllerTest {

    @Mock
    VisitService visitService;

//    @Mock
//    PetService petService;

    @Spy
    PetMapService petService; // A real implementation

    @InjectMocks
    VisitController visitController;

    @DisplayName("Using Spy to call real method")
    @Test()
    void loadPetWithVisit() {
        //given
        Map<String, Object> model = new HashMap<>();
        Pet pet = new Pet(1L);
        Pet pet2 = new Pet(2L);
        petService.save(pet);
        petService.save(pet2);
//        given(petService.findById(anyLong())).willReturn(pet);
        given(petService.findById(anyLong())).willCallRealMethod();

        //when
        Visit visit = visitController.loadPetWithVisit(2L, new HashMap<>());

        //then
        then(petService).should().findById(anyLong());
        assertThat(visit).isNotNull();
        assertThat(visit.getPet()).isNotNull();
        assertThat(visit.getPet().getId()).isEqualTo(2L);
        then(petService).should(times(1)).findById(anyLong());
    }

    @DisplayName("Telling Spy to mock method")
    @Test
    void loadPetWithVisitWithStubbing() {
        //given
        Map<String, Object> model = new HashMap<>();
        Pet pet = new Pet(1L);
        Pet pet2 = new Pet(2L);
        petService.save(pet);
        petService.save(pet2);
        given(petService.findById(anyLong())).willReturn(pet2); // Tell Spy object to NOT call the real method & return the desired object
//        given(petService.findById(anyLong())).willCallRealMethod();

        //when
        Visit visit = visitController.loadPetWithVisit(2L, new HashMap<>());

        //then
        then(petService).should().findById(anyLong());
        assertThat(visit).isNotNull();
        assertThat(visit.getPet()).isNotNull();
        assertThat(visit.getPet().getId()).isEqualTo(2L);
        then(petService).should(times(1)).findById(anyLong());
    }
}