package org.ufg.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.ufg.exception.ValidationException;

/**
 * Service centralizado para validações de regras de negócio
 * Responsável por validações genéricas e reutilizáveis
 */
@ApplicationScoped
public class ValidationService {

    /**
     * Valida se uma string é nula ou vazia
     */
    public void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " é obrigatório.");
        }
    }

    /**
     * Valida tamanho máximo de uma string
     */
    public void validateMaxLength(String value, int maxLength, String fieldName) {
        if (value != null && value.length() > maxLength) {
            throw new ValidationException(fieldName + " deve ter no máximo " + maxLength + " caracteres.");
        }
    }

    /**
     * Valida tamanho mínimo de uma string
     */
    public void validateMinLength(String value, int minLength, String fieldName) {
        if (value != null && value.length() < minLength) {
            throw new ValidationException(fieldName + " deve ter no mínimo " + minLength + " caracteres.");
        }
    }

    /**
     * Valida se um email tem formato válido
     */
    public void validateEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("E-mail é obrigatório.");
        }
        
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            throw new ValidationException("O e-mail ' " + email + " ' é inválido.");
        }
    }

    /**
     * Valida força da senha
     * Requer: maiúscula, minúscula, dígito, caractere especial e mínimo 8 caracteres
     */
    public void validatePasswordStrength(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("A senha é obrigatória.");
        }

        if (password.length() < 8) {
            throw new ValidationException("A senha deve ter no mínimo 8 caracteres.");
        }

        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[@$!%*?&].*");

        if (!hasUppercase || !hasLowercase || !hasDigit || !hasSpecialChar) {
            throw new ValidationException(
                "A senha deve conter maiúsculas, minúsculas, números e caracteres especiais (@$!%*?&)."
            );
        }
    }

    /**
     * Valida formato de WhatsApp brasileiro
     * Formato: (XX) 9XXXX-XXXX ou XXXXXXXXXXX
     */
    public void validateWhatsAppFormat(String whatsapp) {
        if (whatsapp == null || whatsapp.trim().isEmpty()) {
            return; // WhatsApp é opcional
        }

        String whatsappRegex = "^(\\(\\d{2}\\)\\s?9?\\d{4}-?\\d{4}|\\d{10,11})$";
        if (!whatsapp.matches(whatsappRegex)) {
            throw new ValidationException(
                "O número de WhatsApp '" + whatsapp + "' é inválido. Use o formato (XX) 9XXXX-XXXX."
            );
        }
    }

    /**
     * Valida se um valor é nulo
     */
    public void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " é obrigatório.");
        }
    }

    /**
     * Valida níveis de acesso permitidos
     */
    public void validateAccessLevelFormat(String nivelAcesso) {
        if (nivelAcesso == null || nivelAcesso.trim().isEmpty()) {
            return; // Pode ser preenchido por padrão
        }

        String nivel = nivelAcesso.toUpperCase();
        if (!nivel.equals("ADMIN") && !nivel.equals("CLIENTE")) {
            throw new ValidationException(
                "Nível de acesso inválido. Valores permitidos: ADMIN, CLIENTE."
            );
        }
    }

    /**
     * Valida nome de pessoa (sem números no início, comprimento)
     */
    public void validatePersonName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("O nome é obrigatório.");
        }

        if (name.length() < 3) {
            throw new ValidationException("O nome deve ter no mínimo 3 caracteres.");
        }

        if (name.length() > 100) {
            throw new ValidationException("O nome deve ter no máximo 100 caracteres.");
        }

        if (name.matches("^\\d.*")) {
            throw new ValidationException("O nome não pode começar com um número.");
        }
    }

    /**
     * Compara dois valores e verifica se são iguais
     */
    public void validateNotEqual(Object value1, Object value2, String message) {
        if (value1 != null && value2 != null && value1.equals(value2)) {
            throw new ValidationException(message);
        }
    }
}
