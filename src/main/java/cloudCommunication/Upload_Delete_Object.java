package cloudCommunication;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Upload_Delete_Object {
    public static void uploadObject(
            String projectId, String bucketName, String objectName, String filePath) throws IOException {

            Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
            BlobId blobId = BlobId.of(bucketName, objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));

            System.out.println(
                    "File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);
    }


        public static void deleteObject(String projectId, String bucketName, String objectName) {

            Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
            storage.delete(bucketName, objectName);

            System.out.println("Object " + objectName + " was deleted from " + bucketName);
        }


    }
