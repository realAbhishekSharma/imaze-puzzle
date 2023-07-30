package com.elementstore.imazepuzzle;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

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

    public Bitmap getEmptyBoxImage(Bitmap source,int lineStroke, int roundRadius, int solidColor, int strokeColor){
        int imageWidth = source.getWidth();
        Bitmap temp = Bitmap.createBitmap(source,0,0,imageWidth,imageWidth);

        for (int x = 0; x<temp.getWidth(); x++){
            for (int y = 0; y<temp.getHeight(); y++){
                temp.setPixel(x,y,solidColor);
            }
        }

        for(int i = 0; i<imageWidth; i++){
            for (int j = 0; j<lineStroke; j++){
                temp.setPixel(i,j, strokeColor);
                temp.setPixel(i,imageWidth-j-1, strokeColor);
                temp.setPixel(j,i, strokeColor);
                temp.setPixel(imageWidth-j-1, i, strokeColor);
            }
        }

        int tempRadius = roundRadius;

        for (int n = lineStroke; n<lineStroke+roundRadius; n++){
            for(int m = lineStroke; m<tempRadius+lineStroke; m++){
                temp.setPixel(m,n, strokeColor);
                temp.setPixel(m,imageWidth-n, strokeColor);
                temp.setPixel(imageWidth-2*lineStroke-tempRadius+m,n, strokeColor);
                temp.setPixel(imageWidth-2*lineStroke-tempRadius+m,imageWidth-n, strokeColor);
            }
            tempRadius--;
        }
        return temp;
    }
}

