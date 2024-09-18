package com.alexeiddg.backend.util;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

// TODO: Code revision

@Service
public class StringToBitstream {

    public static String stringToBitstream(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        StringBuilder bitstream = new StringBuilder();
        for (byte b : bytes) {
            StringBuilder binary = new StringBuilder(Integer.toBinaryString(Byte.toUnsignedInt(b)));
            while (binary.length() < 8) {
                binary.insert(0, "0");
            }
            bitstream.append(binary);
        }
        return bitstream.toString();
    }

    public static String bitstreamToString(String bitstream) {
        int length = bitstream.length();
        byte[] bytes = new byte[length / 8];
        for (int i = 0; i < length; i += 8) {
            String byteString = bitstream.substring(i, i + 8);
            bytes[i / 8] = (byte) Integer.parseInt(byteString, 2);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[][] bitstreamToMatrix(String bitstream) {
        if (bitstream.length() < 128) {
            StringBuilder paddedBitstream = new StringBuilder(bitstream);
            while (paddedBitstream.length() < 128) {
                paddedBitstream.append("0");
            }
            bitstream = paddedBitstream.toString();
        } else if (bitstream.length() > 128) {
            bitstream = bitstream.substring(0, 128);
        }

        byte[][] matrix = new byte[4][4];
        for (int i = 0; i < 16; i++) {
            String byteString = bitstream.substring(i * 8, (i + 1) * 8);
            byte value = (byte) Integer.parseInt(byteString, 2);
            matrix[i / 4][i % 4] = value;
        }

        return matrix;
    }

    public static byte[][][] bitstreamToMatrixChunks(String bitstream) {
        int chunkCount = (bitstream.length() + 127) / 128; // Calculate the number of 128-bit chunks
        byte[][][] chunks = new byte[chunkCount][4][4];

        for (int chunk = 0; chunk < chunkCount; chunk++) {
            String chunkBitstream = bitstream.substring(chunk * 128, Math.min((chunk + 1) * 128, bitstream.length()));

            for (int i = 0; i < chunkBitstream.length() / 8; i++) {
                String byteString = chunkBitstream.substring(i * 8, (i + 1) * 8);
                byte value = (byte) Integer.parseInt(byteString, 2);
                chunks[chunk][i / 4][i % 4] = value;
            }
        }
        return chunks;
    }

    public static String matrixChunksToBitstream(byte[][][] chunks) {
        StringBuilder bitstream = new StringBuilder();
        for (byte[][] matrix : chunks) {
            for (byte[] row : matrix) {
                for (byte b : row) {
                    String binary = String.format("%8s", Integer.toBinaryString(Byte.toUnsignedInt(b))).replace(' ', '0');
                    bitstream.append(binary);
                }
            }
        }
        return bitstream.toString();
    }

    public static String matrixToBitstream(byte[][] matrix) {
        StringBuilder bitstream = new StringBuilder();
        for (byte[] row : matrix) {
            for (byte b : row) {
                String binary = String.format("%8s", Integer.toBinaryString(Byte.toUnsignedInt(b))).replace(' ', '0');
                bitstream.append(binary);
            }
        }
        return bitstream.toString();
    }



    public static void printMatrix(byte[][] matrix) {
        for (byte[] row : matrix) {
            for (byte element : row) {
                System.out.printf("%02x ", element);
            }
            System.out.println();
        }
    }
}
