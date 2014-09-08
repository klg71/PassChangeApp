package core;

import java.io.File;
import java.util.*;

public class PluginManager {
	// the directory where we keep the plugin classes
	String pluginsDir;

	// a list where we keep an initialized object of each plugin class
	HashMap<String,Website> plugins;


	public PluginManager() {
		
		pluginsDir = "bin\\plugins";

		plugins = new HashMap<String,Website>();
		
	}

	public HashMap<String,Website> getPlugins() {
		File dir = new File(System.getProperty("user.dir") + File.separator + pluginsDir);
		ClassLoader cl = new PluginClassLoader(dir);
		
		if (dir.exists() && dir.isDirectory()) {
			// we'll only load classes directly in this directory -
			// no subdirectories, and no classes in packages are recognized
			String[] files = dir.list();
			for (int i=0; i<files.length; i++) {
				try {
					// only consider files ending in ".class"
					if (! files[i].endsWith(".class"))
						continue;
					Class c = cl.loadClass(files[i].substring(0, files[i].indexOf(".")));
//					Class[] intf = c.getInterfaces();
//					
					
//					for (int j=0; j<intf.length; j++) {
//						if (intf[j].getName().equals("Website")) {
							// the following line assumes that PluginFunction has a no-argument constructor
							if(c.getSuperclass().getName().equals("core.Website")){
								@SuppressWarnings("unchecked")
								Website pf = (Website) c.newInstance();
								plugins.put(pf.getName(),pf);
							}
							//continue;
						//}
					//}
				} catch (Exception ex) {
					System.err.println("File " + files[i] + " does not contain a valid Website class.");
					ex.printStackTrace();
				}
			}
		}
		return plugins;
	}

}
