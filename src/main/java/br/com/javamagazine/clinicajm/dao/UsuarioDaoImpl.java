package br.com.javamagazine.clinicajm.dao;

import br.com.javamagazine.clinicajm.domain.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioDaoImpl implements UsuarioDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void salvar(Usuario usuario) {
        em.persist(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        try {
            Usuario usuario = em.createQuery(
                            "select u from Usuario u where u.email = :email", Usuario.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(usuario);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
