package crc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CRC_TABLE {

    //наш полином: x16 + x12 + x5 + 1 = 0x1021 (в HEX)
    private static final int POL = 0x1021;
    //длина полинома PL (polynomial length) = 16 (по старшей степени)
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
        //инициализируем нулём
        int CRC = 0;
        //для всех байт входной последовательности
        for (int i = 0; i < bytes.length; i += 2) {
            //берём блоки по два байта (байт = 8 бит, у нас 16 бит, (16 / 8 = 2) => по два)
            int inBlock = (bytes[i] << 8) + bytes[(i + 1) % bytes.length];
            CRC = table[(CRC ^ inBlock) & 0xFFFF] & 0xFFFF;
        }
        return CRC;
    }

    private static int[] buildTable() {
        //столько ячеек в таблице, сколько возможных различных значений битового числа такой длины
        //2 ^ n = количество возможных значений
        int[] table = new int[(int) Math.pow(2, PL)];
        for (int i = 0; i < table.length; i++) {
            //ячейка таблицы = вычисленный, для текущего номера CRC
            table[i] = calcCell(i);
        }
        return table;
    }

    private static int calcCell(int cell) {
        //количество итераций = длине полинома
        for (int i = 0; i < PL; i++) {
            //считаем значение CRC на текущей итерации
            cell = iterate(cell);
        }
        return cell;
    }

    private static int iterate(int currentValue) {
        //смотрим какой бит на данный момент первый (выдвинутый) в регистре путём сдвига, а значит и отбрасывания значений младших 15 бит
        int outBit = currentValue >> (PL - 1);
        //сдвигаем влево на одну позицию, выдвигаем старший бит, освобождаем младший
        currentValue <<= 1;
        //обрезаем значение до нужных нам 16ти бит
        currentValue %= (int) Math.pow(2, PL);
        //если выдвинутый бит единица
        if (outBit == 1) {
            //выполняем XOR
            currentValue ^= POL;
        }
        return currentValue;
    }

    private static byte[] buildBytes(String filename) {
        //пробуем
        try {
            //считать все байты из файла
            return Files.readAllBytes(Paths.get(filename));
            //в случае провала    
        } catch (IOException ex) {
            //выводим сообщение
            System.out.println("Файл не найден!");
            //завершаем работу программы
            System.exit(0);
        }
        return null;
    }
}
