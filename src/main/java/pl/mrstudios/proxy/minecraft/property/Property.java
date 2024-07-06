package pl.mrstudios.proxy.minecraft.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.PublicKey;
import java.security.Signature;

import static java.security.Signature.getInstance;
import static org.apache.commons.codec.binary.Base64.decodeBase64;

public record Property(
        @NotNull String name,
        @NotNull String value,
        @Nullable String signature
) {

    public boolean isSignatureValid(@NotNull PublicKey publicKey) {

        try {

            Signature signature = getInstance("SHA1withRSA");

            signature.initVerify(publicKey);
            signature.update(this.value.getBytes());

            return signature.verify(decodeBase64(this.signature));

        } catch (Exception ignored) {}

        return false;

    }

}
