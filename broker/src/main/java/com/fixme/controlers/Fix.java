package com.fixme.controlers;

/**
 * Fix
 */
public class Fix {
	// pattern for fix message
	// \|(([0-9]){1,3}=([a-z0-9]){1,}\|){4,}$

	public Fix() {
	}

	public String encode(String clientID, int id, int quantity, int type, int ma_id, int inst_type, float price) {
		String fixString = "";
		String fixString2 = "";
		fixString += "8=FIX-42|9=";
		fixString2 += "|35=" + type + "|49=" + clientID.trim() + "|56=" + ma_id;
		fixString2 += "|77=" + inst_type + "|88=" + quantity;
		fixString2 += "|99=" + price + "|";

		String tmp = fixString2.replaceAll("|", "");
		fixString += tmp.length();
		fixString += fixString2;
		fixString += "10=" + checksum(fixString) + "|";

		// need to revisist this
		return fixString;
	}

	public String checksum(String s) {
		int n = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			n += (c == '|') ? 1 : c;
		}
		return String.format("%03d", (n % 256));
	}
}
