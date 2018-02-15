public class Image {
    int width;
    int height;
    int channels;
    int depth;
    float data[];

    Image(){
        this.width = 0;
        this.height = 0;
        this.channels = 3;
        this.depth = 0;
        this.data = null;
    }
    Image(int width, int height, int channels, int depth, float data[]){
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.depth = depth;
        this.data = data;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public void setData(float[] data) {
        this.data = data;
    }
}
