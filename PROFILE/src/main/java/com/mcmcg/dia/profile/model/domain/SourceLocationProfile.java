package com.mcmcg.dia.profile.model.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author varias
 *
 */

public class SourceLocationProfile {

	private Long id;
	private String name;
	private List<Location> location = new ArrayList<Location>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Location> getLocation() {
		return location;
	}

	public void setLocation(List<Location> location) {
		this.location = location;
	}

	public static class Location {

		private String accessMethod;
		private String hostname;
		private Long port;
		private String username;
		private String password;
		private String remoteFileLocation;
		private String zippedFile;
		private String zippedFilePassword;

		public String getAccessMethod() {
			return accessMethod;
		}

		public void setAccessMethod(String accessMethod) {
			this.accessMethod = accessMethod;
		}

		public String getHostname() {
			return hostname;
		}

		public void setHostname(String hostname) {
			this.hostname = hostname;
		}

		public Long getPort() {
			return port;
		}

		public void setPort(Long port) {
			this.port = port;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getRemoteFileLocation() {
			return remoteFileLocation;
		}

		public void setRemoteFileLocation(String remoteFileLocation) {
			this.remoteFileLocation = remoteFileLocation;
		}

		public String getZippedFile() {
			return zippedFile;
		}

		public void setZippedFile(String zippedFile) {
			this.zippedFile = zippedFile;
		}

		public String getZippedFilePassword() {
			return zippedFilePassword;
		}

		public void setZippedFilePassword(String zippedFilePassword) {
			this.zippedFilePassword = zippedFilePassword;
		}
	}
}
