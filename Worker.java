public class Worker extends Thread {
    int id;
    int numThreads;
    int kernelSize;
    float[] kernel;
    PPMImage image;
    PPMImage output;

    Worker(int id, int numThreads, PPMImage input,PPMImage output, int kernelSize, float[] kernel){
        this.id = id;
        this.numThreads = numThreads;
        this.kernelSize = kernelSize;
        this.kernel = kernel;
        this.image = input;
        this.output = output;

    }



    public void run(){
        /*divido l'immagine in NUM_THREADS strisce verticali dividendo image.width per NUM_THREAD. ogni thread si prende una striscia.
        * nel caso di divisioni con resto l'ultimo thread si prende la striscia più il resto
        * */

        int ik,jk,kernelIndex,dataIndex;

        float sum=0,currentPixel;

        /* larghezza "comune" a tutti i thread, usata per scorrere correttamente alla giusta striscia dell'immagine */
        int widthOffset = (this.image.width / numThreads);
        /* larghezza specifica del thread, diversa per l'ultimo thread che avrà l'eccedenza nel caso di divisione con resto */
        int sliceWidth = (this.id == numThreads-1)? (this.image.width / numThreads) + (this.image.width % numThreads) : (this.image.width / numThreads);

        for(int i = 0; i < this.image.height; i++) {
            for (int j = 0; j < sliceWidth; j++) {
                for (int c = 0; c < this.image.channels; c++) {

                    for (int ii = 0; ii < this.kernelSize; ii++) {
                        ik = ((i - this.kernelSize / 2 + ii) < 0) ? 0 : ((i - this.kernelSize / 2 + ii) > this.image.height - 1) ? this.image.height - 1 : i - this.kernelSize / 2 + ii;
                        for (int jj = 0; jj < this.kernelSize; jj++) {

                            /* devo aggiustare le condizioni tenendo di conto che l'indice, se non esce dai bordi dell'immagine
                               deve poter "guardare all'indietro" nelle striscia del thread precedente per poter effettuare
                               corretamente la convoluzione. Le condizioni sono aggiustate con widthOffset*this.id
                               in questo modo tutti i thread tranne lo zero potranno avere indice jk negativo, in modo da poter tornare
                               indietro alla striscia precedente, mentre il thread 0 avrà fissato l'indice a 0, per l'estensione
                               dei bordi prevista
                            */
                            jk = ((j - this.kernelSize / 2 + jj + widthOffset*this.id) < 0) ? 0 : ((j - this.kernelSize / 2 + jj + widthOffset*this.id) > this.image.width - 1) ? this.image.width - 1 - widthOffset*this.id : j - this.kernelSize / 2 + jj;

                            dataIndex = (ik * this.image.width + jk + widthOffset*this.id) * this.image.channels + c;
                            currentPixel = this.image.data[dataIndex];

                            kernelIndex = (this.kernelSize - 1 - ii) * this.kernelSize + (this.kernelSize - 1 - jj);
                            sum += (currentPixel * this.kernel[kernelIndex]);

                        }

                    }
                    output.data[(i* this.image.width + j + widthOffset*this.id)* this.image.channels + c] = sum;
                    sum = 0;
                }

            }
        }

    }
}
