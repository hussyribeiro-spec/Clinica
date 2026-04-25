package br.com.javamagazine.clinicajm.domain.dto;

import br.com.javamagazine.clinicajm.domain.Usuario;
import lombok.Data;

@Data
public class UsuarioRespostaDTO {

    private Long id;
    private String nome;
    private String email;

    public UsuarioRespostaDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
    }
}
