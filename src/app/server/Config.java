package app.server;

public class Config {	
	public static int ELECTION_IN_PORT = 4447;
//	public static int ELECTION_OUT_PORT = 4448;
	
	public static int LEADER_LISTEN_PORT = 4445;
	public static int SLAVES_LISTEN_PORT = 4446;
	public static int SLAVES_UDP_SENDER_PORT = 4447; //Patch
	public static int IM_ALIVE_PORT = 4448; //Patch
	public static int IM_ALIVE_PORT2 = IM_ALIVE_PORT + 1; //Patch
	
//	public static String FRONT_END_NAME = "taos";
//	public static String SLAVE1_NAME = "tewa";
//	public static String SLAVE2_NAME = "tano";
//	public static String LEADER_NAME = "taos";

	public static String FRONT_END_NAME = "localhost";
	//public static String SLAVE1_NAME = "tewa";
	public static String SLAVE2_NAME = "localhost";
	public static String LEADER_NAME = "localhost";
	public static boolean IS_DEBUG = true;
	public static String DATA_FOLDER = System.getProperty( "user.home" ) + "/workspace/data/";
}

/*
 * For Demo
 * Change IS_DEBUG to false
 * Change DATA_FOLDER
 * Change FRONT_END_NAME, SLAVE1_NAME, SLAVE2_NAME, LEADER_NAME
 * Create folder data in workspace
 * */
        