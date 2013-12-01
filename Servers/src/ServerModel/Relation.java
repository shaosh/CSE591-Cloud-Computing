package ServerModel;

import java.io.Serializable;

public class Relation implements Serializable{
	private static final long serialVersionUID = -288002855915204255L; 
	private String target;
	private long fileId;
	private long relationId;
	private int isOwner;
	private String source;

	public Relation(){}
	
	public Relation(String target, long fileId, long relationId, int isOwner, String source){
		this.target = target;
		this.fileId = fileId;
		this.relationId = relationId;
		this.isOwner = isOwner;
		this.source = source;
	}	
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public long getRelationId() {
		return relationId;
	}

	public int getIsOwner() {
		return isOwner;
	}

	public void setIsOwner(int isOwner) {
		this.isOwner = isOwner;
	}

	public void setRelationId(long relationId) {
		this.relationId = relationId;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}
}
