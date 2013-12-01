package ServerModel;

import java.util.Date;

public class UploadedFile {
	private long fileId;
	private String fileName;
	private String fileSize;

	public UploadedFile() {
	}

	public UploadedFile(long fileId, String fileName, String fileSize) {
		this.fileId = fileId;
		this.fileName = new String(fileName);
		this.fileSize = fileSize;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public void setFileName(String fileName) {
		this.fileName = new String(fileName);
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public long getFileId() {
		return fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileSize() {
		return fileSize;
	}
}
