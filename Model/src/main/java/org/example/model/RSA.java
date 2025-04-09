package org.example.model;

import java.lang.*;
import java.util.*;
import java.math.BigInteger;
import java.security.*;

public class RSA {
    BigInteger p, q, N, e, d, euler, k, km1, S;
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
        //TODO: verification needed or we can assume that e,d,k,N already satisfies our requirements
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
        //k do ślepego podpisu
        k = BigInteger.probablePrime(keyLen, new Random());
        while (true)
            if (k.gcd(euler).equals(BigInteger.ONE)) break;
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

    public BigInteger[] encrypt(byte[] message) {
        int ileZnakow = (N.bitLength() - 1) / 8;
        while (message.length % ileZnakow != 0) {
            message = Arrays.copyOf(message, message.length + 1); // powiększa o 1 i uzupełnia zerem
            message[message.length - 1] = '\000';
        }
        int chunks = message.length / ileZnakow;
        BigInteger[] cipher = new BigInteger[chunks];
        for (int i = 0; i < chunks; i++) {
            byte[] pom = Auxx.podtablica(message, ileZnakow * i, ileZnakow * (i + 1));
            cipher[i] = new BigInteger(1, pom);
            cipher[i] = cipher[i].modPow(e, N);
        }
        return cipher;
    }


    public BigInteger[] encrypt(String message) {
        int ileZnakow = (N.bitLength() - 1) / 8;
        while (message.length() % ileZnakow != 0)
            message += ' ';
        int chunks = message.length() / ileZnakow;
        BigInteger[] cipher = new BigInteger[chunks];
        for (int i = 0; i < chunks; i++) {
            String s = message.substring(ileZnakow * i, ileZnakow * (i + 1));
            cipher[i] = Auxx.stringToBigInt(s);
            cipher[i] = cipher[i].modPow(e, N);
        }
        return cipher;
    }


    public String encryptFromStringToString(String message) {
        String str = new String();
        BigInteger[] bi_table = encrypt(message);
        for (int i = 0; i < bi_table.length; i++)
            str += bi_table[i] + "\n";
        return str;
    }

    public String decrypt(BigInteger[] cipher) {
        String s = new String();
        for (int i = 0; i < cipher.length; i++) {
            s += Auxx.bigIntToString(cipher[i].modPow(d, N));
        }
        return s;
    }

    public BigInteger[] decryptToBigInt(BigInteger[] cipher) {
        BigInteger[] wynik = new BigInteger[cipher.length];
        for (int i = 0; i < cipher.length; i++)
            wynik[i] = cipher[i].modPow(d, N);
        return wynik;
    }

    public String decryptFromStringToString(String cipher) {
        String[] wiersze = cipher.split("\n");
        BigInteger[] bi_table = new BigInteger[wiersze.length];
        for (int i = 0; i < wiersze.length; i++)
            bi_table[i] = new BigInteger(wiersze[i]);
        return decrypt(bi_table);
    }

    public BigInteger podpisuj(byte[] tekst) {
        digest.update(tekst);
        BigInteger podpis = new BigInteger(1, digest.digest());
        podpis = podpis.modPow(d, N);
        return podpis;
    }

    public BigInteger podpisuj(String tekst) {
        digest.update(tekst.getBytes());
        BigInteger podpis = new BigInteger(1, digest.digest());
        podpis = podpis.modPow(d, N);
        return podpis;
    }


    public boolean weryfikujBigInt(byte[] tekstJawny, BigInteger podpis) {
        digest.update(tekstJawny);
        BigInteger hash = new BigInteger(1, digest.digest());
        podpis = podpis.modPow(e, N);
        if (hash.compareTo(podpis) == 0) return true;
        else return false;
    }


    //zakładamy, że podpis jest w postaci hexadecymalnych znaków
    public boolean weryfikujString(String tekstJawny, String podpis) {
        digest.update(tekstJawny.getBytes());
        BigInteger hash = new BigInteger(1, digest.digest());
        BigInteger bi = new BigInteger(1, Auxx.hexToBytes(podpis));
        bi = bi.modPow(e, N);
        if (hash.compareTo(bi) == 0) return true;
        else return false;
    }


    public BigInteger podpisujSlepo(byte[] tekst) {
        digest.update(tekst);
        BigInteger podpis = new BigInteger(1, digest.digest());
        podpis = podpis.multiply(k.modPow(e, N)).mod(N);
        podpis = podpis.modPow(d, N);
        S = podpis;
        podpis = podpis.multiply(km1).mod(N);
        return podpis;
    }

    public BigInteger podpisujSlepo(String tekst) {
        digest.update(tekst.getBytes());
        BigInteger podpis = new BigInteger(1, digest.digest());
        podpis = podpis.multiply(k.modPow(e, N)).mod(N);
        podpis = podpis.modPow(d, N);
        S = podpis;
        podpis = podpis.multiply(km1).mod(N);
        return podpis;
    }


    public boolean weryfikujBigIntSlepo(byte[] tekstJawny, BigInteger podpis) {
        digest.update(tekstJawny);
        BigInteger pom = new BigInteger(1, digest.digest());
        pom = pom.multiply(k.modPow(e, N)).mod(N);
        pom = pom.modPow(d, N);//S=podpis;
        pom = pom.multiply(km1).mod(N);
        if (podpis.compareTo(pom) == 0) return true;
        else return false;
    }


    //zakładamy, że podpis jest w postaci hexadecymalnych znaków
    public boolean weryfikujStringSlepo(String tekstJawny, String podpis) {
        digest.update(tekstJawny.getBytes());
        BigInteger pom = new BigInteger(1, digest.digest());
        pom = pom.multiply(k.modPow(e, N)).mod(N);
        pom = pom.modPow(d, N);//S=podpis;
        pom = pom.multiply(km1).mod(N);
        BigInteger bi = new BigInteger(1, Auxx.hexToBytes(podpis));
        if (bi.compareTo(pom) == 0) return true;
        else return false;
    }


}
