package org.diylc.appframework.miscutils;

public enum Environment {

	INSTANCE;

	OsType osType = OsType.UNKNOWN;

	private Environment() {
		String osName = System.getProperty("os.name").toLowerCase();

		if (osName.indexOf("mac") != -1) {
			osType = OsType.OSX;
		} else if (osName.indexOf("windows") != -1) {
			osType = OsType.WINDOWS;
		} else if (osName.indexOf("linux") != -1) {
			osType = OsType.LINUX;
		}
	}

	public OsType getOsType() {
		return osType;
	}

}
