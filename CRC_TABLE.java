package crc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CRC_TABLE {

    //��� �������: x16 + x12 + x5 + 1 = 0x1021 (� HEX)
    private static final int POL = 0x1021;
    //����� �������� PL (polynomial length) = 16 (�� ������� �������)
    private static final int PL = 16;

    public static void main(String[] args) {
        byte[] fileBytes = buildBytes("some_file");
        int[] table = buildTable();
        int CRC = calcCRC(fileBytes, table);
        System.out.println("====================");
        System.out.println("CRC-16:");
        System.out.println("--------------------");
        System.out.println("HEX " + String.format("%04x", CRC).toUpperCase());
        System.out.println("====================");
    }

    private static int calcCRC(byte[] bytes, int[] table) {
        //�������������� ����
        int CRC = 0;
        //��� ���� ���� ������� ������������������
        for (int i = 0; i < bytes.length; i += 2) {
            //���� ����� �� ��� ����� (���� = 8 ���, � ��� 16 ���, (16 / 8 = 2) => �� ���)
            int inBlock = (bytes[i] << 8) + bytes[(i + 1) % bytes.length];
            CRC = table[(CRC ^ inBlock) & 0xFFFF] & 0xFFFF;
        }
        return CRC;
    }

    private static int[] buildTable() {
        //������� ����� � �������, ������� ��������� ��������� �������� �������� ����� ����� �����
        //2 ^ n = ���������� ��������� ��������
        int[] table = new int[(int) Math.pow(2, PL)];
        for (int i = 0; i < table.length; i++) {
            //������ ������� = �����������, ��� �������� ������ CRC
            table[i] = calcCell(i);
        }
        return table;
    }

    private static int calcCell(int cell) {
        //���������� �������� = ����� ��������
        for (int i = 0; i < PL; i++) {
            //������� �������� CRC �� ������� ��������
            cell = iterate(cell);
        }
        return cell;
    }

    private static int iterate(int currentValue) {
        //������� ����� ��� �� ������ ������ ������ (����������) � �������� ���� ������, � ������ � ������������ �������� ������� 15 ���
        int outBit = currentValue >> (PL - 1);
        //�������� ����� �� ���� �������, ��������� ������� ���, ����������� �������
        currentValue <<= 1;
        //�������� �������� �� ������ ��� 16�� ���
        currentValue %= (int) Math.pow(2, PL);
        //���� ���������� ��� �������
        if (outBit == 1) {
            //��������� XOR
            currentValue ^= POL;
        }
        return currentValue;
    }

    private static byte[] buildBytes(String filename) {
        //�������
        try {
            //������� ��� ����� �� �����
            return Files.readAllBytes(Paths.get(filename));
            //� ������ �������    
        } catch (IOException ex) {
            //������� ���������
            System.out.println("���� �� ������!");
            //��������� ������ ���������
            System.exit(0);
        }
        return null;
    }
}
