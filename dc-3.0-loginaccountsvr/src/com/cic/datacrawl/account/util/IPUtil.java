package com.cic.datacrawl.account.util;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class IPUtil {

	/**
	 * 获取本机IP
	 * @return
	 */
	public static String getHostIP() {
		String hostIP = "127.0.0.1";
		try {
			Enumeration<NetworkInterface> networkInterfaces = (Enumeration<NetworkInterface>) NetworkInterface
					.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) networkInterfaces.nextElement();
				Enumeration<InetAddress> e2 = ni.getInetAddresses();
				boolean found = false;
				while (e2.hasMoreElements()) {
					InetAddress ia = (InetAddress) e2.nextElement();
					if (ia instanceof Inet6Address) {
						continue; // omit IPv6 address
					}
					hostIP = ia.getHostAddress();
					System.out.println("addr:" + hostIP);
					if (!hostIP.equals("127.0.0.1")) {
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return hostIP;
	}

}
