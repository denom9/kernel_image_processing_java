import java.io.*;


import static java.lang.Math.ceil;

public class PPMImage extends Image {
    PPMImage(){
        this.width = 0;
        this.height = 0;
        this.channels = 0;
        this.depth = 0;
        this.data = null;
    }
    PPMImage(int width, int height, int channels, int depth){
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.depth = depth;
        this.data = new float[width*height*channels];
    }

    PPMImage(int width, int height, int channels, int depth, float[] data){
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.depth = depth;
        this.data = data;
    }


    public float clamp(float x, float start, float end){
        return Float.min(Float.max(x,start),end);
    }

    public String skipSpaces(String line){
        int i = 0;
        char a = line.charAt(i);
        while(a == ' ' || a == '\t') {
            i++;
            a = line.charAt(i);
            if(i == line.length())
                break;
        }
        if(i > 0)
            line = line.substring(i);
        return line;
    }
    public boolean isComment(String line){
        return line.startsWith("#");
    }
    public void skipLine(InputStream is){
        try {
            int tmp = 0;
            while (tmp != 10)
                tmp = is.read();

        } catch (java.io.IOException e){

        }

    }
    public String nextLine(InputStream is){
        String tmpString = "";
        try {
            int tmp;

            tmp = is.read();
            if ((char) tmp == '#')
                skipLine(is);
            else if (tmp != 10)
                tmpString += (char) tmp;
            while (tmp != 10) {
                tmp = is.read();
                if (tmp != 10)
                    tmpString += (char) tmp;
            }
            //return tmpString;
        } catch (java.io.IOException e){

        }
        return tmpString;
    }
    public boolean PPM_import(String filename){
        String header,line;
        int tmp;

        /* apertura del file */
        try{
            InputStream inputStream = new FileInputStream(filename);



            header = nextLine(inputStream);
            //System.out.println(header);
            if(header == null) {
                System.out.println("Could not read from " + filename);
                return false;
            }
            else {
                if (header.equals("P5") || header.equals("P5\n")) {
                    this.channels = 1;
                    line = nextLine(inputStream);
                    line = skipSpaces(line);
                    parseDimensions(line);
                } else if (header.equals("P6") || header.equals("P6\n")) {
                    this.channels = 3;
                    line = nextLine(inputStream);
                    line = skipSpaces(line);
                    parseDimensions(line);
                } else {
                    line = nextLine(inputStream);
                    line = skipSpaces(line);
                }


                line = nextLine(inputStream);
                line = skipSpaces(line);
                this.depth = Integer.parseInt(line);


                this.data = new float[this.width * this.height * this.channels];

                byte[] byteData = new byte[this.width * this.height * this.channels];
                inputStream.read(byteData);

                float scale = 1.0f / ((float) this.depth);
                for(int i = 0; i < byteData.length; i++){
                    tmp = (byteData[i] < 0)? byteData[i] + 256 : byteData[i]; //signed to unsigned
                    this.data[i] = (float) tmp * scale;
                }

                inputStream.close();

                return true;
            }


        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Could not open file " + filename);
        }
        return false;
    }

    public boolean PPM_export(String filename){
        int width,height,channels,depth;

        /* apertura del file */
        try{
            width = this.width;
            height = this.height;
            depth = this.depth;
            channels = this.channels;

            OutputStream outputStream = new FileOutputStream(filename);


            //scrivo l'header
            String tmp = "P6\n" + width + " " + height + "\n" + depth + "\n";
            outputStream.write(tmp.getBytes());

            int x;
            byte[] writeData = new byte[width*height*channels];
            for(int i = 0; i < width*height*channels; i++) {
                x = (int)ceil(clamp(this.data[i],0,1)*depth);
                writeData[i] = (byte)x;
            }

            outputStream.write(writeData);

            outputStream.close();
            return true;
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Could not open file" + filename);
        }
        return false;
    }


    public void parseDimensions(String line){
        line = skipSpaces(line);
        String[] dimensions = line.split("\\s+");
        this.width = Integer.parseInt(dimensions[0]);
        this.height = Integer.parseInt(dimensions[1]);
    }

    public void parseDimensions(String line, int width, int height, int channels){
        line = skipSpaces(line);
        String[] dimensions = line.split("\\s+");
        width = Integer.parseInt(dimensions[0]);
        height = Integer.parseInt(dimensions[1]);
        channels = Integer.parseInt(dimensions[2]);
    }

}
