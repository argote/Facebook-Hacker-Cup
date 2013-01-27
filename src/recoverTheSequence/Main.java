package recoverTheSequence;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
	private static Scanner in = new Scanner(System.in);
	private static int count = 0;
	private static String debug = "";

	public static void main(String[] args) {
		int cases = in.nextInt();
		for (int i = 0; i < cases; i++) {
			count = 0;
			debug = "";
			int size = in.nextInt();
			in.nextLine(); // Just burn the rest of the line

			debug = in.nextLine();

			int[] arr = process(size);

			int result = checksum(arr);

			System.out.println("Case #" + (i + 1) + ": " + result);
		}
	}

	private static int[] process(int size) {
		int[] arr = new int[size];

		for (int i = 0; i < arr.length; i++) {
			arr[i] = i;
		}

		int[] arrTmp = mergeSort(arr);

		for (int i = 0; i < arr.length; i++) {
			arr[arrTmp[i]] = i + 1;
		}

		return arr;
	}

	private static int[] mergeSort(int[] list) {
		if (list.length <= 1) {
			return list;
		}
		int mid = list.length / 2;

		int[] arr1 = Arrays.copyOfRange(list, 0, mid);
		int[] arr2 = Arrays.copyOfRange(list, mid, list.length);

		// System.out.println("Took " + Arrays.toString(list));
		// System.out.println("arr1: " + Arrays.toString(arr1) + " arr2: "
		// + Arrays.toString(arr2) + "\n");

		int[] firstHalf = mergeSort(arr1);
		int[] secondHalf = mergeSort(arr2);
		return merge(firstHalf, secondHalf);
	}

	private static int[] merge(int[] arr1, int[] arr2) {
		int[] out = new int[arr1.length + arr2.length];

		int i = 0;
		int a1 = 0;
		int a2 = 0;
		while (a1 < arr1.length && a2 < arr2.length) {
			if (debug.charAt(count) == '1') {
				out[i] = arr1[a1];
				a1++;
				count++;
			} else {
				out[i] = arr2[a2];
				a2++;
				count++;
			}
			i++;
		}

		while (a1 < arr1.length) {
			out[i] = arr1[a1];
			a1++;
			i++;
		}

		while (a2 < arr2.length) {
			out[i] = arr2[a2];
			a2++;
			i++;
		}

		// System.out.println("Merged " + Arrays.toString(arr1) + " and "
		// + Arrays.toString(arr2) + " into " + Arrays.toString(out));

		return out;
	}

	private static int checksum(int[] arr) {
		int result = 1;
		for (int i : arr) {
			result = (31 * result + i) % 1000003;
		}
		return result;
	}
}
