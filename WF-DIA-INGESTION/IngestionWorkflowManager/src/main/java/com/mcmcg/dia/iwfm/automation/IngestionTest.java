package com.mcmcg.dia.iwfm.automation;

/**
 * 
 * @author wporras
 *
 */
public class IngestionTest {

	private String user;
	private Long totalDocuments;
	private Long batchSize;
	private Integer workFlowThreads;
	private Integer documentProcessorCron;
	private boolean createSnippets;
	private boolean pdfTagging;
	private Integer nodes;
	private Integer cores;
	private Integer couchBaseIOPS;

	public IngestionTest() {
		super();
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Long getTotalDocuments() {
		return totalDocuments;
	}

	public void setTotalDocuments(Long totalDocuments) {
		this.totalDocuments = totalDocuments;
	}

	public Long getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(Long batchSize) {
		this.batchSize = batchSize;
	}

	public Integer getDocumentProcessorCron() {
		return documentProcessorCron;
	}

	public void setDocumentProcessorCron(Integer documentProcessorCron) {
		this.documentProcessorCron = documentProcessorCron;
	}

	public boolean isCreateSnippets() {
		return createSnippets;
	}

	public void setCreateSnippets(boolean createSnippets) {
		this.createSnippets = createSnippets;
	}

	public boolean isPdfTagging() {
		return pdfTagging;
	}

	public void setPdfTagging(boolean pdfTagging) {
		this.pdfTagging = pdfTagging;
	}

	public Integer getWorkFlowThreads() {
		return workFlowThreads;
	}

	public void setWorkFlowThreads(Integer workFlowThreads) {
		this.workFlowThreads = workFlowThreads;
	}

	public Integer getNodes() {
		return nodes;
	}

	public void setNodes(Integer nodes) {
		this.nodes = nodes;
	}

	public Integer getCores() {
		return cores;
	}

	public void setCores(Integer cores) {
		this.cores = cores;
	}

	public Integer getCouchBaseIOPS() {
		return couchBaseIOPS;
	}

	public void setCouchBaseIOPS(Integer couchBaseIOPS) {
		this.couchBaseIOPS = couchBaseIOPS;
	}

}
