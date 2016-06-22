package com.ccpress.izijia.util;

/**
 * Created by WLH on 2015/6/24 15:58.
 */
public class SortUtil {

    public static int partition(int a[], int low, int height) {
        int key = a[low];
        while (low < height) {
            while (low < height && a[height] >= key)
                height--;
            a[low] = a[height];

            while (low < height && a[low] <= key)
                low++;
            a[height] = a[low];
        }
        a[low] = key;
        return low;
    }


    public static void quickSort(int a[], int low, int height) {
        if (low < height) {
            int result = partition(a, low, height);
            quickSort(a, low, result - 1);
            quickSort(a, result + 1, height);
        }
    }

    public static int getMinIndex(int a[]) {

        int min = a[0];
        int minSub = 0;
        for (int i = 1; i < a.length; i++) {
            if (a[i] < min) {
                min = a[i];
                minSub = i;
            }
        }

        return minSub;
    }

}
