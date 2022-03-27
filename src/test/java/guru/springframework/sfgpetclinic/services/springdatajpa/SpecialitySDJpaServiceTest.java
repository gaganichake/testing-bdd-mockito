package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Speciality;
import guru.springframework.sfgpetclinic.repositories.SpecialtyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialitySDJpaServiceTest {

    @Mock(lenient = true)
    SpecialtyRepository specialtyRepository;

    @InjectMocks
    SpecialitySDJpaService service;

    @Test
    void testDeleteByObject() {
        //given
        Speciality speciality = new Speciality();

        //when
        service.delete(speciality);

        //then
        then(specialtyRepository).should().delete(any(Speciality.class));
    }

    // Verify interactions complete within as specified time with Mockito timeout
    @Test
    void testDeleteByObjectTimeout() {
        //given
        Speciality speciality = new Speciality();

        //when
        service.delete(speciality);

        //then
        then(specialtyRepository).should(timeout(5000)).delete(any(Speciality.class));
    }

    @Test
    void findByIdTest() {
        //given
        given(specialtyRepository.findById(1L)).willReturn(Optional.of(new Speciality()));

        //when
        Speciality foundSpecialty = service.findById(1L);

        //then
        assertThat(foundSpecialty).isNotNull();
        then(specialtyRepository).should().findById(anyLong());
        then(specialtyRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void deleteById() {
        //given
        Long id = 1L;

        //when
        service.deleteById(id);
        service.deleteById(id);

        //then
        then(specialtyRepository).should(times(2)).deleteById(1L);
    }

    @Test
    void deleteByIdAtLeast() {
        //give
        Long id = 1L;

        //when
        service.deleteById(1L);
        service.deleteById(1L);

        //then
        then(specialtyRepository).should(atLeastOnce()).deleteById(1L);
    }

    @Test
    void deleteByIdAtMost() {
        //give
        Long id = 1L;

        //when
        service.deleteById(1L);
        service.deleteById(1L);

        //then
        then(specialtyRepository).should(atMost(5)).deleteById(1L);
    }

    @Test
    void deleteByIdNever() {
        //give
        Long id = 1L;

        //when
        service.deleteById(1L);
        service.deleteById(1L);

        //then
        then(specialtyRepository).should(atLeastOnce()).deleteById(1L);
        then(specialtyRepository).should(never()).deleteById(5L);
    }

    @Test
    void testDelete() {
        //given
        Speciality speciality = new Speciality();

        //when
        service.delete(new Speciality());

        //then
        then(specialtyRepository).should().delete(any());
    }

    // Throwing Exception with Mockito where the method has some return type - BDD style
    @Test
    void findByIdThrowsBDDTest(){
        //given
        given(specialtyRepository.findById(1L)).willThrow(new RuntimeException());

        //given + when
        assertThrows(RuntimeException.class, () -> service.findById(1L));

        //then
        then(specialtyRepository).should().findById(1L);
    }

    // Throwing Exception with Mockito where the method has a void return type - BDD style
    @Test
    void testDeleteBDD(){
        //given(specialtyRepository.delete(any())).willThrow(new RuntimeException());// try this
        willThrow(new RuntimeException("boom")).given(specialtyRepository).delete(any());


        assertThrows(RuntimeException.class, () -> service.delete(new Speciality()));

        then(specialtyRepository).should().delete(any());
    }

    // Throwing Exception with Mockito where the method has a void return type - Normal TDD style
    @Test
    void testDeleteDoThrow(){
        doThrow(new RuntimeException("boom")).when(specialtyRepository).delete(any());

        assertThrows(RuntimeException.class, () -> service.delete(new Speciality()));

        verify(specialtyRepository).delete(any());
    }

    @Test
    void testSaveLamda(){
        //give
        final String MATCH_ME = "MATCH_ME";
        Speciality savedSpeciality = new Speciality();
        savedSpeciality.setId(1L);
        // Need mock to only return on match MATCH_ME string
        given(specialtyRepository.save(argThat(argument -> argument.getDescription().equals(MATCH_ME)))).willReturn(savedSpeciality);

        //when
        Speciality speciality = new Speciality();
        speciality.setDescription(MATCH_ME);
        Speciality returnedSpeciality = service.save(speciality);

        //then
        assertThat(returnedSpeciality.getId()).isEqualTo(1L);
    }

    @Test
    void testSaveLamdaNotMatch(){
        //give
        final String MATCH_ME = "MATCH_ME";
        Speciality savedSpeciality = new Speciality();
        savedSpeciality.setId(1L);
        // Need mock to only return on match MATCH_ME string
        given(specialtyRepository.save(argThat(argument -> argument.getDescription().equals(MATCH_ME)))).willReturn(savedSpeciality);

        //when
        Speciality speciality = new Speciality();
        speciality.setDescription("MATCH_ME_NOT");
        Speciality returnedSpeciality = service.save(speciality);

        //then
        assertNull(returnedSpeciality); // will return null object because the String will not match
        // Also see @Mock(lenient = true)
    }

}