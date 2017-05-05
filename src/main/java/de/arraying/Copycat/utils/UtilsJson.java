package de.arraying.Copycat.utils;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.data.DataConfig;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Jacob on 5/5/2017.
 */
public class UtilsJson {

	public static boolean saveConfig(File file) {
		String json = Copycat.save.toJson(Copycat.dataConfig);
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
		} catch(IOException e) {
			Copycat.logger.fatal("Config file failed to write! (Could not create FileWriter)");
			e.printStackTrace();
			return false;
		}
		try {
			assert fw != null;
			fw.write(json);
			fw.close();
		} catch(IOException | NullPointerException e) {
			Copycat.logger.fatal("Config file failed to write! (Could not write)");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static DataConfig loadConfig(File file) {
		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch(FileNotFoundException e) {
			Copycat.logger.fatal("Config file failed to load! (Could not make FileReader)");
			e.printStackTrace();
			System.exit(1);
		}
		assert fr != null;
		return Copycat.load.fromJson(fr, DataConfig.class);
	}
}
