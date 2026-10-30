package trabalho;

import java.util.Arrays;
import java.util.Random;

// Merge Sort serial: divide o vetor em duas copias, ordena cada uma e junta.
public class Mergesort {

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
        mergeSort(array, array.length);
    }

    public static void mergeSort(int[] a, int n) {
        if (n < 2) {
            return;
        }
        int mid = n / 2;
        int[] left = new int[mid];
        int[] right = new int[n - mid];

        for (int i = 0; i < mid; i++) {
            left[i] = a[i];
        }
        for (int i = mid; i < n; i++) {
            right[i - mid] = a[i];
        }
        mergeSort(left, mid);
        mergeSort(right, n - mid);

        merge(a, left, right, mid, n - mid);
    }

    public static void merge(int[] a, int[] left, int[] right, int leftSize, int rightSize) {
        int i = 0, j = 0, k = 0;
        while (i < leftSize && j < rightSize) {
            if (left[i] <= right[j]) {
                a[k++] = left[i++];
            } else {
                a[k++] = right[j++];
            }
        }
        while (i < leftSize) {
            a[k++] = left[i++];
        }
        while (j < rightSize) {
            a[k++] = right[j++];
        }
    }
}
