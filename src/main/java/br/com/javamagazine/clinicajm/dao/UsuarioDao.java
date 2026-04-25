package br.com.javamagazine.clinicajm.dao;

import br.com.javamagazine.clinicajm.domain.Usuario;
import java.util.Optional;

public interface UsuarioDao {
    void salvar(Usuario usuario);
    Optional<Usuario> buscarPorEmail(String email);
}
