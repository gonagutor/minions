package com.gonagutor.minions.utils;

public class UtilLibrary {
	// Joinked from https://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java/12968022
	public static String intToRoman(int num) {
		StringBuilder str = new StringBuilder();
		RomanDataStore arr[] = new RomanDataStore[13];
		arr[0] = new RomanDataStore(1000, "M");
		arr[1] = new RomanDataStore(900, "CM");
		arr[2] = new RomanDataStore(500, "D");
		arr[3] = new RomanDataStore(400, "CD");
		arr[4] = new RomanDataStore(100, "C");
		arr[5] = new RomanDataStore(90, "XC");
		arr[6] = new RomanDataStore(50, "L");
		arr[7] = new RomanDataStore(40, "XL");
		arr[8] = new RomanDataStore(10, "X");
		arr[9] = new RomanDataStore(9, "IX");
		arr[10] = new RomanDataStore(5, "V");
		arr[11] = new RomanDataStore(4, "IV");
		arr[12] = new RomanDataStore(1, "I");
		int itr = 0;
		RomanDataStore temp = null;
		while (num != 0) {
			temp = arr[itr];
			if (num >= temp.val) {
				for (int i = 0; i < num / temp.val; i++) {
					str.append(temp.s);
				}
				num = num % temp.val;
			}
			itr++;
		}
		return str.toString();
	}

	private static class RomanDataStore {
		private int val;
		private String s;

		RomanDataStore(int val, String s) {
			this.val = val;
			this.s = s;
		}
	}
}
