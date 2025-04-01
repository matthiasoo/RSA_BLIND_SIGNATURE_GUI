package org.example.model;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.*;

//klasa zawiera metody do wczytywania i zapisywania do plików, konwertujące i takie tam
public class Auxx {
    //wczytuje całą zawartość pliku o podanej nazwie do tablicy bajtów
    public static byte[] wczytajZPliku(String nazwa_pliku) throws Exception {
        FileInputStream fis = new FileInputStream(nazwa_pliku);
        int ileWPliku = fis.available();
        byte[] dane = new byte[ileWPliku];
        fis.read(dane);
        fis.close();
        return dane;
    }

    //zapisuje do pliku o podanej nazwie zawartość tablicy bajtów
    public static void zapiszDoPliku(byte dane[], String nazwa_pliku) throws Exception {
        FileOutputStream fos = new FileOutputStream(nazwa_pliku);
        fos.write(dane);
        fos.close();
    }


    //zapisuje do pliku tablicę BigIntegerów dodając po każdym znak nowej linii
    public static void zapiszDoPlikuTabliceBigIntNewLine(BigInteger dane[], String nazwa_pliku) {
        try {
            File file = new File(nazwa_pliku);
            FileWriter writer = new FileWriter(file);
            for (int i = 0; i < dane.length; i++) writer.write(dane[i].toString() + "\n");

            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //zapisuje do pliku tablicę BigIntegerów
    public static void zapiszDoPlikuTabliceBigInt(BigInteger dane[], String nazwa_pliku) {
        byte[] tab;
        try {
            File file = new File(nazwa_pliku);
            FileOutputStream fos = new FileOutputStream(file);
            for (int i = 0; i < dane.length; i++) {
                if (dane[i].equals(BigInteger.ZERO)) {
                    tab = new byte[1];
                    tab[0] = '\000';
                    fos.write(tab);
                } else {
                    tab = dane[i].toByteArray();
                    // byteArray powinna miec długosc 31
                    if (tab[0] == '\000' && tab.length == 32) tab = podtablica(tab, 1, tab.length);
                    if (tab.length == 30) tab = dodajZero(tab);
                    if (i == dane.length - 1) // usun nulle z uzupelnienia
                        tab = podtablicaBezZer(tab);
                    fos.write(tab);
                }
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //wczytuje dane z pliku do tabliy BigIntegerów
    public static BigInteger[] wczytajZPlikuTabliceBigInt(String nazwa_pliku) {
        BigInteger[] array = new BigInteger[1];
        try {
            File file = new File(nazwa_pliku);
            Scanner sc = new Scanner(file);
            int i = 0;
            while (sc.hasNextBigInteger()) {
                if (i > 0) array = Arrays.copyOf(array, array.length + 1);
                array[i] = sc.nextBigInteger();
                i++;
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return array;
    }


    //konwertuje tablicę bajtów na ciąg znaków w systemie heksadecymalnym
    public static String bytesToHex(byte bytes[]) {
        byte rawData[] = bytes;
        StringBuilder hexText = new StringBuilder();
        String initialHex = null;
        int initHexLength = 0;

        for (int i = 0; i < rawData.length; i++) {
            int positiveValue = rawData[i] & 0x000000FF;
            initialHex = Integer.toHexString(positiveValue);
            initHexLength = initialHex.length();
            while (initHexLength++ < 2) {
                hexText.append("0");
            }
            hexText.append(initialHex);
        }
        return hexText.toString();
    }

    //konwertuje ciąg znaków w systemie heksadecymalnym na tablicę bajtów
    public static byte[] hexToBytes(String tekst) {
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
                    JOptionPane.showMessageDialog(null, "Problem z przekonwertowaniem HEX->BYTE.\n Sprawdź wprowadzone dane.", "Problem z przekonwertowaniem HEX->BYTE", JOptionPane.ERROR_MESSAGE);
                }
            }
            return wynik;
        }
    }

    //konwertuje stringa na BigIntegera
    public static BigInteger stringToBigInt(String str) {
        byte[] tab = new byte[str.length()];
        for (int i = 0; i < tab.length; i++)
            tab[i] = (byte) str.charAt(i);
        return new BigInteger(1, tab);
    }

    //konwertuje BigIntegera na string
    public static String bigIntToString(BigInteger n) {
        byte[] tab = n.toByteArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < tab.length; i++)
            sb.append((char) tab[i]);
        return sb.toString();
    }


    //zwraca podtablice z elementami od..do z podanej tablicy
    public static byte[] podtablica(byte dane[], int poczatek, int koniec) {
        byte[] subArray = new byte[koniec - poczatek];
        for (int i = 0; poczatek < koniec; poczatek++, i++) subArray[i] = dane[poczatek];
        return subArray;
    }

    //dodaje zero do tablicy bajtów
    public static byte[] dodajZero(byte dane[]) {
        byte[] wynik = new byte[dane.length + 1];
        wynik[0] = '\000';
        for (int i = 0; i < dane.length; i++) wynik[i + 1] = dane[i];
        return wynik;
    }

    //zwraca tablice z podanej tablicy z powycinanymi zerami
    public static byte[] podtablicaBezZer(byte dane[]) {
        ArrayList<Byte> tab = new ArrayList<Byte>();
        for (int i = dane.length - 1; i >= 0; i--) {
            if (dane[i] == '\000') continue;
            else tab.add(dane[i]);
        }
        byte[] wynik = new byte[tab.size()];
        for (int j = 0, i = tab.size() - 1; i >= 0; i--, j++)
            wynik[j] = tab.get(i).byteValue();
        return wynik;
    }

    //sprawdza czy w bajcie podany bit jest ustawiony
    public static boolean isBitSet(byte bajt, int bit) {
        return (bajt & (1 << bit)) != 0;
    }

    //ustawia podany bit w bajcie
    public static byte setBitOne(byte bajt, int poz) {
        return (byte) (bajt | (1 << poz));
    }

    //kasuje podany bit w bajcie
    public static byte setBitZero(byte bajt, int poz) {
        return (byte) (bajt & ~(1 << poz));
    }

    // zwraca wartość 0/1 bitu na podanej pozycji w podanej tablicy bajtów
    public static int getBitAt(byte[] data, int poz) {
        int posByte = poz / 8;
        int posBit = poz % 8;
        byte valByte = data[posByte];
        int valInt = valByte >> (7 - posBit) & 1;
        return valInt;
    }

    //ustawia lub kasuje bit na podanej pozycji w podanej tablicy bajtów
    public static void setBitAt(byte[] data, int pos, int val) {
        byte oldByte = data[pos / 8];
        oldByte = (byte) (((0xFF7F >> (pos % 8)) & oldByte) & 0x00FF);
        byte newByte = (byte) ((val << (7 - (pos % 8))) | oldByte);
        data[pos / 8] = newByte;
    }

    //xoruje dwie tablice bajtów
    public static byte[] XORBytes(byte[] a, byte[] b) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ b[i]);
        }
        return out;
    }


    //cykliczne przesunięcie bitów w lewo o zadana ilość pozycji
    public static byte[] rotateLeft(byte[] in, int len, int step) {
        byte[] out = new byte[(len - 1) / 8 + 1];
        for (int i = 0; i < len; i++) {
            int val = getBitAt(in, (i + step) % len);
            setBitAt(out, i, val);
        }
        return out;
    }

    //zwraca podaną ilość bitów od podanej pozycji z tablicy bajtów
    public static byte[] selectBits(byte[] in, int pos, int len) {
        int numOfBytes = (len - 1) / 8 + 1;
        byte[] out = new byte[numOfBytes];
        for (int i = 0; i < len; i++) {
            int val = Auxx.getBitAt(in, pos + i);
            Auxx.setBitAt(out, i, val);
        }
        return out;
    }

    //wybiera bity z tablicy bajtów i zwraca w nowej tablicy
    //wybierane te bity, które wskazane w drugim parametrze(każdy bajt tablicy map jest numerem bitu do pobrania z tablicy in)
    public static byte[] selectBits(byte[] in, byte[] map) {
        int numOfBytes = (map.length - 1) / 8 + 1;
        byte[] out = new byte[numOfBytes];
        for (int i = 0; i < map.length; i++) {
            int val = getBitAt(in, map[i] - 1);
            setBitAt(out, i, val);
        }
        return out;
    }


    //wypisuje dowolny tekst po którym wartości poszczególnych bitów z tabeli bajtów
    public static void printBytes(byte[] data, String tekst) {
        System.out.println(tekst + ":");
        for (int i = 0; i < data.length; i++) {
            System.out.print(byteToBits(data[i]) + " ");
        }
        System.out.println();
    }

    //zwraca wartości poszczególnych bitów w bajcie w postaci stringa
    public static String byteToBits(byte b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append((int) (b >> (8 - (i + 1)) & 0x0001));
        }
        return sb.toString();
    }


}//koniec klasy Auxx
