package wyopcl.util;

import java.util.Collections;
import java.util.Properties;
import wybs.lang.Build;
import wycc.util.Logger;

public class InterpreterConfiguration {
	private Properties properties;
	
	public InterpreterConfiguration(Build.Project project){
		this.properties = new Properties();
		this.properties.put("verbose", false);
		this.properties.put("logger", Logger.NULL);
		this.properties.put("filename", "");
		this.properties.put("project", project);
		this.properties.put("arguments", Collections.emptyList());
	}
	
	public Object getProperty(String key){
		if(!this.properties.containsKey(key)){
			throw new RuntimeException("Unknown property:" +key);
		}		
		return this.properties.get(key);
	}
	
	public void setProperty(String key, Object value){
		this.properties.put(key, value);		
	}	
}
