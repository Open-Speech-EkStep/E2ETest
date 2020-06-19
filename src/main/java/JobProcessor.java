import dto.FileProcessor;


import java.io.*;


public class JobProcessor {

    private FileProcessorService fileProcessorService = new FileProcessorService();



    public void porcessor(String link, String mode, String cluster, String bucket , String filename_prefix ) {
        FileProcessor fileProcessor = new FileProcessor(link, mode,filename_prefix);

        String configfilePath = fileProcessorService.processFile(fileProcessor);
        executeJob( cluster,bucket,configfilePath);
    }



    private void executeJob(String cluster, String bucket,String configfilePath ) {


      /*  PythonInterpreter interp = new PythonInterpreter();
        interp.exec("");
*/

        Process process = null;
        try {
           // ProcessBuilder processBuilder = new ProcessBuilder("python", "download.py");
            //pb.directory(new File("download.py"));
           // process = pb.start();
            //processBuilder.redirectErrorStream(true);

           // System.out.println(bucket);
            // process = processBuilder.start();

            String cmd[]= new String[5];
            cmd[0]="python -m Users.amulya.ahuja.Downloads.Ekstep.audio-to-speech-pipeline.packages.datacollector_youtube.src.scripts.download";
            cmd[2]=cluster;
            cmd[3]=bucket;
            cmd[4]=configfilePath;


       //process = Runtime.getRuntime().exec(cmd[0]  + " "+ cmd[1]+ " " +  cmd[2]+ " " + cmd[3]+ " " + cmd[4]);
       process = Runtime.getRuntime().exec(new String[]{cmd[0],cmd[2],cmd[3],cmd[4]});

     //  process.waitFor();
        }

        catch (Exception e) {
            System.out.println("Exception Raised" + e.toString());
        }

        InputStream stdout = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
        BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;

      /*  OutputStream outputStream = process.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        printStream.println();*/



        try {
            while ((line = reader.readLine()) != null) {
                System.out.println("stdout: " + line);
            }
            while((line = error.readLine()) != null){
                System.out.println(line);
            }
            error.close();

        } catch (IOException e) {
            System.out.println("Exception in reading output" + e.toString());
        }
    }
}
