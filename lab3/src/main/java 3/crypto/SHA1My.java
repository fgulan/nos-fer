package crypto;

public class SHA1My {

    public static String digest(byte[] input) {
        byte[] digest = byteDigest(input);
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < digest.length; i++) {
            builder.append(Integer.toString((digest[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return builder.toString();
    }

    private static byte[] addPadding(byte[] input) {
        int startLength = input.length;
        long startLengthBits = startLength * 8;

        // Append the bit '1' to the message e.g. by adding 0x80 if message length is a multiple of 8 bits.
        byte[] onePadding = new byte[startLength + 1];
        System.arraycopy(input, 0, onePadding, 0, startLength);
        onePadding[onePadding.length - 1] = (byte) 0x80;
        int newLength = onePadding.length * 8;

        while (newLength % 512 != 448) {
            newLength += 8;
        }

        // append 0 ≤ k < 512 bits '0', such that the resulting message length in bits
        // is congruent to −64 ≡ 448 (mod 512)
        byte[] zerosPadding = new byte[newLength/8];
        System.arraycopy(onePadding, 0 , zerosPadding, 0, onePadding.length);

        // append ml, the original message length, as a 64-bit big-endian integer.
        // Thus, the total length is a multiple of 512 bits.
        byte[] output = new byte[zerosPadding.length + 8];
        for (int i = 0; i < 8; i++) {
            output[output.length -1 - i] = (byte) ((startLengthBits >>> (8 * i)) & 0xFF);
        }
        System.arraycopy(zerosPadding, 0 , output, 0, zerosPadding.length);
        return output;
    }

    // Pseudo-code from https://en.wikipedia.org/wiki/SHA-1
    public static byte[] byteDigest(byte[] data) {
        byte[] output = addPadding(data);

        // Initialize variables:
        int[] H = new int[] {0x67452301, 0xEFCDAB89, 0x98BADCFE, 0x10325476, 0xC3D2E1F0};

        // Process the message in successive 512-bit chunks:
        int numberOfChunks = output.length * 8 /512;

        for (int i = 0; i < numberOfChunks; i++) {
            int[] w = new int[80];

            // Break chunk into sixteen 32-bit big-endian words w[i], 0 ≤ i ≤ 15
            for (int j = 0; j < 16; j++) {
                w[j] = ((output[i * 512/8 + 4*j] << 24) & 0xFF000000) | ((output[i * 512/8 + 4*j + 1] << 16) & 0xFF0000);
                w[j] |= ((output[i * 512/8 + 4*j + 2] << 8) & 0xFF00) | (output[i * 512/8 + 4*j + 3] & 0xFF);
            }

            // Extend the sixteen 32-bit words into eighty 32-bit words:
            for (int j = 16; j < 80; j++) {
                w[j] = rotateLeft(w[j-3] ^ w[j-8] ^ w[j-14] ^ w[j-16], 1);
            }

            // Initialize hash value for this chunk:
            int a = H[0], b = H[1], c = H[2], d = H[3], e = H[4], f = 0, k = 0;

            // Main loop:
            for (int j = 0; j < 80; j++) {
                if (0 <= j && j <= 19) {
                    f = (b & c) | ((~b) & d);
                    k = 0x5A827999;
                } else if(20 <= j && j <= 39) {
                    f = b ^ c ^ d;
                    k = 0x6ED9EBA1;
                } else if(40 <= j && j <= 59) {
                    f = (b & c) | (b & d) | (c & d);
                    k = 0x8F1BBCDC;
                } else if(60 <= j && j <= 79) {
                    f = b ^ c ^ d;
                    k = 0xCA62C1D6;
                }

                int temp = rotateLeft(a, 5) + f + e + k + w[j];
                e = d; d = c;
                c = rotateLeft(b, 30);
                b = a; a = temp;
            }
            // Add this chunk's hash to result so far:
            H[0] += a; H[1] += b; H[2] += c; H[3] += d; H[4] += e;
        }

        // Produce the final hash value (big-endian) as a 160-bit number:
        byte[] digest = new byte[20];
        for (int i = 0; i < H.length; i++) {
            for (int j = 0; j < 4; j++) {
                digest[j+4*i] = (byte) ((H[i] >>> 24-j*8) & 0xFF);
            }
        }
        return digest;
    }

    private static int rotateLeft(int number, int count) {
        return (number << count) | (number >>> (32 - count));
    }
}
