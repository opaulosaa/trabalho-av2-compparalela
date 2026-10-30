package trabalho;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

// Quick Sort serial com pivo aleatorio e dois ponteiros.
public class Quicksort {

    public static void main(String[] args) {
        int[] numeros = new int[10];
        for (int i = 0; i < numeros.length; i++) {
            numeros[i] = ThreadLocalRandom.current().nextInt(100);
        }
        System.out.println(Arrays.toString(numeros));
        ordenar(numeros);
        System.out.println(Arrays.toString(numeros));
    }

    public static void ordenar(int[] array) {
        quickSort(array, 0, array.length - 1);
    }

    public static void quickSort(int[] array, int lowIndex, int highIndex) {
        if (lowIndex >= highIndex) {
            return;
        }
        int indicePivo = particionar(array, lowIndex, highIndex);
        quickSort(array, lowIndex, indicePivo - 1);
        quickSort(array, indicePivo + 1, highIndex);
    }

    // Sorteia o pivo, leva para o fim e separa menores a esquerda e maiores a direita.
    // Devolve a posicao final do pivo.
    public static int particionar(int[] array, int lowIndex, int highIndex) {
        int randa = ThreadLocalRandom.current().nextInt(highIndex - lowIndex + 1) + lowIndex;
        swap(array, randa, highIndex);
        int pivo = array[highIndex];
        int leftPointer = lowIndex;
        int rightPointer = highIndex;

        while (leftPointer < rightPointer) {
            while (leftPointer < rightPointer && array[leftPointer] <= pivo) {
                leftPointer++;
            }
            while (leftPointer < rightPointer && array[rightPointer] >= pivo) {
                rightPointer--;
            }
            swap(array, leftPointer, rightPointer);
        }
        swap(array, leftPointer, highIndex);
        return leftPointer;
    }

    public static void swap(int[] array, int index1, int index2) {
        int temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }
}
