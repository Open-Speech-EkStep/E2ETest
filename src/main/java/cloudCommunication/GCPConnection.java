package cloudCommunication;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import Constants.*;

import java.util.ArrayList;
import java.util.List;

public class GCPConnection  implements Constants {


    public static int bucketSize(String folderPath) {

        Storage storage = StorageOptions.newBuilder().setProjectId(PROJECT_ID).build().getService();
        Bucket bucket = storage.get(BUCKET_NAME);


        Page<Blob> blobs =
                bucket.list(
                        Storage.BlobListOption.prefix(folderPath),
                        Storage.BlobListOption.currentDirectory());

        List<String> list = new ArrayList<String>();

        for (Blob blob : blobs.iterateAll()) {
            list.add(blob.getName());
            System.out.println(list);
        }
        return list.size();
    }



   /* public static void main(String aa[])
    {

        System.out.println(bucketSize(DOWNLOAD_DIRECTORY));
        System.out.println(bucketSize(CATALOGUE_DIRECTORY));

    }*/

}