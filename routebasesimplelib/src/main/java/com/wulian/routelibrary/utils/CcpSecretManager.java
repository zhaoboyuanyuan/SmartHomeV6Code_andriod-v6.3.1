/**
 * Project Name:  RouteBaseSimpleLib
 * File Name:     Des.java
 * Package Name:  com.wulian.routelibrary.utils
 * @Date:         2016年7月12日
 * Copyright (c)  2016, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.utils;

/**
 * @ClassName: Des
 * @Function:
 * @Date: 2016年7月12日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class CcpSecretManager {

	static final int[] pc_1_cp = new int[] { 57, 49, 41, 33, 25, 17, 9, 1, 58,
			50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52,
			44, 36 };

	static final int[] pc_1_dp = new int[] { 63, 55, 47, 39, 31, 23, 15, 7, 62,
			54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20,
			12, 4 };

	static final int[] pc_2p = new int[] { 14, 17, 11, 24, 1, 5, 3, 28, 15, 6,
			21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2, 41, 52, 31, 37,
			47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50,
			36, 29, 32 };

	static final int[] ls_countp = new int[] { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2,
			2, 2, 2, 2, 1 };

	static final int[] iip_tab_p = new int[] { 58, 50, 42, 34, 26, 18, 10, 2,
			60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64,
			56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51,
			43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47,
			39, 31, 23, 15, 7 };

	static final int[] _iip_tab_p = new int[] { 40, 8, 48, 16, 56, 24, 64, 32,
			39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37,
			5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3,
			43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41,
			9, 49, 17, 57, 25 };

	static final int[] e_r_p = new int[] { 32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9,
			8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20,
			21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31,
			32, 1 };

	static final int[] local_PP = new int[] { 16, 7, 20, 21, 29, 12, 28, 17, 1,
			15, 23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30,
			6, 22, 11, 4, 25 };

	static final int[][][] ccom_SSS_p = new int[][][] {
			{ { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7 },
					{ 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8 },
					{ 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0 },
					{ 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 } },
			{ { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10 },
					{ 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5 },
					{ 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15 },
					{ 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 } },
			{ { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8 },
					{ 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1 },
					{ 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7 },
					{ 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 } },
			{ { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15 },
					{ 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9 },
					{ 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4 },
					{ 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 } },
			{ { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9 },
					{ 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6 },
					{ 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14 },
					{ 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 } },
			{ { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11 },
					{ 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8 },
					{ 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6 },
					{ 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 } },
			{ { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1 },
					{ 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6 },
					{ 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2 },
					{ 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 } },
			{ { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7 },
					{ 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2 },
					{ 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8 },
					{ 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } } };

	static final String[] keys = new String[] { "20:57:10", "vm(697):",
			"!@#$%^&+", "215K,ext", "wuliancc", "GC_EXPLI", "dalvikvm",
			"Instance", "x=x-0x37", "htons100" };

	private int keyIndex = 0;

	private String defaultKey;

	private static CcpSecretManager instance = new CcpSecretManager();

	byte[][] C;

	byte[][] D;

	byte[][] K;

	public CcpSecretManager() {
		this.defaultKey = keys[this.keyIndex];
		this.C = new byte[17][28];
		this.D = new byte[17][28];
		this.K = new byte[17][48];
	}

	private byte iu2b(int input) {
		return (byte) (input & 255);
	}

	private int b2iu(byte b) {
		return b < 0 ? b & 255 : b;
	}

	private void desMemcpy(byte[] output, byte[] input, int outpos, int inpos,
			int len) {
		for (int i = 0; i < len; ++i) {
			output[outpos + i] = input[inpos + i];
		}

	}

	private void Fexpand0(byte[] in, byte[] out) {
		for (int i = 0; i < 8; ++i) {
			int divide = 7;

			for (int j = 0; j < 8; ++j) {
				byte temp1 = in[i];
				out[8 * i + j] = this.iu2b(this.b2iu(temp1) >>> divide & 1);
				--divide;
			}
		}

	}

	private void FLS(byte[] bits, byte[] buffer, int count) {
		for (int i = 0; i < 28; ++i) {
			buffer[i] = bits[(i + count) % 28];
		}

	}

	private void Fson(byte[] cc, byte[] dd, byte[] kk) {
		byte[] buffer = new byte[56];

		int i;
		for (i = 0; i < 28; ++i) {
			buffer[i] = cc[i];
		}

		for (i = 28; i < 56; ++i) {
			buffer[i] = dd[i - 28];
		}

		for (i = 0; i < 48; ++i) {
			kk[i] = buffer[pc_2p[i] - 1];
		}

	}

	private void Fsetkeystar(byte[] bits) {
		int i;
		for (i = 0; i < 28; ++i) {
			this.C[0][i] = bits[pc_1_cp[i] - 1];
		}

		for (i = 0; i < 28; ++i) {
			this.D[0][i] = bits[pc_1_dp[i] - 1];
		}

		for (int j = 0; j < 16; ++j) {
			this.FLS(this.C[j], this.C[j + 1], ls_countp[j]);
			this.FLS(this.D[j], this.D[j + 1], ls_countp[j]);
			this.Fson(this.C[j + 1], this.D[j + 1], this.K[j + 1]);
		}

	}

	private void Fiip(byte[] text, byte[] ll, byte[] rr) {
		byte[] buffer = new byte[64];
		this.Fexpand0(text, buffer);

		int i;
		for (i = 0; i < 32; ++i) {
			ll[i] = buffer[iip_tab_p[i] - 1];
		}

		for (i = 0; i < 32; ++i) {
			rr[i] = buffer[iip_tab_p[i + 32] - 1];
		}

	}

	private void Fs_box(byte[] aa, byte[] bb) {
		byte[] ss = new byte[8];
		int m = 0;

		for (int i = 0; i < 8; ++i) {
			int j = 6 * i;
			int y = this.b2iu(aa[j]) * 2 + this.b2iu(aa[j + 5]);
			int z = this.b2iu(aa[j + 1]) * 8 + this.b2iu(aa[j + 2]) * 4
					+ this.b2iu(aa[j + 3]) * 2 + this.b2iu(aa[j + 4]);
			ss[i] = this.iu2b(ccom_SSS_p[i][y][z]);
			y = 3;

			for (int k = 0; k < 4; ++k) {
				bb[m++] = this.iu2b(this.b2iu(ss[i]) >>> y & 1);
				--y;
			}
		}

	}

	private void FF(int n, byte[] ll, byte[] rr, byte[] LL, byte[] RR) {
		byte[] buffer = new byte[64];
		byte[] tmp = new byte[64];

		int i;
		for (i = 0; i < 48; ++i) {
			buffer[i] = rr[e_r_p[i] - 1];
		}

		for (i = 0; i < 48; ++i) {
			buffer[i] = this.iu2b(this.b2iu(buffer[i])
					+ this.b2iu(this.K[n][i]) & 1);
		}

		this.Fs_box(buffer, tmp);

		for (i = 0; i < 32; ++i) {
			buffer[i] = tmp[local_PP[i] - 1];
		}

		for (i = 0; i < 32; ++i) {
			RR[i] = this.iu2b(this.b2iu(buffer[i]) + this.b2iu(ll[i]) & 1);
		}

		for (i = 0; i < 32; ++i) {
			LL[i] = rr[i];
		}

	}

	private void _Fiip(byte[] text, byte[] ll, byte[] rr) {
		byte[] tmp = new byte[64];

		int i;
		for (i = 0; i < 32; ++i) {
			tmp[i] = ll[i];
		}

		for (i = 32; i < 64; ++i) {
			tmp[i] = rr[i - 32];
		}

		for (i = 0; i < 64; ++i) {
			text[i] = tmp[_iip_tab_p[i] - 1];
		}

	}

	private void Fcompress0(byte[] out, byte[] in) {
		for (int i = 0; i < 8; ++i) {
			int times = 7;
			in[i] = 0;

			for (int j = 0; j < 8; ++j) {
				in[i] = this.iu2b(this.b2iu(in[i])
						+ (this.b2iu(out[i * 8 + j]) << times));
				--times;
			}
		}

	}

	private void Fencrypt0(byte[] text, byte[] mtext) {
		byte[] ll = new byte[64];
		byte[] rr = new byte[64];
		byte[] LL = new byte[64];
		byte[] RR = new byte[64];
		byte[] tmp = new byte[64];
		this.Fiip(text, ll, rr);

		for (int i = 1; i < 17; ++i) {
			this.FF(i, ll, rr, LL, RR);

			for (int j = 0; j < 32; ++j) {
				ll[j] = LL[j];
				rr[j] = RR[j];
			}
		}

		this._Fiip(tmp, rr, ll);
		this.Fcompress0(tmp, mtext);
	}

	private void FDES(byte[] key, byte[] text, byte[] mtext) {
		byte[] tmp = new byte[64];
		this.Fexpand0(key, tmp);
		this.Fsetkeystar(tmp);
		this.Fencrypt0(text, mtext);
	}

	private byte[] encrypt(byte[] key, byte[] s, int len) {
		byte[] d = new byte[1024];
		int size = d.length;
		byte[] cData = new byte[8];
		byte[] cEncryptData = new byte[8];

		int i;
		byte[] result;
		for (i = 0; i < len; i += 8) {
			if (i + 8 > len) {
				this.desMemcpy(cData, s, 0, i, len - i);

				for (int j = len - i; j < 8; ++j) {
					cData[j] = 0;
				}
			} else {
				this.desMemcpy(cData, s, 0, i, 8);
			}

			this.FDES(key, cData, cEncryptData);
			if (i + 8 >= size) {
				size += size;
				result = new byte[size];
				System.arraycopy(d, 0, result, 0, i);
				d = result;
			}

			this.desMemcpy(d, cEncryptData, i, 0, 8);
		}

		result = new byte[i];
		System.arraycopy(d, 0, result, 0, i);
		return result;
	}

	private void Fdiscrypt0(byte[] mtext, byte[] text) {
		byte[] ll = new byte[64];
		byte[] rr = new byte[64];
		byte[] LL = new byte[64];
		byte[] RR = new byte[64];
		byte[] tmp = new byte[64];
		this.Fiip(mtext, ll, rr);

		for (int i = 16; i > 0; --i) {
			this.FF(i, ll, rr, LL, RR);

			for (int j = 0; j < 32; ++j) {
				ll[j] = LL[j];
				rr[j] = RR[j];
			}
		}

		this._Fiip(tmp, rr, ll);
		this.Fcompress0(tmp, text);
	}

	private void _FDES(byte[] key, byte[] mtext, byte[] text) {
		byte[] tmp = new byte[64];
		this.Fexpand0(key, tmp);
		this.Fsetkeystar(tmp);
		this.Fdiscrypt0(mtext, text);
	}

	private byte[] decrypt(byte[] key, byte[] d, int len) {
		byte[] s = new byte[1024];
		int size = s.length;
		byte[] cData = new byte[8];
		byte[] cEncryptData = new byte[8];

		int i;
		byte[] result;
		for (i = 0; i < len; i += 8) {
			this.desMemcpy(cEncryptData, d, 0, i, 8);
			this._FDES(key, cEncryptData, cData);
			if (i + 8 >= size) {
				size += size;
				result = new byte[size];
				System.arraycopy(s, 0, result, 0, i);
				s = result;
			}

			this.desMemcpy(s, cData, i, 0, 8);
		}

		result = new byte[i];
		System.arraycopy(s, 0, result, 0, i);
		return result;
	}

	private String byteHEX(byte ib) {
		char[] Digit = new char[] { '0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] ob = new char[] { Digit[ib >>> 4 & 15], Digit[ib & 15] };
		String s = new String(ob);
		return s;
	}

	private String byteArr2HexStr(byte[] byteArr) {
		String hexStr = "";

		for (int i = 0; i < byteArr.length; ++i) {
			hexStr = hexStr + this.byteHEX(byteArr[i]);
		}

		return hexStr;
	}

	private byte[] hexStr2ByteArr(String hexStr) {
		byte[] arrB = hexStr.getBytes();
		int iLen = arrB.length;
		byte[] arrOut = new byte[iLen / 2];

		for (int i = 0; i < iLen; i += 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}

		return arrOut;
	}

	public String encode(String str) {
		byte[] byteMingW = str.getBytes();
		byte[] byteMiW = this.encrypt(this.defaultKey.getBytes(), byteMingW,
				byteMingW.length);
		String digestHexStr = this.byteArr2HexStr(byteMiW);
		return digestHexStr;
	}

	public String encode(String str, String key) {
		if (key != null && !key.equals("")) {
			byte[] byteMingW = str.getBytes();
			byte[] byteMiW = this.encrypt(key.getBytes(), byteMingW,
					byteMingW.length);
			String digestHexStr = this.byteArr2HexStr(byteMiW);
			return digestHexStr;
		} else {
			return this.encode(str);
		}
	}

	public String decode(String hexStr) {
		byte[] byteMiW = this.hexStr2ByteArr(hexStr);
		byte[] byteMingW = this.decrypt(this.defaultKey.getBytes(), byteMiW,
				byteMiW.length);
		String digestStr = (new String(byteMingW)).trim();
		return digestStr;
	}

	public String decode(String hexStr, String key) {
		if (key != null && !key.equals("")) {
			byte[] byteMiW = this.hexStr2ByteArr(hexStr);
			byte[] byteMingW = this.decrypt(key.getBytes(), byteMiW,
					byteMiW.length);
			String digestStr = (new String(byteMingW)).trim();
			return digestStr;
		} else {
			return this.decode(hexStr);
		}
	}

	public static CcpSecretManager getInstance() {
		return instance;
	}
}
