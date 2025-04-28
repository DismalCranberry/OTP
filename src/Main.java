import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.time.Instant;

public class Main {
    private static final String TOTP_SHARED_SECRET = "GIO32ZTF6KSJKNBG"; //Template example

    public static void main(String[] args) throws Exception {
        System.out.println(generateTOTPCodeManual());
    }

    private static String generateTOTPCodeManual() throws Exception {
        Base32 codec = new Base32();
        byte[] keyBytes = codec.decode(Main.TOTP_SHARED_SECRET);

        long nowSeconds = Instant.now().getEpochSecond();
        long timeStep = nowSeconds / 30;

        byte[] counter = ByteBuffer.allocate(8).putLong(timeStep).array();

        Mac hmac = Mac.getInstance("HmacSHA1");
        hmac.init(new SecretKeySpec(keyBytes, "HmacSHA1"));
        byte[] hash = hmac.doFinal(counter);

        int offset = hash[hash.length - 1] & 0x0F;
        int binary = ((hash[offset] & 0x7F) << 24) | ((hash[offset + 1] & 0xFF) << 16) | ((hash[offset + 2] & 0xFF) << 8) | (hash[offset + 3] & 0xFF);

        int otp = binary % 1_000_000;
        return String.format("%06d", otp);

    }
}
