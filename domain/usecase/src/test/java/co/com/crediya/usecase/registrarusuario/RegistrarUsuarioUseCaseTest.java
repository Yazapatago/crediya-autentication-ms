package co.com.crediya.usecase.registrarusuario;

import co.com.crediya.model.exception.BusinessException;
import co.com.crediya.model.exception.ErrorCode;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegistrarUsuarioUseCaseTest {

    private UserRepository userRepository;
    private RegistrarUsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        useCase = new RegistrarUsuarioUseCase(userRepository);
    }

    private static User usuarioValido() {
        return User.builder()
                .nombre(" Juan ")
                .apellido(" Pérez ")
                .email(" Juan.Perez@Correo.com ")
                .salarioBase(new BigDecimal("3500000"))
                .build();
    }

    @Test
    void registraUsuarioValido() {
        when(userRepository.existsByEmail("juan.perez@correo.com")).thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> ((User) invocation.getArgument(0)).toBuilder().id(1L).build());

        User resultado = useCase.registrar(usuarioValido());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User guardado = captor.getValue();
        assertNull(guardado.getId());
        assertEquals("Juan", guardado.getNombre());
        assertEquals("Pérez", guardado.getApellido());
        assertEquals("juan.perez@correo.com", guardado.getEmail());
        assertEquals(1L, resultado.getId());
    }

    @Test
    void fallaSiElEmailYaEstaRegistrado() {
        when(userRepository.existsByEmail("juan.perez@correo.com")).thenReturn(true);
        User usuario = usuarioValido();

        BusinessException ex = assertThrows(BusinessException.class, () -> useCase.registrar(usuario));

        assertEquals(ErrorCode.EMAIL_ALREADY_REGISTERED, ex.getErrorCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void fallaSiElUsuarioEsNulo() {
        BusinessException ex = assertThrows(BusinessException.class, () -> useCase.registrar(null));
        assertEquals(ErrorCode.REQUIRED_FIELD, ex.getErrorCode());
    }

    @Test
    void fallaSiElNombreEstaVacio() {
        assertError(usuarioValido().toBuilder().nombre("  ").build(), ErrorCode.REQUIRED_FIELD);
    }

    @Test
    void fallaSiElApellidoEsNulo() {
        assertError(usuarioValido().toBuilder().apellido(null).build(), ErrorCode.REQUIRED_FIELD);
    }

    @Test
    void fallaSiElEmailEsNulo() {
        assertError(usuarioValido().toBuilder().email(null).build(), ErrorCode.REQUIRED_FIELD);
    }

    @Test
    void fallaSiElEmailTieneFormatoInvalido() {
        assertError(usuarioValido().toBuilder().email("correo-invalido").build(), ErrorCode.INVALID_FORMAT);
    }

    @Test
    void fallaSiElSalarioEsNulo() {
        assertError(usuarioValido().toBuilder().salarioBase(null).build(), ErrorCode.REQUIRED_FIELD);
    }

    @Test
    void fallaSiElSalarioEsNegativo() {
        assertError(usuarioValido().toBuilder().salarioBase(new BigDecimal("-1")).build(),
                ErrorCode.SALARY_OUT_OF_RANGE);
    }

    @Test
    void fallaSiElSalarioSuperaElMaximo() {
        assertError(usuarioValido().toBuilder().salarioBase(new BigDecimal("15000000.01")).build(),
                ErrorCode.SALARY_OUT_OF_RANGE);
    }

    private void assertError(User usuario, ErrorCode esperado) {
        BusinessException ex = assertThrows(BusinessException.class, () -> useCase.registrar(usuario));
        assertEquals(esperado, ex.getErrorCode());
        verify(userRepository, never()).save(any());
    }
}
