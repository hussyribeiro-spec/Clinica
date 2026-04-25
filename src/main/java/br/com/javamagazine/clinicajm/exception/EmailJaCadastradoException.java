package br.com.javamagazine.clinicajm.exception;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException() {
        super("E-mail já cadastrado.");
    }
}
