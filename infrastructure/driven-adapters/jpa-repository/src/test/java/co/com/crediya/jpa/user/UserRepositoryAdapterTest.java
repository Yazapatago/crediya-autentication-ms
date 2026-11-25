package co.com.crediya.jpa.user;

import co.com.crediya.model.exception.BusinessException;
import co.com.crediya.model.exception.ErrorCode;
import co.com.crediya.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserRepositoryAdapterTest {

    private UserDataRepository userDataRepository;
    private UserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        userDataRepository = mock(UserDataRepository.class);
        adapter = new UserRepositoryAdapter(userDataRepository);
    }

    private static User usuario() {
        return User.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@correo.com")
                .salarioBase(new BigDecimal("3500000"))
                .build();
    }

    @Test
    void guardaYRetornaElUsuarioConId() {
        when(userDataRepository.saveAndFlush(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setId(10L);
            return entity;
        });

        User resultado = adapter.save(usuario());

        assertEquals(10L, resultado.getId());
        assertEquals("juan.perez@correo.com", resultado.getEmail());
    }

    @Test
    void convierteViolacionDeIntegridadEnErrorDeNegocio() {
        when(userDataRepository.saveAndFlush(any(UserEntity.class)))
                .thenThrow(new DataIntegrityViolationException("duplicado"));
        User usuario = usuario();

        BusinessException ex = assertThrows(BusinessException.class, () -> adapter.save(usuario));

        assertEquals(ErrorCode.EMAIL_ALREADY_REGISTERED, ex.getErrorCode());
    }

    @Test
    void consultaSiExisteElEmail() {
        when(userDataRepository.existsByEmail("juan.perez@correo.com")).thenReturn(true);

        assertTrue(adapter.existsByEmail("juan.perez@correo.com"));
    }
}
