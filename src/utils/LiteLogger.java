package utils;

import app.server.Config;


public class LiteLogger {
	public static void log(String message) {
		if (Config.IS_DEBUG) {
			System.out.println(message);
		}
	}
	
	public static void log(Object ... params) {
		if (Config.IS_DEBUG) {
			System.out.println();
			for (Object param : params) {
				if (param == null) {
					System.out.print("null ");
				}
				else {
					System.out.print(param.toString() + " " );					
				}
			}
			System.out.println();
		}
	}
}
