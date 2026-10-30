package trabalho;

import java.util.Arrays;
import java.util.Random;

// Insertion Sort serial: encaixa cada elemento na parte ja ordenada.
public class InsertionSort {

    public static void main(String[] args) {
        Random rand = new Random();
        int[] numeros = new int[10];
        for (int i = 0; i < numeros.length; i++) {
            numeros[i] = rand.nextInt(100);
        }
        System.out.println(Arrays.toString(numeros));
        ordenar(numeros);
        System.out.println(Arrays.toString(numeros));
    }

    public static void ordenar(int[] array) {
        insertionSort(array, 0, array.length - 1);
    }

    public static void insertionSort(int[] array, int lowIndex, int highIndex) {
        for (int i = lowIndex + 1; i <= highIndex; i++) {
            int chave = array[i];
            int j = i - 1;

            while (j >= lowIndex && array[j] > chave) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = chave;
        }
    }
}
