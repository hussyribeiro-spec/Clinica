package br.com.javamagazine.clinicajm.controller;

import br.com.javamagazine.clinicajm.domain.Usuario;
import br.com.javamagazine.clinicajm.domain.dto.UsuarioCadastroDTO;
import br.com.javamagazine.clinicajm.domain.dto.UsuarioRespostaDTO;
import br.com.javamagazine.clinicajm.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioRespostaDTO> cadastrar(@Valid @RequestBody UsuarioCadastroDTO dto) {
        Usuario usuario = usuarioService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioRespostaDTO(usuario));
    }
}
