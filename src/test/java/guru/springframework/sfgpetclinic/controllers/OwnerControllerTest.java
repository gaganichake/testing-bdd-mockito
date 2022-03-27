package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.NoMoreInteractions;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    private static final String OWNERS_CREATE_OR_UPDATE_OWNER_FORM = "owners/createOrUpdateOwnerForm";
    private static final String REDIRECT_OWNERS_5 = "redirect:/owners/5";
    @Mock
    OwnerService ownerService;

    @Mock
    BindingResult bindingResult;

    @InjectMocks
    OwnerController controller;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    // Inline ArgumentCaptor
    @Test
    void processFindFormWildCardString() {
        //given
        List<Owner> ownerList = new ArrayList<>();
        ownerList.add(new Owner(1L, "Joe", "Buck"));
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        given(ownerService.findAllByLastNameLike(captor.capture())).willReturn(ownerList);

        //when
        String view = controller.processFindForm(new Owner(1L, "Joe", "Buck"), bindingResult, null);

        //then
        then(ownerService).should().findAllByLastNameLike(anyString());
        assertThat("%Buck%").isEqualTo(captor.getValue());// Assert changing value of an argument (Last name) using ArgumentCaptor
    }

    // ArgumentCaptor Using Annotation
    @Test
    void processFindFormWildCardStringAnnotation() {
        //given
        List<Owner> ownerList = new ArrayList<>();
        ownerList.add(new Owner(1L, "Joe", "Buck"));
        given(ownerService.findAllByLastNameLike(stringArgumentCaptor.capture())).willReturn(ownerList);

        //when
        String view = controller.processFindForm(new Owner(1L, "Joe", "Buck"), bindingResult, null);

        //then
        then(ownerService).should().findAllByLastNameLike(anyString());
        assertThat("%Buck%").isEqualTo(stringArgumentCaptor.getValue());// Assert changing value of an argument (Last name) using ArgumentCaptor
    }

    @Test
    void processCreationFormHasErrors() {
        //given
        given(bindingResult.hasErrors()).willReturn(true);

        //when
        String view = controller.processCreationForm(new Owner(1L, "Joe", "Now"), bindingResult);

        //then
        then(ownerService).should(never()).save(any(Owner.class));
        assertThat(view).isEqualTo(OWNERS_CREATE_OR_UPDATE_OWNER_FORM);
    }

    @Test
    void processCreationForm() {
        //given
        Owner owner = new Owner(5L, "Joe", "Now");
        given(bindingResult.hasErrors()).willReturn(false);
        given(ownerService.save(any())).willReturn(owner);

        //when
        String view = controller.processCreationForm(new Owner(5L, "Joe", "Now"), bindingResult);

        //then
        then(ownerService).should().save(any(Owner.class));
        assertThat(view).isEqualTo(REDIRECT_OWNERS_5);
    }
}