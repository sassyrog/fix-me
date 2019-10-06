package com.fixme.controlers;

/**
 * Fix
 */
public class Fix {
	// pattern for fix message
	// \|(([0-9]){1,3}=([a-z0-9]){1,}\|){4,}$

	public Fix() {
	}

	public String encode(int id, int quantity, int type, int ma_id) {
		String fixString = "";
		fixString += "8=FIX-42|9=" + type + "|35=%len%|";
		return "";
	}
}
