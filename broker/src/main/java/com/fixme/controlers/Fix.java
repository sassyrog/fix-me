package com.fixme.controlers;

/**
 * Fix
 */
public class Fix {
	// pattern for fix message
	// \|(([0-9]){1,3}=([a-z0-9]){1,}\|){4,}$

	public Fix() {
	}

	public String encode(int id, int quantity) {
		System.out.println("yeah");
		String fixString = "";
		String fixString2 = "";
		fixString += "8=FIX-42|";
		return "";
	}
}
