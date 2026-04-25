package br.com.javamagazine.clinicajm.service;

import br.com.javamagazine.clinicajm.dao.UsuarioDao;
import br.com.javamagazine.clinicajm.domain.Usuario;
import br.com.javamagazine.clinicajm.domain.dto.UsuarioCadastroDTO;
import br.com.javamagazine.clinicajm.exception.EmailJaCadastradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private UsuarioDao usuarioDao;

    @Override
    public Usuario salvar(UsuarioCadastroDTO dto) {
        usuarioDao.buscarPorEmail(dto.getEmail()).ifPresent(u -> {
            throw new EmailJaCadastradoException();
        });

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(encoder.encode(dto.getSenha()));

        usuarioDao.salvar(usuario);
        return usuario;
    }
}
