package crc;

public class CRC {

    public static void main(String[] args) {
        calc(encode(14390039), 0x589);
    }

    private static int encode(int NZK) {
        //�������� ���������
        int message = 0;
        //����������� � �������� ������� ����� �������� ������,
        //��� ����� - ������������ 4�� ������
        //   1    4    3    9    0    0    3    9
        //0001 0100 0011 1001 0000 0000 0011 1001
        System.out.println("=======================================");
        System.out.println("�\tdec\tbin\tshift<<");
        System.out.println(".......................................");
        //����, ������� � 7�� � ���������� ����
        for (int i = 7; i >= 0; i--) {
            //�� ���� ����� ������� � ����� �� ������ � ������� i, � ����� ���� ������� �� ������� �� 10
            //������: (891 / 100) % 10 = 8; (891 / 10) % 10 = 9; (891) % 10 = 1 
            int decimal = (NZK / (int) Math.pow(10, i)) % 10;
            //����� �� ������ �������� ��� ����� ����� �� ����������� ���-�� �������, ������ ������� 4�
            //����������  � ���������� ����� �� �������
            message += decimal << i * 4;
            //��������� � �������� ������, �� 4 �������
            System.out.println(7 - i + "\t" + decimal + "\t" + toBinary(decimal, 4) + "\t" + i * 4);
        }
        System.out.println("=======================================");
        System.out.println("��� � �������� �������������:");
        System.out.println(".......................................");
        System.out.println(toBinary(message, 32));
        return message;
    }

    private static void calc(int M, int G) {
        //������� � CRC ����
        int result = 0;
        //���� ����������:
        //0001 0100 0011 1001 0000 0000 0011 1001
        //������� � ������� ��������� ���� ��������� ��������� ����� �� ���� ������� 
        //400 ��������, ��� ��� ����� ��������� - 400
        for (int i = 399; i >= 0; i--) {
            //����� ����� ��� �� ������������������, �������� � ������ �� ����� ���������� �������,
            //����� ��������� � ���������� �������� ������ ��� � ����� � �������� �� 2, ����� ����� ��� ��������
            //(i + 16) % 32 ���������� �� 0 �� 31, ��� ���� � ������ ��������, ����� �������� i=399 �� ������ �������� 31
            //(399 + 16) % 32 = 31
            //[0]001 0100 0011 1001 0000 0000 0011 1001 >> 31 = 
            //[0] (��� ���� ������ 31 ��� �����������)
            //[0] % 2 = 0 (����� �������� ���� � ��� ���������� ����)
            result = iterate(399 - i, result, (M >> (i + 16) % 32) % 2, G);
        }
        //����� ������� - ����� ������������ CRC (53CA) ��� ������� �����
        int chainEnd = 0x53CA;
        //��������� ������ � 16� (�� ������� ��������) ������ ����� 
        for (int i = 15; i >= 0; i--) {
            //���� ��� ����������� ������� ����� ����� ���� �� ������� �����, ����� ��������� CRC
            //���� �� ������������ ����� CRC, ����� ��������� ��������
            result = iterate(400 + (15 - i), result, (chainEnd >> i) % 2, G);
        }
        System.out.println("=======================================");
    }

    private static int iterate(int number, int currentBits, int inBit, int G) {
        //������� ����� ��� �� ������ ������ ������ (����������) � �������� ���� ������, � ������ � ������������ �������� ������� 15 ���
        int outBit = currentBits >> 15;
        //�������� ����� �� ���� �������, ��������� ������� ���, ����������� �������
        currentBits <<= 1;
        //�������� �������� �� ������ ��� 16�� ���
        currentBits %= (int) Math.pow(2, 16);
        //��������� � ����� ��������, �� �������������� ����� �������� ��� �� ����������
        currentBits += inBit;
        //���� ���������� ��� �������
        if (outBit == 1) {
            //��������� XOR
            currentBits ^= G;
        }
        System.out.println("=======================================");
        System.out.println("�:\t" + number);
        System.out.println("---------------------------------------");
        System.out.println("out:\t" + outBit);
        System.out.println("in:\t" + inBit);
        System.out.println("---------------------------------------");
        System.out.println("\tBin\t\t\tHex");
        System.out.println(".......................................");
        System.out.println("CRC:\t" + toBinary(currentBits, 16) + "\t" + toHex(currentBits, 4));
        return currentBits;
    }

    private static String toBinary(int data, int len) {
        return formatTo4(formatToLen(Integer.toBinaryString(data), len));
    }

    private static String toHex(int data, int len) {
        return "0x" + formatToLen(Integer.toHexString(data), len).toUpperCase();
    }

    private static String formatTo4(String string) {
        return string.replaceAll("(.{4})", "$1 ");
    }

    private static String formatToLen(String string, int length) {
        return String.format("%" + length + "s", string).replace(' ', '0');
    }

}
