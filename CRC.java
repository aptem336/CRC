package crc;

public class CRC {

    public static void main(String[] args) {
        calc(encode(14390039), 0x589);
    }

    private static int encode(int NZK) {
        //обнуляем сообщение
        int message = 0;
        //переведённый в двоичную систему номер зачётной книжки,
        //где цифра - закодирована 4мя битами
        //   1    4    3    9    0    0    3    9
        //0001 0100 0011 1001 0000 0000 0011 1001
        System.out.println("=======================================");
        System.out.println("№\tdec\tbin\tshift<<");
        System.out.println(".......................................");
        //Цикл, начиная с 7ми и заканчивая нулём
        for (int i = 7; i >= 0; i--) {
            //Мы берём номер зачётки и делим на десять в степени i, а затем берём остаток от деления на 10
            //Пример: (891 / 100) % 10 = 8; (891 / 10) % 10 = 9; (891) % 10 = 1 
            int decimal = (NZK / (int) Math.pow(10, i)) % 10;
            //Затем мы должны сдвинуть это число влево на определённой кол-во позиций, всегда кратное 4м
            //Прибавляем  к результату число со сдвигом
            message += decimal << i * 4;
            //Переводим в бинарную строку, по 4 символа
            System.out.println(7 - i + "\t" + decimal + "\t" + toBinary(decimal, 4) + "\t" + i * 4);
        }
        System.out.println("=======================================");
        System.out.println("НЗК в бинарном представлении:");
        System.out.println(".......................................");
        System.out.println(toBinary(message, 32));
        return message;
    }

    private static void calc(int M, int G) {
        //заносим в CRC ноль
        int result = 0;
        //наша комбинация:
        //0001 0100 0011 1001 0000 0000 0011 1001
        //начиная с первого проверяем биты сдвигаясь сообщение влево на одну позицию 
        //400 итераций, так как длина сообщения - 400
        for (int i = 399; i >= 0; i--) {
            //чтобы взять бит из последовательности, сдвигаем её вправо на такое количество позиций,
            //чтобы последним в комбинации оказался нужный нам и делим с остатком на 2, чтобы взять его значение
            //(i + 16) % 32 колеблется от 0 до 31, при этом в первой итерации, когда значение i=399 мы должны получить 31
            //(399 + 16) % 32 = 31
            //[0]001 0100 0011 1001 0000 0000 0011 1001 >> 31 = 
            //[0] (при этом правые 31 бит отбросились)
            //[0] % 2 = 0 (таким способом берём и все оставшиеся биты)
            result = iterate(399 - i, result, (M >> (i + 16) % 32) % 2, G);
        }
        //Конец цепочки - ранее рассчитанный CRC (53CA) или цепочка нулей
        int chainEnd = 0x53CA;
        //продолжит расчёт с 16ю (по степени полинома) битами конца 
        for (int i = 15; i >= 0; i--) {
            //биты для продолжения расчёта будем брать либо из цепочки нулей, чтобы вычислить CRC
            //либо из вычисленного ранее CRC, чтобы выполнить проверку
            result = iterate(400 + (15 - i), result, (chainEnd >> i) % 2, G);
        }
        System.out.println("=======================================");
    }

    private static int iterate(int number, int currentBits, int inBit, int G) {
        //смотрим какой бит на данный момент первый (выдвинутый) в регистре путём сдвига, а значит и отбрасывания значений младших 15 бит
        int outBit = currentBits >> 15;
        //сдвигаем влево на одну позицую, выдвигаем старший бит, освобождаем младший
        currentBits <<= 1;
        //обрезаем значение до нужных нам 16ти бит
        currentBits %= (int) Math.pow(2, 16);
        //добавляем в конец регистра, на освободившееся место следущий бит из комбинации
        currentBits += inBit;
        //если выдвинутый бит единица
        if (outBit == 1) {
            //выполняем XOR
            currentBits ^= G;
        }
        System.out.println("=======================================");
        System.out.println("№:\t" + number);
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
