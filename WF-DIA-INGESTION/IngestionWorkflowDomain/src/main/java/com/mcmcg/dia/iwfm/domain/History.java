package com.mcmcg.dia.iwfm.domain;

/**
 * 
 * 2015-12-31-13-27-29-030 | SVC-DIA-PROFILE | 000 | service-start successful
 * 
 * @author Jose Aleman
 *
 */

public class History {

	private String id;
	private String serviceName;
	private int eventCode;
	private String eventMessage;
	private String entryDate;

	public History(String serviceName, int eventCode, String eventMessage, String entryDate) {
		this.serviceName = serviceName;
		this.eventCode = eventCode;
		this.eventMessage = eventMessage;
		this.entryDate = entryDate;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public int getEventCode() {
		return eventCode;
	}

	public void setEventCode(int eventCode) {
		this.eventCode = eventCode;
	}

	public String getEventMessage() {
		return eventMessage;
	}

	public void setEventMessage(String eventMessage) {
		this.eventMessage = eventMessage;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getEntryDate() {
		return entryDate;
	}
}
