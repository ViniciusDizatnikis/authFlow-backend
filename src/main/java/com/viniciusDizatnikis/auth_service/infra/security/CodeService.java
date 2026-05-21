package com.viniciusDizatnikis.auth_service.infra.security;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class CodeService {

    private final SecureRandom random = new SecureRandom();

    // gera codigo raw — esse vai no email
    public String generateCode() {
        int code = random.nextInt(900000) + 100000; // garante 6 digitos
        return String.valueOf(code);
    }

    // hash do codigo — esse vai no banco
    public String hashCode(String code) {
        return DigestUtils.sha256Hex(code);
    }

    // compara codigo digitado com hash salvo
    public boolean verify(String inputCode, String savedHash) {
        return DigestUtils.sha256Hex(inputCode).equals(savedHash);
    }
}