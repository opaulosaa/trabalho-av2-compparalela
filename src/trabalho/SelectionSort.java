package trabalho;

import java.util.Arrays;
import java.util.Random;

// Selection Sort serial: acha o menor do restante e coloca na posicao atual.
public class SelectionSort {

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
        selectionSort(array, 0, array.length - 1);
    }

    public static void selectionSort(int[] array, int lowIndex, int highIndex) {
        for (int i = lowIndex; i < highIndex; i++) {
            int indiceMenor = i;

            for (int j = i + 1; j <= highIndex; j++) {
                if (array[j] < array[indiceMenor]) {
                    indiceMenor = j;
                }
            }
            swap(array, i, indiceMenor);
        }
    }

    private static void swap(int[] array, int index1, int index2) {
        int temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }
}
