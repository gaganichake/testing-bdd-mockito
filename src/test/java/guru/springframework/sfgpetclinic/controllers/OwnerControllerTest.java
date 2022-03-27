package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.fauxspring.Model;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

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

    // Combining the Argument Captor with Will Answer functionality of Mockito
    @BeforeEach()
    void setup() {
        given(ownerService.findAllByLastNameLike(stringArgumentCaptor.capture())).willAnswer(
                invocation -> {
                    List<Owner> ownerList = new ArrayList<>();
                    String name = invocation.getArgument(0);

                    if(name.equals("%Buck%")) {
                        ownerList.add(new Owner(1L, "Joe", "Buck"));
                        return ownerList;
                    } else if(name.equals("%NotFound%")) {
                        return ownerList;
                    } else if (name.equals("%Found%")) {
                        ownerList.add(new Owner(1L, "Joe", "Buck"));
                        ownerList.add(new Owner(2L, "Jim", "Tim"));
                        return ownerList;
                    }

                    throw new RuntimeException("Invalid Argument");
                });
    }

    @Test
    void processFindFormWildCardFoundOne() {
        //given
        List<Owner> ownerList = new ArrayList<>();

        //when
        String view = controller.processFindForm(new Owner(1L, "Joe", "Buck"), bindingResult, null);

        //then
        then(ownerService).should().findAllByLastNameLike(anyString());
        assertThat("%Buck%").isEqualTo(stringArgumentCaptor.getValue());// Assert changing value of an argument (Last name) using ArgumentCaptor
        assertThat("redirect:/owners/1").isEqualTo(view);
    }

    @Test
    void processFindFormWildCardNotFound() {
        //given
        List<Owner> ownerList = new ArrayList<>();

        //when
        String view = controller.processFindForm(new Owner(1L, "Joe", "NotFound"), bindingResult, null);

        //then
        then(ownerService).should().findAllByLastNameLike(anyString());
        assertThat("%NotFound%").isEqualTo(stringArgumentCaptor.getValue());// Assert changing value of an argument (Last name) using ArgumentCaptor
        assertThat("owners/findOwners").isEqualTo(view);
    }

    @Test
    void processFindFormWildCardFound() {
        //given
        List<Owner> ownerList = new ArrayList<>();

        //when
        String view = controller.processFindForm(new Owner(1L, "Joe", "Found"), bindingResult, Mockito.mock(Model.class));// Adding inline mock object

        //then
        then(ownerService).should().findAllByLastNameLike(anyString());
        assertThat("%Found%").isEqualTo(stringArgumentCaptor.getValue());// Assert changing value of an argument (Last name) using ArgumentCaptor
        assertThat("owners/ownersList").isEqualTo(view);
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