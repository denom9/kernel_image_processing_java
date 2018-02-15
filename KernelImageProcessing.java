

public class KernelImageProcessing {

    public static void main(String[] args)  throws InterruptedException{
        PPMImage input = new PPMImage(),output;
        input.PPM_import(args[0]);
        String outputPath = args[1];
        int kernelSize = Integer.parseInt(args[2]);
        int numThreads = Integer.parseInt(args[3]);

        output = new PPMImage(input.width,input.height,input.channels,input.depth);
        float[] kernel = (kernelSize == 5)? Globals.kernel5 : (kernelSize == 7)? Globals.kernel7 : Globals.kernel3;


        long start = System.currentTimeMillis();


        Worker[] workers = new Worker[numThreads];
        for (int i = 0; i < numThreads; i++)
            workers[i] = new Worker(i,numThreads, input, output, kernelSize, kernel);


        for (int i = 0; i < numThreads; i++)
            workers[i].start();

        for (int i = 0; i < workers.length; i++)
            workers[i].join();


        long end = System.currentTimeMillis();

        long elapsed = (end - start);

        System.out.print((double) elapsed / 1000);

        output.PPM_export(outputPath + "output_java" + numThreads + ".ppm");

    }


    public static boolean differenceImage(String img1, String img2){
        PPMImage image1 = new PPMImage();
        PPMImage image2 = new PPMImage();

        image1.PPM_import(img1);
        image2.PPM_import(img2);

        boolean result = true;
        PPMImage out = new PPMImage(image1.width,image1.height,image1.channels,image1.depth);

        for(int i = 0; i < image1.data.length; i++) {
            if(image1.data[i] == image2.data[i])
                out.data[i] = 0;
            else{
                out.data[i] = 1;
                result = false;
            }
        }

        //out.PPM_export(outputPath + "../tools/difference.ppm");
        return result;
    }

}
