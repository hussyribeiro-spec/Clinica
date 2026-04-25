package br.com.javamagazine.clinicajm.service;

import br.com.javamagazine.clinicajm.dao.UsuarioDao;
import br.com.javamagazine.clinicajm.domain.Usuario;
import br.com.javamagazine.clinicajm.domain.dto.UsuarioCadastroDTO;
import br.com.javamagazine.clinicajm.exception.EmailJaCadastradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioDao usuarioDao;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UsuarioCadastroDTO dto;

    @BeforeEach
    void setUp() {
        dto = new UsuarioCadastroDTO();
        dto.setNome("João Silva");
        dto.setEmail("joao@email.com");
        dto.setSenha("senha123");
    }

    @Test
    void salvar_deveEncriptarSenha() {
        when(usuarioDao.buscarPorEmail(dto.getEmail())).thenReturn(Optional.empty());

        Usuario resultado = usuarioService.salvar(dto);

        assertThat(resultado.getSenha()).startsWith("$2a$");
        assertThat(new BCryptPasswordEncoder().matches("senha123", resultado.getSenha())).isTrue();
    }

    @Test
    void salvar_deveLancarExcecaoSeEmailDuplicado() {
        Usuario existente = new Usuario();
        existente.setEmail(dto.getEmail());
        when(usuarioDao.buscarPorEmail(dto.getEmail())).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> usuarioService.salvar(dto))
                .isInstanceOf(EmailJaCadastradoException.class)
                .hasMessageContaining("joao@email.com");
    }

    @Test
    void salvar_devePersistirERetornarUsuario() {
        when(usuarioDao.buscarPorEmail(dto.getEmail())).thenReturn(Optional.empty());

        Usuario resultado = usuarioService.salvar(dto);

        verify(usuarioDao).salvar(any(Usuario.class));
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getEmail()).isEqualTo("joao@email.com");
        assertThat(resultado.getSenha()).isNotEqualTo("senha123");
    }
}
