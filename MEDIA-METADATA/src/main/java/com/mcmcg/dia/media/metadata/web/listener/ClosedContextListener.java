/**
 * 
 */
package com.mcmcg.dia.media.metadata.web.listener;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import com.couchbase.client.java.Cluster;
import com.mcmcg.dia.media.metadata.annotation.Auditable;
import com.mcmcg.dia.media.metadata.util.EventCode;

/**
 * @author jaleman
 *
 */
@Component
public class ClosedContextListener implements ApplicationListener<ContextClosedEvent> {

	private static final Logger LOG = Logger.getLogger(ClosedContextListener.class);

	@Autowired
	private Cluster couchbaseCluster;
	/**
	 * 
	 */
	public ClosedContextListener() {

	}

	@Auditable(eventCode = EventCode.STOP)
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		LOG.info("Context stopped ");
		
		if(couchbaseCluster != null){
			couchbaseCluster.disconnect();
			couchbaseCluster = null;
		}
	}

}
