import com.google.cloud.storage.Bucket;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;

public class GCPConnection {


        public static void listObjectsWithPrefix(String projectId, String bucketName, String directoryPrefix) {
            // The ID of your GCP project
             //String projectId = "your-project-id";

            // The ID of your GCS bucket
            // String bucketName = "your-unique-bucket-name";

            // The directory prefix to search for
            // String directoryPrefix = "myDirectory/"

            Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
            Bucket bucket = storage.get(bucketName);
            /**
             * Using the Storage.BlobListOption.currentDirectory() option here causes the results to display
             * in a "directory-like" mode, showing what objects are in the directory you've specified, as
             * well as what other directories exist in that directory. For example, given these blobs:
             *
             * <p>a/1.txt a/b/2.txt a/b/3.txt
             *
             * <p>If you specify prefix = "a/" and don't use Storage.BlobListOption.currentDirectory(),
             * you'll get back:
             *
             * <p>a/1.txt a/b/2.txt a/b/3.txt
             *
             * <p>However, if you specify prefix = "a/" and do use
             * Storage.BlobListOption.currentDirectory(), you'll get back:
             *
             * <p>a/1.txt a/b/
             *
             * <p>Because a/1.txt is the only file in the a/ directory and a/b/ is a directory inside the
             * /a/ directory.
             */
            Page<Blob> blobs =
                    bucket.list(
                            Storage.BlobListOption.prefix(directoryPrefix),
                            Storage.BlobListOption.currentDirectory());

            for (Blob blob : blobs.iterateAll()) {
                System.out.println(blob.getName());
            }
        }
    }