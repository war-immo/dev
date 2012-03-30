package de.zorgk.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Main {

	public static void main(String[] args) {
		boolean give_help = false;

		@SuppressWarnings("unused")
		boolean relax = false;

		try {

			InputStream inputStream = null;
			OutputStream outputStream = System.out;

			switch (args.length) {
			case 3:
				if (args[0].startsWith("--") == false) {
					give_help = true;
					break;
				} else {
					outputStream = new FileOutputStream(new File(args[2]));
				}

			case 2:
				if (args[0].startsWith("--") == false)
					outputStream = new FileOutputStream(new File(args[1]));
				else {
					if (args[1].equals("-"))
						inputStream = System.in;
					else
						inputStream = new FileInputStream(new File(args[1]));
				}

			case 1:
				if (args[0].startsWith("--") == false) {
					if (args[0].equals("-"))
						inputStream = System.in;
					else
						inputStream = new FileInputStream(new File(args[0]));
				} else {
					if (args[0].equalsIgnoreCase("--relax") == false) {
						give_help = true;
						break;
					}
					relax = true;
				}

				XmlHandler.transformStream(inputStream, outputStream);

				break;

			default:
				give_help = true;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		if (give_help == true) {
			System.out
					.println("Usage: java -jar dani2immo.jar [--relax] INPUT [OUTPUT]\n"
							+ "   where INPUT is either a path to the input file or '-' for\n"
							+ "   standard input.\n\n"
							+ "   Output will be written to the optional parameter OUTPUT or\n"
							+ "   in case of absence to the standard output.\n");
		}

	}

}
