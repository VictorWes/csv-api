package com.csv.infra;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Converter
@Component
public class CryptoConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    private static final byte[] KEY = "MinhaChaveSecretaDe16Bytes!!".getBytes();

    @Override
    public String convertToDatabaseColumn(String dadoSensiveis) {
        if (dadoSensiveis == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(KEY, "AES"));
            return Base64.getEncoder().encodeToString(cipher.doFinal(dadoSensiveis.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar dado sensível", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dadoCriptografadoBanco) {
        if (dadoCriptografadoBanco == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY, "AES"));
            return new String(cipher.doFinal(Base64.getDecoder().decode(dadoCriptografadoBanco)));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar dado sensível", e);
        }
    }
}
