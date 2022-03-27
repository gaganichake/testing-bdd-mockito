package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.model.Pet;
import guru.springframework.sfgpetclinic.model.Visit;
import guru.springframework.sfgpetclinic.services.PetService;
import guru.springframework.sfgpetclinic.services.VisitService;
import guru.springframework.sfgpetclinic.services.map.PetMapService;
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

    @Test
    void loadPetWithVisit() {
        //given
        Map<String, Object> model = new HashMap<>();
        Pet pet = new Pet(1L);
        petService.save(pet);
//        given(petService.findById(anyLong())).willReturn(pet);
        given(petService.findById(anyLong())).willCallRealMethod();

        //when
        Visit visit = visitController.loadPetWithVisit(1L, new HashMap<>());

        //then
        then(petService).should().findById(anyLong());
        assertThat(visit).isNotNull();
        assertThat(visit.getPet()).isNotNull();
        assertThat(visit.getPet().getId()).isEqualTo(1L);
    }
}