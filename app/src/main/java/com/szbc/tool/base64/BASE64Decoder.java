package com.szbc.tool.base64;

import java.util.Arrays;

public class BASE64Decoder {
	private static final byte[] table;
	static {
		table = new byte[128];
		for (int i = 0; i < 128; i++) {
			table[i] = -1;
		}
		for (int i = 'A'; i <= 'Z'; i++) {
			table[i] = (byte) (i - 'A');
		}
		for (int i = 'a'; i <= 'z'; i++) {
			table[i] = (byte) (i - 'a' + 26);
		}
		for (int i = '0'; i <= '9'; i++) {
			table[i] = (byte) (i - '0' + 52);
		}
		table['+'] = 62;
		table['/'] = 63;
	}

	public static byte[] decode(CharSequence data) {
		int length = data.length();
		byte[] bytes = new byte[length];
		int byteLength = 0;
		for (int i = 0; i < length; i++) {
			char c = data.charAt(i);
			if (c < 128 && table[c] != -1) {
				bytes[byteLength] = (byte) c;
				byteLength++;
			}
		}
		length = byteLength & 0xFFFFFFFC;
		int j = 0;
		for (int i = 0; i < length; i += 4, j += 3) {
			int b1 = table[data.charAt(i)];
			int b2 = table[data.charAt(i + 1)];
			int b3 = table[data.charAt(i + 2)];
			int b4 = table[data.charAt(i + 3)];
			bytes[j] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[j + 1] = (byte) ((b2 << 4) | (b3 >> 2));
			bytes[j + 2] = (byte) ((b3 << 6) | b4);
		}
		switch (byteLength % 4) {
		case 2: {
			int b1 = table[data.charAt(data.length() - 4)];
			int b2 = table[data.charAt(data.length() - 3)];
			bytes[j] = (byte) ((b1 << 2) | (b2 >> 4));
			j++;
			break;
		}
		case 3: {
			int b1 = table[data.charAt(data.length() - 4)];
			int b2 = table[data.charAt(data.length() - 3)];
			int b3 = table[data.charAt(data.length() - 2)];
			bytes[j] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[j + 1] = (byte) ((b2 << 4) | (b3 >> 2));
			j += 2;
		}
		}
		return Arrays.copyOf(bytes, j);
	}
}
