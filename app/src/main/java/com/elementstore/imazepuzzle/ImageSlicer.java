package com.elementstore.imazepuzzle;

import android.graphics.Bitmap;

public class ImageSlicer {

    Bitmap[][] mainImage;
    Bitmap resourceImage;
    int tilesHeight,tilesWidth, sliceSize;

    public ImageSlicer(Bitmap resourceImage, int sliceSize){
        this.resourceImage = resourceImage;
        this.sliceSize = sliceSize;
        this.mainImage = new Bitmap[sliceSize][sliceSize];
        this.tilesHeight = resourceImage.getHeight()/sliceSize;
        this.tilesWidth = resourceImage.getWidth()/sliceSize;

    }

    public Bitmap[][] getSlicedStrokedImage(int lineStroke, int roundRadius, int strokeColor){

        for (int x = 0; x<sliceSize; x++){
            for (int y = 0; y<sliceSize; y++){
                mainImage[y][x] = Bitmap.createBitmap(resourceImage, x*tilesWidth,y*tilesHeight,tilesWidth, tilesHeight);
                for(int i = 0; i<tilesWidth; i++){
                    for (int j = 0; j<lineStroke; j++){
                        mainImage[y][x].setPixel(i,j, strokeColor);
                        mainImage[y][x].setPixel(i,tilesWidth-j-1, strokeColor);
                        mainImage[y][x].setPixel(j,i, strokeColor);
                        mainImage[y][x].setPixel(tilesWidth-j-1, i, strokeColor);
                    }
                }

                int tempRadius = roundRadius;

                for (int n = lineStroke; n<lineStroke+roundRadius; n++){
                    for(int m = lineStroke; m<tempRadius+lineStroke; m++){
                        mainImage[y][x].setPixel(m,n, strokeColor);
                        mainImage[y][x].setPixel(m,tilesWidth-n, strokeColor);
                        mainImage[y][x].setPixel(tilesWidth-2*lineStroke-tempRadius+m,n, strokeColor);
                        mainImage[y][x].setPixel(tilesWidth-2*lineStroke-tempRadius+m,tilesWidth-n, strokeColor);
                    }
                    tempRadius--;
                }
            }
        }

        return mainImage;
    }
}

