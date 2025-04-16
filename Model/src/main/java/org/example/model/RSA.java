package org.example.model;

import java.lang.*;
import java.util.*;
import java.math.BigInteger;
import java.security.*;

public class RSA {
    BigInteger p, q, N, e, d, euler, k, km1;
    MessageDigest digest;
    int keyLen = 256;

    public RSA() {
        generateKey();
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new RSAException("Can't find SHA-256 algorithm");
        }
    }

    public RSA(BigInteger e, BigInteger d, BigInteger k, BigInteger N) {
        this.e = e;
        this.d = d;
        this.N = N;
        this.k = k;
        this.km1 = k.modInverse(N);
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new RSAException("Can't find SHA-256 algorithm");
        }
    }

    public void generateKey() {
        p = BigInteger.probablePrime(keyLen, new Random());
        q = BigInteger.probablePrime(keyLen, new Random());
        N = p.multiply(q);
        euler = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.probablePrime(keyLen, new Random());
        while (true)
            if (e.gcd(euler).equals(BigInteger.ONE)) break;
            else e = e.nextProbablePrime();
        d = e.modInverse(euler);
        k = BigInteger.probablePrime(keyLen, new Random());
        while (true)
            if (k.gcd(N).equals(BigInteger.ONE)) break;
            else k = k.nextProbablePrime();
        km1 = k.modInverse(N);
    }

    public BigInteger getK() {
        return k;
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getD() {
        return d;
    }

    public BigInteger getN() {
        return N;
    }

    public BigInteger podpisujSlepo(String tekst) {
        digest.update(tekst.getBytes());
        BigInteger podpis = new BigInteger(1, digest.digest());
        podpis = podpis.multiply(k.modPow(e, N)).mod(N);
        podpis = podpis.modPow(d, N);
        podpis = podpis.multiply(km1).mod(N);
        return podpis;
    }

    //zakładamy, że podpis jest w postaci hexadecymalnych znaków
    public boolean weryfikujStringSlepo(String tekstJawny, String podpis) {
        digest.update(tekstJawny.getBytes());
        BigInteger pom = new BigInteger(1, digest.digest());
        pom = pom.multiply(k.modPow(e, N)).mod(N);
        pom = pom.modPow(d, N);
        pom = pom.multiply(km1).mod(N);
        BigInteger bi = new BigInteger(1, hexToBytes(podpis));
        if (bi.compareTo(pom) == 0) return true;
        else return false;
    }

    //zamiana tablicy bajtów na ich reprezentację heksadecymalną
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return "";
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    //konwertuje ciąg znaków w systemie heksadecymalnym na tablicę bajtów
    public byte[] hexToBytes(String tekst) {
        if (tekst == null) {
            return null;
        } else if (tekst.length() < 2) {
            return null;
        } else {
            if (tekst.length() % 2 != 0) tekst += '0';
            int dl = tekst.length() / 2;
            byte[] wynik = new byte[dl];
            for (int i = 0; i < dl; i++) {
                try {
                    wynik[i] = (byte) Integer.parseInt(tekst.substring(i * 2, i * 2 + 2), 16);
                } catch (NumberFormatException e) {
                    wynik[i] = (byte) 0;
                }
            }
            return wynik;
        }
    }
}