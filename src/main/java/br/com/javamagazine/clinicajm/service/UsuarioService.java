package br.com.javamagazine.clinicajm.service;

import br.com.javamagazine.clinicajm.domain.Usuario;
import br.com.javamagazine.clinicajm.domain.dto.UsuarioCadastroDTO;

public interface UsuarioService {
    Usuario salvar(UsuarioCadastroDTO dto);
}
