package ServerModel;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import ServerController.Command;
import ServerModel.SessionFactoryUtil;

public class UploadedFileManager {
	private static Session session;
	
	private static void createSession(){
		session = SessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
	}
	
	public static long addFile(String fileName, String fileSize){
		UploadedFile file = new UploadedFile();
		file.setFileName(fileName);
		file.setFileSize(fileSize);
		createSession();
		session.save(file);
		session.getTransaction().commit();
		session.close();
		return file.getFileId();
	}
	
	private static UploadedFile queryFile(long fileId){
		createSession();
		String hql = "from UploadedFile as u where u.fileId=:fileId";
		Query query = session.createQuery(hql);
		query.setLong("fileId", fileId);
		List <UploadedFile>list = query.list();
		UploadedFile file = null;
		java.util.Iterator<UploadedFile> iter = list.iterator();
		while (iter.hasNext()) {
			file = iter.next();
		}					
		session.getTransaction().commit();
		session.close();
		return file;	
	}
	
	public static UploadedFile fetchFile(long fileId){
		return queryFile(fileId);
	}
	
//	public static ArrayList<FilenameOwnerId> fetchFileList(List<Relation> list){
	public static String fetchFileList(List<Relation> list){
//		ArrayList<FilenameOwnerId> files = new ArrayList<FilenameOwnerId>();
		String files = "";
		Relation relation = null;
		java.util.Iterator<Relation> iter = list.iterator();
		long fileId;
		UploadedFile file;
		while (iter.hasNext()) {
			relation = iter.next();
			fileId = relation.getFileId();
			file = queryFile(fileId);
//			FilenameOwnerId item = new FilenameOwnerId();
//			item.setFileName(file.getFileName());
//			item.setOwnerId(relation.getSource());
//			item.setFileId(fileId);
//			files.add(item);
			if(files.equals("")){
				files = files.concat(file.getFileName() + Command.INTERNAL_DELETMITER + relation.getSource());
			}
			else{
				files = files.concat(Command.DELIMITER + file.getFileName() + Command.INTERNAL_DELETMITER + relation.getSource());
			}
		}
		return files;
	}
}
